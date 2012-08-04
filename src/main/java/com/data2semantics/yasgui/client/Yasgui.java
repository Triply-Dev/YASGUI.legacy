package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.queryform.QueryLayout;
import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IconButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.menu.IconMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.toolbar.RibbonBar;
import com.smartgwt.client.widgets.toolbar.RibbonGroup;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Yasgui implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		View view = new View();
		view.draw();
		postProcess();
	}

	private void postProcess() {
		QueryLayout.attachCodeMirror(QueryLayout.QUERY_INPUT_ID);
	}
	
//	public void onModuleLoad() {
//		RibbonBar ribbonBar = new RibbonBar();  
//        ribbonBar.setLeft(0);  
//        ribbonBar.setTop(75);  
//        ribbonBar.setWidth100();  
//  
//        ribbonBar.setMembersMargin(2);  
//        ribbonBar.setLayoutMargin(2);  
//  
//        Menu menu = new Menu();  
//  
//        RibbonGroup fileGroup = new RibbonGroup();  
//        fileGroup.setTitle("File");  
//        fileGroup.setTitleAlign(Alignment.LEFT);  
//        fileGroup.setNumRows(1);  
//        fileGroup.setRowHeight(76);  
//        fileGroup.addControl(getIconMenuButton("New", "piece_blue", menu, true));  
//        fileGroup.addControl(getIconButton("Open", "star_yellow", true));  
//        fileGroup.addControl(getIconButton("Save", "pawn_red", true));  
//        fileGroup.addControl(getIconMenuButton("Save As", "cube_green", menu, true));  
//  
//        RibbonGroup editGroup = new RibbonGroup();  
//        editGroup.setTitle("Edit");  
//        editGroup.setNumRows(3);  
//        editGroup.setRowHeight(24);  
//        fileGroup.addControl(getIconButton("Edit", "piece_blue", false));  
//        fileGroup.addControl(getIconButton("Copy", "pawn_green", false));  
//        fileGroup.addControl(getIconButton("Paste", "cube_yellow", false));  
//        fileGroup.addControl(getIconMenuButton("Undo", null, menu, false));  
//        fileGroup.addControl(getIconMenuButton("Redo", null, menu, false));  
//  
//  
//        RibbonGroup insertGroup = new RibbonGroup();  
//        insertGroup.setTitle("Insert");  
//        insertGroup.setNumRows(3);  
//        insertGroup.setRowHeight(24);  
//        fileGroup.addControl(getIconMenuButton("Picture", null, menu, false));  
//        fileGroup.addControl(getIconButton("Link", "pawn_white", false));  
//        fileGroup.addControl(getIconButton("Document", "star_yellow", false));  
//        fileGroup.addControl(getIconButton("Video", "piece_red", false));  
//  
//        ribbonBar.addMember(fileGroup);  
//        ribbonBar.addMember(editGroup);  
//        ribbonBar.addMember(insertGroup);  
//  
//        ribbonBar.draw();  
//		
//    }  
//  
//    private IconButton getIconButton(String title, String iconName, boolean vertical) {  
//        IconButton button = new IconButton(title);  
//        button.setTitle(title);  
//        if (iconName == null) iconName = "cube_blue";  
//        button.setIcon("/images/pieces/16/" + iconName + ".png");  
//        button.setLargeIcon("/images/pieces/48/" + iconName + ".png");  
//        if (vertical == true) button.setOrientation("vertical");  
//        return button;  
//    }  
//  
//    private IconMenuButton getIconMenuButton(String title, String iconName, Menu menu, boolean vertical) {  
//        IconMenuButton button = new IconMenuButton();  
//        button.setTitle(title);  
//        if (iconName == null) iconName = "cube_blue";  
//        button.setIcon("/images/pieces/16/" + iconName + ".png");  
//        button.setLargeIcon("/images/pieces/48/" + iconName + ".png");  
//        if (vertical == true) button.setOrientation("vertical");  
//        if (menu != null) button.setMenu(menu);  
//  
//        button.setShowMenuIcon(true);  
//        return button;  
//    }  
}
