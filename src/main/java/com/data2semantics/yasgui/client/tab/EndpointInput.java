package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.ClientOnlyDataSource;
import com.data2semantics.yasgui.client.View;
import com.smartgwt.client.types.TextMatchStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class EndpointInput extends DynamicForm {
	private View view;
	private ComboBoxItem endpoint;
	private String latestEndpointValue; //used to know when to check for cors enabled. Not just on blur, but only on blur and when value has changed
	private QueryTab queryTab;
	public EndpointInput(View view, QueryTab queryTab) {
		this.queryTab = queryTab;
		this.view = view;
		setTitleOrientation(TitleOrientation.TOP);
		createTextInput();
		
	}
	public EndpointInput() {
		setTitleOrientation(TitleOrientation.TOP);
		createTextInput();
		
	}
	
	private void createTextInput() {
		endpoint = new ComboBoxItem("endpoint", "Endpoint");
		endpoint.setTextMatchStyle(TextMatchStyle.SUBSTRING);
		endpoint.setValueField("endpointUri");
		endpoint.setAddUnknownValues(true);
		endpoint.setCompleteOnTab(true);
		endpoint.setWidth(250);
		endpoint.setOptionDataSource(ClientOnlyDataSource.getInstance());
		ListGrid pickListProperties = new ListGrid();  
		ListGridField endpointField = new ListGridField("endpointUri", "Endpoint");
		ListGridField descriptionField = new ListGridField("description", "Description");
		//pickListProperties.setFields(endpointField, descriptionField);
		endpoint.setPickListFields(endpointField, descriptionField);
        pickListProperties.setCellHeight(50);  
        pickListProperties.setCanHover(true);  
        pickListProperties.setShowHover(true);  
        pickListProperties.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
//				getView().getLogger().severe("56");
                String descStr = record.getAttribute("description");  
//                getView().getLogger().severe("sdf" +descStr);
                if (descStr == null) descStr = "[no description]";  
  
                String datasetUri = record.getAttribute("endpointUri");  
  
                String retStr =  datasetUri; 
                return retStr;  
  
            }  
        });  
        pickListProperties.setHoverCustomizer(new HoverCustomizer() {  
            @Override  
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {  
                String descStr = record.getAttribute("description");  
                if (descStr == null) descStr = "[no description]";  
                return descStr;  
            }  
        });  
//        endpoint.setDisplayField("endpointUri");
//        endpoint.setValueField("description");
        endpoint.setPickListProperties(pickListProperties);
        
        setFields(endpoint);
//		endpoint = new TextItem();
//		setTitleOrientation(TitleOrientation.TOP);
//		endpoint.setTitle("Endpoint");
//		endpoint.setWidth(250);
//		endpoint.setDefaultValue(getQueryTab().getTabSettings().getEndpoint());
//		//For this default value, also retrieve CORS setting
//		JsMethods.checkCorsEnabled(getView().getSelectedTabSettings().getEndpoint());
//		endpoint.setName(ENDPOINT_INPUT_NAME);
//		endpoint.addFocusHandler(new FocusHandler() {
//			@Override
//			public void onFocus(FocusEvent event) {
//				latestEndpointValue = getEndpoint();
//				
//			}
//		});
//		endpoint.addBlurHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				if (!latestEndpointValue.equals(getEndpoint())) {
//					JsMethods.checkCorsEnabled(getEndpoint());
//					getView().getSettings().getSelectedTabSettings().setEndpoint(getEndpoint());
//					LocalStorageHelper.storeSettingsInCookie(getView().getSettings());
//				}
//			}
//
//		});
//		setFields(endpoint);
	}
	
	public String getEndpoint() {
		return endpoint.getValueAsString();
	}
	
	private View getView() {
		return this.view;
	}
	
	private QueryTab getQueryTab() {
		return this.queryTab;
	}
}
