package com.data2semantics.yasgui.server;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
		String html = "";
		if (report.getBody() != null) html += report.getBody() + "<br><br>";
		if (report.getUserAgent() != null) html += "<strong>UserAgent</strong>: " + report.getUserAgent() + "<br>";
		if (report.getThrowable() != null) {
			html += "<strong>Stacktrace</strong>: <br>";
			html += ExceptionUtils.getStackTrace(report.getThrowable());
		}
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
