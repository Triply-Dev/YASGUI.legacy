package com.data2semantics.yasgui.client.tab.optionbar.bookmarks;

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

import java.util.ArrayList;
import java.util.HashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.ChangedEvent;
import com.smartgwt.client.widgets.grid.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.events.RecordCollapseEvent;
import com.smartgwt.client.widgets.grid.events.RecordCollapseHandler;
import com.smartgwt.client.widgets.grid.events.RecordExpandEvent;
import com.smartgwt.client.widgets.grid.events.RecordExpandHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BookmarkedQueries extends Img {
	private View view;
	private Window window;
	private static int WINDOW_WIDTH = 700;
	private static int WINDOW_HEIGHT = 500;
	private static int ICON_WIDTH = 33;
	private static int ICON_HEIGHT = 27;
	private HLayout rollOverCanvas;
	private boolean enabled = false;
	private BookmarkRecord rollOverRecord;
	private ListGrid listGrid;
	private HashMap<Integer,BookmarkRecord> updatedRecords;//hashmap, so we have a unique set of records
	private ArrayList<BookmarkRecord> deletedRecords = new ArrayList<BookmarkRecord>();
	public BookmarkedQueries(View view) {
		this.view = view;
		setEnabled(view.getOpenId() != null && view.getOpenId().isLoggedIn());
		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setShowDown(false);
		setShowRollOver(false);
		addHandlers();
	}
	
	/**
	 * set bookmarking functionality enabled/disabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			setSrc(Imgs.get(Imgs.SHOW_BOOKMARKS));
			setTooltip("show bookmarks");
			setCursor(Cursor.POINTER);
		} else {
			setSrc(Imgs.getDisabled(Imgs.SHOW_BOOKMARKS));
			setTooltip("log in to use your bookmarks");
			setCursor(Cursor.DEFAULT);
		}
		
	}
	
	/**
	 * attach bookmark icon handlers
	 */
	private void addHandlers() {
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (enabled) {
					rollOverCanvas = null;
					view.getElements().onLoadingStart("loading bookmarks");
					view.getRemoteService().getBookmarks(new AsyncCallback<Bookmark[]>() {
						public void onFailure(Throwable caught) {
							setSrc(Imgs.get(Imgs.BOOKMARK_QUERY));
							if (caught instanceof OpenIdException) {
								view.getElements().onError(caught.getMessage() + ". Logging out");
								view.getOpenId().logOut();
							} else {
								view.getElements().onError(caught);
							}
						}
			
						public void onSuccess(Bookmark[] bookmarks) {
							updatedRecords = new HashMap<Integer, BookmarkRecord>();
							deletedRecords = new ArrayList<BookmarkRecord>();
							view.getElements().onLoadingFinish();
							window = new Window();
							window.setZIndex(ZIndexes.MODAL_WINDOWS);
							window.setTitle("Bookmarked queries");
							window.setIsModal(true);
							window.setDismissOnOutsideClick(true);
							window.setAutoCenter(true);
							window.setWidth(WINDOW_WIDTH);
							window.setHeight(WINDOW_HEIGHT);
							window.setShowMinimizeButton(false);
							window.addItem(getWindowContent(bookmarks));
							window.addVisibilityChangedHandler(new VisibilityChangedHandler() {
								@Override
								public void onVisibilityChanged(VisibilityChangedEvent event) {
									storeCurrentTextArea();//to make sure currently edited query bookmark is stored as well
									if (updatedRecords.size() > 0) {
										updateBookmarks(new ArrayList<BookmarkRecord>(updatedRecords.values()));
									}
									if (deletedRecords.size() > 0) {
										deleteRecords(deletedRecords);
									}
									resetVariables();
								}
							});
							window.draw();
						}
					});
				}
			}
		});
	}
	
	/**
	 * reset variables stored in object, and delete initiated js variables (such as codemirror objects)
	 */
	private void resetVariables() {
		JsMethods.deleteElementsWithPostfixId(BookmarkRecord.APPEND_INPUT_ID);
		updatedRecords = new HashMap<Integer, BookmarkRecord>();
		deletedRecords = new ArrayList<BookmarkRecord>();
	}
	
	/**
	 * get listgrid to show in window
	 * @param bookmarks
	 * @return
	 */
	private ListGrid getWindowContent(Bookmark[] bookmarks) {
		
		listGrid = new ListGrid() {
			@Override
			protected Canvas getExpansionComponent(ListGridRecord record) {
				Canvas canvas = super.getExpansionComponent(record);
				canvas.setMargin(5);
				return canvas;
			}

			protected Canvas getRollOverCanvas(final Integer rowNum, Integer colNum) {
				rollOverRecord = (BookmarkRecord)(this.getRecord(rowNum));

				if (rollOverCanvas == null) {
					rollOverCanvas = new HLayout(3);
					rollOverCanvas.setSnapTo("CR");
					rollOverCanvas.setWidth(50);
					rollOverCanvas.setHeight(22);

					ImgButton delImg = new ImgButton();
					delImg.setShowDown(false);
					delImg.setShowRollOver(false);
					delImg.setLayoutAlign(Alignment.CENTER);
					delImg.setSrc(Imgs.get(Imgs.CROSS));
					delImg.setPrompt("delete");
					delImg.setHeight(16);
					delImg.setWidth(16);
					delImg.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							listGrid.removeData(rollOverRecord);
							deletedRecords.add(rollOverRecord);
						}
					});

					ImgButton addImg = new ImgButton();
					addImg.setShowDown(false);
					addImg.setShowRollOver(false);
					addImg.setLayoutAlign(Alignment.CENTER);
					addImg.setSrc(Imgs.get(Imgs.ADD_TAB));
					addImg.setPrompt("select");
					addImg.setHeight(16);
					addImg.setWidth(16);
					addImg.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							String endpoint = rollOverRecord.getEndpoint();
							String query = rollOverRecord.getQuery();
							if (endpoint != null && endpoint.length() > 0) {
								view.getSelectedTabSettings().setEndpoint(endpoint);
								view.getSelectedTab().setEndpoint(endpoint);
							}
							view.getSelectedTabSettings().setQueryString(query);
							view.getSelectedTab().setQueryString(query);
							LocalStorageHelper.storeSettingsInCookie(view.getSettings());
							window.destroy();
						}
					});

					rollOverCanvas.addMember(delImg);
					rollOverCanvas.addMember(addImg);
				}
				return rollOverCanvas;

			}
		};
		

		listGrid.setShowRollOverCanvas(true);

		listGrid.setWidth100();
		listGrid.setHeight100();
		listGrid.setCanExpandRecords(true);
		listGrid.setExpansionMode(ExpansionMode.DETAIL_FIELD);
		listGrid.setDetailField(BookmarkRecord.KEY_QUERY_TEXTAREA);
		listGrid.setCanExpandMultipleRecords(false);
		listGrid.addRecordExpandHandler(new RecordExpandHandler(){

			@Override
			public void onRecordExpand(RecordExpandEvent event) {
				final BookmarkRecord record = (BookmarkRecord)(event.getRecord());
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.attachCodeMirrorToBookmarkedQuery(record.getInputId());
						listGrid.getCurrentExpansionComponent(record).setBackgroundColor("white");
					}
				});
				
			}});
		listGrid.addRecordCollapseHandler(new RecordCollapseHandler(){

			@Override
			public void onRecordCollapse(RecordCollapseEvent event) {
				final BookmarkRecord record = (BookmarkRecord)(event.getRecord());
				JsMethods.resetHeightSetting(record.getInputId());
				
			}});
		ListGridField titleField = new ListGridField(BookmarkRecord.KEY_TITLE, "Title");
		titleField.addChangedHandler(new ChangedHandler(){
			@Override
			public void onChanged(ChangedEvent event) {
				BookmarkRecord record = (BookmarkRecord)(listGrid.getRecord(event.getRowNum()));
				updatedRecords.put(record.getBookmarkId(), record);
			}});
		
		if (!view.getSettings().inSingleEndpointMode()) {
			ListGridField endpointField = new ListGridField(BookmarkRecord.KEY_ENDPOINT, "Endpoint");
			endpointField.addChangedHandler(new ChangedHandler(){
				@Override
				public void onChanged(ChangedEvent event) {
					BookmarkRecord record = (BookmarkRecord)(listGrid.getRecord(event.getRowNum()));
					updatedRecords.put(record.getBookmarkId(), record);
				}});
			listGrid.setFields(titleField, endpointField);
		} else {
			listGrid.setFields(titleField);
		}
		listGrid.setCanEdit(true);
		listGrid.setEditEvent(ListGridEditEvent.CLICK);
		listGrid.setEditByCell(true);
		
		listGrid.setData(getRecords(bookmarks));

		return listGrid;
	}

	/**
	 * Get bookmark objects as record objects
	 * 
	 * @param bookmarks
	 * @return
	 */
	private BookmarkRecord[] getRecords(Bookmark[] bookmarks) {
		ArrayList<BookmarkRecord> records = new ArrayList<BookmarkRecord>();
		for (Bookmark bookmark: bookmarks) {
			records.add(new BookmarkRecord(bookmark));
		}
		return records.toArray(new BookmarkRecord[records.size()]);
	}
	

	/**
	 * Method executed on 'onBlur' of codemirror query area, to make sure our listgrid records contain the updated info 
	 */
	public void storeCurrentTextArea() {
		ListGridRecord[] records = listGrid.getRecords();
		for (ListGridRecord record:records) {
			BookmarkRecord brecord = (BookmarkRecord)record;
			if (listGrid.getCurrentExpansionComponent(brecord) != null) {
				JsMethods.saveCodeMirror(brecord.getInputId());
				String query = JsMethods.getValueUsingId(brecord.getInputId());
				brecord.setQuery(query);
				updatedRecords.put(brecord.getBookmarkId(), brecord);
			}
		}
		
	}

	/**
	 * change the height of the container (containing the codemirror text area)
	 * @param height
	 */
	public void adjustQueryInputForContent(int height) {
		ListGridRecord[] records = listGrid.getRecords();
		for (ListGridRecord record: records) {
			if (listGrid.isExpanded(record)) {
				listGrid.getCurrentExpansionComponent(record).setHeight(height);
			}
		}
	}
	
	
	/**
	 * get bookmark id for a given input string (used for codemirror js object)
	 * @param inputId
	 * @return
	 */
	public String getBookmarkId(String inputId) {
		return inputId.substring(0, inputId.length() - BookmarkRecord.APPEND_INPUT_ID.length());
		
	}
	
	/**
	 * Update the bookmarks on remote server
	 * @param records
	 */
	private void updateBookmarks(ArrayList<BookmarkRecord> records) {
		//Filter for items which are updated -and- deleted (in that case, just delete them)
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (BookmarkRecord record: records) {
			if (deletedRecords.contains(record) == false) {
				bookmarks.add(record.toBookmark());
			}
		}
		if (bookmarks.size() > 0) {
			view.getRemoteService().updateBookmarks(bookmarks.toArray(new Bookmark[bookmarks.size()]), new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					view.getElements().onError(caught);
				}
				public void onSuccess(Void result) {
				}
			});
		}
		
		
	}
	
	/**
	 * Delete records on remote service
	 * 
	 * @param records
	 */
	private void deleteRecords(ArrayList<BookmarkRecord> records) {
		if (records.size() > 0) {
			int[] bookmarkIds = new int[records.size()];
			for (int i = 0; i < records.size(); i++) {
				bookmarkIds[i] = records.get(i).getBookmarkId();
			}
		
			view.getRemoteService().deleteBookmarks(bookmarkIds, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					view.getElements().onError(caught);
				}
	
				public void onSuccess(Void result) {
				}
			});
		}
	}

}
