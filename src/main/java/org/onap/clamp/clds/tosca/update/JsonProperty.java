package main.java;

import com.google.gson.JsonObject;

public class JsonProperty {
	
	private String name;
	private JsonObject fields;
	
	public JsonProperty(String name, JsonObject fields) {
		super();
		this.name = name;
		this.fields = fields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JsonObject getFields() {
		return fields;
	}
	public void setFields(JsonObject fields) {
		this.fields = fields;
	}
	
	public String getMetadaValue() {
		JsonObject content = fields.getAsJsonObject("metadata");
		String value = content.get("clamp_possible_values").getAsString();
		return value;
	}

}
