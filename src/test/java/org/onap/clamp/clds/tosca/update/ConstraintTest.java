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

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import junit.framework.TestCase;

public class ConstraintTest extends TestCase {

    TemplateManagement templateManagement = new TemplateManagement("constraints.yaml", "templates.properties");
    Component component = templateManagement.getComponents().get("onap.datatype.controlloop.Operation");

    public ConstraintTest() throws IOException {
    }

    public void testGetValuesArray() {
        Property property = component.getProperties().get("timeout");
        Template template = templateManagement.getTemplates().get("integer");
        JsonObject resultProcess = new JsonObject();
        property.addConstraintsAsJson(resultProcess, (ArrayList<Object>) property.getItems().get("constraints"),
                template);
        String reference = "{\"enum\":[3,4,5.5,6,10]}";
        assertEquals(reference, String.valueOf(resultProcess));
        property = component.getProperties().get("success");
        template = templateManagement.getTemplates().get("string");
        resultProcess = new JsonObject();
        property.addConstraintsAsJson(resultProcess, (ArrayList<Object>) property.getItems().get("constraints"),
                template);
        reference = "{\"enum\":[\"VALID\",\"TERMINATED\"]}";
        assertEquals(reference, String.valueOf(resultProcess));
    }

    public void testGetSpecificLength() {
        //Test for string type, same process for array
        Property property = component.getProperties().get("id");
        Template template = templateManagement.getTemplates().get("string");
        JsonObject resultProcess = new JsonObject();
        property.addConstraintsAsJson(resultProcess, (ArrayList<Object>) property.getItems().get("constraints"),
                template);
        int specificLength = 8;
        int toTest = resultProcess.get("minLength").getAsInt();
        assertEquals(specificLength, toTest);
        toTest = resultProcess.get("maxLength").getAsInt();
        assertEquals(specificLength, toTest);
    }

    public void testGetLimitValue() {
        //Test for array type, same process for string
        Property property = component.getProperties().get("description");
        Template template = templateManagement.getTemplates().get("array");
        JsonObject resultProcess = new JsonObject();
        property.addConstraintsAsJson(resultProcess, (ArrayList<Object>) property.getItems().get("constraints"),
                template);
        int minItems = 5, maxItems = 7;
        int toTest = resultProcess.get("minItems").getAsInt();
        assertEquals(minItems, toTest);
        toTest = resultProcess.get("maxItems").getAsInt();
        assertEquals(maxItems, toTest);
    }

}