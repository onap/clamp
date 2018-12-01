/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.tosca;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.skyscreamer.jsonassert.JSONAssert;

public class ToscaYamltoJsonConvertorTest {

    /**
     * This Test validates TOSCA yaml to JSON Schema conversion based on JSON Editor
     * Schema
     *
     * @throws IOException
     *
     */
    @Test
    public final void testParseToscaYaml() throws IOException {
        String toscaModelYaml = "tosca_definitions_version: tosca_simple_yaml_1_0_0\r\nnode_types:\r\n  policy.nodes.cdap.tca.hi.lo.app:\r\n    derived_from: policy.nodes.Root\r\n    properties:\r\n      domain:\r\n        type: string\r\n        description: Domain\r\n        constraints:\r\n        - equal: measurementsForVfScaling\r\n      functionalRole:\r\n        type: string\r\n        description: Function of the event source e.g., vnf1, vnf2, vnf3\r\n      thresholds:\r\n        type: list\r\n        description: Thresholds\r\n        entry_schema:\r\n          type: policy.data.thresholds\r\ndata_types:\r\n  policy.data.thresholds:\r\n    properties:\r\n      closedLoopControlName:\r\n        type: string\r\n        description: A UNIQUE string identifying the Closed Loop ID this event is for.\r\n      direction:\r\n        type: string\r\n        constraints:\r\n        - valid_values: [ LESS, LESS_OR_EQUAL, GREATER, GREATER_OR_EQUAL]\r\n      fieldPath:\r\n        description: Field Path\r\n        type: string\r\n      severity:\r\n        type: string\r\n        description: event severity or priority\r\n        constraints:\r\n        - valid_values: [CRITICAL, MAJOR, MINOR, WARNING, NORMAL]\r\n      thresholdValue:\r\n        type: integer\r\n        description: ThresholdValue\r\n        default: 0\r\n        constraints:\r\n          - in_range: [ 0, 65535 ]\r\n      version:\r\n        type: string\r\n        description: Version for the closed loop message\r\n        constraints:\r\n          - min_length: 1\r\n      dummySignatures:\r\n        type: list\r\n        description: dummy Signatures\r\n        required: true\r\n        entry_schema:\r\n          type: policy.data.dummySignatureTraversal\r\n  policy.data.dummySignatureTraversal:\r\n    derived_from: tosca.nodes.Root\r\n    properties:\r\n      signature:\r\n        type: policy.data.DUMMY_Signature_FM\r\n        required: true\r\n      traversal:\r\n        type: policy.data.traverse\r\n        required: true\r\n  policy.data.traverse:\r\n    derived_from: tosca.nodes.Root\r\n    properties:\r\n      traversal:\r\n        type: string\r\n        description: Dummy Traverse\r\n        required: true\r\n        constraints:\r\n          - valid_values: [ ONE, TWO, THREE ]\r\n  policy.data.DUMMY_Signature_FM:\r\n    derived_from: tosca.nodes.Root\r\n    properties:\r\n      filter_clause:\r\n        type: string\r\n        description: Filter Clause\r\n        required: true\r\n        constraints:\r\n          - valid_values: [ OR, AND, NOT ]\r\n";
        ToscaYamltoJsonConvertor convertor = new ToscaYamltoJsonConvertor(null);

        String parsedJsonSchema = convertor.parseToscaYaml(toscaModelYaml);
        assertNotNull(parsedJsonSchema);
        JSONAssert.assertEquals(
            ResourceFileUtil.getResourceAsString("example/json-editor-schema/tca-policy-json-editor-schema.json"),
            parsedJsonSchema, true);
    }
}
