package main.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Component {

	/**
	 * name parameter is used as "key", in the LinkedHashMap of Components
	 */
	private String name;
	private String derived_from;
	private String version;
	private String type_version;
	private String description;
	private LinkedHashMap<String, Property> properties;
	
	public Component() {}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Component(String name, String derived_from, String description) {
		super();
		this.name = name;
		this.derived_from = derived_from;
		this.description = description;
		this.properties = new LinkedHashMap();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDerived_from() {
		return derived_from;
	}
	public void setDerived_from(String derived_from) {
		this.derived_from = derived_from;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType_version() {
		return type_version;
	}
	public void setType_version(String type_version) {
		this.type_version = type_version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LinkedHashMap<String, Property> getProperties() {
		return properties;
	}
	public void setProperties(LinkedHashMap<String, Property> properties) {
		this.properties = properties;
	}
	
	public void addProperties(Property property) {
		this.properties.put(property.getName(), property);
	}
	
	public ArrayList<String> propertiesNames() {
		ArrayList<String> names = new ArrayList();
		
		properties.keySet().forEach(value-> names.add(value));
		
		return names;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + ": " + description + ", version: " + version +
				", nb de properties: " + properties.size() + "\n" + propertiesNames();
	}
	

}
