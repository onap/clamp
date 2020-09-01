/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.models.tosca.simple.provider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.models.base.PfConceptKey;
import org.onap.policy.models.base.PfModelException;
import org.onap.policy.models.dao.DaoParameters;
import org.onap.policy.models.dao.PfDao;
import org.onap.policy.models.dao.PfDaoFactory;
import org.onap.policy.models.dao.impl.DefaultPfDao;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaDataType;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaDataTypes;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaPolicyType;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaPolicyTypes;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaServiceTemplate;

/**
 * Test the {@link SimpleToscaProvider} class.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class SimpleToscaServiceTemplateProviderTest {
    private static final String TEMPLATE_IS_NULL = "^serviceTemplate is marked .*on.*ull but is null$";
    private static final String DAO_IS_NULL = "^dao is marked .*on.*ull but is null$";

    private PfDao pfDao;

    /**
     * Set up the DAO towards the database.
     *
     * @throws Exception on database errors
     */
    @Before
    public void setupDao() throws Exception {
        final DaoParameters daoParameters = new DaoParameters();
        daoParameters.setPluginClass(DefaultPfDao.class.getName());

        daoParameters.setPersistenceUnit("ToscaConceptTest");

        Properties jdbcProperties = new Properties();
        jdbcProperties.setProperty(PersistenceUnitProperties.JDBC_USER, "policy");
        jdbcProperties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, "P01icY");

        // H2, use "org.mariadb.jdbc.Driver" and "jdbc:mariadb://localhost:3306/policy" for locally installed MariaDB
        jdbcProperties.setProperty(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
        jdbcProperties.setProperty(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:testdb");

        daoParameters.setJdbcProperties(jdbcProperties);

        pfDao = new PfDaoFactory().createPfDao(daoParameters);
        pfDao.init(daoParameters);
    }

    @After
    public void teardown() {
        pfDao.close();
    }

    @Test
    public void testCreateUpdateGetDeleteDataType() throws PfModelException {
        JpaToscaServiceTemplate serviceTemplate = new JpaToscaServiceTemplate();

        JpaToscaServiceTemplate dbServiceTemplate =
            new SimpleToscaServiceTemplateProvider().write(pfDao, serviceTemplate);

        assertEquals(serviceTemplate, dbServiceTemplate);

        JpaToscaServiceTemplate readServiceTemplate = new SimpleToscaServiceTemplateProvider().read(pfDao);
        assertEquals(serviceTemplate, readServiceTemplate);

        assertNull(readServiceTemplate.getDataTypes());

        PfConceptKey dataType0Key = new PfConceptKey("DataType0", "0.0.1");
        JpaToscaDataType dataType0 = new JpaToscaDataType();
        dataType0.setKey(dataType0Key);
        serviceTemplate.setDataTypes(new JpaToscaDataTypes());
        serviceTemplate.getDataTypes().getConceptMap().put(dataType0Key, dataType0);

        dbServiceTemplate = new SimpleToscaServiceTemplateProvider().write(pfDao, serviceTemplate);
        assertEquals(serviceTemplate, dbServiceTemplate);

        readServiceTemplate = new SimpleToscaServiceTemplateProvider().read(pfDao);
        assertEquals(serviceTemplate, readServiceTemplate);

        assertEquals(1, readServiceTemplate.getDataTypes().getConceptMap().size());
        assertEquals(dataType0, readServiceTemplate.getDataTypes().get(dataType0Key));
        assertNull(readServiceTemplate.getDataTypes().get(dataType0Key).getDescription());

        dataType0.setDescription("Updated Description");

        dbServiceTemplate = new SimpleToscaServiceTemplateProvider().write(pfDao, serviceTemplate);
        assertEquals(serviceTemplate, dbServiceTemplate);

        readServiceTemplate = new SimpleToscaServiceTemplateProvider().read(pfDao);
        assertEquals(serviceTemplate, readServiceTemplate);

        assertEquals(dataType0, readServiceTemplate.getDataTypes().get(dataType0Key));
        assertEquals("Updated Description", readServiceTemplate.getDataTypes().get(dataType0Key).getDescription());

        PfConceptKey policyType0Key = new PfConceptKey("PolicyType0", "0.0.1");

        JpaToscaPolicyType policyType0 = new JpaToscaPolicyType();

        policyType0.setKey(policyType0Key);
        serviceTemplate.setPolicyTypes(new JpaToscaPolicyTypes());

        serviceTemplate.getPolicyTypes().getConceptMap().put(policyType0Key, policyType0);

        dbServiceTemplate = new SimpleToscaServiceTemplateProvider().write(pfDao, serviceTemplate);
        assertEquals(serviceTemplate, dbServiceTemplate);

        readServiceTemplate = new SimpleToscaServiceTemplateProvider().read(pfDao);
        assertEquals(serviceTemplate, readServiceTemplate);

        JpaToscaServiceTemplate deletedServiceTemplate = new SimpleToscaServiceTemplateProvider().delete(pfDao);
        assertEquals(serviceTemplate, deletedServiceTemplate);

        readServiceTemplate = new SimpleToscaServiceTemplateProvider().read(pfDao);
        assertNull(readServiceTemplate);
    }

    @Test
    public void testNonNulls() {
        assertThatThrownBy(() -> {
            new SimpleToscaServiceTemplateProvider().write(null, null);
        }).hasMessageMatching(DAO_IS_NULL);

        assertThatThrownBy(() -> {
            new SimpleToscaServiceTemplateProvider().write(pfDao, null);
        }).hasMessageMatching(TEMPLATE_IS_NULL);

        assertThatThrownBy(() -> {
            new SimpleToscaServiceTemplateProvider().write(null, new JpaToscaServiceTemplate());
        }).hasMessageMatching(DAO_IS_NULL);

        assertThatThrownBy(() -> {
            new SimpleToscaServiceTemplateProvider().read(null);
        }).hasMessageMatching(DAO_IS_NULL);

        assertThatThrownBy(() -> {
            new SimpleToscaServiceTemplateProvider().delete(null);
        }).hasMessageMatching(DAO_IS_NULL);
    }
}
