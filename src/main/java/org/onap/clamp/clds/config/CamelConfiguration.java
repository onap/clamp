/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
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
 */
package org.onap.clamp.clds.config;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.onap.clamp.clds.model.CldsInfo;
import org.springframework.stereotype.Component;

@Component
public class CamelConfiguration extends RouteBuilder {

    @Override
    public void configure() {
    restConfiguration().component("servlet")
          .bindingMode(RestBindingMode.json);

              rest("/clds")
              .get("/test").description("Find user by id").outType(CldsInfo.class).produces("application/json")
                  .to("bean:org.onap.clamp.clds.service.CldsService?method=getCldsInfo()") ;
    }
}
