/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 Nokia Intellectual Property. All rights
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
package org.onap.clamp.clds.client;


import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.clamp.clds.config.ClampProperties;


@RunWith(MockitoJUnitRunner.class)
public class DcaeDispatcherServicesTest {

    private static final String DEPLOYMENT_STATUS_URL = "http://portal.api.simpledemo.onap.org:30297/dcae-deployments/" +
            "closedLoop_c9c8b281-6fbd-4702-ba13-affa90411152_deploymentId/operation/a97b46f6-d77c-42a1-9449-d5ae71e8f688";
    @Mock
    ClampProperties clampProperties;

    @Mock
    DcaeHttpConnectionManager dcaeHttpConnectionManager;

    @InjectMocks
    DcaeDispatcherServices dcaeDispatcherServices;

    private final String STATUS_RESPONSE_PROCESSING = "{\"operationType\": \"deploy\",\"status\": \"processing\"}";
    private final String STATUS_RESPONSE_ACTIVE = "{\"operationType\": \"deploy\",\"status\": \"succeeded\"}";

    @Before
    public void setUp() {
        ImmutableMap.<String, String>builder()
                .put("dcae.dispatcher.retry.limit", "3")
                .put("dcae.dispatcher.retry.interval", "0")
                .build()
                .forEach((property, value) -> {
                    Mockito.when(clampProperties.getStringValue(Matchers.matches(property), Matchers.any())).thenReturn(value);
                    Mockito.when(clampProperties.getStringValue(Matchers.matches(property))).thenReturn(value);
                });
    }

    @Test
    public void shouldReturnDcaeOperationSataus() throws IOException {
        //given
        Mockito.when(dcaeHttpConnectionManager.doDcaeHttpQuery(DEPLOYMENT_STATUS_URL, "GET", null, null))
                .thenReturn(STATUS_RESPONSE_PROCESSING);
        //when
        String operationStatus = dcaeDispatcherServices.getOperationStatus(DEPLOYMENT_STATUS_URL);

        //then
        Assertions.assertThat(operationStatus).isEqualTo("processing");
    }

    @Test
    public void shouldTryMultipleTimesWhenProcessing() throws IOException, InterruptedException {
        //given
        Mockito.when(dcaeHttpConnectionManager.doDcaeHttpQuery(DEPLOYMENT_STATUS_URL, "GET", null, null))
                .thenReturn(STATUS_RESPONSE_PROCESSING, STATUS_RESPONSE_PROCESSING, STATUS_RESPONSE_ACTIVE);
        //when
        String operationStatus = dcaeDispatcherServices.getOperationStatusWithRetry(DEPLOYMENT_STATUS_URL);

        //then
        Assertions.assertThat(operationStatus).isEqualTo("succeeded");
        Mockito.verify(dcaeHttpConnectionManager, Mockito.times(3))
                .doDcaeHttpQuery(DEPLOYMENT_STATUS_URL, "GET", null, null);

    }

    @Test
    public void shouldTryOnlyAsManyTimesAsConfigured() throws IOException, InterruptedException {
        //given
        Mockito.when(dcaeHttpConnectionManager
                .doDcaeHttpQuery(DEPLOYMENT_STATUS_URL, "GET", null, null))
                .thenReturn(STATUS_RESPONSE_PROCESSING, STATUS_RESPONSE_PROCESSING, STATUS_RESPONSE_PROCESSING,
                        STATUS_RESPONSE_PROCESSING, STATUS_RESPONSE_PROCESSING);
        //when
        String operationStatus = dcaeDispatcherServices.getOperationStatusWithRetry(DEPLOYMENT_STATUS_URL);

        //then
        Assertions.assertThat(operationStatus).isEqualTo("processing");
        Mockito.verify(dcaeHttpConnectionManager, Mockito.times(3))
                .doDcaeHttpQuery(DEPLOYMENT_STATUS_URL, "GET", null, null);

    }
}