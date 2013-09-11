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

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Writing a manifest file from a given set of resources
 * 
 * @author Daniel Kurka
 * 
 */
public class ManifestWriter {
	/**
	 * Write a manifest file for the given set of artifacts and return it as a
	 * string
	 * 
	 * @param staticResources - the static resources of the app, such as
	 *            index.html file
	 * @param cacheResources the gwt output artifacts like cache.html files
	 * @return the manifest as a string
	 */
	public String writeManifest(Set<String> staticResources, Set<String> cacheResources, Map<String, String> fallbacks) {
		if (staticResources == null)
			throw new IllegalArgumentException("staticResources can not be null");
		if (cacheResources == null)
			throw new IllegalArgumentException("cacheResources can not be null");

		StringBuilder sb = new StringBuilder();
		sb.append("CACHE MANIFEST\n");
		//build something unique so that the manifest file changes on recompile
		sb.append("# Unique id #" + (new Date()).getTime() + "." + Math.random() + "\n");
		sb.append("\n");
		sb.append("CACHE:\n");
		sb.append("# Static app files\n");
		for (String resources : staticResources) {
			sb.append(resources + "\n");
		}

		sb.append("\n# GWT compiled files\n");
		for (String resources : cacheResources) {
			sb.append(resources + "\n");
		}
		
		if (fallbacks != null && fallbacks.size() > 0) {
			sb.append("\n\n");
			sb.append("FALLBACK:\n");
			for (Entry<String, String> entry: fallbacks.entrySet()) {
				sb.append(entry.getKey() + " " + entry.getValue());
			}
		}

		sb.append("\n\n");
		sb.append("# All other resources require the client to be online.\n");
		sb.append("NETWORK:\n");
		sb.append("*\n");
		return sb.toString();
	}
}
