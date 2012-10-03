package com.data2semantics.yasgui.client.tab;

import java.util.ArrayList;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.shared.Endpoints;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.TextMatchStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;

public class EndpointInput extends DynamicForm {
	private View view;
	private ComboBoxItem endpoint;
	private String latestEndpointValue; //used to detect when to check for cors enabled. Not just on blur, but only on blur and when value has changed
	private QueryTab queryTab;
	private ListGrid pickListProperties;
	private static int COL_WIDTH_DATASET_TITLE = 150;
	private static int COL_WIDTH_MORE_INFO = 22;
	private boolean pickListRecordSelected = false;
	private ArrayList<String> cols = new ArrayList<String>();
	
	public EndpointInput(View view, QueryTab queryTab) {
		this.queryTab = queryTab;
		this.view = view;
		setTitleOrientation(TitleOrientation.TOP);
		createTextInput();
		
	}
	public EndpointInput() {
		setTitleOrientation(TitleOrientation.TOP);
		createTextInput();
		//Init value
		latestEndpointValue = getEndpoint();
		
	}
	
	private void createTextInput() {
		endpoint = new ComboBoxItem("endpoint", "Endpoint");
		endpoint.setTextMatchStyle(TextMatchStyle.SUBSTRING);
		endpoint.setValueField(Endpoints.KEY_ENDPOINT);
		endpoint.setAddUnknownValues(true);
		endpoint.setCompleteOnTab(true);
		endpoint.setWidth(420);
		endpoint.setOptionDataSource(view.getEndpointDataSource());
		endpoint.setHideEmptyPickList(true);
		endpoint.setDefaultValue(getQueryTab().getTabSettings().getEndpoint());
		//For this default value, also retrieve CORS setting
		JsMethods.checkCorsEnabled(view.getSelectedTabSettings().getEndpoint());

		initPickList();
		
        endpoint.setPickListProperties(pickListProperties);
        
        setFields(endpoint);
		endpoint.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				latestEndpointValue = getEndpoint();
			}
		});
		endpoint.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				String endpoint = getEndpoint();
				if (!latestEndpointValue.equals(endpoint)) {
					storeEndpoint(endpoint);
				}
			}
		});
		endpoint.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				if (pickListRecordSelected == true) {
					//only perform when item from listgrid was selected. Otherwise on every keypress we'll do lots of processing
					pickListRecordSelected = false;
					storeEndpoint(getEndpoint());
				}
			}});
	}
	
	private void setPickListFieldsForComboBox(ArrayList<ListGridField> fields) {
		//Also store index and field key in arraylist. 
		//Need this because somehow using the listgrid 'getField' method in the setcellformatter causes stack overflow (maybe due to different initialization of listgrid)
		for (ListGridField field: fields) {
			cols.add(field.getName());
		}
		endpoint.setPickListFields(fields.toArray(new ListGridField[fields.size()]));
	}
	
	public String getEndpoint() {
		String endpointString = endpoint.getValueAsString();
		if (endpointString == null) {
			endpointString = "";
		}
		return endpointString;
	}
	
	public void storeEndpoint(String endpoint) {
		JsMethods.checkCorsEnabled(endpoint);
		view.getSettings().getSelectedTabSettings().setEndpoint(endpoint);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	public void setEndpoint(String endpointString) {
		endpoint.setValue(endpointString);
		view.getSelectedTabSettings().setEndpoint(endpointString);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	private QueryTab getQueryTab() {
		return this.queryTab;
	}
	
	private void initPickList() {
		pickListProperties = new ListGrid();
		pickListProperties.setAutoFitData(Autofit.VERTICAL);
		pickListProperties.setHoverWidth(300);
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		ListGridField datasetTitle = new ListGridField(Endpoints.KEY_TITLE, "Dataset", COL_WIDTH_DATASET_TITLE);
		
		fields.add(datasetTitle);
		fields.add(new ListGridField(Endpoints.KEY_ENDPOINT, "Endpoint"));
		fields.add(new ListGridField(Endpoints.KEY_DATASETURI, " ", COL_WIDTH_MORE_INFO));
		
		setPickListFieldsForComboBox(fields);
		pickListProperties.setShowHeaderContextMenu(false);
		pickListProperties.setFixedRecordHeights(false);
		pickListProperties.setWrapCells(true);
		pickListProperties.setAutoFetchTextMatchStyle(TextMatchStyle.SUBSTRING);
        pickListProperties.setCanHover(true);  
        pickListProperties.setShowHover(true);
        pickListProperties.addSelectionUpdatedHandler(new SelectionUpdatedHandler(){

			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				//Thing is: the current value of the combobox is still the old one.
				//The new value (selected record) is not retrievable (getting it from 'selectedRecord' explodes our stack size... (bug?)
				//And we don't want to use the onchanged handler of the combobox itself (because then on every keypress we check for cors stuff)
				//Therefor, set this flag. The onchanged handler of the combobox can then check and reset this flag and store the endpoint
				pickListRecordSelected = true;
			}});
        pickListProperties.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (rowNum == 0 && colNum == 0 && Helper.recordIsEmpty(record)) {
					return "Empty";
				}
				String colName = cols.get(colNum);
				String cellValue = record.getAttribute(colName);
				
				if (cellValue != null) {
					if (colName.equals(Endpoints.KEY_TITLE)) {
//						return "<div style=\"width: " + COL_WIDTH_DATASET_TITLE + "\">" + cellValue + "</div>";
						return cellValue;
					} else if (colName.equals(Endpoints.KEY_ENDPOINT)) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\">" + cellValue + "</a>";
					} else if (colName.equals(Endpoints.KEY_DATASETURI) && cellValue.length() > 0) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\"><img src=\"images/icons/fugue/information.png\"/ width=\"16\" height=\"16\"></a>";
					}
				}
                return null;
            }  
        });  
        pickListProperties.setHoverCustomizer(new HoverCustomizer() {  
            @Override  
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {  
                return StringUtil.asHTML(record.getAttribute(Endpoints.KEY_DESCRIPTION)); 
            }  
        });  
	}
	
	
}
