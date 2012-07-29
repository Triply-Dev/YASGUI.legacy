package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.queryform.QueryForm;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	public View() {
		setMargin(20);
		addMember(new QueryForm(this));
	}

	public void onError(String error) {
		onLoadingFinish();
		final Window winModal = new Window();
		winModal.setWidth(360);
		winModal.setHeight(115);
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

	public void onLoadingFinish() {
		// loading.loadingEnd();
	}

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}

}
