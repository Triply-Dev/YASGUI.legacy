package com.data2semantics.yasgui.client.tab;

import java.util.LinkedHashMap;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.shared.Output;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class OutputSelection extends DynamicForm {
	private View view;
	SelectItem selectItem;

	public OutputSelection(View view, QueryTab tab) {
		this.view = view;
		selectItem = new SelectItem();
		selectItem.setTitle("Output");
		selectItem.setTitleOrientation(TitleOrientation.TOP);
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(Output.OUTPUT_TABLE, "Table");
		valueMap.put(Output.OUTPUT_TABLE_SIMPLE, "Simple Table");
		valueMap.put(Output.OUTPUT_CSV, "CSV");
		valueMap.put(Output.OUTPUT_JSON, "JSON");
		
		LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
		valueIcons.put(Output.OUTPUT_TABLE, Output.OUTPUT_TABLE);
		valueIcons.put(Output.OUTPUT_TABLE_SIMPLE, Output.OUTPUT_TABLE_SIMPLE);
		valueIcons.put(Output.OUTPUT_CSV, Output.OUTPUT_CSV);
		valueIcons.put(Output.OUTPUT_JSON, Output.OUTPUT_JSON);
		selectItem.setValueIcons(valueIcons);

		selectItem.setValueMap(valueMap);
		selectItem.setImageURLPrefix("icons/formats/");
		selectItem.setImageURLSuffix(".png");
		selectItem.setDefaultValue(tab.getTabSettings().getOutputFormat());
		selectItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				getView().getSelectedTabSettings().setOutputFormat(selectItem.getValueAsString());
				LocalStorageHelper.storeSettingsInCookie(getView().getSettings());
			}
		});
		setItems(selectItem);
	}

	private View getView() {
		return this.view;
	}
}
