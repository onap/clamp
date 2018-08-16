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
 * Modifications copyright (c) 2018 Nokia
 * ===================================================================
 *
 */

package org.onap.clamp.clds.client.req.policy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.model.properties.ModelProperties;
import org.onap.clamp.clds.model.properties.PolicyChain;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.policy.api.AttributeType;
import org.onap.policy.controlloop.policy.ControlLoopPolicy;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.controlloop.policy.Target;
import org.onap.policy.controlloop.policy.TargetType;
import org.onap.policy.controlloop.policy.builder.BuilderException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationalPolicyRequestAttributesConstructorTest {

    private static final String CONTROL_NAME = "ClosedLoop-d4629aee-970f-11e8-86c9-02552dda865e";
    private ModelProperties modelProperties;
    private PolicyChain policyChain;

    private OperationalPolicyYamlFormatter operationalPolicyYamlFormatter = new OperationalPolicyYamlFormatter();
    private OperationalPolicyRequestAttributesConstructor operationalPolicyRequestAttributesConstructor = new OperationalPolicyRequestAttributesConstructor(operationalPolicyYamlFormatter);

    @Before
    public void setUp() throws Exception {
        String modelProp = ResourceFileUtil.getResourceAsString("example/model-properties/policy/modelBpmnProperties.json");
        modelProperties = new ModelProperties("CLAMPDemoVFW_v1_0_3af8daec-6f10-4027-a3540",
                CONTROL_NAME, "PUT", false, "{}", modelProp);
        policyChain = readPolicyChainFromResources();
    }


    @Test
    public void shouldFormatRequestAttributes() throws IOException, BuilderException {
        // given
        ClampProperties mockClampProperties = createMockClampProperties(ImmutableMap.<String, String>builder()
                .put("op.templateName", "ClosedLoopControlName")
                .put("op.notificationTopic", "POLICY-CL-MGT")
                .put("op.controller", "amsterdam")
                .put("op.recipeTopic", "APPC")
                .build());

        //when
        Map<AttributeType, Map<String, String>> requestAttributes
                = operationalPolicyRequestAttributesConstructor.formatAttributes(mockClampProperties, modelProperties,
                "789875c1-e788-432f-9a76-eac8ed889734", policyChain);
        //then
        assertThat(requestAttributes).containsKeys(AttributeType.MATCHING, AttributeType.RULE);
        assertThat(requestAttributes.get(AttributeType.MATCHING)).contains(entry(OperationalPolicyRequestAttributesConstructor.CONTROLLER, "amsterdam"));

        Map<String, String> ruleParameters = requestAttributes.get(AttributeType.RULE);
        assertThat(ruleParameters).containsExactly(
                entry(OperationalPolicyRequestAttributesConstructor.MAX_RETRIES, "3"),
                entry(OperationalPolicyRequestAttributesConstructor.TEMPLATE_NAME, "ClosedLoopControlName"),
                entry(OperationalPolicyRequestAttributesConstructor.NOTIFICATION_TOPIC, "POLICY-CL-MGT"),
                entry(OperationalPolicyRequestAttributesConstructor.RECIPE_TOPIC, "APPC"),
                entry(OperationalPolicyRequestAttributesConstructor.RECIPE, "healthCheck"),
                entry(OperationalPolicyRequestAttributesConstructor.RESOURCE_ID, "cdb69724-57d5-4a22-b96c-4c345150fd0e"),
                entry(OperationalPolicyRequestAttributesConstructor.RETRY_TIME_LIMIT, "180"),
                entry(OperationalPolicyRequestAttributesConstructor.CLOSED_LOOP_CONTROL_NAME, CONTROL_NAME + "_1")
        );
    }

    @Test
    public void shouldFormatRequestAttributesWithProperControlLoopYaml() throws IOException, BuilderException {
        //given
        ClampProperties mockClampProperties = createMockClampProperties(ImmutableMap.<String, String>builder()
                .put("op.templateName", "ClosedLoopControlName")
                .put("op.operationTopic", "APPP-CL")
                .put("op.notificationTopic", "POLICY-CL-MGT")
                .put("op.controller", "amsterdam")
                .put("op.recipeTopic", "APPC")
                .build());

        Policy expectedPolicy = new Policy("6f76ad0b-ea9d-4a92-8d7d-6a6367ce2c77", "healthCheck Policy",
                "healthCheck Policy - the trigger (no parent) policy - created by CLDS", "APPC",
                null, new Target(TargetType.VM, "cdb69724-57d5-4a22-b96c-4c345150fd0e"),
                "healthCheck", 3, 180);

        //when
        Map<AttributeType, Map<String, String>> requestAttributes = operationalPolicyRequestAttributesConstructor.formatAttributes(mockClampProperties, modelProperties,
                "789875c1-e788-432f-9a76-eac8ed889734", policyChain);

        //then
        assertThat(requestAttributes).containsKeys(AttributeType.MATCHING, AttributeType.RULE);
        assertThat(requestAttributes.get(AttributeType.MATCHING)).contains(entry("controller", "amsterdam"));

        Map<String, String> ruleParameters = requestAttributes.get(AttributeType.RULE);
        assertThat(ruleParameters).contains(
                entry(OperationalPolicyRequestAttributesConstructor.OPERATION_TOPIC, "APPP-CL"),
                entry(OperationalPolicyRequestAttributesConstructor.TEMPLATE_NAME, "ClosedLoopControlName"),
                entry(OperationalPolicyRequestAttributesConstructor.NOTIFICATION_TOPIC, "POLICY-CL-MGT"),
                entry(OperationalPolicyRequestAttributesConstructor.CLOSED_LOOP_CONTROL_NAME, CONTROL_NAME + "_1")
        );

        String controlLoopYaml = URLDecoder.decode(ruleParameters.get(OperationalPolicyRequestAttributesConstructor.CONTROL_LOOP_YAML), "UTF-8");
        ControlLoopPolicy controlLoopPolicy = new Yaml().load(controlLoopYaml);

        assertThat(controlLoopPolicy.getControlLoop().getControlLoopName()).isEqualTo(CONTROL_NAME);
        assertThat(controlLoopPolicy.getPolicies())
                .usingElementComparatorIgnoringFields("id")
                .containsExactly(expectedPolicy);
    }


    private ClampProperties createMockClampProperties(ImmutableMap<String, String> propertiesMap) {
        ClampProperties props = mock(ClampProperties.class);
        propertiesMap.forEach((property, value) ->
                when(props.getStringValue(matches(property), any())).thenReturn(value)
        );
        return props;
    }

    private PolicyChain readPolicyChainFromResources() throws IOException {
        String policyChainText = ResourceFileUtil.getResourceAsString("example/operational-policy/json-policy-chain.json");
        JsonNode policyChainNode = new ObjectMapper().readTree(policyChainText);
        return new PolicyChain(policyChainNode);
    }
}
