package com.data2semantics.yasgui.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.Settings;
import com.data2semantics.yasgui.shared.SparqlRuntimeException;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {
	public static String CACHE_DIR = "/cache";
	
	public String queryGetText(Settings settings) throws IllegalArgumentException, SparqlRuntimeException {
		ResultSet resultSet = SparqlService.query(settings.getEndpoint(), settings.getQueryString());
		return SparqlService.getAsFormattedString(resultSet, settings.getOutputFormat());
	}

	public ResultSetContainer queryGetObject(Settings settings) throws IllegalArgumentException, SparqlRuntimeException {
		ResultSet resultSet = SparqlService.query(settings.getEndpoint(), settings.getQueryString());
		return SparqlService.getAsResultSetContainer(resultSet);
	}

	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException {
		return Prefixes.fetch(forceUpdate, getServletContext().getRealPath(CACHE_DIR));
	}
	
}
