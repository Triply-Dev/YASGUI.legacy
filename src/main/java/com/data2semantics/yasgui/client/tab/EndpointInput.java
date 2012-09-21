package com.data2semantics.yasgui.client.tab;

import java.util.ArrayList;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.shared.Endpoints;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.TextMatchStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class EndpointInput extends DynamicForm {
	private View view;
	private ComboBoxItem endpoint;
	private String latestEndpointValue; //used to detect when to check for cors enabled. Not just on blur, but only on blur and when value has changed
	private QueryTab queryTab;
	private ListGrid pickListProperties;
	private static int COL_WIDTH_DATASET_TITLE = 150;
	private static int COL_WIDTH_MORE_INFO = 22;
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
		endpoint.setOptionDataSource(getView().getEndpointDataSource());
		endpoint.setHideEmptyPickList(true);
		endpoint.setDefaultValue(getQueryTab().getTabSettings().getEndpoint());
		//For this default value, also retrieve CORS setting
		JsMethods.checkCorsEnabled(getView().getSelectedTabSettings().getEndpoint());

		
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
		
		
		
//        pickListProperties.setCellHeight(40);  
        pickListProperties.setCanHover(true);  
        pickListProperties.setShowHover(true);
        pickListProperties.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (rowNum == 0 && colNum == 0 && recordIsEmpty(record)) {
					return "Empty";
				}
				String colName = cols.get(colNum);
				String cellValue = record.getAttribute(colName);
				
				if (cellValue != null) {
					if (colName.equals(Endpoints.KEY_TITLE)) {
//						return "<div style=\"width: " + COL_WIDTH_DATASET_TITLE + "\">" + cellValue + "</div>";
						return cellValue;
					} else if (colName.equals(Endpoints.KEY_ENDPOINT)) {
						return cellValue;
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
                return record.getAttribute(Endpoints.KEY_DESCRIPTION); 
            }  
        });  
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
				if (!latestEndpointValue.equals(getEndpoint())) {
					JsMethods.checkCorsEnabled(getEndpoint());
					String endpoint = getEndpoint();
					getView().getSettings().getSelectedTabSettings().setEndpoint(endpoint);
					LocalStorageHelper.storeSettingsInCookie(getView().getSettings());
				}
			}
		});
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
	
	private View getView() {
		return this.view;
	}
	
	private QueryTab getQueryTab() {
		return this.queryTab;
	}
	
	private boolean recordIsEmpty(ListGridRecord record) {
		boolean empty = true;
		String[] attributes = record.getAttributes();
		for (String attribute: attributes) {
			if (record.getAttribute(attribute).length() > 0) {
				empty = false;
				break;
			}
		}
		return empty;
	}
	
	
}
