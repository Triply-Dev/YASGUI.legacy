package com.data2semantics.yasgui.server.autocompletions;

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
		
		
//		  {
//			     "response": {
//			        "status": 0,
//			        "startRow": 0,
//			        "endRow": 76,
//			        "totalRows": 546,
//			        "data": [
//			            {"field1": "value", "field2": "value"},
//			            {"field1": "value", "field2": "value"},
//			            ... 76 total records ...
//			        ]
//			     }
//			  }
		
		jsonResponse.put("data", dataArray);
		resultObject.put("response", jsonResponse);
		return resultObject;
	}
	
	public static JSONObject create(HttpServletRequest request, HttpServletResponse response, File webappDir) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		AutocompletionsInfoResponseCreator creator = new AutocompletionsInfoResponseCreator(request, response, webappDir);
		return creator.createJson();
	}

}
