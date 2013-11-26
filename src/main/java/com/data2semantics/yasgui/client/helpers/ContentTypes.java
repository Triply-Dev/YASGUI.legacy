package com.data2semantics.yasgui.client.helpers;

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

import java.util.EnumSet;

public class ContentTypes {
	public enum Type {
		SELECT_JSON("application/sparql-results+json", "json", ".json"),
		SELECT_XML("application/sparql-results+xml", "xml", ".xml"),
		SELECT_CSV("text/csv", null, ".xml"),
		SELECT_TSV("text/tab-separated-values", null, ".tsv"),
		CONSTRUCT_TURTLE("text/turtle", "text/turtle", ".ttl"),
		CONSTRUCT_XML("application/rdf+xml", "xml", ".xml"),
		CONSTRUCT_CSV("text/csv", null, ".csv"),
		CONSTRUCT_TSV("text/tab-separated-values", null, ".tsv");
		
		private String contentType;
		private String cmMode;
		private String extension;
	
		private Type(String contentType, String cmMode, String extension) {
			this.contentType = contentType;
			this.cmMode = cmMode;
			this.extension = extension;
		}
		public String getContentType() {
			return this.contentType;
		}
		public String getCmMode() {
			return this.cmMode;
		}
		public String getFileExtension() {
			return extension;
		}
		public boolean matchesContentType(String contentTypeString) {
			boolean matches = false;
			String[] splittedContentType = contentType.split("/");
			if (splittedContentType.length > 1) {
				String strToMatch = splittedContentType[1];
				if (contentTypeString.contains(strToMatch)) {
					matches = true;
				}
			}
			return matches;
		}
	}
	
	public static Type detectContentType(String contentTypeString) {
		Type contentType = null;
		
		for (Type type : EnumSet.allOf(Type.class)) {
			if (type.matchesContentType(contentTypeString)) {
				contentType = type;
				break;
			}
		}
//		} else if (contentType.contains("sparql-results+json")) {
//			resultFormat = ResultContainer.CONTENT_TYPE_SELECT_JSON;
//		} else if (contentType.contains("sparql-results+xml")) {
//			resultFormat = ResultContainer.CONTENT_TYPE_XML;
//		} else if (contentType.contains(QueryConfigMenu.CONTENT_TYPE_SELECT_CSV)) {
//			resultFormat = ResultContainer.CONTENT_TYPE_CSV;
//		} else if (contentType.contains(QueryConfigMenu.CONTENT_TYPE_SELECT_TSV)) {
//			resultFormat = ResultContainer.CONTENT_TYPE_TSV;
		return contentType;
	}
}
