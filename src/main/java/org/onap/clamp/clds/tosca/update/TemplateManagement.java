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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Properties;

import com.google.gson.JsonObject;

public class TemplateManagement {

	private LinkedHashMap<String, Template> templates;
	private LinkedHashMap<String, Component> components;
	private ParserToJSON parserToJSON;
	private Extractor extractor;
	public TemplateManagement(String pathToComponent, String pathToTemplates) throws FileNotFoundException, IOException {
			File source = new File(pathToComponent);
			String[] splitPathFile = pathToComponent.split("\\.");
			if(source.exists() && splitPathFile[1].equals("yaml")) {
			this.extractor = new Extractor(pathToComponent);
			this.components = extractor.getAllItems();
			this.templates = initializeTemplates(pathToTemplates);
			}
			else{
				components = null;
			}
	}
	//GETTERS & SETTERS
	public LinkedHashMap<String, Component> getComponents() {
		return components;
	}
	public void setComponents(LinkedHashMap<String, Component> components) {
		this.components = components;
	}
	public ParserToJSON getParseToJSON() {
		return parserToJSON;
	}
	public void setParseToJSON(ParserToJSON parserToJSON) {
		this.parserToJSON = parserToJSON;
	}
	public LinkedHashMap<String, Template> getTemplates() {
		return templates;
	}
	public void setTemplates(LinkedHashMap<String, Template> templates) {
		this.templates = templates;
	}
	public Extractor getExtractor() {
		return extractor;
	}
	public void addTemplate(String name, ArrayList<String> fields) {
		Template template = new Template(name, fields);
		//If it is true, the operation does not have any interest : replace OR put two different object with the same body
		if(!templates.containsKey(template.getName()) || !this.hasTemplate(template)) {
			this.templates.put(template.getName(), template);
		}
	}
	/**
	 * @param nameTemplate
	 *
	 * By name, find and remove a given template
	 *
	 */
	public void removeTemplate(String nameTemplate) {
		this.templates.remove(nameTemplate);
	}
	/**
	 * @param nameTemplate
	 * @param fieldName
	 * @param operation
	 *
	 * Update Template : adding with true flag, removing with false
	 *
	 */
	public void updateTemplate(String nameTemplate, String fieldName, Boolean operation) {
		// Operation = true && field is not present => add Field
		if(operation && !this.templates.get(nameTemplate).getFields().contains(fieldName)) {
			this.templates.get(nameTemplate).addField(fieldName);
		}
		// Operation = false && field is present => remove Field
		else if(!operation && this.templates.get(nameTemplate).getFields().contains(fieldName)) {
			this.templates.get(nameTemplate).removeField(fieldName);
		}
	}
	/**
	 * @param template
	 * @return
	 *
	 * Check if the JSONTemplates have the same bodies
	 *
	 */
	public boolean hasTemplate(Template template) {
		boolean duplicateTemplate = false;
		Collection<String> templatesName = templates.keySet();
		if(templatesName.contains(template.getName())) {
			Template existingTemplate = templates.get(template.getName());
			duplicateTemplate = existingTemplate.checkFields(template);
		}
		return duplicateTemplate;
	}

	/**
	 * @param name
	 * @return
	 *
	 * For a given Component, get a corresponding JsonObject, through parseToJSON
	 *
	 */
	public JsonObject launchTranslation(String componentName) throws UnknownComponentException {
		this.parserToJSON = new ParserToJSON(components, templates);
		if(parserToJSON.matchComponent(componentName) == null)
			throw new UnknownComponentException(componentName);
		JsonObject componentAsJson = parserToJSON.getJsonProcess(componentName);
		return componentAsJson;
	}
	/**
	 * @return
	 *
	 * Create and complete several Templates from file.properties
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 */
	private LinkedHashMap<String, Template> initializeTemplates(String propertiesFilePath) throws FileNotFoundException, IOException {

		LinkedHashMap<String, Template> generatedTemplates = new LinkedHashMap<String, Template>();

		Properties templates = new Properties();
		templates.load(new FileInputStream(propertiesFilePath));

		for(Object key : templates.keySet()) {
			String fields = (String) templates.get(key);
			String[] fieldsInArray = fields.split(",");
			ArrayList<String> fieldsContainer = new ArrayList<>();
			for(String field : fieldsInArray) {
				fieldsContainer.add(field);
			}
			Template template = new Template((String)key, fieldsContainer);
			generatedTemplates.put(template.getName(), template);
		}
		return generatedTemplates;
	}
}
