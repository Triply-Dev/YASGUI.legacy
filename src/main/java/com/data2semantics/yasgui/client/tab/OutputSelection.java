/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
	@SuppressWarnings("unused")
	private View view;
	SelectItem selectItem;

	public OutputSelection(final View view, QueryTab tab) {
		this.view = view;
		selectItem = new SelectItem();
		selectItem.setTitle("Output");
		selectItem.setTitleOrientation(TitleOrientation.TOP);
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(Output.OUTPUT_TABLE, "Table");
		valueMap.put(Output.OUTPUT_TABLE_SIMPLE, "Simple Table");
		valueMap.put(Output.OUTPUT_CSV, "CSV");
		valueMap.put(Output.OUTPUT_RAW_RESPONSE, "Query Response");
		
		LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
		valueIcons.put(Output.OUTPUT_TABLE, Output.OUTPUT_TABLE);
		valueIcons.put(Output.OUTPUT_TABLE_SIMPLE, Output.OUTPUT_TABLE_SIMPLE);
		valueIcons.put(Output.OUTPUT_CSV, Output.OUTPUT_CSV);
		valueIcons.put(Output.OUTPUT_RAW_RESPONSE, Output.OUTPUT_RAW_RESPONSE);
		selectItem.setValueIcons(valueIcons);

		selectItem.setValueMap(valueMap);
		selectItem.setImageURLPrefix("icons/formats/");
		selectItem.setImageURLSuffix(".png");
		selectItem.setDefaultValue(tab.getTabSettings().getOutputFormat());
		selectItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getSelectedTabSettings().setOutputFormat(selectItem.getValueAsString());
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		setItems(selectItem);
	}
}
