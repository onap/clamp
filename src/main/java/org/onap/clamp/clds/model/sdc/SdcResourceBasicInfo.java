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
 *
 */

package org.onap.clamp.clds.model.sdc;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.math.BigDecimal;

public class SdcResourceBasicInfo implements Comparable<SdcResourceBasicInfo> {

    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(SdcResourceBasicInfo.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();

    private String uuid;
    private String invariantUuid;
    private String name;
    private String version;
    private String toscaModelUrl;
    private String category;
    private String subCategory;
    private String resourceType;
    private String lifecycleState;
    private String lastUpdaterUserId;

    @Override
    public int compareTo(SdcResourceBasicInfo in) {
        // Compares this object with the specified object for order.
        // Returns a negative integer, zero, or a positive integer as this
        // object is less than, equal to, or greater than the specified object.
        // first compare based on name
        int rtn = name.compareToIgnoreCase(in.name);

        if (rtn == 0) {
            BigDecimal myVersion = convertVersion(version);
            BigDecimal inVersion = convertVersion(in.version);
            rtn = myVersion.compareTo(inVersion);
        }
        return rtn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        SdcResourceBasicInfo other = (SdcResourceBasicInfo) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    /**
     * Convert version String into a BigDecimal.
     *
     * @param version version
     * @return version in BigDecimal
     */
    private BigDecimal convertVersion(String version) {
        BigDecimal rtn = BigDecimal.valueOf(0.0);
        try {
            rtn = new BigDecimal(version);
        } catch (NumberFormatException nfe) {
            logger.warn("SDC version=" + version + " is not decimal for name=" + name);
        }
        return rtn;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInvariantUuid() {
        return invariantUuid;
    }

    public void setInvariantUuid(String invariantUuid) {
        this.invariantUuid = invariantUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getToscaModelUrl() {
        return toscaModelUrl;
    }

    public void setToscaModelUrl(String toscaModelUrl) {
        this.toscaModelUrl = toscaModelUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public String getLastUpdaterUserId() {
        return lastUpdaterUserId;
    }

    public void setLastUpdaterUserId(String lastUpdaterUserId) {
        this.lastUpdaterUserId = lastUpdaterUserId;
    }

    public EELFLogger getLogger() {
        return logger;
    }
}
