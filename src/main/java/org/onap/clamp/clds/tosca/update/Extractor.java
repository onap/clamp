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
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

public class Extractor {

	private LinkedHashMap<String, Component> allItems = new LinkedHashMap<>();
	private String source;
	@SuppressWarnings("unchecked")
	public Extractor(String toParse) throws IOException {
		this.source = toParse;
		getAllAsMaps();
	}
	public LinkedHashMap<String, Component> getAllItems() {
		return allItems;
	}
	public String getSource() {
		return source;
	}
	/**
	 * @return
	 * @throws IOException
	 * Yaml Parse gets raw policies and datatypes, in different sections : necessary to extract
	 * all entities and put them at the same level
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> getAllAsMaps() throws IOException {
		InputStream input = new FileInputStream(new File(source));
	    Yaml yaml = new Yaml();
	    Object contentFile = yaml.load(input);
	    LinkedHashMap<String, LinkedHashMap<String, Object>> file = (LinkedHashMap<String, LinkedHashMap<String, Object>>) contentFile;
	    // Get DataTypes
	    LinkedHashMap<String, Object> datatypes = file.get("data_types");
	    // Get Policies : first, get topology and after extract policies from it
		LinkedHashMap<String, Object> policy_types = file.get("policy_types");
		// Put the policies and datatypes in the same collection
	    LinkedHashMap<String, Object> allMaps = datatypes;
	    allMaps.putAll(policy_types);
	    parseInComponent(allMaps);
	    return allMaps;
	}
	/**
	 * @param allMaps
	 * @throws FileNotFoundException
	 * @throws IOException
	 * With all the component, get as Map, Components and Components' properties are created
	 */
	@SuppressWarnings("unchecked")
	public void parseInComponent(LinkedHashMap<String, Object> allMaps) throws FileNotFoundException, IOException {
		//Component creations, from the file maps
		for (Entry<String, Object> itemToParse : allMaps.entrySet()) {
			LinkedHashMap<String, Object> componentBody = (LinkedHashMap<String, Object>) itemToParse.getValue();
			Component component = new Component(itemToParse.getKey(),(String) componentBody.get("derived_from"),(String) componentBody.get("description"));
			//If policy, version and type_version :
			if(componentBody.get("type_version") != null) {
				component.setVersion((String) componentBody.get("type_version"));
				component.setType_version((String) componentBody.get("type_version"));
			}
			//Properties creation, from the map
		    if(componentBody.get("properties") != null) {
		    	LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) componentBody.get("properties");
		    	for (Entry<String, Object> itemToProperty : properties.entrySet()) {
		    		Property property = new Property(itemToProperty.getKey(), (LinkedHashMap<String, Object>) itemToProperty.getValue());
		    		component.addProperties(property);
				}
		    }
		    this.allItems.put(component.getName(), component);
		}
	}
}
