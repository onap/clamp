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
import java.util.List;
import junit.framework.TestCase;
import org.onap.clamp.clds.util.ResourceFileUtil;

public class ToscaTemplateManagerTest extends TestCase {

    /**
     * Test the launch translation wit operational policies.
     *
     * @throws IOException               In case of failure
     * @throws UnknownComponentException In case of failure
     */
    public void testLaunchTranslationTca() throws IOException, UnknownComponentException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("http-cache/example/policy/api/v1/policytypes/onap"
                                + ".policies.monitoring.cdap.tca.hi.lo.app/versions/1.0.0&#63;"
                                + "connectionTimeToLive=5000/.file"), ResourceFileUtil.getResourceAsString(
                        "clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        assertNull(toscaTemplateManager.getParseToJson());
        String componentName = "onap.policies.monitoring.cdap.tca.hi.lo.app";
        toscaTemplateManager.launchTranslation(componentName);
        assertNotNull(toscaTemplateManager.getParseToJson());
    }

    /**
     * Test the launch translation wit operational policies.
     *
     * @throws IOException               In case of failure
     * @throws UnknownComponentException In case of failure
     */
    public void testLaunchTranslationFrequencyLimiter() throws IOException, UnknownComponentException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("http-cache/example/policy/api/v1/policytypes/onap"
                                + ".policies.controlloop.guard.common.FrequencyLimiter/versions/1.0.0&#63;"
                                + "connectionTimeToLive=5000/.file"), ResourceFileUtil.getResourceAsString(
                        "clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        assertNull(toscaTemplateManager.getParseToJson());
        String componentName = "onap.policies.controlloop.guard.common.FrequencyLimiter";
        toscaTemplateManager.launchTranslation(componentName);
        assertNotNull(toscaTemplateManager.getParseToJson());
    }

    /**
     * Test the launch translation wit operational policies.
     *
     * @throws IOException               In case of failure
     * @throws UnknownComponentException In case of failure
     */
    public void testLaunchTranslationApex() throws IOException, UnknownComponentException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("http-cache/example/policy/api/v1/policytypes/onap"
                                + ".policies.controlloop.operational.common.Apex/versions/1.0.0&#63;"
                                + "connectionTimeToLive=5000/.file"), ResourceFileUtil.getResourceAsString(
                        "clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        assertNull(toscaTemplateManager.getParseToJson());
        String componentName = "onap.policies.controlloop.operational.common.Apex";
        toscaTemplateManager.launchTranslation(componentName);
        assertNotNull(toscaTemplateManager.getParseToJson());
    }

    /**
     * Test the launch translation wit operational policies.
     *
     * @throws IOException               In case of failure
     * @throws UnknownComponentException In case of failure
     */
    public void testLaunchTranslationDrools() throws IOException, UnknownComponentException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("http-cache/example/policy/api/v1/policytypes/onap"
                                + ".policies.controlloop.operational.common.Drools/versions/1.0.0&#63;"
                                + "connectionTimeToLive=5000/.file"), ResourceFileUtil.getResourceAsString(
                        "clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        assertNull(toscaTemplateManager.getParseToJson());
        String componentName = "onap.policies.controlloop.operational.common.Drools";
        toscaTemplateManager.launchTranslation(componentName);
        assertNotNull(toscaTemplateManager.getParseToJson());
    }

    /**
     * Test the launch translation.
     *
     * @throws IOException               In case of failure
     * @throws UnknownComponentException In case of failure
     */
    public void testLaunchTranslation() throws IOException, UnknownComponentException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("tosca/new-converter/sampleOperationalPolicies.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        assertNull(toscaTemplateManager.getParseToJson());
        String componentName = "onap.policies.controlloop.operational.common.Drools";
        toscaTemplateManager.launchTranslation(componentName);
        assertNotNull(toscaTemplateManager.getParseToJson());
    }

    /**
     * Test addTemplate.
     *
     * @throws IOException In case of failure
     */
    public void testAddTemplate() throws IOException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("tosca/new-converter/sampleOperationalPolicies.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        int count = toscaTemplateManager.getTemplates().size();
        List<Field> templateFields = new ArrayList<>(Arrays.asList(new Field("type"), new Field("description"),
                new Field(
                "required"),
                new Field("metadata"), new Field("constraints")));
        toscaTemplateManager.addTemplate("test", templateFields);
        assertNotSame(count, toscaTemplateManager.getTemplates().size());
    }

    /**
     * test Remove template.
     *
     * @throws IOException In case of failure
     */
    public void testRemoveTemplate() throws IOException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("tosca/new-converter/sampleOperationalPolicies.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        int count = toscaTemplateManager.getTemplates().size();
        toscaTemplateManager.removeTemplate("string");
        assertNotSame(count, toscaTemplateManager.getTemplates().size());
    }

    /**
     * Test update template.
     *
     * @throws IOException In case of failure
     */
    public void testUpdateTemplate() throws IOException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("tosca/new-converter/sampleOperationalPolicies.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        int count = toscaTemplateManager.getTemplates().get("integer").getFields().size();
        toscaTemplateManager.updateTemplate("integer", new Field("type"), false);
        assertNotSame(count, toscaTemplateManager.getTemplates().get("integer").getFields().size());
    }

    /**
     * Test has template.
     *
     * @throws IOException In case of failure
     */
    public void testHasTemplate() throws IOException {
        ToscaTemplateManager toscaTemplateManager =
                new ToscaTemplateManager(
                        ResourceFileUtil.getResourceAsString("tosca/new-converter/sampleOperationalPolicies.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/defaultToscaTypes.yaml"),
                        ResourceFileUtil.getResourceAsString("clds/tosca_update/templates.json"));
        boolean has = true;
        List<Field> templateFieldsString =
                new ArrayList<>(Arrays.asList(new Field("type"), new Field("description"), new Field("required"),
                        new Field("metadata"), new Field("constraints")));
        Template templateTest = new Template("String", templateFieldsString);
        has = toscaTemplateManager.hasTemplate(templateTest);
        assertEquals(false, has);
    }

}
