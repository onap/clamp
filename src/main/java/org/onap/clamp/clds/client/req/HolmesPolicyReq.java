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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.onap.clamp.clds.model.prop.Global;
import org.onap.clamp.clds.model.prop.Holmes;
import org.onap.clamp.clds.model.prop.ModelProperties;
import org.onap.clamp.clds.model.prop.ResourceGroup;
import org.onap.clamp.clds.model.prop.ServiceConfiguration;
import org.onap.clamp.clds.model.prop.StringMatch;
import org.onap.clamp.clds.model.refprop.RefProp;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HolmesPolicyReq {
	protected static final EELFLogger logger        = EELFManager.getInstance().getLogger(StringMatchPolicyReq.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();

    /**
     * Add holmesConfigurations to json
     *
     * @param refProf
     * @param appendToNode
     * @param holmes
     * @param prof
     * @throws IOException
     */
    public static void appendHolmesConfigurations(RefProp refProp, ObjectNode appendToNode,
            Holmes holmes, ModelProperties prop) throws IOException {
        // "serviceConfigurations":{
        ObjectNode hNodes = appendToNode.with("holmesConfigurations");

        int index = 0;
        if (holmes != null) {
            		hNodes.put("configBody", holmes.getCorrelationLogic());
            		hNodes.put("configBodyType", "Other");
            		hNodes.put("configName", "HolmesPolicy");
            		hNodes.put("ecompName", "DCAE");
            		hNodes.put("policyConfigType", "Base");
            		hNodes.put("policyName", holmes.getOperationalPolicy());
        }
    }
}
