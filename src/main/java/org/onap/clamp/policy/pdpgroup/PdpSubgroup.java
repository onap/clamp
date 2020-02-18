/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
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

package org.onap.clamp.policy.pdpgroup;

import java.util.List;

import org.onap.clamp.loop.template.PolicyModelId;

import com.google.gson.annotations.Expose;

/**
 * This class maps the Policy get PDP Group response to a nice pojo.
 */
public class PdpSubgroup {

    @Expose
    private String subPdpGroup;

    @Expose
    private List<PolicyModelId> supportedPolicyTypes;

    public String getSubPdpGroup() {
        return subPdpGroup;
    }

    public void setSubPdpGroup(String subPdpGroup) {
        this.subPdpGroup = subPdpGroup;
    }

    public List<PolicyModelId> getSupportedPolicyTypes() {
        return supportedPolicyTypes;
    }

    public void setSupportedPolicyTypes(List<PolicyModelId> supportedPolicyTypes) {
        this.supportedPolicyTypes = supportedPolicyTypes;
    }

}
