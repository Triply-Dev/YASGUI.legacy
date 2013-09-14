package com.data2semantics.yasgui.mgwtlinker.linker;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.data2semantics.yasgui.mgwtlinker.server.BindingProperty;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.linker.impl.SelectionInformation;

@LinkerOrder(LinkerOrder.Order.POST)
@Shardable
public class PermutationMapLinker extends AbstractLinker {

	public static final String EXTERNAL_FILES_CONFIGURATION_PROPERTY_NAME = "html5manifestlinker_files";
	public static final String PERMUTATION_MANIFEST_FILE_ENDING = ".appcache";
	public static final String PERMUTATION_FILE_ENDING = ".perm.xml";
	public static final String MANIFEST_MAP_FILE_NAME = "manifest.map";

	private XMLPermutationProvider xmlPermutationProvider;

	public PermutationMapLinker() {
		xmlPermutationProvider = new XMLPermutationProvider();
		manifestWriter = new ManifestWriter();
	}

	private ManifestWriter manifestWriter;

	@Override
	public String getDescription() {
		return "PermutationMapLinker";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException {
		if (onePermutation) {
			Map<String, Set<BindingProperty>> permutationMap = buildPermutationMap(logger, context, artifacts);
			Set<Entry<String, Set<BindingProperty>>> entrySet = permutationMap.entrySet();

			// since we are in onePermutation there should be just one
			// strongName
			// better make sure..
			if (permutationMap.size() != 1) {
				logger.log(Type.ERROR, "There should be only one permutation right now, but there were: '" + permutationMap.size() + "'");
				throw new UnableToCompleteException();
			}

			Entry<String, Set<BindingProperty>> next = entrySet.iterator().next();
			String strongName = next.getKey();
			Set<BindingProperty> bindingProperties = next.getValue();

			// all artifacts for this compilation
			Set<String> artifactsForCompilation = getArtifactsForCompilation(logger, context, artifacts);

			ArtifactSet toReturn = new ArtifactSet(artifacts);
			PermutationArtifact permutationArtifact = new PermutationArtifact(PermutationMapLinker.class, strongName, artifactsForCompilation,
					bindingProperties);

			toReturn.add(permutationArtifact);
			return toReturn;
		}

		ArtifactSet toReturn = new ArtifactSet(artifacts);
		Map<String, Set<BindingProperty>> map = buildPermutationMap(logger, context, artifacts);

		if (map.size() == 0) {
			// hosted mode
			return toReturn;
		}

		Map<String, PermutationArtifact> permutationArtifactAsMap = getPermutationArtifactAsMap(artifacts);
		
		//we need different file sets/manifests for our dev version (unminimized js), and our stable version
		Set<String> stableExternalFiles = getStableExternalFiles(logger, context);
		Set<String> devExternalFiles = getDevExternalFiles(logger, context);
		

		// build manifest html page for our stable version (included as iframe in our webapp)
		String appcacheService = "manifest.appcache";
		String manifestHtmlPage = buildManifestHtmlPage(appcacheService);
		toReturn.add(emitString(logger, manifestHtmlPage, appcacheService + ".html"));
		
		// build manifest html page for our stable version (included as iframe in our webapp)
		String devManifestHtmlPage = buildManifestHtmlPage(appcacheService + "?type=dev");
		toReturn.add(emitString(logger, devManifestHtmlPage, "manifest.dev.appcache.html"));
		
		Set<String> allPermutationFiles = getAllPermutationFiles(permutationArtifactAsMap);

		// get all artifacts
		Set<String> allArtifacts = getArtifactsForCompilation(logger, context, artifacts);

		for (Entry<String, PermutationArtifact> entry : permutationArtifactAsMap.entrySet()) {
			PermutationArtifact permutationArtifact = entry.getValue();
			// make a copy of all artifacts
			HashSet<String> filesForCurrentPermutation = new HashSet<String>(allArtifacts);
			// remove all permutations
			filesForCurrentPermutation.removeAll(allPermutationFiles);
			// add files of the one permutation we are interested in
			// leaving the common stuff for all permutations in...
			filesForCurrentPermutation.addAll(entry.getValue().getPermutationFiles());
			filesForCurrentPermutation = appendVersionIfNeeded(filesForCurrentPermutation);
			
			String permXml = buildPermXml(logger, permutationArtifact, filesForCurrentPermutation, stableExternalFiles);

			// emit permutation information file
			SyntheticArtifact emitString = emitString(logger, permXml, permutationArtifact.getPermutationName() + PERMUTATION_FILE_ENDING);
			toReturn.add(emitString);
			
			
			// build manifest for our stable version
			String manifestFile = entry.getKey() + PERMUTATION_MANIFEST_FILE_ENDING;
			@SuppressWarnings("serial")
			Map<String, String> fallbacks = new HashMap<String, String>(){{put("/", "../index.jsp");}};
			String maniFest = buildManiFest(entry.getKey(), stableExternalFiles, filesForCurrentPermutation, fallbacks);
			toReturn.add(emitString(logger, maniFest, manifestFile));
			
			// build manifest for our dev version
			String devManifestFile = entry.getKey() + ".dev" + PERMUTATION_MANIFEST_FILE_ENDING;
			String devManiFest = buildManiFest(entry.getKey(), devExternalFiles, filesForCurrentPermutation);
			toReturn.add(emitString(logger, devManiFest, devManifestFile));

		}

		toReturn.add(createPermutationMap(logger, map));
		return toReturn;

	}

	
	private HashSet<String> appendVersionIfNeeded(HashSet<String> filesForCurrentPermutation) {
		HashSet<String> newFileSet = new HashSet<String>();
		for (String file: filesForCurrentPermutation) {
			if (file.contains("nocache")) {
				file += "?" + StaticConfig.VERSION;
			}
			newFileSet.add(file);
		}
		return newFileSet;
	}

	protected String buildPermXml(TreeLogger logger, PermutationArtifact permutationArtifact, Set<String> gwtCompiledFiles, Set<String> otherResources)
			throws UnableToCompleteException {
		HashSet<String> namesForPermXml = new HashSet<String>(gwtCompiledFiles);
		namesForPermXml.addAll(otherResources);

		try {
			return xmlPermutationProvider.writePermutationInformation(permutationArtifact.getPermutationName(), permutationArtifact.getBindingProperties(),
					namesForPermXml);
		} catch (XMLPermutationProviderException e) {
			logger.log(Type.ERROR, "can not build xml for permutation file", e);
			throw new UnableToCompleteException();
		}

	}

	/**
	 * @param permutationArtifactAsMap
	 * @return
	 */
	protected Set<String> getAllPermutationFiles(Map<String, PermutationArtifact> permutationArtifactAsMap) {
		Set<String> allPermutationFiles = new HashSet<String>();

		for (Entry<String, PermutationArtifact> entry : permutationArtifactAsMap.entrySet()) {
			allPermutationFiles.addAll(entry.getValue().getPermutationFiles());
		}
		return allPermutationFiles;
	}

	protected Map<String, PermutationArtifact> getPermutationArtifactAsMap(ArtifactSet artifacts) {
		Map<String, PermutationArtifact> hashMap = new HashMap<String, PermutationArtifact>();
		for (PermutationArtifact permutationArtifact : artifacts.find(PermutationArtifact.class)) {
			hashMap.put(permutationArtifact.getPermutationName(), permutationArtifact);
		}
		return hashMap;
	}

	protected boolean shouldArtifactBeInManifest(String pathName) {
		if (
				pathName.endsWith("symbolMap") || 
				pathName.endsWith(".xml.gz") || 
				pathName.endsWith("rpc.log") || 
				pathName.endsWith("gwt.rpc") || 
				pathName.endsWith("manifest.txt") || 
				pathName.startsWith("rpcPolicyManifest") || 
				pathName.startsWith("soycReport") || 
				pathName.endsWith(".cssmap") ||
				pathName.contains(" ")
			) {
			return false;
		}
		return true;
	}

	protected Set<String> getArtifactsForCompilation(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) {
		Set<String> artifactNames = new HashSet<String>();
		for (EmittedArtifact artifact : artifacts.find(EmittedArtifact.class)) {
			String pathName = artifact.getPartialPath();
			if (shouldArtifactBeInManifest(pathName)) {
				artifactNames.add(pathName);
			}
		}
		return artifactNames;

	}

	protected String buildManiFest(String moduleName, Set<String> staticResources, Set<String> cacheResources) {
		return manifestWriter.writeManifest(staticResources, cacheResources, null);
	}
	
	protected String buildManiFest(String moduleName, Set<String> staticResources, Set<String> cacheResources, Map<String, String> fallbacks) {
		return manifestWriter.writeManifest(staticResources, cacheResources, fallbacks);
	}

	protected String buildManifestHtmlPage(String manifestFileLocation) {
		String html = "<html manifest=\"" + manifestFileLocation + "\">\n";
		html += "<head></head>\n";
		html += "<body></body>\n";
		html += "</html>\n";
		return html;
	}
	
	private Set<String> getFontFiles() {
		HashSet<String> set = new HashSet<String>();
		set.add("../fonts/fonts.css?" + StaticConfig.VERSION);
		set.addAll(getExternalFilesFromDir("fonts", "", "eot", "svg", "ttf", "woff"));//don't append version string here
		return set;
	}

	/**
	 * Retrieves files we should add to manifest, but which are not generated by GWT (i.e. images/js files we use separately)
	 * @param logger
	 * @param context
	 * @return
	 */
	protected Set<String> getStableExternalFiles(TreeLogger logger, LinkerContext context) {
		HashSet<String> set = new HashSet<String>();
		//all external js/css files should be minimized/aggregated by our maven plugin
		set.add("../static/yasgui.js?" + StaticConfig.VERSION);
		set.add("../static/yasgui.css?" + StaticConfig.VERSION);
		set.add("../index.jsp");
		set.addAll(getFontFiles());
		set.addAll(getExternalFilesFromDir("images", "?" + StaticConfig.VERSION.replace(".", ""), "png", "jpg", "gif"));
		return set;
	}

	/**
	 * Retrieves files we should add to manifest, but which are not generated by GWT (i.e. images/js files we use separately)
	 * @param logger
	 * @param context
	 * @return
	 */
	protected Set<String> getDevExternalFiles(TreeLogger logger, LinkerContext context) {
		HashSet<String> set = new HashSet<String>(getExternalFilesFromDir("assets", "?" + StaticConfig.VERSION, "js", "css"));
		set.addAll(getExternalFilesFromDir("images", "?" + StaticConfig.VERSION.replace(".", ""), "png", "jpg", "gif"));
		set.addAll(getFontFiles());
		set.add("../dev.jsp");
		return set;
	}
	
	protected EmittedArtifact createPermutationMap(TreeLogger logger, Map<String, Set<BindingProperty>> map) throws UnableToCompleteException {

		try {
			String string = xmlPermutationProvider.serializeMap(map);
			return emitString(logger, string, MANIFEST_MAP_FILE_NAME);
		} catch (XMLPermutationProviderException e) {
			logger.log(Type.ERROR, "can not build manifest map", e);
			throw new UnableToCompleteException();
		}

	}

	protected Map<String, Set<BindingProperty>> buildPermutationMap(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
			throws UnableToCompleteException {

		HashMap<String, Set<BindingProperty>> map = new HashMap<String, Set<BindingProperty>>();

		for (SelectionInformation result : artifacts.find(SelectionInformation.class)) {
			Set<BindingProperty> list = new HashSet<BindingProperty>();
			map.put(result.getStrongName(), list);

			TreeMap<String, String> propMap = result.getPropMap();
			Set<Entry<String, String>> set = propMap.entrySet();

			for (Entry<String, String> entry : set) {
				BindingProperty bindingProperty = new BindingProperty(entry.getKey(), entry.getValue());
				list.add(bindingProperty);
			}

		}

		return map;

	}
	/**
	 * I didnt find a proper way to get the relative location of our assets/images dir: are we in the project root, or are we in the target dir?
	 * Therefore, use this 
	 * This depends on whether this project is deployed from eclipse, or via maven.
	 * Therefore, this (ugly) workaround
	 * @return
	 */
	private String getWebappPath() {
		String webappPath = "";
		File file = new File("src/main/webapp");
		if (file.exists()) {
			webappPath = file.getPath() + "/";
		}
		return webappPath;
	}
	
	private Set<String> getExternalFilesFromDir(String dir, String append, String... includeExtensions) {
		HashSet<String> fileSet = new HashSet<String>();
		String webappPath = getWebappPath();
		@SuppressWarnings("unchecked")
		ArrayList<File> assetFiles = new ArrayList<File>(FileUtils.listFiles(new File(webappPath + dir), includeExtensions, true));
		for (File assetFile: assetFiles) {
			String manifestFile = assetFile.getPath();
			if (webappPath.length() > 0) {
				//we need to make the manifest files relative to webap dir
				manifestFile = manifestFile.substring(webappPath.length());
			}
			//the manifest file is located in the Yasgui subdir of our webapp. The files we are adding are in the parent folder:
			fileSet.add("../" + manifestFile + append);
		};
		return fileSet;
	}
}
