package com.data2semantics.yasgui.server.autocompletions;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 - 2014 Laurens Rietveld
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.smartgwt.client.rpc.RPCResponse;

public class AutocompletionsInfoResponseCreator {
	
	private DbHelper dbHelper;

	private AutocompletionsInfoResponseCreator(HttpServletRequest request, HttpServletResponse response, File webappDir) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		this.dbHelper = new DbHelper(webappDir, request);
		
	}
	
	private JSONObject createJson() throws JSONException, OpenIdException, SQLException {
		JSONObject resultObject = new JSONObject();
		
		JSONObject jsonResponse = new JSONObject();
		
		JSONArray dataArray = dbHelper.getPersonalAutocompletionsInfo();
		jsonResponse.put("status", RPCResponse.STATUS_SUCCESS);
		jsonResponse.put("startRow", 0);
		jsonResponse.put("endRow", dataArray.length() - 1);
		jsonResponse.put("totalRows", dataArray.length());
		jsonResponse.put("data", dataArray);
		resultObject.put("response", jsonResponse);
		return resultObject;
	}
	
	public static JSONObject create(HttpServletRequest request, HttpServletResponse response, File webappDir) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		AutocompletionsInfoResponseCreator creator = new AutocompletionsInfoResponseCreator(request, response, webappDir);
		return creator.createJson();
	}

}
