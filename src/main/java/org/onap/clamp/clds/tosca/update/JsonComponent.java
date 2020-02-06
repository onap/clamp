/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */
package org.onap.clamp.clds.tosca.update;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonComponent {

	private String componentName;
	private JsonObject component;
	private LinkedHashMap<String, JsonProperty> jsonProperty = new LinkedHashMap<String, JsonProperty>();
	/**
	 * @param name
	 * @param source
	 * Constructor : Extract each properties, to create a new JsonProperty (more accessible,
	 * and parsable).
	 */
	@SuppressWarnings("unchecked")
	public JsonComponent(String name, JsonObject source) {
		this.componentName = name;
		component = new JsonObject();
		this.component = source.getAsJsonObject(name);
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
	/**
	 * Check each property for a metadata, to treat it
	 */
	public void extractMetadatas() {
		for (Entry<String, JsonProperty> property : jsonProperty.entrySet()) {
			if(containsMetadata(property.getValue())) {
				property.getValue().getPossibleValues();
			}
		}
	}
	public Boolean containsMetadata(JsonProperty property) {
		return property.getFields().has("metadata");
	}
	/**
	 * @return
	 *
	 * Get the JsonComponent as a JsonObject
	 *
	 */
	public JsonObject getAsJson() {
		JsonObject updateComponent = new JsonObject();
		updateComponent.add(componentName, component);
		return updateComponent;
	}
	/**
	 * @return
	 * Launch all the process : switch metadatas, get and return
	 * he component as a classical JSON
	 */
	public JsonObject getFinalJsonObject() {
		JsonObject finalJsonObject;
		this.extractMetadatas();
		finalJsonObject = this.getAsJson();
		return finalJsonObject;
	}
}
