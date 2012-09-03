package com.data2semantics.yasgui.client;

import java.util.HashMap;
import java.util.logging.Logger;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.queryform.ToolBar;
import com.data2semantics.yasgui.client.queryform.grid.ResultGrid;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.Settings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private QueryTextArea queryTextArea;
	private EndpointInput endpointInput;
	private Label loading;
	private ToolBar toolBar;
	private VLayout queryResultContainer = new VLayout();
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private Settings settings = new Settings();
	private ResultGrid resultGrid;
	public View() {
		settings = Helper.getSettingsFromCookie();
		JsMethods.declareCallableViewMethods(this);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		initLoadingWidget();
		setMargin(10);
		setWidth100();
		this.toolBar = new ToolBar(this);
		addMember(this.toolBar);
		
		queryTextArea = new QueryTextArea(this); 
		addMember(queryTextArea);
		queryTextArea.setAutocompletePrefixes(false);
		
		endpointInput = new EndpointInput(this);
		addMember(endpointInput);
		
		addMember(queryResultContainer);
		
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.isCtrlKeyDown() && event.getKeyName().equals("S")) {
					// Save settings in cookie
					Helper.getAndStoreSettingsInCookie();
				}
			}
		});
	}


	

	public void resetQueryResult() {
		Canvas[] members = queryResultContainer.getMembers();
		for (Canvas member : members) {
			queryResultContainer.removeMember(member);
		}
	}

	public void addQueryResult(ResultGrid resultGrid) {
		resetQueryResult();
		this.resultGrid = resultGrid;
		queryResultContainer.addMember(resultGrid);
	}

	public void storePrefixes() {
		String query = JsMethods.getValueUsingId(QueryTextArea.QUERY_INPUT_ID);
		RegExp regExp = RegExp.compile("^\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$", "gm");
		while (true) {
			MatchResult matcher = regExp.exec(query);
			if (matcher == null)
				break;
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
		loading = new Label();
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid grey");
		loading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		loading.getElement().getStyle().setBottom(0, Unit.PX);
		loading.getElement().getStyle().setLeft(0, Unit.PX);
		loading.setHeight(30);
		loading.setAutoWidth();
		loading.setOverflow(Overflow.VISIBLE);
		loading.setWrap(false);
		loading.setAlign(Alignment.CENTER);
		loading.adjustForContent(false);
		loading.setZIndex(999999999);
		loading.hide();
		loading.redraw();

	}

	public void onLoadingStart() {
		onLoadingStart(DEFAULT_LOADING_MESSAGE);
	}
	
	public void onLoadingStart(String message) {
		//Add spaces to end of message, as we have autowidth enabled to this Label
		loading.setContents(message + "&nbsp;&nbsp;");
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
		settings = Helper.getSettings(); // Gets settings from all js objects
	}
	public QueryTextArea getQueryTextArea() {
		return this.queryTextArea;
	}


	public void drawResultsInTable(String jsonResult) {
		resultGrid.drawQueryResultsFromJson(jsonResult);
	}
}
