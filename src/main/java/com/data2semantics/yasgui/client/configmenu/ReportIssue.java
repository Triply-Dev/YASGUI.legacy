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

import java.util.LinkedHashMap;

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.shared.IssueReport;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class ReportIssue extends Window {

	private static int HEIGHT = 350;
	private static int WIDTH = 600;
	private View view;
	private DynamicForm form; 
	private VLayout mainWindowLayout;
	private ButtonItem reportButton;
	private SelectItem reportType;
	private TextAreaItem reportBody;
	private TextItem subject;
	private Throwable throwable;
	public ReportIssue(View view) {
		this.view = view;
		setHeight(HEIGHT);
		setWidth(WIDTH);
		setZIndex(ZIndexes.MODAL_WINDOWS);
		setTitle("Report a bug / enhancement");
		setIsModal(true);
		setDismissOnOutsideClick(true);
		setAutoCenter(true);
	}
	
	private void setAndDrawContent() {
		addContent();
		draw();
	}

	private void addContent() {
		mainWindowLayout = new VLayout();
		mainWindowLayout.setWidth100();
		mainWindowLayout.setHeight100();
		form = new DynamicForm();

		subject = new TextItem();
		subject.setTitle("Subject");
		subject.setRequired(true);
		if (throwable != null) subject.setDefaultValue(throwable.getMessage());
		subject.setWidth(250);
		subject.setTitleOrientation(TitleOrientation.TOP);

		reportType = new SelectItem(); 
		reportType.setRequired(true);
        reportType.setTitle("Type of report");  
        reportType.setType("comboBox");  
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put(IssueReport.REPORT_TYPE_BUG, "Bug");
        valueMap.put(IssueReport.REPORT_TYPE_ENHANCEMENT, "Enhancement");
        reportType.setValueMap(valueMap); 
        reportType.setDefaultValue(IssueReport.REPORT_TYPE_BUG);
        reportType.setTitleOrientation(TitleOrientation.TOP);
        
		reportBody = new TextAreaItem();
		reportBody.setTitle("Issue description");
		reportBody.setRequired(true);
		reportBody.setTitleOrientation(TitleOrientation.TOP);
		reportBody.setColSpan(2);
		reportBody.setLength(5000);
		reportBody.setWidth(WIDTH-40);
		reportBody.setHeight(200);

		reportButton = new ButtonItem();  
        reportButton.setName("Report");  
        reportButton.setTitle("Report");  
        reportButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {  

			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if (form.validate()) {
					final IssueReport report = new IssueReport();
					report.setUserAgent(JsMethods.getUserAgent());
					report.setSubject(subject.getValueAsString());
					report.setBody(reportBody.getValueAsString());
					report.setReportType(reportType.getValueAsString());
					if (throwable != null) {
						report.setThrowable(throwable);
					}
					new GwtCallbackWrapper<String>(view) {
						public void onCall(AsyncCallback<String> callback) {
							view.getRemoteService().reportIssue(report, callback);
						}
	
						protected void onFailure(Throwable throwable) {
							view.getErrorHelper().onError(throwable);
						}
	
						protected void onSuccess(String response) {
							showResponse(response);
						}
					}.call();
				}
				
				
			}  
        });  
		
        form.setItems(subject, reportType, reportBody, reportButton);
        mainWindowLayout.addMember(form);
        
		addItem(mainWindowLayout);
		
	}
	private void showResponse(String response) {
		response = "<div style='width:100%; height:100%;text-align:center;'>" + response + "</div>";
		reportButton.setDisabled(true);
		HTMLFlow responseLabel = new HTMLFlow(response);
		responseLabel.setWidth100();
		responseLabel.setHeight100();
		mainWindowLayout.addMember(responseLabel);
		
	}



	public Throwable getThrowable() {
		return throwable;
	}


	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public static void report(View view) {
		ReportIssue report = new ReportIssue(view);
		report.setAndDrawContent();
	}
	public static void report(View view, Throwable throwable) {
		ReportIssue report = new ReportIssue(view);
		report.setThrowable(throwable);
		report.setAndDrawContent();
	}
	
}
