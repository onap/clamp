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

package org.onap.clamp.clds.policy.microservice;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.onap.clamp.clds.loop.Loop;
import org.onap.clamp.clds.serialization.JsonObjectAttributeConverter;
import org.onap.clamp.clds.policy.Policy;

@Entity
@Table(name = "micro_service_policies")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class MicroServicePolicy implements Serializable, Policy {
    /**
     *
     */
    private static final long serialVersionUID = 6271238288583332616L;

    @Expose
    @Id
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @Expose
    @Type(type = "json")
    @Column(columnDefinition = "json", name = "properties")
    private JsonObject properties;

    @Expose
    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @Expose
    @Column(name = "policy_tosca", nullable = false)
    private String policyTosca;

    @Expose
    @Column(columnDefinition = "json", name = "json_representation", nullable = false)
    @Convert(converter = JsonObjectAttributeConverter.class)
    private JsonObject jsonRepresentation;

    @ManyToMany(mappedBy = "microServicePolicies")
    private Set<Loop> usedByLoops = new HashSet<>();

    public MicroServicePolicy() {
        //serialization
    }

    public MicroServicePolicy(String name, String policyTosca, Boolean shared, JsonObject jsonRepresentation,
        Set<Loop> usedByLoops) {
        this.name = name;
        this.policyTosca = policyTosca;
        this.shared = shared;
        this.jsonRepresentation = jsonRepresentation;
        this.usedByLoops = usedByLoops;
    }

    public String getName() {
        return name;
    }

    public JsonObject getProperties() {
        return properties;
    }

    public void setProperties(JsonObject properties) {
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

    public JsonObject getJsonRepresentation() {
        return jsonRepresentation;
    }

    public void setJsonRepresentation(JsonObject jsonRepresentation) {
        this.jsonRepresentation = jsonRepresentation;
    }

    public Set<Loop> getUsedByLoops() {
        return usedByLoops;
    }

    public void setUsedByLoops(Set<Loop> usedBy) {
        this.usedByLoops = usedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MicroServicePolicy that = (MicroServicePolicy) o;
        return Objects.equal(name, that.name) &&
            Objects.equal(properties, that.properties) &&
            Objects.equal(shared, that.shared) &&
            Objects.equal(policyTosca, that.policyTosca) &&
            Objects.equal(jsonRepresentation, that.jsonRepresentation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, properties, shared, policyTosca, jsonRepresentation);
    }
}
