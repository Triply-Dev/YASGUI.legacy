package org.data2semantics.yasgui.selenium;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.runner.Description;

public class FailNotification {
	private static String ERROR_LOG_DIR = "errorLog";
	private File errorLogPath;
	private SeleniumBase baseTest;
	private Description description;
	private Throwable exception;
	private String failInfo;

	public FailNotification(Throwable e, Description description, SeleniumBase baseTest) {
		this.baseTest = baseTest;
		this.description = description;
		this.exception = e;
		initErrorLogPath();
		logFail();
		if (baseTest.props.sendMail()) {
			sendMail(getSubject(), failInfo, getScreenshotPath());
		}
	}

	private void logFail()  {
		generateFailString();
		try {
			FileUtils.writeStringToFile(new File(errorLogPath + File.separator + "log.err"), failInfo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		SeleniumBase.selenium.captureScreenshot(getScreenshotPath());
		
	}


	private String getScreenshotPath() {
		return errorLogPath + File.separator + SeleniumBase.runningBrowser.replace("*", "") + "_" + description.getMethodName() + ".png";
	}

	private void generateFailString() {
		failInfo = "<strong>Method:</strong> " + description.getMethodName() + "<br>";
		failInfo += "<strong>Host:</strong> " + baseTest.props.getHostToTest() + "<br>";
		failInfo += "<strong>Browser:</strong> " + SeleniumBase.runningBrowser + "<br>";
		failInfo += "<strong>Exception:</strong> " + exception.getMessage() + "<br>";
		failInfo += "<hr>";
		failInfo += "<strong>Stack trace:</strong><br>" + ExceptionUtils.getStackTrace(exception);
	}

	private String getSubject() {
		return "Selenium test " + description.getMethodName() + " failed";
	}

	private void initErrorLogPath() {
		File mainErrorLogDir = new File(ERROR_LOG_DIR);
		if (!mainErrorLogDir.exists()) {
			mainErrorLogDir.mkdir();
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
		Date date = new Date();
		errorLogPath = new File(mainErrorLogDir + File.separator + dateFormat.format(date));
		if (!errorLogPath.exists()) {
			errorLogPath.mkdir();
		}
	}

	public static void doNotify(Throwable e, Description description, SeleniumBase baseTest) {
		new FailNotification(e, description, baseTest);
	}


	private void sendMail(String subject, String content, String fileName) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(baseTest.props.getMailUserName(), baseTest.props.getMailPassWord());
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(baseTest.props.getMailUserName()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(baseTest.props.getMailSendTo()));
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			// message body
			messageBodyPart.setContent(content, "text/html; charset=utf-8");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileName);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("screenshot.png");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);

			
			Transport.send(message);

			System.out.println("Email send");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
