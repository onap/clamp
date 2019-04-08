/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.config.spring;

import javax.sql.DataSource;
import javax.xml.transform.TransformerConfigurationException;

import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.config.EncodedPasswordBasicDataSource;
import org.onap.clamp.clds.transform.XslTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("clamp-default")
public class CldsConfiguration {

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private ClampProperties refProp;

    /**
     * Clds Identity database DataSource configuration
     *
     * @return
     */
    @Bean(name = "cldsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cldsdb")
    public DataSource cldsDataSource() {
        return new EncodedPasswordBasicDataSource();
    }

    @Bean(name = "mapper")
    public PropertiesFactoryBean mapper() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(appContext.getResource(refProp.getStringValue("files.systemProperties")));
        return bean;
    }

    @Bean(name = "cldsBpmnTransformer")
    public XslTransformer getCldsBpmnXslTransformer() throws TransformerConfigurationException {
        XslTransformer xslTransformer = new XslTransformer();
        xslTransformer.setXslResourceName("xsl/clds-bpmn-transformer.xsl");
        return xslTransformer;
    }
}