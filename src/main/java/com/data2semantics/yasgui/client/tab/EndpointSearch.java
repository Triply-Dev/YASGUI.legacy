package com.data2semantics.yasgui.client.tab;

import java.util.ArrayList;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.ZIndexes;
import com.data2semantics.yasgui.shared.Endpoints;
public class EndpointSearch extends ImgButton {
	private static int WINDOW_HEIGHT = 600;
	private static int WINDOW_WIDTH = 1000;
	private static int COL_WIDTH_DATASET_TITLE = 150;
	private static int COL_WIDTH_MORE_INFO = 22;
	private ListGrid searchGrid;
	private View view;
	private Window window;
	public EndpointSearch(View view) {
		this.view = view;
		//Use a custom one. Setting margins on layout fucks up layout (smartgwt bug)
		//So this image has 5px whitespace on right, en 2 px whitespace on bottom
		setSrc("icons/custom/magnifier.png");
		
		setWidth(23);
		setHeight(20);
		setShowDown(false);
		setShowRollOver(false);
		
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window = new Window();
				window.setZIndex(ZIndexes.MODAL_WINDOWS);
				window.setTitle("Search endpoints");
				window.setIsModal(true);
				window.setDismissOnOutsideClick(true);
				window.setWidth(WINDOW_WIDTH);
				window.setHeight(WINDOW_HEIGHT);
				window.setShowMinimizeButton(false);
				window.setAutoCenter(true);
				window.addItem(getListGridSearchTable());
				window.draw();
				
			}

		});
		
	}
	
	/**
	 * get listgrid to search endpoints in
	 * 
	 * @return
	 */
	private ListGrid getListGridSearchTable() {
		searchGrid = new ListGrid();
		searchGrid.setCellFormatter(new CellFormatter(){
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (rowNum == 0 && colNum == 0 && Helper.recordIsEmpty(record)) {
					return "Empty";
				}
				String colName = searchGrid.getFieldName(colNum);
				String cellValue = record.getAttribute(colName);
				
				if (cellValue != null) {
					if (colName.equals(Endpoints.KEY_TITLE) || colName.equals(Endpoints.KEY_DESCRIPTION)) {
						return StringUtil.asHTML(cellValue);
					} else if (colName.equals(Endpoints.KEY_ENDPOINT)) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\">" + cellValue + "</a>";
					} else if (colName.equals(Endpoints.KEY_DATASETURI) && cellValue.length() > 0) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\"><img src=\"images/icons/fugue/information.png\"/ width=\"16\" height=\"16\"></a>";
					}
				}
                return null;
			}});
		searchGrid.addRecordClickHandler(new RecordClickHandler(){
			@Override
			public void onRecordClick(RecordClickEvent event) {
				view.getSelectedTab().getEndpointInput().storeEndpoint(event.getRecord().getAttributeAsString(Endpoints.KEY_ENDPOINT));
				window.destroy();
			}});
		searchGrid.setFixedRecordHeights(false);
		searchGrid.setHeight(WINDOW_HEIGHT);
		searchGrid.setWidth100();
		searchGrid.setFilterButtonPrompt("");
		searchGrid.setAutoFitData(Autofit.VERTICAL);
		searchGrid.setWrapCells(true);
		searchGrid.setShowFilterEditor(true);
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(new ListGridField(Endpoints.KEY_TITLE, "Dataset", COL_WIDTH_DATASET_TITLE));
		fields.add(new ListGridField(Endpoints.KEY_ENDPOINT, "Endpoint"));
		fields.add(new ListGridField(Endpoints.KEY_DESCRIPTION, "Description"));
		ListGridField dataUriField = new ListGridField(Endpoints.KEY_DATASETURI, " ", COL_WIDTH_MORE_INFO);
		dataUriField.setFilterEditorType(new SpacerItem());//don't want to be able to filter on datauri
		fields.add(dataUriField);
		searchGrid.setFields(fields.toArray(new ListGridField[fields.size()]));
		searchGrid.setFilterOnKeypress(true);
		searchGrid.setDataSource(view.getEndpointDataSource());
		searchGrid.setWrapCells(true);
		searchGrid.setCanResizeFields(true);
		searchGrid.fetchData();//We are using a client-only datasource. need to manually fetch to fill grid
		return searchGrid;
	}
}
