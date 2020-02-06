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

public class Constraint {

	private LinkedHashMap<String, Object> constraints;
	private Template template;

	public Constraint(LinkedHashMap<String, Object> constraints, Template template) {
		this.template = template;
		this.constraints = constraints;
	}

	/**
	 * @param jsonSchema
	 * @param typeProperty
	 * @return
	 * Deploy the linkedhashmap which contains the constraints, to extract them one to one
	 */
	public JsonObject deployConstraints(JsonObject jsonSchema, String typeProperty) {
		for (Entry<String, Object> constraint : constraints.entrySet()) {
			this.parseConstraint(jsonSchema,constraint.getKey(),constraint.getValue(), typeProperty);
		}
		return jsonSchema;
	}

	/**
	 * @param jsonSchema
	 * @param nameConstraint
	 * @param valueConstraint
	 * @param typeProperty
	 * Each case of Tosca constraints below parse specifically the field in the JsonObject
	 */
	@SuppressWarnings("unchecked")
	public void parseConstraint(JsonObject jsonSchema, String nameConstraint, Object valueConstraint, String typeProperty) {
		switch (nameConstraint) {
		case "equal":
			checkTemplateField("const",jsonSchema,valueConstraint);
			break;
		case "greater_than":
			checkTemplateField("exclusiveMinimum",jsonSchema,valueConstraint);
			break;
		case "greater_or_equal":
			checkTemplateField("minimum",jsonSchema,valueConstraint);
			break;
		case "less_than":
			checkTemplateField("exclusiveMaximum",jsonSchema,valueConstraint);
			break;
		case "less_or_equal":
			checkTemplateField("maximum",jsonSchema,valueConstraint);
			break;
		case "in_range":
			ArrayList<Integer> limitValues = (ArrayList<Integer>) valueConstraint;
			checkTemplateField("minimum",jsonSchema,limitValues.get(0));
			checkTemplateField("maximum",jsonSchema,limitValues.get(1));
			break;
		case "pattern":
			jsonSchema.addProperty(nameConstraint, (String) valueConstraint);
			break;
		case "length":
			this.getSpecificLength(jsonSchema, valueConstraint, typeProperty);
			break;
		case "min_length":
			String[] prefixValues = nameConstraint.split("_");
			this.getLimitValue(jsonSchema, valueConstraint, typeProperty, prefixValues[0]);
			break;
		case "max_length":
			String[] maxtab = nameConstraint.split("_");
			this.getLimitValue(jsonSchema, valueConstraint, typeProperty, maxtab[0]);
			break;
		default://valid_value
			this.getValueArray(jsonSchema, valueConstraint, typeProperty);
			break;
		}

	}

	/**
	 * @param jsonSchema
	 * @param fieldValue
	 * @param typeProperty
	 * For the complex components, get a specific number of items/properties
	 */
	public void getSpecificLength(JsonObject jsonSchema, Object fieldValue, String typeProperty) {
		switch (typeProperty) {
		case "string":
			checkTemplateField("minLength",jsonSchema, fieldValue);
			checkTemplateField("maxLength",jsonSchema,fieldValue);
			break;
		case "array":
			if(fieldValue.equals(1) && template.hasFields("uniqueItems")) {
				jsonSchema.addProperty("uniqueItems", true);
			}
			else   {
				checkTemplateField("minItems",jsonSchema,fieldValue);
				checkTemplateField("maxItems",jsonSchema,fieldValue);
			}
			break;
		default:// Map && List
			checkTemplateField("minProperties",jsonSchema,fieldValue);
			checkTemplateField("maxProperties",jsonSchema,fieldValue);
			break;
		}

	}

	/**
	 * @param jsonSchema
	 * @param fieldValue
	 * @param typeProperty
	 * @param side
	 * Get the limits fieldValue for the properties : depend of the type of the component
	 */
	public void getLimitValue(JsonObject jsonSchema, Object fieldValue, String typeProperty, String side) {
		switch (typeProperty) {
		case "string":
			if(side.equals("min")) {
				checkTemplateField("minLength",jsonSchema,fieldValue);
			}
			else {
				checkTemplateField("maxLength",jsonSchema,fieldValue);
			}
			break;
		default:// Array
			if(side.equals("min")) {
				checkTemplateField("minItems",jsonSchema,fieldValue);
			}
			else {
				checkTemplateField("maxItems",jsonSchema,fieldValue);
			}
			break;
		}

	}

	/**
	 * @param jsonSchema
	 * @param fieldValue
	 * @param typeProperty
	 * Get as Enum the valid values for the property
	 */
	public void getValueArray(JsonObject jsonSchema, Object fieldValue, String typeProperty) {
		if(template.hasFields("enum")) {
			JsonArray enumeration = new JsonArray();
			if(typeProperty.equals("string")|| typeProperty.equals("String")) {
				ArrayList<String> arrayValues = (ArrayList<String>) fieldValue;
				for (String arrayItem : arrayValues) {
					enumeration.add(arrayItem);
				}
				jsonSchema.add("enum", enumeration);
			}
			else {
				ArrayList<Number> arrayValues = (ArrayList<Number>) fieldValue;
				for (Number arrayItem : arrayValues) {
					enumeration.add(arrayItem);
				}
				jsonSchema.add("enum", enumeration);
			}
		}
	}

	/**
	 * @param field
	 * @param jsonSchema
	 * @param fieldValue
	 * Simple way to avoid code duplication
	 */
	public void checkTemplateField(String field, JsonObject jsonSchema, Object fieldValue){
		if(template.hasFields(field)){
			String typeField = fieldValue.getClass().getSimpleName();
			switch (typeField){
				case "String":
					jsonSchema.addProperty(field,(String)fieldValue);
					break;
				case "Integer":
					jsonSchema.addProperty(field,(Integer)fieldValue);
					break;
				case "Number":
					jsonSchema.addProperty(field,(Number)fieldValue);
					break;
				case "Boolean":
					jsonSchema.addProperty(field,(Boolean) fieldValue);
					break;
				default:
					break;
			}
		}
	}

}