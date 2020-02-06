package main.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonComponent {
	
	private JsonObject component;
	private LinkedHashMap<String, JsonProperty> jsonProperty = new LinkedHashMap<String, JsonProperty>();
	
	@SuppressWarnings("unchecked")
	public JsonComponent(JsonObject source) {
		this.component = source;
		if(this.component.has("properties")) {
			JsonObject entrySet =  (JsonObject) component.get("properties");
			for (Entry<String, JsonElement> entry : entrySet.entrySet()) {
				JsonObject property = (JsonObject) entry.getValue();
				JsonProperty componentProperty = new JsonProperty(entry.getKey(), property);
				jsonProperty.put(entry.getKey(), componentProperty);
			}
		}
	}

	public JsonObject getSource() {
		return component;
	}

	public LinkedHashMap<String, JsonProperty> getJsonProperty() {
		return jsonProperty;
	}

	public void setJsonProperty(LinkedHashMap<String, JsonProperty> jsonProperty) {
		this.jsonProperty = jsonProperty;
	}
	
	public LinkedHashMap<String,String> extractMetadata() {
		
		LinkedHashMap<String,String> metaDataValues = new LinkedHashMap<String,String>();
		
		for (Entry<String, JsonProperty> property : jsonProperty.entrySet()) {
			if(containsMetadata(property.getValue())) {
				metaDataValues.put(property.getKey(), property.getValue().getMetadaValue());
			}
		}
		return metaDataValues;
	}
	
	public Boolean containsMetadata(JsonProperty property) {
		Boolean exist = false;
		if(property.getFields().has("metadata")) {
			exist = true;
		}
		return exist;
	}
	
	
}
