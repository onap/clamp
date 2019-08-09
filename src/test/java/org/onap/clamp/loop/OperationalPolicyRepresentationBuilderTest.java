/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import org.junit.Test;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.clamp.policy.operational.OperationalPolicyRepresentationBuilder;

public class OperationalPolicyRepresentationBuilderTest {

    @Test
    public void testOperationalPolicyPayloadConstruction() throws IOException {
        JsonObject jsonModel = new GsonBuilder().create()
                .fromJson(ResourceFileUtil.getResourceAsString("tosca/model-properties.json"), JsonObject.class);

        Loop loop = new Loop();
        loop.setName("TestLoop");
        loop.setModelPropertiesJson(jsonModel);
        JsonObject jsonSchema = OperationalPolicyRepresentationBuilder.generateJsonRepresentation(loop);

        assertThat(jsonSchema).isNotNull();
        /*
         * assertThat(policy.createPolicyPayloadYaml())
         * .isEqualTo(ResourceFileUtil.getResourceAsString(
         * "tosca/operational-policy-payload.yaml"));
         * 
         * assertThat(policy.createPolicyPayload())
         * .isEqualTo(ResourceFileUtil.getResourceAsString(
         * "tosca/operational-policy-payload.json"));
         */
    }

}
