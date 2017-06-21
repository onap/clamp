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

package org.onap.clamp.clds.client;

import org.onap.clamp.clds.client.req.DcaeReq;
import org.onap.clamp.clds.model.prop.ModelProperties;
import org.onap.clamp.clds.model.refprop.RefProp;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;


/**
 * Send control loop model to dcae proxy.
 */
public class DcaeReqDelegate implements JavaDelegate {
    // currently uses the java.util.logging.Logger like the Camunda engine
    private static final Logger logger = Logger.getLogger(DcaeReqDelegate.class.getName());

    @Autowired
    private RefProp refProp;

    /**
     * Perform activity.  Send to dcae proxy.
     *
     * @param execution
     */
    public void execute(DelegateExecution execution) throws Exception {
        ModelProperties prop = ModelProperties.create(execution);
        String dcaeReq = DcaeReq.format(refProp, prop);
        if (dcaeReq != null) {
            execution.setVariable("dcaeReq", dcaeReq.getBytes());
        }
        execution.setVariable("dcaeUrl", System.getProperty("CLDS_DCAE_URL") + "/" + prop.getControlName());
    }
}
