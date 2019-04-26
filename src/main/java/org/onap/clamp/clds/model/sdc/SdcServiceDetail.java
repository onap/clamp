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

import java.util.List;

public class SdcServiceDetail {

    private String uuid;
    private String invariantUuid;
    private String name;
    private String version;
    private String toscaModelUrl;
    private String category;
    private String lifecycleState;
    private String lastUpdaterUserId;
    private String distributionStatus;
    private String lastUpdaterFullName;
    private List<SdcResource> resources;
    private List<SdcArtifact> artifacts;

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

    public String getDistributionStatus() {
        return distributionStatus;
    }

    public void setDistributionStatus(String distributionStatus) {
        this.distributionStatus = distributionStatus;
    }

    public String getLastUpdaterFullName() {
        return lastUpdaterFullName;
    }

    public void setLastUpdaterFullName(String lastUpdaterFullName) {
        this.lastUpdaterFullName = lastUpdaterFullName;
    }

    public List<SdcResource> getResources() {
        return resources;
    }

    public void setResources(List<SdcResource> resources) {
        this.resources = resources;
    }

    public List<SdcArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<SdcArtifact> artifacts) {
        this.artifacts = artifacts;
    }

}
