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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Canvas;
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
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.shared.Bookmark;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BookmarkedQueries extends ImgButton {
	private View view;
	private Window window;
	private static int WINDOW_WIDTH = 700;
	private static int WINDOW_HEIGHT = 500;
	private static int ICON_WIDTH = 25;
	private static int ICON_HEIGHT = 25;
	private HLayout rollOverCanvas;
	private BookmarkRecord rollOverRecord;
	private ListGrid listGrid;
	private boolean somethingChanged = false;
	public BookmarkedQueries(View view) {
		this.view = view;
		setSrc("link.png");

		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setShowDown(false);
		setShowRollOver(false);
		
		addWindowSetup();
		

	}
	private void addWindowSetup() {
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getElements().onLoadingStart("loading bookmarks");
				view.getRemoteService().getBookmarks(new AsyncCallback<Bookmark[]>() {
					public void onFailure(Throwable caught) {
						view.getElements().onError(caught);
					}
		
					public void onSuccess(Bookmark[] bookmarks) {
						view.getElements().onLoadingFinish();
						somethingChanged = false;
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
								if (somethingChanged) {
									storeRecords();
								}
							}
						});
						window.draw();
					}
				});
				
			}
		});
	}
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
					rollOverCanvas.setSnapTo("TR");
					rollOverCanvas.setWidth(50);
					rollOverCanvas.setHeight(22);

					ImgButton delImg = new ImgButton();
					delImg.setShowDown(false);
					delImg.setShowRollOver(false);
					delImg.setLayoutAlign(Alignment.CENTER);
					delImg.setSrc("icons/fugue/cross.png");
					delImg.setPrompt("delete");
					delImg.setHeight(16);
					delImg.setWidth(16);
					delImg.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							listGrid.removeData(rollOverRecord);
							somethingChanged = true;
						}
					});

					ImgButton addImg = new ImgButton();
					addImg.setShowDown(false);
					addImg.setShowRollOver(false);
					addImg.setLayoutAlign(Alignment.CENTER);
					addImg.setSrc("icons/fugue/plus-button.png");
					addImg.setPrompt("select");
					addImg.setHeight(16);
					addImg.setWidth(16);
					addImg.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							String endpoint = rollOverRecord.getEndpoint();
							String query = rollOverRecord.getQuery();
							view.getSelectedTabSettings().setEndpoint(endpoint);
							view.getSelectedTabSettings().setQueryString(query);
							view.getSelectedTab().setEndpoint(endpoint);
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
		ListGridField endpointField = new ListGridField(BookmarkRecord.KEY_ENDPOINT, "Endpoint");
		endpointField.addChangedHandler(new ChangedHandler(){
			@Override
			public void onChanged(ChangedEvent event) {
				somethingChanged = true;
				
			}});
		titleField.addChangedHandler(new ChangedHandler(){
			@Override
			public void onChanged(ChangedEvent event) {
				somethingChanged = true;
				
			}});
		listGrid.setFields(titleField, endpointField);

		listGrid.setCanEdit(true);
		listGrid.setEditEvent(ListGridEditEvent.CLICK);
		listGrid.setEditByCell(true);
		
		listGrid.setData(getRecords(bookmarks));

		return listGrid;
	}

	
	private BookmarkRecord[] getRecords(Bookmark[] bookmarks) {
//		return new BookmarkRecord[] { new BookmarkRecord(1, "title", "endpoin", "SELECT * \n{?x ?Y ?h} \nLIMIT 10"),
//				new BookmarkRecord(2, "title2", "endpoin2", "SELECT * {?x ?Y ?h} LIMIT 10"),
//				new BookmarkRecord(3, "title3", "endpoin3", "SELECT * {?x ?Y ?h} LIMIT 10"), };
		ArrayList<BookmarkRecord> records = new ArrayList<BookmarkRecord>();
		for (Bookmark bookmark: bookmarks) {
			records.add(new BookmarkRecord(bookmark));
		}
		return records.toArray(new BookmarkRecord[records.size()]);
	}
	
	
	/**
	 * Method executed on 'onBlur' of codemirror query area, to make sure our listgrid records contain the updated info 
	 * @param inputId
	 * @param query
	 */
	public void updateQuery(String inputId, String query) {
		ListGridRecord[] records = listGrid.getRecords();
		for (ListGridRecord record:records) {
			BookmarkRecord brecord = (BookmarkRecord)record;
			if (brecord.getInputId().equals(inputId)) {
				brecord.setQuery(query);
				somethingChanged = true;
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

	public String getBookmarkId(String inputId) {
		return inputId.substring(0, inputId.length() - BookmarkRecord.APPEND_INPUT_ID.length());
		
	}
	
	private void storeRecords() {
		view.getElements().onLoadingStart("updating bookmarks");
		ListGridRecord[] records = listGrid.getRecords();
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (ListGridRecord record: records) {
			bookmarks.add(((BookmarkRecord)record).toBookmark());
		}
		
		view.getRemoteService().storeBookmarks(bookmarks.toArray(new Bookmark[bookmarks.size()]), new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				view.getElements().onError(caught);
			}

			public void onSuccess(Void result) {
				view.getElements().onLoadingFinish();
			}
		});
		
		
	}

}
