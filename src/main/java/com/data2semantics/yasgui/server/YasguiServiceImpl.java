package com.data2semantics.yasgui.server;

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

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.client.services.YasguiService;
import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.fetchers.ConfigFetcher;
import com.data2semantics.yasgui.server.fetchers.PrefixesFetcher;
import com.data2semantics.yasgui.server.fetchers.endpoints.EndpointsFetcher;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.IssueReport;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.exceptions.FetchException;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.rosaloves.bitlyj.Url;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {
	
	public static String CACHE_DIR = "/cache";
	private static Logger LOGGER = Logger.getLogger(YasguiServiceImpl.class.getName());
	
	

	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, FetchException {
		String prefixes = "";
		try {
			prefixes = PrefixesFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR)));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "exception", e);
			throw new FetchException(PrefixesFetcher.PREFIX_FETCH_EXCEPTION_MSG, e);
		}
		return prefixes;
	}

	public String getShortUrl(String longUrlString) throws IllegalArgumentException, FetchException {
		String shortUrl;
		try {
			ServletContext servletContext = this.getServletContext();
			JSONObject config = ConfigFetcher.getJsonObjectFromPath(servletContext.getRealPath("/"));
			String bitlyApiKey = config.getString(SettingKeys.BITLY_API_KEY);
			String bitlyUsername = config.getString(SettingKeys.BITLY_USERNAME);
			LOGGER.info("issuing bitly call for url " + longUrlString);
			Url url = as(bitlyUsername, bitlyApiKey).call(shorten(longUrlString));
			LOGGER.info("bitly call done");
			shortUrl = url.getShortUrl();
			LOGGER.info("retrieved short url: " + shortUrl);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "exception", e);
			throw new FetchException("Unable to create short url", e);
		}
		return shortUrl;
	}

	public String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, FetchException {
		String endpoints = "";
		try {
			endpoints = EndpointsFetcher.fetch(forceUpdate, new File(getServletContext().getRealPath(CACHE_DIR)));
		} catch (Exception e) {
			throw new FetchException("unable to fetch endpoints", e);
		}
		return endpoints;
	}

	@Override
	public void addBookmark(Bookmark bookmark) throws IllegalArgumentException, FetchException, OpenIdException {
		DbHelper db = null;
		try {
			db = new DbHelper(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
			db.addBookmarks(bookmark);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} finally {
			if (db != null)
				db.close();
		}
	}

	public Bookmark[] getBookmarks() throws IllegalArgumentException, FetchException, OpenIdException {
		DbHelper db = null;
		Bookmark[] bookmarks = null;
		try {
			db = new DbHelper(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
			bookmarks = db.getBookmarks();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} finally {
			if (db != null)
				db.close();
		}
		return bookmarks;
	}

	@Override
	public void updateBookmarks(Bookmark[] bookmarks) throws IllegalArgumentException, FetchException, OpenIdException {
		DbHelper db = null;

		try {
			db = new DbHelper(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
			db.updateBookmarks(bookmarks);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} finally {
			if (db != null)
				db.close();
		}
	}

	@Override
	public void deleteBookmarks(int[] bookmarkIds) throws IllegalArgumentException, FetchException, OpenIdException {
		DbHelper db = null;
		try {
			db = new DbHelper(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
			db.clearBookmarks(bookmarkIds);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} finally {
			if (db != null)
				db.close();
		}
	}


	@Override
	public boolean isOnline() throws IllegalArgumentException {
		return true;
	}

	@Override
	public void logException(Throwable t) {
		if (System.getProperty("catalina.base") != null) {
			try {
				Handler handler = new FileHandler(System.getProperty("catalina.base") + "/logs/yasgui.err", true);
				handler.setFormatter(new SimpleFormatter());
				LOGGER.addHandler(handler);
				LOGGER.log(Level.SEVERE, t.getMessage(), t);
				handler.close();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Unable to log exception to server", t);
			}
		} else {
			LOGGER.log(Level.SEVERE, "Unable to log exception to server", t);
		}
	}

	@Override
	public String reportIssue(IssueReport issueReport) throws IllegalArgumentException {
		try {
			String url = IssueReporter.reportIssue(new File(getServletContext().getRealPath("/")), issueReport);
			return "Your issue is reported. To keep track of any progress, visit <a href='" + url + "' target='_blank'>this GitHub page</a>";
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to report issue", e);
			throw new IllegalArgumentException("Unsuccesfull in reporting this issue. Please report manually at http://github.com/LaurensRietveld/issues");
		}
	}

	@Override
	public void logLazyQuery(String query, String endpoint) throws IllegalArgumentException {
		try {
			
			QueryPropertyExtractor.store(new DbHelper(new File(getServletContext().getRealPath("/"))), query, endpoint);
		} catch (Exception e) {
			//fail silently. doesnt matter when analysis of a single query fails..
		}
		
	}

	@Override
	public AutocompletionsInfo getAutocompletionsInfo() throws IllegalArgumentException, FetchException {
		try {
			DbHelper db = new DbHelper(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
			return db.getAutocompletionInfo();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage());
		}
	}

}
