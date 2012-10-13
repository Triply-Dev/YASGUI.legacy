package com.data2semantics.yasgui.client.helpers;

public class GoogleAnalyticsEvent {
	
	public GoogleAnalyticsEvent(String category, String action) {
		setCategory(category);
		setAction(action);
	}
	private String category;
	private String action;
	private String optLabel;
	private int optValue;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getOptLabel() {
		return optLabel;
	}
	public void setOptLabel(String optLabel) {
		this.optLabel = optLabel;
	}
	public int getOptValue() {
		return optValue;
	}
	public void setOptValue(int optValue) {
		this.optValue = optValue;
	}
	

}
