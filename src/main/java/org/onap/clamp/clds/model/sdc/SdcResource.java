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

package org.onap.clamp.clds.model.sdc;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.math.BigDecimal;
import java.util.List;

public class SdcResource implements Comparable<SdcResource> {

    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(SdcResource.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();

    private String resourceInstanceName;
    private String resourceName;
    private String resourceInvariantUuid;
    private String resourceVersion;
    private String resoucreType;
    private String resourceUuid;
    private List<SdcArtifact> artifacts;

    public String getResourceInstanceName() {
        return resourceInstanceName;
    }

    public void setResourceInstanceName(String resourceInstanceName) {
        this.resourceInstanceName = resourceInstanceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceInvariantUuid() {
        return resourceInvariantUuid;
    }

    public void setResourceInvariantUuid(String resourceInvUuid) {
        this.resourceInvariantUuid = resourceInvUuid;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public String getResoucreType() {
        return resoucreType;
    }

    public void setResoucreType(String resoucreType) {
        this.resoucreType = resoucreType;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public List<SdcArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<SdcArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public int compareTo(SdcResource in) {
        // Compares this object with the specified object for order.
        // Returns a negative integer, zero, or a positive integer as this
        // object is less than, equal to, or greater than the specified object.

        // first compare based on name
        int rtn = resourceInstanceName.compareToIgnoreCase(in.resourceInstanceName);

        if (rtn == 0) {
            BigDecimal myVersion = convertVersion(resourceVersion);
            BigDecimal inVersion = convertVersion(in.resourceVersion);
            rtn = myVersion.compareTo(inVersion);
        }
        return rtn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceInstanceName == null) ? 0 : resourceInstanceName.hashCode());
        result = prime * result + ((resourceVersion == null) ? 0 : resourceVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SdcResource other = (SdcResource) obj;
        if (resourceInstanceName == null) {
            if (other.resourceInstanceName != null) {
                return false;
            }
        } else if (!resourceInstanceName.equals(other.resourceInstanceName)) {
            return false;
        }
        if (resourceVersion == null) {
            if (other.resourceVersion != null) {
                return false;
            }
        } else if (!resourceVersion.equals(other.resourceVersion)) {
            return false;
        }
        return true;
    }

    /**
     * Convert version String into a BigDecimal.
     *
     * @param versionText version
     * @return version in BigDecimal
     */
    private BigDecimal convertVersion(String versionText) {
        BigDecimal rtn = BigDecimal.valueOf(0.0);
        try {
            rtn = new BigDecimal(versionText);
        } catch (NumberFormatException nfe) {
            logger.warn("SDC version=" + versionText + " is not decimal for name=" + resourceInstanceName);
        }
        return rtn;
    }
}
