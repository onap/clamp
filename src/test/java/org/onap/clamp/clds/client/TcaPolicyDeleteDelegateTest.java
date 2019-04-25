/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Samsung. All rights reserved.
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

import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.clamp.clds.client.req.policy.PolicyClient;
import org.onap.clamp.clds.exception.ModelBpmnException;

@RunWith(MockitoJUnitRunner.class)
public class TcaPolicyDeleteDelegateTest {

    private static final String TCA_ID_FROM_JSON = "{\"tca\":[{\"id\":\"\",\"from\":\"\"}]}";
    private static final String HOLMES_ID_FROM_JSON = "{\"holmes\":[{\"id\":\"\",\"from\":\"\"}]}";
    private static final String ID_JSON = "{\"id\":\"\"}";
    private static final String NOT_JSON = "not json";
    private static final String MODEL_BPMN_KEY = "modelBpmnProp";
    private static final String MODEL_PROP_KEY = "modelProp";
    private static final String TEST_KEY = "isTest";
    private static final String PROPERTY_NAME = "tcaPolicyDeleteResponseMessage";
    private static final String MESSAGE = "message";

    @Mock
    private Exchange camelExchange;

    @Mock
    private PolicyClient policyClient;

    @InjectMocks
    private TcaPolicyDeleteDelegate tcaPolicyDeleteDelegate;

    @Test
    public void shouldExecuteSuccessfully() {
        //given
        Mockito.when(camelExchange.getProperty(MODEL_BPMN_KEY)).thenReturn(TCA_ID_FROM_JSON);
        Mockito.when(camelExchange.getProperty(MODEL_PROP_KEY)).thenReturn(ID_JSON);
        Mockito.when(camelExchange.getProperty(TEST_KEY)).thenReturn(false);

        Mockito.when(policyClient.deleteMicrosService(Mockito.any())).thenReturn(MESSAGE);

        //when
        tcaPolicyDeleteDelegate.execute(camelExchange);

        //then
        Mockito.verify(camelExchange).setProperty(PROPERTY_NAME, MESSAGE.getBytes());
    }

    @Test
    public void shouldExecuteTcaNotFound() {
        //given
        Mockito.when(camelExchange.getProperty(MODEL_BPMN_KEY)).thenReturn(HOLMES_ID_FROM_JSON);
        Mockito.when(camelExchange.getProperty(MODEL_PROP_KEY)).thenReturn(ID_JSON);
        Mockito.when(camelExchange.getProperty(TEST_KEY)).thenReturn(false);

        //when
        tcaPolicyDeleteDelegate.execute(camelExchange);

        //then
        Mockito.verify(policyClient, Mockito.never()).deleteMicrosService(Mockito.any());
        Mockito.verify(camelExchange, Mockito.never()).setProperty(Mockito.any(), Mockito.any());
    }

    @Test(expected = ModelBpmnException.class)
    public void shouldThrowModelBpmnException() {
        //given
        Mockito.when(camelExchange.getProperty(MODEL_BPMN_KEY)).thenReturn(NOT_JSON);
        Mockito.when(camelExchange.getProperty(TEST_KEY)).thenReturn(false);

        //when
        tcaPolicyDeleteDelegate.execute(camelExchange);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        //when
        tcaPolicyDeleteDelegate.execute(camelExchange);
    }
}
