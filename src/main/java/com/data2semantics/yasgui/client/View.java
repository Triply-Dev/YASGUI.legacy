package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.queryform.QueryLayout;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();

	public View() {
		setMargin(20);
		addMember(new QueryLayout(this));
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
		winModal.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClientEvent event) {
				winModal.destroy();
			}
		});
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
