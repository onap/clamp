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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class ParserToJSON {
	private LinkedHashMap<String, Component> components;
	private LinkedHashMap<String, Template> templates;
	public ParserToJSON(LinkedHashMap<String, Component> components, LinkedHashMap<String, Template> templates) {
		this.components = components;
		this.templates = templates;
	}
	/**
	 * @param name
	 * @return
	 * For a given component, launch process to parse it in Json
	 */
	public JsonObject parse(String name){
		Component component = components.get(name);
		JsonObject analyzedComponent = getDerivedFrom(component);
		return analyzedComponent;
	}
	/**
	 * Check if the component has another "Mother" Component, than Root. In that case, the same method is called. Otherwise,
	 * there is a call, to extract all of these properties.
	 */
	@SuppressWarnings("unchecked")
	public JsonObject getDerivedFrom(Component component) {
		JsonObject returnFile = new JsonObject();
		JsonObject rootObject = new JsonObject();
		//If Component has another Component than Root
		if(component.getDerived_from().equals("tosca.policies.Root")
				|| component.getDerived_from().equals("tosca.datatypes.Root")) {
			//Simple String : tosca.policies.Root ou tosca.datatypes.Root
			rootObject.addProperty("derived_from", component.getDerived_from());
			//Get Requirements
			rootObject.add("required", this.getRequirements(component));
			//Component.getProperties
			rootObject.add("properties", this.getProperties(component));
		}
		//Else
		else {
			//Find and instanciate father Component
			Component fatherComponent = this.matchComponent(component.getDerived_from());
			//Same process : father's Component.getderivedfrom
			rootObject.add("derived_from", this.getDerivedFrom(fatherComponent));
			//Get Requirements
			rootObject.add("required", this.getRequirements(component));
			//Component.getProperties
			rootObject.add("properties", this.getProperties(component));
		}
		returnFile.add(component.getName(), rootObject);
		return returnFile;
	}
	/**
	 * @param component
	 * @return
	 *
	 * Loop on each properties : if it's a "Composed" type (Data or Policy), the process is restart to get the parents and its
	 * owns properties. Otherwise, it is a primitive type, the matchType method is called.
	 *
	 */
	@SuppressWarnings("unchecked")
	public JsonObject getProperties(Component component) {
		JsonObject getProperties = new JsonObject();
		//Properties' Loop from Component
		Collection<Property> properties = component.getProperties().values();
		for (Property property : properties) {
			//If Property has an Object Type >>> Treat as a Component || Treat Metadata
			if(property.getItems().containsKey("entry_schema") || property.getItems().containsKey("metadata")
					|| matchComponent((String) property.getItems().get("type")) !=null ) {
				getProperties.add(property.getName(), parseComplex(property));
			}
			//If Property is a Primitive Type (Array Include) >>> Print via Template
			else {
				getProperties.add(property.getName(), matchType(property));
			}
		}
		return getProperties;
	}
	/**
	 * @param name
	 * @return
	 *
	 * By a string entry, find and get the parent's Component in the list of the components
	 *
	 */
	public Component matchComponent(String name) {
		Component correspondingComponent = null;
		Collection<Component> listofComponent = components.values();
		for (Component component : listofComponent) {
			if(component.getName().equals(name)) {
			correspondingComponent = component;
			}
		}
		return correspondingComponent;
	}
	/**
	 * @param component
	 * @return
	 *
	 * Check if the properties of the component are required, or not (push name in an array for the json object)
	 *
	 */
	@SuppressWarnings("unchecked")
	public JsonElement getRequirements(Component component) {
		JsonArray requirements = new JsonArray();
		Collection<Property> properties = component.getProperties().values();
		for (Property property : properties) {
		if(property.getItems().containsKey("required") &&
				property.getItems().get("required").equals(true)) {
			requirements.add(property.getName());
			}
		}
		return requirements;
	}
	/**
	 * @param property
	 * @return
	 *
	 * For a "primitive" type, get the bound template.
	 *
	 */
	@SuppressWarnings("unchecked")
	public JsonObject matchType(Property property) {
		JsonObject matchingTemplate = new JsonObject();
		//Get type from the property parameter
		String propertyType = (String) property.getItems().get("type");
		//Loop on all templates
		for (String typeTemplate : templates.keySet()) {
			//Same type
			if(propertyType.equals(templates.get(typeTemplate).getName())) {
				//Initialize JSON with method which links the matching fields
				matchingTemplate = this.getSpecifiedFields(property, templates.get(typeTemplate));
			}
		}
		return matchingTemplate;
	}
	/**
	 * @param property
	 * @param template
	 * @return
	 *
	 * Match between the templates fields and properties fields : Only the fields matched in the two components are used in
	 * the json Object!!
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JsonObject getSpecifiedFields(Property property, Template template) {
		JsonObject matchingFields = new JsonObject();
		//Loop for all the fields of the property
		for (String propertyField : property.getItems().keySet()) {
			if(template.hasFields(propertyField)) {
				//Matching field : get the value and the type
				Object fieldValue = property.getItems().get(propertyField);
				this.addFieldToJson(matchingFields, propertyField, fieldValue);
				}
		}
		return matchingFields;
	}
	/**
	 * @param object
	 * @return
	 *
	 * Each Array type is contains LinkedHashMap type (name => valuesArray). It is necessary to
	 * loop the Array, and after its LinkedHashMap to get the value of it.
	 *
	 */
	@SuppressWarnings("unchecked")
	public JsonObject parseArray(ArrayList<Object> arrayProperties) {
		JsonObject arrayContent = new JsonObject();
		ArrayList<LinkedHashMap<String, ArrayList>> arrayComponent = new ArrayList<>();
		for (Object container : arrayProperties) {
			if(container instanceof LinkedHashMap<?, ?>)
				arrayComponent.add((LinkedHashMap<String, ArrayList>) container);
		}
		ArrayField af = new ArrayField(arrayComponent);
		arrayContent = af.deploy();
		return arrayContent;
	}

	@SuppressWarnings("unchecked")
	public JsonObject parseComplex(Property property) {

		String propertyType = (String) property.getItems().get("type");
		Template template = templates.get(propertyType);

		JsonObject matchingFields = new JsonObject();
		for (String propertyField : property.getItems().keySet()) {
			//if metadata
			if(propertyField.equals("metadata")) {
				JsonObject fieldsMetadata = new JsonObject();
				LinkedHashMap<String, String> entrySchemaFields = (LinkedHashMap<String, String>) property.getItems().get("metadata");
				for (Entry<String, String> field : entrySchemaFields.entrySet()) {
					fieldsMetadata.addProperty(field.getKey(), "some special treatment");
				}
				if(template.hasFields(propertyField)) {
				matchingFields.add(propertyField, fieldsMetadata);
				}
			}
			//entry_schema (Component ou Simple type)
			else if(propertyField.equals("entry_schema")) {
				LinkedHashMap<String, String> entrySchemaFields = (LinkedHashMap<String, String>) property.getItems().get("entry_schema");
				String type = entrySchemaFields.get("type");
				Component component = matchComponent(type);
				if(template.hasFields(propertyField)) {
				matchingFields.add("entry_schema", this.getAsComponent(component, true, type));
				}
			}
			//typeProperty : component
			else if(propertyField.equals("type") && matchComponent((String) property.getItems().get("type")) !=null ) {
				String type = (String) property.getItems().get("type");
				Component component = matchComponent(type);
				if(template.hasFields(propertyField)) {
				matchingFields.add("type", this.getAsComponent(component, false, type));
				}
			}
			//classic Field
			else {
				Object fieldValue = property.getItems().get(propertyField);
				if(template.hasFields(propertyField)) {
				this.addFieldToJson(matchingFields, propertyField, fieldValue);
				}
			}
		}
		return matchingFields;
	}

	/**
	 * @param component
	 * @param operation
	 * @param type
	 * @return
	 * Depends of the context (entry_schema or propertyType), getComponent.
	 */
	public JsonObject getAsComponent(Component component, boolean operation, String type) {
		JsonObject settingsReturn = new JsonObject();
		if(operation) {
			if(component !=null) {
				settingsReturn.add("type", getDerivedFrom(component));
			}
			else {
				settingsReturn.addProperty("type", type);
			}
			return settingsReturn;
		}else {

			return getDerivedFrom(component);
		}

	}

	/**
	 * @param fieldsContent
	 * @param fieldName
	 * @param value
	 * For a primitive type, cast the value for JsonObject (cfr gson Doc)
	 */
	public void addFieldToJson(JsonObject fieldsContent, String fieldName, Object value) {

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
