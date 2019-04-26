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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.onap.clamp.clds.service.CldsService;

public class CldsServiceData implements Serializable {

    private static final long serialVersionUID = -9153372664377279423L;
    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(CldsServiceData.class);
    protected static final EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();
    private String serviceInvariantUuid;
    private String serviceUuid;
    private Long ageOfRecord;
    private List<CldsVfData> cldsVfs;

    public String getServiceInvariantUuid() {
        return serviceInvariantUuid;
    }

    public void setServiceInvariantUuid(String serviceInvariantUuid) {
        this.serviceInvariantUuid = serviceInvariantUuid;
    }

    public List<CldsVfData> getCldsVfs() {
        return cldsVfs;
    }

    public void setCldsVfs(List<CldsVfData> cldsVfs) {
        this.cldsVfs = cldsVfs;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public Long getAgeOfRecord() {
        return ageOfRecord;
    }

    public void setAgeOfRecord(Long ageOfRecord) {
        this.ageOfRecord = ageOfRecord;
    }

    /**
     * Filter out any VFs that the user is not authorized for. Use the
     * CldsService to determine if the user is authorized for a VF.
     *
     * @param svc The clds service
     */
    public void filterVfs(CldsService svc) {
        List<CldsVfData> filteredCldsVfs = new ArrayList<>();
        if (cldsVfs == null) {
            logger.debug("cldsVfs == null");
        } else {
            for (CldsVfData vf : cldsVfs) {
                // if user is authorized for the VF then add it to the filtered
                // list
                try {
                    if (svc.isAuthorizedForVf(vf.getVfInvariantResourceUUID())) {
                        filteredCldsVfs.add(vf);
                    }
                } catch (NotAuthorizedException e) {
                    logger.error("user not authorized for {}" + vf.getVfInvariantResourceUUID(), e);
                    // when not NotAuthorizedException - don't add to
                    // filteredCldsVfs list
                }
            }
        }
        // new filtered list replaces the list of VFs for the user
        cldsVfs = filteredCldsVfs;
    }
}
