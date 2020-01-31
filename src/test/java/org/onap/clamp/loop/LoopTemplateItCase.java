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

package org.onap.clamp.loop;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.Application;
import org.onap.clamp.loop.template.LoopElementModel;
import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.loop.template.LoopTemplatesRepository;
import org.onap.clamp.loop.template.LoopTemplatesService;
import org.onap.clamp.loop.template.PolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class LoopTemplateItCase {

    @Autowired
    LoopTemplatesService loopTemplatesService;

    @Autowired
    LoopTemplatesRepository loopTemplatesRepository;
    
    private final static String OPEN_LOOP_TYPE = "open";
    private static final String POLICY_MODEL_TYPE_1 = "org.onap.test";
    private static final String VERSION = "1.0.0";
    
    private LoopElementModel getLoopElementModel(String yaml, String name, String loopElementType, String createdBy,
            PolicyModel policyModel) {
        LoopElementModel model = new LoopElementModel(name, loopElementType, yaml);
        model.addPolicyModel(policyModel);
        return model;
    }

    private PolicyModel getPolicyModel(String policyType, String policyModelTosca, String version, String policyAcronym,
            String createdBy) {
        return new PolicyModel(policyType, policyModelTosca, version, policyAcronym);
    }

    private LoopTemplate getLoopTemplate(String name, String blueprint, String svgRepresentation, String createdBy,
            Integer maxInstancesAllowed) {
        LoopTemplate template = new LoopTemplate(name, blueprint, svgRepresentation, maxInstancesAllowed, null);
        template.addLoopElementModel(getLoopElementModel("yaml", "microService1", "MicroService", createdBy,
                getPolicyModel(POLICY_MODEL_TYPE_1, "yaml", VERSION, "MS1", createdBy)));
        template.setAllowedLoopType(OPEN_LOOP_TYPE);
        return template;
    }
    
    /**
     * Test to validate the Loop Template is created successfully
     * 
     */
    @Test
    @Transactional
    public void shouldSaveOrUpdateLoopTemplate() {
        LoopTemplate loopTemplate = getLoopTemplate("TemplateName", null, "svg", "xyz", -1);
        LoopTemplate actualLoopTemplate = loopTemplatesService.saveOrUpdateLoopTemplate(loopTemplate);
        
        assertNotNull(actualLoopTemplate);
        assertEquals(loopTemplate.getName(), loopTemplate.getName());
    }
    
    /**
     * Test to validate all loop templates are returned
     */
    @Test
    @Transactional
    public void shouldReturnAllLoopemplates() {
        LoopTemplate loopTemplate = getLoopTemplate("TemplateName", null, "svg", "xyz", -1);
        loopTemplatesService.saveOrUpdateLoopTemplate(loopTemplate);
        List<LoopTemplate> LoopTemplateList = loopTemplatesService.getAllLoopTemplates();
        
        assertNotNull(LoopTemplateList);
    }
    
    /**
     * Test to validate all Loop Template Names are returned
     */
    @Test
    @Transactional
    public void shouldReturnLoopemplateNames() {
        LoopTemplate loopTemplate = getLoopTemplate("TemplateName", null, "svg", "xyz", -1);
        loopTemplatesService.saveOrUpdateLoopTemplate(loopTemplate);
        List<String> LoopTemplateNames = loopTemplatesService.getLoopTemplateNames();
        
        assertNotNull(LoopTemplateNames);
        assertEquals("TemplateName", LoopTemplateNames.get(0));
    }
    
    /**
     * Test to validate Loop Template is retrieved by name
     */
    @Test
    @Transactional
    public void shouldReturnLoopemplate() {
        LoopTemplate loopTemplate = getLoopTemplate("TemplateName", null, "svg", "xyz", -1);
        loopTemplatesService.saveOrUpdateLoopTemplate(loopTemplate);
        LoopTemplate actualLoopTemplate = loopTemplatesService.getLoopTemplate("TemplateName");
        
        assertNotNull(actualLoopTemplate);
        assertEquals(loopTemplate.getName(), loopTemplate.getName());
    }
    
    /**
     * Test to validate the Loop Template is deleted successfully
     */
    @Test
    @Transactional
    public void shouldDeleteLoopemplate() {
        LoopTemplate loopTemplate = getLoopTemplate("TemplateName", null, "svg", "xyz", -1);
        loopTemplatesService.saveOrUpdateLoopTemplate(loopTemplate);
        loopTemplatesService.deleteLoopTemplate("TemplateName");
        LoopTemplate actualLoopTemplate = loopTemplatesService.getLoopTemplate("TemplateName");
        assertNull(actualLoopTemplate);
    }
    
}
