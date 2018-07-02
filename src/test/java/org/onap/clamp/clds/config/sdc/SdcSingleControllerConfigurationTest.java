/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 * Modifications copyright (c) 2018 Nokia
 * ================================================================================
 *
 */

package org.onap.clamp.clds.config.sdc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.junit.Test;
import org.onap.clamp.clds.exception.sdc.controller.SdcParametersException;
import org.onap.clamp.clds.util.ResourceFileUtil;

/**
 * This class tests the SDC Controller config.
 */
public class SdcSingleControllerConfigurationTest {

    private SdcSingleControllerConfiguration loadControllerConfiguration(String fileName, String sdcControllerName)
            throws IOException {
        JsonNode jsonNode = new ObjectMapper().readValue(ResourceFileUtil.getResourceAsStream(fileName),
                JsonNode.class);
        SdcSingleControllerConfiguration sdcSingleControllerConfiguration = new SdcSingleControllerConfiguration(
                jsonNode, sdcControllerName);
        return sdcSingleControllerConfiguration;
    }

    @Test
    public final void testTheInit() throws SdcParametersException, IOException {
        SdcSingleControllerConfiguration sdcConfig = loadControllerConfiguration("clds/sdc-controller-config-TLS.json",
                "sdc-controller1");
        assertEquals("User", sdcConfig.getUser());
        assertEquals("ThePassword", sdcConfig.getPassword());
        assertEquals("consumerGroup", sdcConfig.getConsumerGroup());
        assertEquals("consumerId", sdcConfig.getConsumerID());
        assertEquals("environmentName", sdcConfig.getEnvironmentName());
        assertEquals("hostname:8080", sdcConfig.getAsdcAddress());
        assertEquals(10, sdcConfig.getPollingInterval());
        assertEquals(30, sdcConfig.getPollingTimeout());

        assertThat(SdcSingleControllerConfiguration.SUPPORTED_ARTIFACT_TYPES_LIST)
            .hasSameSizeAs(sdcConfig.getRelevantArtifactTypes());
        assertEquals("ThePassword", sdcConfig.getKeyStorePassword());
        assertTrue(sdcConfig.activateServerTLSAuth());
        assertThat(sdcConfig.getMsgBusAddress()).contains("localhost");
    }

    @Test(expected = SdcParametersException.class)
    public final void testAllRequiredParameters() throws IOException {
        SdcSingleControllerConfiguration sdcConfig = loadControllerConfiguration("clds/sdc-controller-config-TLS.json",
                "sdc-controller1");
        // No exception should be raised
        sdcConfig.testAllRequiredParameters();
        sdcConfig = loadControllerConfiguration("clds/sdc-controller-config-bad.json", "sdc-controller1");
        fail("Should have raised an exception");
    }

    @Test
    public final void testAllRequiredParametersEmptyEncrypted()
            throws IOException {
        SdcSingleControllerConfiguration sdcConfig = loadControllerConfiguration(
                "clds/sdc-controller-config-empty-encrypted.json", "sdc-controller1");
        sdcConfig.testAllRequiredParameters();
        assertNull(sdcConfig.getKeyStorePassword());
    }

    @Test
    public final void testConsumerGroupWithNull() throws IOException {
        SdcSingleControllerConfiguration sdcConfig = loadControllerConfiguration("clds/sdc-controller-config-NULL.json",
                "sdc-controller1");
        assertTrue(sdcConfig.getConsumerGroup() == null);
    }
}
