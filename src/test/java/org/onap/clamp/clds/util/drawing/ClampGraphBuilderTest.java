/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights
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

package org.onap.clamp.clds.util.drawing;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClampGraphBuilderTest {
    @Mock
    private Painter mockPainter;

    @Captor
    private ArgumentCaptor<String> collectorCaptor;

    @Captor
    private ArgumentCaptor<List<String>> microServicesCaptor;

    @Captor
    private ArgumentCaptor<String> policyCaptor;

    @Test
    public void clampGraphBuilderCompleteChainTest() {
        String collector = "VES";
        String ms1 = "ms1";
        String ms2 = "ms2";
        String policy = "Policy";
        List<String> microServices = Arrays.asList(ms1, ms2);

        ClampGraphBuilder clampGraphBuilder = new ClampGraphBuilder(mockPainter);
        clampGraphBuilder.collector(collector).microService(ms1).microService(ms2).policy(policy).build();

        verify(mockPainter, times(1))
            .doPaint(collectorCaptor.capture(), microServicesCaptor.capture(), policyCaptor.capture());

        Assert.assertEquals(collector, collectorCaptor.getValue());
        Assert.assertEquals(microServices, microServicesCaptor.getValue());
        Assert.assertEquals(policy, policyCaptor.getValue());
    }

    @Test(expected = InvalidStateException.class)
    public void clampGraphBuilderNoPolicyGivenTest() {
        String collector = "VES";
        String ms1 = "ms1";
        String ms2 = "ms2";
        List<String> microServices = Arrays.asList(ms1, ms2);

        ClampGraphBuilder clampGraphBuilder = new ClampGraphBuilder(mockPainter);
        clampGraphBuilder.collector(collector).microService(ms1).microService(ms2).build();
        Assert.fail();
    }

    @Test(expected = InvalidStateException.class)
    public void clampGraphBuilderNoMicroServiceGivenTest() {
        String collector = "VES";
        String policy = "Policy";

        ClampGraphBuilder clampGraphBuilder = new ClampGraphBuilder(mockPainter);
        clampGraphBuilder.collector(collector).policy(policy).build();
        Assert.fail();
    }
}