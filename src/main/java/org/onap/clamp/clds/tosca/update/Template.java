package main.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Template {
	
	
	/**
	 * name parameter is used as "key", in the LinkedHashMap of Templates
	 */
	private String name;
	private ArrayList<String> fields;
	
	
	public Template(String name) {
		
		this.name = name;
		this.fields = new ArrayList<String>();
	}
	
	public Template(String name, ArrayList<String> fields) {
		
		this.name = name;
		this.fields = fields;
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getFields() {
		return fields;
	}
	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
	
	public boolean hasFields(String name) {
		
		if(fields.contains(name)) {
			return true;
		}
		else {
			return false;
		}	
	}
	
	public void addField(String field) {
		fields.add(field);
	}
	
	public void removeField(String field) {
		fields.remove(field);
	}
	
	public String toString() {
		return " fields : " + fields;
	}

}
