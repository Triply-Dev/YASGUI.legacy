/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client.tab.results.input;

import java.util.HashMap;
import java.util.Map;

import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.Prefix;
import com.smartgwt.client.util.StringUtil;


/**
 * Interface for getting sparql or xml sparql result in a common object form
 */
public class ResultsHelper {
	public static String getLiteralFromBinding(HashMap<String, String> binding) {
		String literal = "\"" + StringUtil.asHTML(binding.get("value")) + "\"";
		if (binding.containsKey("xml:lang")) {
			literal += "@" + binding.get("xml:lang");
		} else if (binding.containsKey("datatype")) {
			String dataType = binding.get("datatype");
			if (dataType.contains(ResultContainer.XSD_DATA_PREFIX)) {
				literal += "^^xsd:" + dataType.substring(ResultContainer.XSD_DATA_PREFIX.length());
			} else {
				literal += "^^<" + dataType + ">";
			}
		}
		return literal;
	}
	
	/**
	 * Check for a uri whether there is a prefix defined in the query.
	 * 
	 * @param uri
	 * @return Short version of this uri if prefix is defined. Long version
	 *         otherwise
	 */
	public static String getShortUri(String uri, HashMap<String, Prefix> queryPrefixes) {
		for (Map.Entry<String, Prefix> entry : queryPrefixes.entrySet()) {
			String prefixUri = entry.getKey();
			if (uri.startsWith(prefixUri)) {
				uri = uri.substring(prefixUri.length());
				uri = entry.getValue().getPrefix() + ":" + uri;
				break;
			}
		}
		return uri;
	}
	
	public static String getHtmlLinkForUri(HashMap<String, String> binding, HashMap<String, Prefix> queryPrefixes) {
		String uri = binding.get("value");
		return  "<a href=\"" + uri + "\" target=\"_blank\">" + StringUtil.asHTML(getShortUri(uri, queryPrefixes)) + "</a>";
	}
}
