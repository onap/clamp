/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.Application;
import org.onap.clamp.loop.log.LoopLogService;
import org.onap.clamp.loop.service.LoopTemplateService;
import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.loop.template.LoopTemplatesRepository;
import org.onap.clamp.policy.microservice.MicroServicePolicyService;
import org.onap.clamp.policy.operational.OperationalPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class LoopTemplateServiceTestItCase {

    @Autowired
    LoopTemplateService loopTemplateService;

    @Autowired
    LoopTemplatesRepository loopTemplatesRepository;

    @Autowired
    MicroServicePolicyService microServicePolicyService;

    @Autowired
    OperationalPolicyService operationalPolicyService;

    @Autowired
    LoopLogService loopLogService;

    @Test
    public void testGetLoopTemplateNames() {
              
        List<String> tnames = loopTemplateService.getLoopTemplateNames();      
        // then
        assertThat(tnames).isNotNull();
        assertThat(tnames).size().isGreaterThanOrEqualTo(1);
        
    }
    
    @Test
    
    public void testGetAllLoopTemplates() {
        List<LoopTemplate> loopTemplates = loopTemplateService.getAllLoopTemplates();      
        // then
        assertThat(loopTemplates).isNotNull();
        assertThat(loopTemplates).size().isGreaterThanOrEqualTo(1);
    }
    
   
}