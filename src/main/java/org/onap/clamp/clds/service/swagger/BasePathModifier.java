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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.service.swagger;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;

import java.util.Map;
import java.util.stream.Collectors;

@SwaggerDefinition
public class BasePathModifier implements ReaderListener {

    @Override
    public void beforeScan(Reader reader, Swagger swagger) {
        // needed to avoid to have to add @Api on every api
        ((DefaultReaderConfig)reader.getConfig()).setScanAllResources(true);
    }

    @Override
    public void afterScan(Reader reader, Swagger swagger) {
        swagger.setPaths(swagger.getPaths().entrySet().stream().collect(Collectors.toMap(e -> "/restservices/clds/v1" + e.getKey(), Map.Entry::getValue)));
    }

}
