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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import junit.framework.TestCase;

public class ParserToJSONTest extends TestCase {

	public void testSpecialFields() throws FileNotFoundException, IOException, UnknownComponentException {

		String componentFilePath = "final.yaml";
		String templateFilePath = "templates.properties";
		TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
		JsonObject temporary = templateManagment.launchTranslation("onap.datatype.controlloop.Actor");
		JsonObject value = getMetadataValue(temporary, "onap.datatype.controlloop.Actor", "target");
		JsonObject standard = new JsonObject();
		standard.addProperty("clamp_possible_values", "some special treatment");
		assertEquals(standard, value );

	}

	public void testCheckJson() throws FileNotFoundException, IOException, JSONException, UnknownComponentException {

		String filename = "rendu.json";
		JsonParser parser = new JsonParser();
		JsonObject awaited = (JsonObject) parser.parse(new FileReader(filename));
		String awaitedAsString = awaited.toString();

		String componentFilePath = "final.yaml";
		String templateFilePath = "templates.properties";
		TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
		JsonObject result = templateManagment.launchTranslation("onap.policies.controlloop.operational.common.Drools");
		String resultAsString = result.toString();


		JSONAssert.assertEquals(awaitedAsString, resultAsString, true);

	}

	private JsonObject getMetadataValue(JsonObject toTest, String componentName, String propertyName) {
		JsonObject policy = toTest.getAsJsonObject(componentName);
		JsonObject properties = policy.getAsJsonObject("properties");
		JsonObject operation = properties.getAsJsonObject(propertyName);
		JsonObject metadata = operation.getAsJsonObject("metadata");
		return metadata;

	}


}
