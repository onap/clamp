/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Modifications copyright (c) 2019 Nokia
 * ===================================================================
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

package org.onap.clamp.flow;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.clamp.clds.util.LoggingUtils;
import org.onap.clamp.clds.util.ONAPLogConstants;
import org.onap.clamp.flow.log.FlowLogOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class FlowLogOperationTestItCase {

    @Autowired
    CamelContext camelContext;

    @Test
    public void testStratLog() {
        //given
        FlowLogOperation flowLogOperation = new FlowLogOperation();
        Exchange exchange = new DefaultExchange(camelContext);
        LoggingUtils loggingUtils = mock(LoggingUtils.class);
        ReflectionTestUtils.setField(flowLogOperation, "util", loggingUtils);

        //when
        Mockito.when(loggingUtils.getProperties(ONAPLogConstants.MDCs.REQUEST_ID)).thenReturn("MockRequestId");
        Mockito.when(loggingUtils.getProperties(ONAPLogConstants.MDCs.INVOCATION_ID)).thenReturn("MockInvocationId");
        Mockito.when(loggingUtils.getProperties(ONAPLogConstants.MDCs.PARTNER_NAME)).thenReturn("MockPartnerName");
        flowLogOperation.startLog(exchange, "serviceName");

        //then
        assertThat(exchange.getProperty(ONAPLogConstants.Headers.REQUEST_ID)).isEqualTo("MockRequestId");
        assertThat(exchange.getProperty(ONAPLogConstants.Headers.INVOCATION_ID)).isEqualTo("MockInvocationId");
        assertThat(exchange.getProperty(ONAPLogConstants.Headers.PARTNER_NAME)).isEqualTo("MockPartnerName");
    }
}