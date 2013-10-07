package com.data2semantics.yasgui.shared;

import java.io.Serializable;

public class IssueReport implements Serializable{
	
	public static String REPORT_TYPE_BUG = "bug";
	public static String REPORT_TYPE_ENHANCEMENT = "enhancement";
	private static final long serialVersionUID = -2879399582269679948L;
	private String reportText;
	private String reportSubject;
	private String reportType;
	private Throwable throwable;
	private String userAgent;
	public IssueReport(){}
	public String getBody() {
		return reportText;
	}
	public void setBody(String reportText) {
		this.reportText = reportText;
	}
	public String getSubject() {
		return reportSubject;
	}
	public void setSubject(String reportSubject) {
		this.reportSubject = reportSubject;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getUserAgent() {
		return userAgent;
	}
	

}
