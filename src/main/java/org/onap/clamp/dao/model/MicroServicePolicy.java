/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
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

package org.onap.clamp.dao.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "micro_service_policies")
public class MicroServicePolicy implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6271238288583332616L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "properties")
    private String properties;

    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @Column(name = "policy_tosca", nullable = false)
    private String policyTosca;

    @Column(name = "json_representation", nullable = false)
    private String jsonRepresentation;

    @ManyToMany(mappedBy = "usingMicroServicePolicies")
    private Set<Loop> usedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getPolicyTosca() {
        return policyTosca;
    }

    public void setPolicyTosca(String policyTosca) {
        this.policyTosca = policyTosca;
    }

    public String getJsonRepresentation() {
        return jsonRepresentation;
    }

    public void setJsonRepresentation(String jsonRepresentation) {
        this.jsonRepresentation = jsonRepresentation;
    }

    public Set<Loop> getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(Set<Loop> usedBy) {
        this.usedBy = usedBy;
    }

}
