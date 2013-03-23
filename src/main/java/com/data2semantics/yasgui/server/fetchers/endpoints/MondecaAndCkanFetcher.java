package com.data2semantics.yasgui.server.fetchers.endpoints;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import au.com.bytecode.opencsv.CSVReader;
import com.data2semantics.yasgui.shared.Endpoints;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class MondecaAndCkanFetcher {
	
	private static String MONDECA_CSV = "http://labs.mondeca.com/sparqlEndpointsStatus/stats/sparql_availability.csv";
	private static int CSV_KEY_DATASET = 0;
	private static int[] CSV_KEYS_AVAILABILITY = new int[]{1,2,3};
	public static String fetch() throws JSONException, IOException, ParseException {
		ArrayList<String> availableEndpoints = fetchAvailableEndpointsFromCsv();
		ResultSet ckanResultSet = CkanFetcher.queryCkan();
		
		
		JSONArray endpoints = new JSONArray();
		while (ckanResultSet.hasNext()) {
			QuerySolution querySolution = ckanResultSet.next();
			
			String datasetUri = EndpointsFetcher.getBindingValueAsString(querySolution.get(Endpoints.KEY_DATASETURI));
			if (availableEndpoints.contains(datasetUri)) {
				JSONObject endpointObject = new JSONObject();
				Iterator<String> varNames = querySolution.varNames();
				while (varNames.hasNext()) {
					String varName = varNames.next();
					endpointObject.put(varName, EndpointsFetcher.getBindingValueAsString(querySolution.get(varName)));
				}
				endpoints.put(endpointObject);
			}
		}
		if (endpoints.length() == 0) {
			throw new IOException("No endpoints retrieved using Mondeca + Ckan");
		}
		return endpoints.toString();
	}
	
	private static ArrayList<String> fetchAvailableEndpointsFromCsv() throws MalformedURLException, IOException, ParseException {
		InputStream inputStream = new URL( MONDECA_CSV ).openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        CSVReader reader = new CSVReader(in,';');
        ArrayList<String> availableEndpoints = new ArrayList<String>();;
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
        	boolean available = true;
        	NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            
        	for (int rowKey: CSV_KEYS_AVAILABILITY) {
        		Number number = format.parse(nextLine[rowKey]);
        		double d = number.doubleValue();
        		if (d == 0) {
        			available = false;
        		}
        		
        	}
        	if (available) {
    			availableEndpoints.add(nextLine[CSV_KEY_DATASET]);
    		}
        }
        in.close();
        reader.close();
        return availableEndpoints;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(MondecaAndCkanFetcher.fetch());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
