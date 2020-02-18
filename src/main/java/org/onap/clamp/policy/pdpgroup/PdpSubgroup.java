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

import com.google.gson.annotations.Expose;
import java.util.List;

/**
 * This class maps the Policy get PDP Group response to a nice pojo.
 */
public class PdpSubgroup {

    @Expose
    private String pdpType;

    @Expose
    private List<PolicyModelKey> supportedPolicyTypes;

    public String getPdpType() {
        return pdpType;
    }

    public void setPdpType(String pdpType) {
        this.pdpType = pdpType;
    }

    public List<PolicyModelKey> getSupportedPolicyTypes() {
        return supportedPolicyTypes;
    }

    public void setSupportedPolicyTypes(List<PolicyModelKey> supportedPolicyTypes) {
        this.supportedPolicyTypes = supportedPolicyTypes;
    }

}
