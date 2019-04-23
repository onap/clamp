/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

public class CldsDbServiceCache {

    private String invariantId;
    private String serviceId;
    private InputStream cldsDataInstream;

    /**
     * Constructor.
     * @param cldsServiceData The cles service data
     * @throws IOException IO Exception
     */
    public CldsDbServiceCache(CldsServiceData cldsServiceData) throws IOException {
        setCldsDataInstream(cldsServiceData);
        setInvariantId(cldsServiceData.getServiceInvariantUuid());
        setServiceId(cldsServiceData.getServiceUuid());
    }

    public String getInvariantId() {
        return invariantId;
    }

    public void setInvariantId(String invariantId) {
        this.invariantId = invariantId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public InputStream getCldsDataInstream() {
        return cldsDataInstream;
    }

    public void setCldsDataInstream(InputStream cldsDataInputstream) {
        this.cldsDataInstream = cldsDataInputstream;
    }

    public void setCldsDataInstream(CldsServiceData cldsServiceData) throws IOException {
        this.cldsDataInstream = getInstreamFromObject(cldsServiceData);
    }

    private InputStream getInstreamFromObject(CldsServiceData cldsServiceData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cldsServiceData);
        oos.flush();
        oos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
