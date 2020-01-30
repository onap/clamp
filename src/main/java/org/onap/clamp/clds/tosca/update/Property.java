package main.java;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
	
//	public String hasMetadataField(String field) {
//		String fieldValue = null;
//		for (Entry<String, Object> fieldProperty : items.entrySet()) {
//			if(fieldProperty.getKey().equals(field)) {
//				fieldValue = (String) fieldProperty.getValue();
//			}
//		}
//		return fieldValue;
//	}
	
	
}
