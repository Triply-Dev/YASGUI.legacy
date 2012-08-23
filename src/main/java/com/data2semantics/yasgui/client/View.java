package com.data2semantics.yasgui.client;

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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	public static String QUERY_INPUT_ID = "queryInput";
	
	public static String ENDPOINT_INPUT_NAME = "EndpointInput";
	private Label loading;
	private TextItem endpoint;
	private ToolBar toolBar;
	private VLayout queryResultContainer = new VLayout();
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private Settings settings = new Settings();

	public View() {
		settings = Helper.getSettingsFromCookie();
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
		endpoint.setName(ENDPOINT_INPUT_NAME);
		endpoint.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				updateSettings();
			}
			
		});
		endpointForm.setFields(endpoint);
		addMember(endpointForm);
		addMember(queryResultContainer);
		setAutocompletePrefixes(false);
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.isCtrlKeyDown() && event.getKeyName().equals("S")) {
					//Save settings in cookie
					Helper.getAndStoreSettingsInCookie();
				}
			}
		});
		
	}

	private String getTextArea() {
		String textArea = "" + "<textarea " + "id=\"" + QUERY_INPUT_ID + "\"" + ">" + settings.getQueryString() + "</textarea>";
		return textArea;

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
		String query = JsMethods.getValueUsingId(QUERY_INPUT_ID);
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
	
	public void updateSettings() {
		settings = Helper.getSettings(); //Gets settings from all js objects
	}
	
	
}
