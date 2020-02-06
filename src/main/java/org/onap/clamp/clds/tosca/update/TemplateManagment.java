package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class TemplateManagment {

	private LinkedHashMap<String, Template> templates;
	private LinkedHashMap<String, Component> components;
	private ParserToJSON parserToJSON;
	private Extractor extractor;
	
	public TemplateManagment(String pathToComponent) throws FileNotFoundException, IOException {
		
			File source = new File(pathToComponent);
			String[] splitPathFile = pathToComponent.split("\\.");
			if(source.exists() && splitPathFile[1].equals("yaml")) {
			this.extractor = new Extractor(pathToComponent);
			this.components = extractor.getAllItems();
			this.templates = initializeTemplates();
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
		
			duplicateTemplate = this.checkFields(template);
			
		}
		return duplicateTemplate;		
	}
	
	public boolean checkFields(Template template) {
		
		boolean duplicateFields = false;
		
		Template matchingTemplate = templates.get(template.getName());
		
			if(template.getFields().size()==matchingTemplate.getFields().size()) {
				int count = 0;
				//loop each component of first
				for (String item : template.getFields()) {
					//if component.key is present in the second
					if(matchingTemplate.getFields().contains(item))
						count++;
				}
				//if count = size
				if(template.getFields().size() == count)	
					//duplication = true;
					duplicateFields = true;
		}
		
		return duplicateFields;
	}
	
	/**
	 * @param name
	 * @return
	 * 
	 * For a given Component, get a corresponding JsonObject, through parseToJSON
	 * 
	 */
	public JsonObject launchTranslation(String componentName) {
		this.parserToJSON = new ParserToJSON(components, templates);
		JsonObject componentAsJson = parserToJSON.parse(componentName);
		return componentAsJson;
	}
	
	/**
	 * @return
	 * 
	 * Create and complete several Templates, 
	 * 
	 */
	private LinkedHashMap<String, Template> initializeTemplates() {
		
		LinkedHashMap<String, Template> generatedTemplates = new LinkedHashMap<String, Template>();
		
		ArrayList<String> fieldsInteger = new ArrayList<>(Arrays.asList("type", "description","required"));
		Template integer = new Template("Integer", fieldsInteger);
		generatedTemplates.put(integer.getName(), integer);
		ArrayList<String> fieldsBoolean = new ArrayList<>(Arrays.asList("description","required"));
		Template bool = new Template("Boolean", fieldsBoolean);
		generatedTemplates.put(bool.getName(), bool);
		ArrayList<String> fieldsString = new ArrayList<>(Arrays.asList("type","description","required","metadata","constraints"));
		Template string = new Template("String", fieldsString);
		generatedTemplates.put(string.getName(), string);
		ArrayList<String> fieldsNumber = new ArrayList<>(Arrays.asList("description","required"));
		Template number = new Template("Number", fieldsNumber);
		generatedTemplates.put(number.getName(), number);
		ArrayList<String> fieldsMap = new ArrayList<>(Arrays.asList("type","description","required","entry_schema"));
		Template map = new Template("Map", fieldsMap);
		generatedTemplates.put(map.getName(), map);
		ArrayList<String> fieldsList = new ArrayList<>(Arrays.asList("type","required","entry_schema"));
		Template list = new Template("List", fieldsList);
		generatedTemplates.put(list.getName(), list);
		
		return generatedTemplates;
	}
	
}
