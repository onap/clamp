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
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ArrayField {

	private ArrayList<LinkedHashMap<String, ArrayList>> complexFields;

	public ArrayField(ArrayList<LinkedHashMap<String, ArrayList>> arrayProperties) {

		this.complexFields = arrayProperties;

	}

	/**
	 * @return
	 * Each LinkedHashMap is parsed, to extract the Array and each of his value, which is
	 * cast for the JsonObject
	 */
	public JsonObject deploy() {

		JsonObject arrays = new JsonObject();
		for (LinkedHashMap<String, ArrayList> linkedHashMap : complexFields) {
			for (Entry<String, ArrayList> arrayField : linkedHashMap.entrySet()) {
				JsonArray subPropertyValuesArray = new JsonArray();
				for (Object arrayElement : arrayField.getValue()) {
					//Cast for each Primitive Type
					String typeValue = arrayElement.getClass().getSimpleName();
					switch (typeValue) {
					case "String":
						subPropertyValuesArray.add((String) arrayElement);
						break;
					case "Boolean":
						subPropertyValuesArray.add((Boolean) arrayElement);
						break;
					case "Double":
						subPropertyValuesArray.add((Number) arrayElement);
						break;
					case "Integer":
						subPropertyValuesArray.add((Number) arrayElement);
						break;
					default:
						break;
					}
				}
				arrays.add(arrayField.getKey(), subPropertyValuesArray);
			}
		}
		return arrays;
	}
}
