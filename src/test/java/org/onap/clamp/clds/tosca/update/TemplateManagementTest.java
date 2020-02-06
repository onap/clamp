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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

public class TemplateManagementTest extends TestCase {

	public void testLaunchTranslation() throws IOException, UnknownComponentException {
        String componentFilePath = "sampleOperationalPolicies.yaml";
        String templateFilePath = "templates.properties";
        TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
        assertNull(templateManagment.getParseToJSON());
        String componentName = "onap.policies.controlloop.operational.common.Drools";
        templateManagment.launchTranslation(componentName);
        assertNotNull(templateManagment.getParseToJSON());
    }

    public void testAddTemplate() throws IOException {
        String componentFilePath = "sampleOperationalPolicies.yaml";
        String templateFilePath = "templates.properties";
        TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
        int count = templateManagment.getTemplates().size();
        ArrayList<String> templateFields = new ArrayList<>(Arrays.asList("type","description","required","metadata","constraints"));
        templateManagment.addTemplate("test", templateFields);
        assertNotSame(count, templateManagment.getTemplates().size());
    }

    public void testRemoveTemplate() throws IOException {
        String componentFilePath = "sampleOperationalPolicies.yaml";
        String templateFilePath = "templates.properties";
        TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
        int count = templateManagment.getTemplates().size();
        templateManagment.removeTemplate("string");
        assertNotSame(count, templateManagment.getTemplates().size());
    }

    public void testUpdateTemplate() throws IOException {
        String componentFilePath = "sampleOperationalPolicies.yaml";
        String templateFilePath = "templates.properties";
        TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
        int count = templateManagment.getTemplates().get("integer").getFields().size();
        templateManagment.updateTemplate("integer", "type" , false);
        assertNotSame(count, templateManagment.getTemplates().get("integer").getFields().size());
    }

    public void testHasTemplate() throws IOException {
        String componentFilePath = "sampleOperationalPolicies.yaml";
        String templateFilePath = "templates.properties";
        TemplateManagement templateManagment = new TemplateManagement(componentFilePath,templateFilePath);
        boolean has = true;
        ArrayList<String> templateFieldsString = new ArrayList<>(Arrays.asList("type","description","required","metadata","constraints"));
        Template templateTest = new Template("String", templateFieldsString);
        has = templateManagment.hasTemplate(templateTest);
        assertEquals(false, has);
    }

}
