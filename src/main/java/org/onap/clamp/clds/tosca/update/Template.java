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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Template {
	
	
	/**
	 * name parameter is used as "key", in the LinkedHashMap of Templates
	 */
	private String name;
	private ArrayList<String> fields;
	
	
	public Template(String name) {
		
		this.name = name;
		this.fields = new ArrayList<String>();
	}
	
	public Template(String name, ArrayList<String> fields) {
		
		this.name = name;
		this.fields = fields;
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getFields() {
		return fields;
	}
	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
	
	public boolean hasFields(String name) {
		
		return fields.contains(name);
	}
	
	public void addField(String field) {
		fields.add(field);
	}
	
	public void removeField(String field) {
		fields.remove(field);
	}
	
	public String toString() {
		return " fields : " + fields;
	}

}
