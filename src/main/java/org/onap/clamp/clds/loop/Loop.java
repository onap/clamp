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

package org.onap.clamp.clds.loop;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;
import org.onap.clamp.clds.serialization.JsonObjectAttributeConverter;
import org.onap.clamp.clds.loop.log.LoopLog;
import org.onap.clamp.clds.policy.microservice.MicroServicePolicy;
import org.onap.clamp.clds.policy.operational.OperationalPolicy;

@Entity
@Table(name = "loops")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Loop implements Serializable {

    public static final Loop EMPTY_LOOP = new Loop();
    /**
     *
     */
    private static final long serialVersionUID = -286522707701388642L;

    @Id
    @Expose
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @Expose
    @Column(name = "dcae_deployment_id")
    private String dcaeDeploymentId;

    @Expose
    @Column(name = "dcae_deployment_status_url")
    private String dcaeDeploymentStatusUrl;

    @Expose
    @Column(name = "svg_representation")
    private String svgRepresentation;

    @Expose
    @Convert(converter = JsonObjectAttributeConverter.class)
    @Column(columnDefinition = "json", name = "global_properties_json")
    private JsonObject globalPropertiesJson;

    @Expose
    @Column(nullable = false, name = "blueprint_yaml")
    private String blueprint;

    @Expose
    @Column(nullable = false, name = "last_computed_state")
    @Enumerated(EnumType.STRING)
    private LoopState lastComputedState;

    @Expose
    @OneToMany(targetEntity = OperationalPolicy.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop")
    private Set<OperationalPolicy> operationalPolicies = new HashSet<>();

    @Expose
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "loops_microservicepolicies", joinColumns = @JoinColumn(name = "loop_id"), inverseJoinColumns = @JoinColumn(name = "microservicepolicy_id"))
    private Set<MicroServicePolicy> microServicePolicies = new HashSet<>();

    @Expose
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop")
    private Set<LoopLog> loopLogs = new HashSet<>();

    public Loop() {
    }

    public Loop(String name, String blueprint, String svgRepresentation) {
        this.name = name;
        this.svgRepresentation = svgRepresentation;
        this.blueprint = blueprint;
        this.lastComputedState = LoopState.DESIGN;
        this.globalPropertiesJson = new JsonObject();
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

    String getSvgRepresentation() {
        return svgRepresentation;
    }

    void setSvgRepresentation(String svgRepresentation) {
        this.svgRepresentation = svgRepresentation;
    }

    String getBlueprint() {
        return blueprint;
    }

    void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    LoopState getLastComputedState() {
        return lastComputedState;
    }

    void setLastComputedState(LoopState lastComputedState) {
        this.lastComputedState = lastComputedState;
    }

    Set<OperationalPolicy> getOperationalPolicies() {
        return operationalPolicies;
    }

    void setOperationalPolicies(Set<OperationalPolicy> operationalPolicies) {
        this.operationalPolicies = operationalPolicies;
    }

    Set<MicroServicePolicy> getMicroServicePolicies() {
        return microServicePolicies;
    }

    void setMicroServicePolicies(Set<MicroServicePolicy> microServicePolicies) {
        this.microServicePolicies = microServicePolicies;
    }

    JsonObject getGlobalPropertiesJson() {
        return globalPropertiesJson;
    }

    void setGlobalPropertiesJson(JsonObject globalPropertiesJson) {
        this.globalPropertiesJson = globalPropertiesJson;
    }

    Set<LoopLog> getLoopLogs() {
        return loopLogs;
    }

    void setLoopLogs(Set<LoopLog> loopLogs) {
        this.loopLogs = loopLogs;
    }

    void addLog(LoopLog log) {
        loopLogs.add(log);
        log.setLoop(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Loop loop = (Loop) o;
        return Objects.equal(name, loop.name) &&
            Objects.equal(dcaeDeploymentId, loop.dcaeDeploymentId) &&
            Objects.equal(dcaeDeploymentStatusUrl, loop.dcaeDeploymentStatusUrl) &&
            Objects.equal(svgRepresentation, loop.svgRepresentation) &&
            Objects.equal(globalPropertiesJson, loop.globalPropertiesJson) &&
            Objects.equal(blueprint, loop.blueprint) &&
            lastComputedState == loop.lastComputedState &&
            Objects.equal(operationalPolicies, loop.operationalPolicies) &&
            Objects.equal(microServicePolicies, loop.microServicePolicies) &&
            Objects.equal(loopLogs, loop.loopLogs);
    }

    @Override
    public int hashCode() {
        return Objects
            .hashCode(name, dcaeDeploymentId, dcaeDeploymentStatusUrl, svgRepresentation, globalPropertiesJson,
                blueprint,
                lastComputedState, operationalPolicies, microServicePolicies, loopLogs);
    }
}
