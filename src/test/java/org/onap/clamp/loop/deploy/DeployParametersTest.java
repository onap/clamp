/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Modifications copyright (c) 2019 Nokia
 * Modifications Copyright (c) 2019 Samsung
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

package org.onap.clamp.loop.deploy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.clamp.clds.sdc.controller.installer.BlueprintArtifact;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.clamp.loop.Loop;

import org.onap.clamp.policy.microservice.MicroServicePolicy;

import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;

import com.google.gson.JsonObject;


public class DeployParametersTest {
    private static final String INVARIANT_SERVICE_UUID = "4cc5b45a-1f63-4194-8100-cd8e14248c92";
    private static final String INVARIANT_RESOURCE1_UUID = "07e266fc-49ab-4cd7-8378-ca4676f1b9ec";
    private static final String INVARIANT_RESOURCE2_UUID = "023a3f0d-1161-45ff-b4cf-8918a8ccf3ad";
    private static final String RESOURCE_INSTANCE_NAME_RESOURCE1 = "ResourceInstanceName1";
    private static final String RESOURCE_INSTANCE_NAME_RESOURCE2 = "ResourceInstanceName2";

    private BlueprintArtifact buildFakeBuildprintArtifact(String blueprintFilePath) throws IOException {
        BlueprintArtifact blueprintArtifact = Mockito.mock(BlueprintArtifact.class);
        Mockito.when(blueprintArtifact.getDcaeBlueprint())
                .thenReturn(ResourceFileUtil.getResourceAsString(blueprintFilePath));
        return blueprintArtifact;
    }

    private HashSet<BlueprintArtifact> buildFakeCsarHandler() throws IOException, SdcToscaParserException {

        HashSet<BlueprintArtifact> blueprintSet = new HashSet<BlueprintArtifact>();

        BlueprintArtifact blueprintArtifact = buildFakeBuildprintArtifact("example/sdc/blueprint-dcae/tca.yaml");

        blueprintSet.add(blueprintArtifact);
        // Create fake blueprint artifact 2 on resource2
        blueprintArtifact = buildFakeBuildprintArtifact("example/sdc/blueprint-dcae/tca_2.yaml");
        blueprintSet.add(blueprintArtifact);

        // Create fake blueprint artifact 3 on resource 1 so that it's possible to
        // test multiple CL deployment per Service/vnf
        blueprintArtifact = buildFakeBuildprintArtifact("example/sdc/blueprint-dcae/tca_3.yaml");
        blueprintSet.add(blueprintArtifact);
        return blueprintSet;
    }

    @Test
    public void getDeploymentParametersinJsonTest() throws IOException, SdcToscaParserException {
        Loop loop = Mockito.mock(Loop.class);
        MicroServicePolicy umService = Mockito.mock(MicroServicePolicy.class);
        HashSet<MicroServicePolicy> umServiceSet = new HashSet<MicroServicePolicy>();
        Mockito.when(umService.getName()).thenReturn("testName");
        umServiceSet.add(umService);
        Mockito.when(loop.getMicroServicePolicies()).thenReturn(umServiceSet);

        DeployParameters deployParams = new DeployParameters(buildFakeCsarHandler(), loop);
        JsonObject paramJson = deployParams.getDeploymentParametersinJson();
        System.out.println("value is:"+ paramJson);
        Assert.assertEquals(JsonUtils.GSON_JPA_MODEL.toJson(paramJson), 
            ResourceFileUtil.getResourceAsString("example/sdc/expected-result/deployment-parameters.json"));
    }
}
