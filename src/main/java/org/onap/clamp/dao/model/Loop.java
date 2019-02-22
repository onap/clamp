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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "loops")
public class Loop implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -286522707701388642L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "dcae_deployment_id")
    private String dcaeDeploymentId;

    @Column(name = "dcae_deployment_status_url")
    private String dcaeDeploymentStatusUrl;

    @Column(name = "svg_representation")
    private String svgRepresentation;

    @Column(nullable = false, name = "blueprint")
    private String blueprint;

    @Column(nullable = false, name = "last_computed_state")
    private LoopState lastComputedState;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop")
    private Set<GlobalProperty> globalProperties;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop")
    private Set<OperationalPolicy> operationalPolicies;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "micro_service_policies_used", joinColumns = @JoinColumn(name = "loop_id"), inverseJoinColumns = @JoinColumn(name = "micro_service_policy_id"))
    private Set<MicroServicePolicy> usingMicroServicePolicies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDcaeDeploymentId() {
        return dcaeDeploymentId;
    }

    public void setDcaeDeploymentId(String dcaeDeploymentId) {
        this.dcaeDeploymentId = dcaeDeploymentId;
    }

    public String getDcaeDeploymentStatusUrl() {
        return dcaeDeploymentStatusUrl;
    }

    public void setDcaeDeploymentStatusUrl(String dcaeDeploymentStatusUrl) {
        this.dcaeDeploymentStatusUrl = dcaeDeploymentStatusUrl;
    }

    public String getSvgRepresentation() {
        return svgRepresentation;
    }

    public void setSvgRepresentation(String svgRepresentation) {
        this.svgRepresentation = svgRepresentation;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    public LoopState getLastComputedState() {
        return lastComputedState;
    }

    public void setLastComputedState(LoopState lastComputedState) {
        this.lastComputedState = lastComputedState;
    }

    public Set<GlobalProperty> getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(Set<GlobalProperty> globalProperties) {
        this.globalProperties = globalProperties;
    }

    public Set<OperationalPolicy> getOperationalPolicies() {
        return operationalPolicies;
    }

    public void setOperationalPolicies(Set<OperationalPolicy> operationalPolicies) {
        this.operationalPolicies = operationalPolicies;
    }

    public Set<MicroServicePolicy> getUsingMicroServicePolicies() {
        return usingMicroServicePolicies;
    }

    public void setUsingMicroServicePolicies(Set<MicroServicePolicy> usingMicroServicePolicies) {
        this.usingMicroServicePolicies = usingMicroServicePolicies;
    }

}
