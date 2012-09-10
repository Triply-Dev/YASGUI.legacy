package com.data2semantics.yasgui.client.tab;

import java.util.LinkedHashMap;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.shared.Output;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class OutputSelection extends DynamicForm {
	private View view;
	SelectItem selectItem;

	public OutputSelection(View view) {
		this.view = view;
		selectItem = new SelectItem();
		selectItem.setTitle("Output");
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(Output.OUTPUT_TABLE, "Table");
		valueMap.put(Output.OUTPUT_TABLE_SIMPLE, "Simple Table");
		valueMap.put(Output.OUTPUT_CSV, "CSV");
		valueMap.put(Output.OUTPUT_JSON, "JSON");
		
		LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
		valueIcons.put(Output.OUTPUT_TABLE, "table");
		valueIcons.put(Output.OUTPUT_CSV, "csv");
		valueIcons.put(Output.OUTPUT_JSON, "json");
		valueIcons.put(Output.OUTPUT_TABLE_SIMPLE, "xml");
		selectItem.setValueIcons(valueIcons);

		selectItem.setValueMap(valueMap);
		selectItem.setImageURLPrefix("logos/formats/");
		selectItem.setImageURLSuffix(".png");
		selectItem.setDefaultValue(Output.OUTPUT_TABLE_SIMPLE);
		selectItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				getView().getSettings().setOutputFormat(selectItem.getValueAsString());
				Helper.storeSettingsInCookie(getView().getSettings());
			}
		});
		setItems(selectItem);
	}

	private View getView() {
		return this.view;
	}
}
