package com.data2semantics.yasgui.client.configmenu;

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

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionConfigCols;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.data2semantics.yasgui.shared.autocompletions.Util;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

public class AutocompletionConfigTable extends ListGrid {

	private View view;
	private ListGridRecord rollOverRecord;
	private Canvas emptyRollOverCanvas;
	private HLayout rollOverCanvas;
	private HandlerRegistration rollOverCanvasClickHandler;

	public AutocompletionConfigTable(View view) {
		this.view = view;
		setFixedRecordHeights(false);
		setHeight100();
		setWidth100();
		setAutoFitData(Autofit.VERTICAL);
		setWrapCells(true);
		setCanResizeFields(true);
		setUseCellRollOvers(true);
		createAndSetFields();
		setGroupStartOpen(GroupStartOpen.ALL);
		setGroupByField(AutocompletionConfigCols.ENDPOINT.getKey());
		setShowRollOverCanvas(true);
		initDataSource();
	}

	private void initDataSource() {
		RestDataSource dataSource = new RestDataSource();
		dataSource.setDataFormat(DSDataFormat.JSON);
		dataSource.setDataURL("Yasgui/autocompletionsInfo");
		setDataSource(dataSource);
		setAutoFetchData(true);
	}

	private ListGridField getFieldFromCol(AutocompletionConfigCols col) {
		ListGridField field;
		if (col.getWidth() >= 0) {
			field = new ListGridField(col.getKey(), col.getLabel(), col.getWidth());
		} else {
			field = new ListGridField(col.getKey(), col.getLabel());
		}
		if (col.useWrap()) {
			field.setAttribute("wrap", true);
			setHeaderHeight(30);
		}
		return field;
	}

	private void createAndSetFields() {
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		ListGridField endpointField = new ListGridField(AutocompletionConfigCols.ENDPOINT.getKey());
		endpointField.setHidden(true);
		fields.add(endpointField);
//		fields.add(getFieldFromCol(AutocompletionConfigCols.ENDPOINT));
		fields.add(getFieldFromCol(AutocompletionConfigCols.TYPE));
		fields.add(getFieldFromCol(AutocompletionConfigCols.METHOD_QUERY));
		fields.add(getFieldFromCol(AutocompletionConfigCols.METHOD_QUERY_RESULTS));
		setFields(fields.toArray(new ListGridField[fields.size()]));
	}

	protected Canvas getRollOverCanvas(Integer rowNum, final Integer colNum) {
		rollOverRecord = this.getRecord(rowNum);
		String hoverFieldName = getFieldName(colNum);
		String hoverMethod = null;//method cell being hovered over
		if (hoverFieldName.equals(AutocompletionConfigCols.METHOD_QUERY.getKey())) {
			hoverMethod = hoverFieldName;
		} else if (hoverFieldName.equals(AutocompletionConfigCols.METHOD_QUERY_RESULTS.getKey())) {
			hoverMethod = hoverFieldName;
		}
		final String value = rollOverRecord.getAttribute(hoverFieldName);
		if (hoverMethod != null && value != null && Integer.parseInt(value) > 0) {
			JsMethods.logConsole("hovermethod not null");
			if (rollOverCanvas == null) {
				JsMethods.logConsole("rollover canvas null");
//				String endpoint = rollOverRecord.getAttribute(AutocompletionConfigCols.ENDPOINT.getKey());
//				String type = rollOverRecord.getAttribute(AutocompletionConfigCols.TYPE.getKey());
				
				rollOverCanvas = new HLayout();
				rollOverCanvas.setWidth(22);
				rollOverCanvas.setHeight100();
				rollOverCanvas.setSnapTo("TR");
				rollOverCanvas.setAlign(VerticalAlignment.CENTER);
				
				ImgButton delImg = new ImgButton();
				delImg.setShowDown(false);
				delImg.setShowRollOver(false);
				delImg.setLayoutAlign(Alignment.CENTER);
				delImg.setSrc(Imgs.CROSS.get());
				delImg.setPrompt("Remove completions");
				delImg.setHeight(16);
				delImg.setWidth(16);
				rollOverCanvas.addMember(delImg);
			}
			//now, rollOverCanvas always exists, and always has 1 member. Get the member and change the onclick handler.
			//This way we can use the same canvas for every cell, and still change the click handler
			if (rollOverCanvasClickHandler != null) rollOverCanvasClickHandler.removeHandler();
			rollOverCanvasClickHandler = rollOverCanvas.getMembers()[0].addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					final String endpoint = rollOverRecord.getAttribute(AutocompletionConfigCols.ENDPOINT.getKey());
					final FetchType type = Util.stringToFetchType(rollOverRecord.getAttribute(AutocompletionConfigCols.TYPE.getKey()));
					final FetchMethod method = Util.stringToFetchMethod(getFieldName(colNum));
					new GwtCallbackWrapper<Void>(view) {
						public void onCall(AsyncCallback<Void> callback) {
							view.getRemoteService().clearPrivateCompletions(type, method, endpoint, callback);
						}

						protected void onFailure(Throwable throwable) {
							view.getErrorHelper().onError(throwable);
						}

						protected void onSuccess(Void someResult) {
							//redraw grid
							invalidateCache();
						}

					}.call();
				}
			});
			return rollOverCanvas;
		} else {
			JsMethods.logConsole("empty canvas");
			if (rollOverCanvas != null) {
				JsMethods.logConsole("hiding rollover canvas");
				rollOverCanvas.destroy();
				rollOverCanvas = null;
			}
			return getEmptyCanvas();
		}
	}

	private Canvas getEmptyCanvas() {
		if (emptyRollOverCanvas == null) {
			emptyRollOverCanvas = new Canvas();
			emptyRollOverCanvas.setWidth(1);
			emptyRollOverCanvas.setHeight(1);
		}
		return emptyRollOverCanvas;
	}
}
