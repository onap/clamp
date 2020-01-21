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

package org.onap.clamp.policy.microservice;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.skyscreamer.jsonassert.JSONAssert;

public class MicroServicePayloadTest {

    @Test
    public void testPayloadConstruction() throws IOException {
        MicroServicePolicy policy = new MicroServicePolicy("testPolicy", "onap.policies.monitoring.cdap.tca.hi.lo.app",
            ResourceFileUtil.getResourceAsString("tosca/tosca_example.yaml"), false, new HashSet<>());
        policy.setConfigurationsJson(JsonUtils.GSON.fromJson(
            ResourceFileUtil.getResourceAsString("tosca/micro-service-policy-properties.json"), JsonObject.class));
        JSONAssert.assertEquals(ResourceFileUtil.getResourceAsString("tosca/micro-service-policy-payload.json"),
            policy.createPolicyPayload(), false);
    }
}
