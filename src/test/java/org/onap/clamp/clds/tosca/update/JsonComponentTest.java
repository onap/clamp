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
import java.io.IOException;

import junit.framework.TestCase;
import org.onap.clamp.clds.tosca.update.JsonComponent;
import org.onap.clamp.clds.tosca.update.JsonProperty;
import org.onap.clamp.clds.tosca.update.TemplateManagment;

public class JsonComponentTest extends TestCase {

	public void testHasMetadata() throws FileNotFoundException, IOException {
		String file = "base.yaml";
		String name = "onap.datatype.controlloop.Actor";
		TemplateManagment templateManagment = new TemplateManagment(file);
		JsonComponent jc = new JsonComponent(name, templateManagment.launchTranslation(name));
		JsonProperty propertyTest = jc.getJsonProperty().get("target");
		assertTrue(jc.containsMetadata(propertyTest));
	}
	
	public void testGetValueMetadata() throws FileNotFoundException, IOException {
		String file = "base.yaml";
		String name = "onap.datatype.controlloop.Actor";
		String valueMetadata = "<string:see clamp project for syntax>";
		TemplateManagment templateManagment = new TemplateManagment(file);
		JsonComponent jc = new JsonComponent(name, templateManagment.launchTranslation(name));
		JsonProperty propertyTest = jc.getJsonProperty().get("target");
		assertEquals(valueMetadata, propertyTest.getMetadaValue());
	}
	
}
