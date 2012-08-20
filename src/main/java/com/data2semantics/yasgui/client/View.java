package com.data2semantics.yasgui.client;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.data2semantics.yasgui.client.queryform.Helper;
import com.data2semantics.yasgui.client.queryform.ToolBar;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.Settings;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	public static String QUERY_INPUT_ID = "queryInput";
	private static String COOKIE_SETTINGS = "yasgui_settings";
	private Label loading;
	private TextItem endpoint;
	private ToolBar toolBar;
	private VLayout queryResultContainer = new VLayout();
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private Settings settings = new Settings();

	public View() {
		getSettingsFromCookie();
		initLoadingWidget();
		setMargin(10);
		setWidth100();
		this.toolBar = new ToolBar(this);
		addMember(this.toolBar);
		// Img img = new Img("xml.png");
		// addMember(img);
		HTMLPane queryInput = new HTMLPane();
		queryInput.setHeight("350px");
		queryInput.setContents(getTextArea());
		addMember(queryInput);
		DynamicForm endpointForm = new DynamicForm();
		endpoint = new TextItem();
		endpoint.setTitle("Endpoint");
		endpoint.setWidth(250);
		endpoint.setDefaultValue(settings.getEndpoint());
		endpointForm.setFields(endpoint);
		addMember(endpointForm);
		addMember(queryResultContainer);
		setAutocompletePrefixes(false);
		
	}

	private String getTextArea() {
		String textArea = "" + "<textarea " + "id=\"" + QUERY_INPUT_ID + "\"" + ">" + settings.getQueryString() + "</textarea>";
		return textArea;

	}

	private ToolBar getToolBar() {
		return this.toolBar;
	}

	
	
	public void setAutocompletePrefixes(boolean forceUpdate) {
		onLoadingStart();
		//get prefixes from server
		getRemoteService().fetchPrefixes(forceUpdate,
		new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				onError(caught.getMessage());
			}
			public void onSuccess(String prefixes) {
				JsMethods.setAutocompletePrefixes(prefixes);
				onLoadingFinish();
			}
		});
		
	}
	
	public void resetQueryResult() {
		Canvas[] members = queryResultContainer.getMembers();
		for (Canvas member: members) {
			queryResultContainer.removeMember(member);
		}
	}
	
	public void addQueryResult(Canvas member) {
		resetQueryResult();
		queryResultContainer.addMember(member);
	}
	
	public String getEndpoint() {
		return endpoint.getValueAsString();
	}
	
	
	public void storePrefixes() {
		String query = JsMethods.getQuery(QUERY_INPUT_ID);
		RegExp regExp = RegExp.compile("^\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$", "gm");
		while (true) {
			MatchResult matcher = regExp.exec(query);
			if (matcher == null) break;
			queryPrefixes.put(matcher.getGroup(2), new Prefix(matcher.getGroup(1), matcher.getGroup(2)));
	    }
	}
	
	public HashMap<String, Prefix> getQueryPrefixes() {
		return this.queryPrefixes;
	}
	
	public void onError(String error) {
		onLoadingFinish();
		final Window window = new Window();
		window.setAutoSize(true);
		window.setTitle("Error");
		window.setShowMinimizeButton(false);
		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setAutoCenter(true);
		window.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				window.destroy();
			}
		});
		Label label = new Label(error);
		window.addItem(label);
		window.draw();
	}

	public void onError(Throwable throwable) {
		String st = throwable.getClass().getName() + ": " + throwable.getMessage();
		for (StackTraceElement ste : throwable.getStackTrace()) {
			st += "\n" + ste.toString();
		}
		onError(st);
	}
	
	private void initLoadingWidget() {
		loading = new Label("Loading...");
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid grey");
		loading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		loading.getElement().getStyle().setBottom(0, Unit.PX);
		loading.getElement().getStyle().setLeft(0, Unit.PX);
		loading.setHeight(30);
		loading.setWidth(80);
		loading.setAlign(Alignment.CENTER);
		loading.adjustForContent(false);
		loading.hide();
		loading.redraw();
		
	}
	
	public void onLoadingStart() {
		loading.show();
	}
	
	public void onLoadingFinish() {
		loading.hide();
	}
	

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}
	
	public Logger getLogger() {
		return this.logger;
	}
	
	public Settings getSettings() {
		return this.settings;
	}
	
	public void storeSettingsInCookie() {
		updateSettings();
		Cookies.removeCookie(COOKIE_SETTINGS);
		Cookies.setCookie(COOKIE_SETTINGS, Helper.getHashMapAsJson(settings.getSettingsHashMap()));
	}
	
	public void getSettingsFromCookie() {
		String jsonString = Cookies.getCookie(COOKIE_SETTINGS);
		if (jsonString != null && jsonString.length() > 0) {
			settings = Helper.getSettingsFromJsonString(jsonString);
		} 
	}
	public void updateSettings() {
		settings = new Settings();
		settings.setQueryString(JsMethods.getQuery(QUERY_INPUT_ID));
		settings.setEndpoint(getEndpoint());
		settings.setOutputFormat(getToolBar().getSelectedOutput());
	}
}
