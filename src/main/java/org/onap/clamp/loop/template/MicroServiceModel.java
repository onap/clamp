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

package org.onap.clamp.loop.template;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This class represents a micro service model for a loop template.
 */

@Entity
@Table(name = "micro_service_models")
public class MicroServiceModel implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701376645L;

    @Id
    @Expose
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    /**
     * This variable is used to store the type mentioned in the micro-service
     * blueprint.
     */
    @Expose
    @Column(nullable = false, name = "policy_type")
    private String policyType;

    @Expose
    @Column(nullable = false, name = "blueprint_yaml")
    private String blueprint;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "policy_model_type", referencedColumnName = "policy_model_type"),
        @JoinColumn(name = "policy_model_version", referencedColumnName = "version") })
    private PolicyModel policyModel;

    @Expose
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "microServiceModel")
    private Set<TemplateMicroServiceModel> usedByLoopTemplates = new HashSet<>();

    @Expose
    @CreationTimestamp
    @Column(name = "created_timestamp")
    private Instant createdDate;

    @Expose
    @UpdateTimestamp
    @Column(name = "updated_timestamp")
    private Instant updatedDate;

    @Expose
    @Column(name = "updated_by")
    private String updatedBy;

    @Expose
    @Column(name = "created_by")
    private String createdBy;

    /**
     * @return the policyModel
     */
    public PolicyModel getPolicyModel() {
        return policyModel;
    }

    /**
     * @param policyModel the policyModel to set
     */
    public void setPolicyModel(PolicyModel policyModel) {
        this.policyModel = policyModel;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the policyType
     */
    public String getPolicyType() {
        return policyType;
    }

    /**
     * @param policyType the policyType to set
     */
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    /**
     * @return the blueprint
     */
    public String getBlueprint() {
        return blueprint;
    }

    /**
     * @param blueprint the blueprint to set
     */
    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    /**
     * @return the createdDate
     */
    public Instant getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public Instant getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Default constructor for serialization.
     */
    public MicroServiceModel() {
    }

    public MicroServiceModel(String name, String policyType, String blueprint, PolicyModel policyModel,
            String updatedBy, String createdBy) {
        this.name = name;
        this.policyType = policyType;
        this.blueprint = blueprint;
        this.policyModel = policyModel;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
    }

}
