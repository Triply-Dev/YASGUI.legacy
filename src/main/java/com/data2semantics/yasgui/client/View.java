package com.data2semantics.yasgui.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.data2semantics.yasgui.client.queryform.QueryLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class View extends VLayout {
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private ToolStrip header = new ToolStrip();
	private HLayout content = new HLayout();
	private ArrayList<HashMap<String, String>> postProcessArray;
	public View() {
		postProcessArray = new ArrayList<HashMap<String, String>>();
		setWidth100();  
        setHeight100();
        header.setWidth100();
        header.setHeight(50);
        header.setAlign(Alignment.CENTER);
        header.setAlign(VerticalAlignment.CENTER);
        Label label = new Label();
        label.setAlign(Alignment.CENTER);
        label.setContents("Yet Another SPARQL GUI");
        label.setWidth100();
        header.addMember(label);
        addMember(header);
		content.addMember(new LayoutSpacer());
		content.addMember(new QueryLayout(this));
		content.addMember(new LayoutSpacer());
		addMember(content);
	}

	public void onError(String error) {
		onLoadingFinish();
		final Window winModal = new Window();
		winModal.setWidth(600);
		winModal.setHeight(400);
		winModal.setTitle("Error");
		winModal.setShowMinimizeButton(false);
		winModal.setIsModal(true);
		winModal.setShowModalMask(true);
		winModal.centerInPage();
//		winModal.addCloseClickHandler(new CloseClickHandler() {
//			public void onCloseClick(CloseClientEvent event) {
//				winModal.destroy();
//			}
//		});
		Label label = new Label(error);
		winModal.addItem(label);
		winModal.draw();
	}

	public void onError(Throwable throwable) {
		String st = throwable.getClass().getName() + ": " + throwable.getMessage();
		for (StackTraceElement ste : throwable.getStackTrace()) {
			st += "\n" + ste.toString();
		}
		onError(st);
	}

	public void onLoadingFinish() {
		// loading.loadingEnd();
	}

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}

	

}
