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
 * 
 */

package org.onap.clamp.clds.service;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;
@Component
@ApplicationPath("/restservices/clds/v1")
public class JaxrsApplication extends Application {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(JaxrsApplication.class);
    private Function<BeanDefinition, Optional<Class<?>>> beanDefinitionToClass = b -> {
        try {
            return Optional.of(Class.forName(b.getBeanClassName()));
        } catch (ClassNotFoundException e) {
            logger.error("Could not get class annotated with @Path for swagger documentation generation", e);
            return Optional.empty();
        }
    };
}