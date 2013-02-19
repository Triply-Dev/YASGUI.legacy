package com.data2semantics.yasgui.server.servlets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Servlet implementation class ConfigServlet
 */
public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String CONFIG_FILE = "config.json";
    public ConfigServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File configFile = new File(CONFIG_FILE);
		if (!configFile.exists()) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to locate config file " + configFile.getAbsolutePath());
		} else {
			String jsonString = IOUtils.toString(new FileReader(configFile));
			try {
				new JSONObject(jsonString);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Tried to retrieve invalid config file. Not able to parse json string");
			}
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			out.println(jsonString);
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST not supported");
	}

}
