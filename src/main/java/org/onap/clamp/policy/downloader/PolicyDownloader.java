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

package org.onap.clamp.policy.downloader;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This class implements the communication with the Policy Engine to retrieve
 * policy models. This is a periodic job that is done in the background to
 * synchronize the clamp database.
 */
@Configuration
@Profile("clamp-policy-controller")
public class PolicyDownloader {

    @Autowired
    CamelContext camelContext;

    private void downloadAllPolicies() {
        /*
         * Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext)
         * .withProperty("blueprintResourceId",
         * resourceUuid).withProperty("blueprintServiceId", serviceUuid)
         * .withProperty("blueprintName", artifactName).build();
         * metricsLogger.info("Attempt n°" + i + " to contact DCAE inventory");
         * 
         * Exchange exchangeResponse =
         * camelContext.createProducerTemplate().send("direct:get-all-policy-models",
         * myCamelExchange);
         */
    }
}
