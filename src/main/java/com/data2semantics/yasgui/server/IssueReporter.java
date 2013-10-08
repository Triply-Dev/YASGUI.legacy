package com.data2semantics.yasgui.server;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.fetchers.ConfigFetcher;
import com.data2semantics.yasgui.shared.IssueReport;
import com.data2semantics.yasgui.shared.SettingKeys;

public class IssueReporter {
	private JSONObject config;
	private String oathToken;
	private String repo;
	private String username;
	private IssueReport report;

	public IssueReporter(File configDir, IssueReport report) throws IOException, ParseException, JSONException {
		this.config = ConfigFetcher.getJsonObjectFromPath(configDir);
		this.report = report;
		getInfoFromConfig();
		
	}
	private String report() throws IOException {
		IssueService issueService = new IssueService();
		issueService.getClient().setOAuth2Token(oathToken);
		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token(oathToken);
		Issue createdIssue = issueService.createIssue(repoService.getRepository(username, repo), createNewIssue());
		return createdIssue.getHtmlUrl();
	}
	private void getInfoFromConfig() throws JSONException {
		this.oathToken = config.getString(SettingKeys.GITHUB_OATH_TOKEN);
		this.repo = config.getString(SettingKeys.GITHUB_REPOSITORY).toLowerCase();
		this.username = config.getString(SettingKeys.GITHUB_USERNAME);
	}

	private Issue createNewIssue() {
		Issue issue = new Issue();
		issue.setBody(getBodyHtml());
		issue.setTitle(report.getSubject());
		issue.setLabels(getLabels());
		return issue;
	}
	private String getBodyHtml() {
		String html;
		html = report.getBody() + "<br><br>";
		if (report.getUserAgent() != null) html += "<strong>UserAgent</strong>: " + report.getUserAgent() + "<br>";
		if (report.getThrowable() != null) html += "<strong>Stacktrace</strong>: <br>";
		html += ExceptionUtils.getStackTrace(report.getThrowable());
		return html;
	}
	
	private List<Label> getLabels() {
		String[] labelStrings = new String[]{"AutoIssue", report.getReportType()};
		ArrayList<Label> labels = new ArrayList<Label>();
		for (String labelString: labelStrings) {
			if (labelString != null) {
				Label label = new Label();
				label.setName(labelString);
				labels.add(label);
			}
		}
		return labels;
	}
	public static String reportIssue(File configDir, IssueReport report) throws IOException, ParseException, JSONException {
		IssueReporter issueReporter = new IssueReporter(configDir, report);
		return issueReporter.report();
	}
}
