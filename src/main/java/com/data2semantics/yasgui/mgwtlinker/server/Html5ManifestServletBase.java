package com.data2semantics.yasgui.mgwtlinker.server;

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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import com.data2semantics.yasgui.mgwtlinker.linker.ManifestWriter;
import com.data2semantics.yasgui.mgwtlinker.linker.PermutationMapLinker;
import com.data2semantics.yasgui.mgwtlinker.linker.XMLPermutationProvider;
import com.data2semantics.yasgui.mgwtlinker.linker.XMLPermutationProviderException;
import com.data2semantics.yasgui.mgwtlinker.server.propertyprovider.*;

public class Html5ManifestServletBase extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2540671294104865306L;
	private XMLPermutationProvider permutationProvider;

	private Map<String, PropertyProvider> propertyProviders = new HashMap<String, PropertyProvider>();

	public Html5ManifestServletBase() {
		permutationProvider = new XMLPermutationProvider();

	}

	protected void addPropertyProvider(PropertyProvider propertyProvider) {
		propertyProviders.put(propertyProvider.getPropertyName(), propertyProvider);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String moduleName = getModuleName(req);

		String baseUrl = getBaseUrl(req);
		
		String userAgent = req.getHeader("User-Agent").toLowerCase();
		getPermutationIdString(baseUrl, moduleName, userAgent);

		Set<BindingProperty> computedBindings = calculateBindinPropertiesForClient(req);

		String strongName = getPermutationIdString(baseUrl, moduleName, userAgent);

		if (strongName != null) {
			String manifest = readManifest(baseUrl + moduleName + "/" + strongName + PermutationMapLinker.PERMUTATION_MANIFEST_FILE_ENDING);
			serveStringManifest(req, resp, manifest);
			return;
		}


		// if we got here we just don`t know the device react with 500 -> no
		// manifest...

		throw new ServletException("unkown device: " + userAgent);

	}

	protected String getBaseUrl(HttpServletRequest req) {
		String base = req.getServletPath();
		// cut off module
		return base.substring(0, base.lastIndexOf("/") + 1);
	}


	public Set<String> getFilesForPermutation(String baseUrl, String moduleName, String permutation) throws ServletException {
		String fileName = baseUrl + moduleName + "/" + permutation + PermutationMapLinker.PERMUTATION_FILE_ENDING;
		XMLPermutationProvider xmlPermutationProvider = new XMLPermutationProvider();

		InputStream inputStream = null;
		try {
			File file = new File(getServletContext().getRealPath(fileName));
			inputStream = new FileInputStream(file);
			return xmlPermutationProvider.getPermutationFiles(inputStream);
		} catch (XMLPermutationProviderException e) {
			log("can not read permutation file");
			throw new ServletException("can not read permutation file", e);
		} catch (FileNotFoundException e) {
			log("can not read permutation file");
			throw new ServletException("can not read permutation file", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public String readManifest(String filePath) throws ServletException {
		File manifestFile = new File(getServletContext().getRealPath(filePath));

		StringWriter manifestWriter = new StringWriter();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(manifestFile), "UTF-8"));

			String line = null;

			while ((line = br.readLine()) != null) {

				manifestWriter.append(line + "\n");
			}

			return manifestWriter.toString();
		} catch (FileNotFoundException e) {
			log("could not find manifest file", e);
			throw new ServletException("can not find manifest file", e);
		} catch (IOException e) {
			log("error while reading manifest file", e);
			throw new ServletException("error while reading manifest file", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * @param req
	 * @return
	 * @throws PropertyProviderException
	 */
	public Set<BindingProperty> calculateBindinPropertiesForClient(HttpServletRequest req) throws ServletException {

		try {
			Set<BindingProperty> computedBindings = new HashSet<BindingProperty>();

			Set<Entry<String, PropertyProvider>> set = propertyProviders.entrySet();
			for (Entry<String, PropertyProvider> entry : set) {
				String varValue = entry.getValue().getPropertyValue(req);
				BindingProperty bindingProperty = new BindingProperty(entry.getKey(), varValue);
				computedBindings.add(bindingProperty);
			}
			return computedBindings;
		} catch (PropertyProviderException e) {
			log("cam not calculate properties for client", e);
			throw new ServletException("can not calculate properties for client", e);
		}

	}

	public void serveStringManifest(HttpServletRequest req, HttpServletResponse resp, String manifest) throws ServletException {
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", new Date().getTime());

		resp.setContentType("text/cache-manifest");

		try {
			InputStream is = new ByteArrayInputStream(manifest.getBytes("UTF-8"));
			ServletOutputStream os = resp.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			return;
		} catch (UnsupportedEncodingException e) {
			log("can not write manifest to output stream", e);
			throw new ServletException("can not write manifest to output stream", e);
		} catch (IOException e) {
			log("can not write manifest to output stream", e);
			throw new ServletException("can not write manifest to output stream", e);
		}

	}

	public String getPermutationIdString(String baseUrl, String moduleName, String userAgent) throws ServletException {

		if (moduleName == null) {
			throw new IllegalArgumentException("moduleName can not be null");
		}

		if (userAgent == null) {
			throw new IllegalArgumentException("agent string can not be null");
		}

		String realPath = getServletContext().getRealPath(baseUrl + moduleName + "/" + PermutationMapLinker.MANIFEST_MAP_FILE_NAME);

		FileInputStream fileInputStream = null;
		try {

			fileInputStream = new FileInputStream(realPath);
			Map<String, List<BindingProperty>> map = permutationProvider.getBindingProperties(fileInputStream);
			for (Entry<String, List<BindingProperty>> entry : map.entrySet()) {
				// List<BindingProperty> value = entry.getValue();
				String permutationIdString = entry.getKey();
				
				//loop through values of this permutation, to find whether we need this one
				for (BindingProperty bindingProperty : entry.getValue()) {
					if (!bindingProperty.getName().equals("user.agent")) continue;
					
					String bindingPropertyUa = bindingProperty.getValue();
					if (bindingPropertyUa.contains("gecko")) {
						//firefox and related
						if (userAgent.contains("gecko")) return permutationIdString;
					} else if (bindingPropertyUa.contains("safari")) {
						//all webkit browsers
						if (userAgent.contains("safari")) return permutationIdString;
					} else if (bindingPropertyUa.contains("opera")) {
						//opera
						if (userAgent.contains("opera")) return permutationIdString;
					} else if (bindingPropertyUa.contains("ie6")) {
						//ie6 and 7
						if (userAgent.contains("msie 6") || userAgent.contains("msie 7")) return permutationIdString;
					} else if (bindingPropertyUa.contains("ie8")) {
						//ie8
						if (userAgent.contains("msie 8")) return permutationIdString;
					} else if (bindingPropertyUa.contains("ie9")) {
						//ie9 and 10
						if (userAgent.contains("msie 9") || userAgent.contains("msie 10")) return permutationIdString;
					}
				}
			}
			return null;
		} catch (FileNotFoundException e) {
			log("can not find file: '" + realPath + "'", e);
			throw new ServletException("can not find permutation file", e);
		} catch (XMLPermutationProviderException e) {
			log("can not read xml file", e);
			throw new ServletException("can not read permutation information", e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {

				}
			}
		}

	}

	public String getPermutationStrongName(String baseUrl, String moduleName, Set<BindingProperty> computedBindings) throws ServletException {

		if (moduleName == null) {
			throw new IllegalArgumentException("moduleName can not be null");
		}

		if (computedBindings == null) {
			throw new IllegalArgumentException("computedBindings can not be null");
		}

		String realPath = getServletContext().getRealPath(baseUrl + moduleName + "/" + PermutationMapLinker.MANIFEST_MAP_FILE_NAME);

		FileInputStream fileInputStream = null;
		try {

			fileInputStream = new FileInputStream(realPath);

			Map<String, List<BindingProperty>> map = permutationProvider.getBindingProperties(fileInputStream);
			for (Entry<String, List<BindingProperty>> entry : map.entrySet()) {
				List<BindingProperty> value = entry.getValue();
				if (value.containsAll(computedBindings) && value.size() == computedBindings.size()) {
					return entry.getKey();
				}
			}
			return null;
		} catch (FileNotFoundException e) {
			log("can not find file: '" + realPath + "'", e);
			throw new ServletException("can not find permutation file", e);
		} catch (XMLPermutationProviderException e) {
			log("can not read xml file", e);
			throw new ServletException("can not read permutation information", e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {

				}
			}
		}

	}

	public String getModuleName(HttpServletRequest req) throws ServletException {
		if (req == null) {
			throw new IllegalArgumentException("request can not be null");
		}

		// request url should be something like .../modulename.manifest" within
		// the same folder of your host page...
		Pattern pattern = Pattern.compile("/([a-zA-Z0-9_]+)\\.appcache$");
		Matcher matcher = pattern.matcher(req.getServletPath());
		if (!matcher.find()) {
			log("can not calculate module base from url: '" + req.getServletPath() + "'");
			throw new ServletException("can not calculate module base from url: '" + req.getServletPath() + "'");
		}

		String module = matcher.group(1);
		return module;

	}
}
