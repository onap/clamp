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

import com.google.gson.JsonObject;

public class JsonProperty {
	
	//Name of the property, and all of its fields
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
	
	/**
	 * 
	 * Here, the string from the field "metadata" is split/parse.. in a 
	 * future JsonObject, to replace the previous String value
	 * 
	 */
	public void getPossibleValues() {
		String valueToExploit = this.getMetadaValue();
		/*
		Make JsonObject from String value
		(split, etc...) fieldMetada => New JsonObject	
		*/
		JsonObject newValues = new JsonObject();
		newValues.addProperty("treatment", "specificValues");
		fields.add("metadata", newValues);
	}
	
	/**
	 * @return
	 * 
	 * Get the value of the field "metadata"
	 * 
	 */
	public String getMetadaValue() {
		JsonObject content = fields.getAsJsonObject("metadata");
		String value = content.get("clamp_possible_values").getAsString();
		return value;
	}

}
