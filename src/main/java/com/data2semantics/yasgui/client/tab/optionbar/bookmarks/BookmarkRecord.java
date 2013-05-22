package com.data2semantics.yasgui.client.tab.optionbar.bookmarks;

import com.data2semantics.yasgui.shared.Bookmark;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class BookmarkRecord extends ListGridRecord {
	public static String KEY_ENDPOINT = "endpoint";
	public static String KEY_TITLE = "title";
	public static String KEY_QUERY = "query";
	public static String KEY_QUERY_TEXTAREA = "queryTextArea";
	public static String KEY_ID = "queryId";
	public static String APPEND_INPUT_ID = "_bookmarkQuery";
	
    public BookmarkRecord() {
    }
    public BookmarkRecord(Bookmark bookmark) {
       	setBookmarkId(bookmark.getBookmarkId());
        setTitle(bookmark.getTitle());
        setEndpoint(bookmark.getEndpoint());
        setQuery(bookmark.getQuery());
    }
    public BookmarkRecord(int bookmarkId, String title, String endpoint, String query) {
    	setBookmarkId(bookmarkId);
        setTitle(title);
        setEndpoint(endpoint);
        setQuery(query);
    }
    public void setEndpoint(String endpoint) {
        setAttribute(KEY_ENDPOINT, endpoint);
    }

    public String getEndpoint() {
        return getAttributeAsString(KEY_ENDPOINT);
    }

    public void setTitle(String title) {
        setAttribute(KEY_TITLE, title);
    }

    public String getTitle() {
        return getAttributeAsString(KEY_TITLE);
    }


    public void setQuery(String query) {
        setAttribute(KEY_QUERY, query);
        setQueryAsTextArea(query);
    }
    
    private void setQueryAsTextArea(String query) {
    	setAttribute(KEY_QUERY_TEXTAREA, getQueryAsTextArea(query));
    }

    public String getQuery() {
        return getAttributeAsString(KEY_QUERY);
    }
    public void setBookmarkId(int bookmarkId) {
    	setAttribute(KEY_ID, bookmarkId);
    }
    
    public int getBookmarkId() {
    	String idString = getAttributeAsString(KEY_ID);
    	int id = -1;
    	if (idString == null || idString.length() == 0) {
    		id = Integer.parseInt(idString);
    	}
    	return id;
    }
    
    public String getQueryAsTextArea(String query) {
    	return "<textarea " + "id=\"" + getInputId() + "\"" + ">" + query + "</textarea>";
    }
    
    public String getInputId() {
    	return getBookmarkId() + APPEND_INPUT_ID;
    }
    public Bookmark toBookmark() {
    	Bookmark bookmark = new Bookmark();
    	bookmark.setQuery(getQuery());
    	bookmark.setEndpoint(getEndpoint());
    	bookmark.setTitle(getTitle());
    	bookmark.setBookmarkId(getBookmarkId());
    	return bookmark;
    }
    
}
