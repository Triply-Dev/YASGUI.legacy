package com.data2semantics.yasgui.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private ServerSideApiAsync serverSideApi;

	public View(ServerSideApiAsync serverSideApi) {
		this.serverSideApi = serverSideApi;
		setMargin(20);
		Label label = new Label();
		label.setHeight(30);
		label.setPadding(10);
		label.setAlign(Alignment.CENTER);
		label.setValign(VerticalAlignment.CENTER);
		label.setWrap(false);
		label.setShowEdges(true);
		label.setContents("<i>Approved</i> for release");
		addMember(label);
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

	public ServerSideApiAsync getServerSideApi() {
		return serverSideApi;
	}

}
