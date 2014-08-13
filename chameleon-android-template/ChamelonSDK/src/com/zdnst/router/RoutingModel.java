package com.zdnst.router;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RoutingModel {
	private HashMap<String, MappingModel>  mappings = new HashMap<String, MappingModel>() ;
	private String name ;
	
	public RoutingModel() {
		
	}
	
	public RoutingModel(String json) {
		try {
			parserWithJsongObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, MappingModel> getMappings() {
		return mappings;
	}
	public void setMappings(HashMap<String, MappingModel> mappings) {
		this.mappings = mappings;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	//解析json
	public RoutingModel parserWithJsongObject(String json) throws JSONException {
		JSONObject jsonObject =  new JSONObject(json);
		this.setName(jsonObject.getString("identifier"));
		JSONArray jsonArray = new JSONArray(jsonObject.getString("mappings"));
		RoutingModel r = new RoutingModel();
		for(int i =0;i<jsonArray.length();i++) {
			MappingModel m = new MappingModel();
			JSONObject jsonObjects =  new JSONObject(jsonArray.get(i).toString());
			String key = jsonObjects.keys().next().toString();
			m.setLinkUrlParameters(key);
			m.setPageIdentifier(jsonObjects.getString(key));
			mappings.put(key, m);
			r.setName(getName());
			r.setMappings(mappings);
		}
		return r;
	}

}
