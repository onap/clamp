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
 * ===================================================================
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.client.req;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Assert;
import org.junit.Test;
import org.onap.clamp.clds.client.SdcCatalogServices;
import org.onap.clamp.clds.model.CldsSdcResource;
import org.onap.clamp.clds.model.CldsSdcServiceDetail;
import org.onap.clamp.clds.model.prop.Global;
import org.onap.clamp.clds.model.prop.ModelProperties;

public class SdcReqTest {

    String baseUrl = "AYBABTU";
    String serviceInvariantUUID = "serviceInvariantUUID";

    @Test
    public void getSdcReqUrlsListNoGlobalPropTest() throws Exception {
        ModelProperties prop = mock(ModelProperties.class);
        SdcCatalogServices sdcCatalogServices = mock(SdcCatalogServices.class);
        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        CldsSdcResource cldsSdcResource = mock(CldsSdcResource.class);
        List<CldsSdcResource> cldsSdcResources = new ArrayList<>();
        cldsSdcResources.add(cldsSdcResource);
        List<String> resourceVf = new ArrayList<>();
        resourceVf.add(serviceInvariantUUID);

        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        Global global = mock(Global.class);
        when(prop.getGlobal()).thenReturn(global);
        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        when(global.getService()).thenReturn(serviceInvariantUUID);
        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        CldsSdcServiceDetail cldsSdcServiceDetail = mock(CldsSdcServiceDetail.class);
        when(sdcCatalogServices.getCldsSdcServiceDetailFromJson(null)).thenReturn(cldsSdcServiceDetail);
        when(global.getResourceVf()).thenReturn(new ArrayList<>());
        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        when(cldsSdcServiceDetail.getResources()).thenReturn(cldsSdcResources);
        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        when(cldsSdcResource.getResoucreType()).thenReturn("VF");
        Assert.assertTrue(SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution).isEmpty());

        when(global.getResourceVf()).thenReturn(resourceVf);
        when(cldsSdcResource.getResourceInvariantUUID()).thenReturn(serviceInvariantUUID);
        when(cldsSdcResource.getResourceInstanceName()).thenReturn("Resource instance name");
        List<String> expected = new ArrayList<>();
        expected.add("AYBABTU/null/resourceInstances/resourceinstancename/artifacts");
        Assert.assertEquals(expected, SdcReq.getSdcReqUrlsList(prop, baseUrl, sdcCatalogServices, delegateExecution));
    }
}
