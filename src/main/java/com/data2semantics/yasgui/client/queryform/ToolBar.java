package com.data2semantics.yasgui.client.queryform;

import java.util.LinkedHashMap;
import com.data2semantics.yasgui.client.View;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class ToolBar extends ToolStrip {
	private View view;
	public static String OUTPUT_CSV = "csv";
	public static String OUTPUT_JSON = "json";
	public static String OUTPUT_XML = "xml";
	public static String OUTPUT_TABLE = "table";
	private SelectItem outputSelection;
	public ToolBar(View view) {
		this.view = view;
		
        setWidth100();
        outputSelection = new SelectItem();
        outputSelection.setTitleOrientation(TitleOrientation.TOP);
        outputSelection.setTitle("Output");  
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
        valueMap.put(OUTPUT_TABLE, "Table"); 
        valueMap.put(OUTPUT_CSV, "CSV"); 
        valueMap.put(OUTPUT_JSON, "JSON");
        valueMap.put(OUTPUT_XML, "XML");
        LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();  
        valueIcons.put(OUTPUT_TABLE, "table");  
        valueIcons.put(OUTPUT_CSV, "csv");  
        valueIcons.put(OUTPUT_JSON, "json");  
        valueIcons.put(OUTPUT_XML, "xml");  
        outputSelection.setValueIcons(valueIcons);  
        
        outputSelection.setValueMap(valueMap);  
        outputSelection.setImageURLPrefix("logos/formats/");  
        outputSelection.setImageURLSuffix(".png");  
        outputSelection.setDefaultValue(OUTPUT_TABLE);
        addFormItem(outputSelection);  
	}

	private View getView() {
		return this.view;
	}
	
	public String getSelectedOutput() {
		return this.outputSelection.getValueAsString();
	}
}
