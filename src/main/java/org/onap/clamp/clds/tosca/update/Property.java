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
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Property {

	/**
	 * name parameter is used as "key", in the LinkedHashMap of Components
	 */
	private String name;
	private LinkedHashMap<String, Object> items;
	public Property(String name,LinkedHashMap<String, Object> items) {
		super();
		this.name = name;
		this.items = items;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LinkedHashMap<String, Object> getItems() {
		return items;
	}
	public void setItems(LinkedHashMap<String, Object> items) {
		this.items = items;
	}

	/**
	 * @param fieldsContent
	 * @param fieldName
	 * @param value
	 * For each primitive value, requires to get each field Value and cast it and add it in a Json
	 */
	public void addFieldToJson(JsonObject fieldsContent, String fieldName, Object value) {
		if(value != null) {
		String typeValue = value.getClass().getSimpleName();
		switch (typeValue) {
			case "String":
				fieldsContent.addProperty(fieldName, (String) value);
				break;
			case "Boolean":
				fieldsContent.addProperty(fieldName, (Boolean) value);
				break;
			case "Double":
				fieldsContent.addProperty(fieldName, (Number) value);
				break;
			case "Integer":
				fieldsContent.addProperty(fieldName, (Integer) value);
				break;
			default:
				fieldsContent.add(fieldName,parseArray((ArrayList) value));
				break;
			}
		}
	}

	/**
	 * @param arrayProperties
	 * @return
	 * If a field value is an Array, create an Instance of ArrayField to insert if in the JsonObject
	 */
	public JsonArray parseArray(ArrayList<Object> arrayProperties) {
		JsonArray arrayContent = new JsonArray();
		ArrayList<Object> arrayComponent = new ArrayList<>();
		for (Object itemArray : arrayProperties) {
			arrayComponent.add(itemArray);
		}
		ArrayField af = new ArrayField(arrayComponent);
		arrayContent = af.deploy();
		return arrayContent;
	}

	/**
	 * @param json
	 * @param constraints
	 * @param template
	 * Create an instance of Constraint, to extract the values and add it to the Json (according to the type
	 * of the current property)
	 */
	@SuppressWarnings("unchecked")
	public void addConstraintsAsJson(JsonObject json, ArrayList<Object> constraints,Template template) {
		for (Object constraint : constraints) {
			if(constraint instanceof LinkedHashMap) {
				LinkedHashMap<String, Object> valueConstraint = (LinkedHashMap<String, Object>) constraint;
				Constraint constraintParser = new Constraint(valueConstraint, template);
				constraintParser.deployConstraints(json, (String) getItems().get("type"));
			}
		}

	}

}
