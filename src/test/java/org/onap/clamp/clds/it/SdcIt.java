/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.it;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.onap.clamp.clds.AbstractIt;
import org.onap.clamp.clds.client.SdcCatalogServices;
import org.onap.clamp.clds.client.req.SdcReq;
import org.onap.clamp.clds.model.CldsEvent;
import org.onap.clamp.clds.model.prop.ModelProperties;
import org.onap.clamp.clds.model.refprop.RefProp;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test SDC Blueprint formater.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-no-camunda.properties")
public class SdcIt extends AbstractIt {
    @Autowired
    private RefProp refProp;

    @Autowired
    private SdcCatalogServices sdcCatalogServices;

    @BeforeClass
    public static void oneTimeSetUp() {
        System.setProperty("AJSC_CONF_HOME", System.getProperty("user.dir") + "/src/test/resources/");
    }

    @Test
    public void testTcaBlueprint() throws Exception {
        String modelProp = ResourceFileUtil.getResourceAsString("example/modelPropForPolicy.json");
        String modelBpmnProp = ResourceFileUtil.getResourceAsString("example/modelBpmnPropForPolicy.json");
        String modelName = "example-model06";
        String controlName = "ClosedLoop-FRWL-SIG04-1582f840-test-test-1234-005056a9d756";
        String docText = ResourceFileUtil.getResourceAsString("example/templatePropForTca.json");
        ModelProperties prop = new ModelProperties(modelName, controlName, CldsEvent.ACTION_SUBMIT,
                true, modelBpmnProp, modelProp);
        String blueprint = SdcReq.formatBlueprint(refProp, prop, docText);
        System.out.println("blueprint=" + blueprint);
        //assertEquals(blueprint, "");
    }
}
