package com.data2semantics.yasgui.client;

import java.util.logging.Logger;
import com.data2semantics.yasgui.client.queryform.QueryLayout;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private ToolStrip header = new ToolStrip();
	private HLayout content = new HLayout();
	private QueryLayout queryLayout;
	public View() {
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
		//content.addMember(new LayoutSpacer());
		queryLayout = new QueryLayout(this);
		content.addMember(queryLayout);
		//content.addMember(new LayoutSpacer());
		addMember(content);
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
			@Override
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

	public void onLoadingFinish() {
		// loading.loadingEnd();
	}

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}
	
	public QueryLayout getQueryLayout() {
		return this.queryLayout;
	}

	public Logger getLogger() {
		return this.logger;
	}
}
