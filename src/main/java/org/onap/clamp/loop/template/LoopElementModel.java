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

import org.onap.clamp.loop.common.AuditEntity;

/**
 * This class represents a micro service model for a loop template.
 */

@Entity
@Table(name = "loop_element_models")
public class LoopElementModel extends AuditEntity implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701376645L;

    @Id
    @Expose
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    /**
     * Here we store the blueprint coming from DCAE.
     */
    @Column(nullable = false, name = "blueprint_yaml")
    private String blueprint;

    /**
     * The type of element
     */
    @Column(nullable = false, name = "loop_element_type")
    private String loopElementType;

    /**
     * This variable is used to store the type mentioned in the micro-service
     * blueprint.
     */
    @Expose
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumns({ @JoinColumn(name = "policy_model_type", referencedColumnName = "policy_model_type"),
        @JoinColumn(name = "policy_model_version", referencedColumnName = "version") })
    private PolicyModel policyModel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loopElementModel", orphanRemoval = true)
    private Set<LoopTemplateLoopElementModel> usedByLoopTemplates = new HashSet<>();

    /**
     * policyModel getter.
     * 
     * @return the policyModel
     */
    public PolicyModel getPolicyModel() {
        return policyModel;
    }

    /**
     * policyModel setter.
     * 
     * @param policyModel the policyModel to set
     */
    public void setPolicyModel(PolicyModel policyModel) {
        this.policyModel = policyModel;
    }

    /**
     * name getter.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * name setter.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * blueprint getter.
     * 
     * @return the blueprint
     */
    public String getBlueprint() {
        return blueprint;
    }

    /**
     * blueprint setter.
     * 
     * @param blueprint the blueprint to set
     */
    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    /**
     * @return the loopElementType
     */
    public String getLoopElementType() {
        return loopElementType;
    }

    /**
     * @param loopElementType the loopElementType to set
     */
    public void setLoopElementType(String loopElementType) {
        this.loopElementType = loopElementType;
    }

    /**
     * usedByLoopTemplates getter.
     * 
     * @return the usedByLoopTemplates
     */
    public Set<LoopTemplateLoopElementModel> getUsedByLoopTemplates() {
        return usedByLoopTemplates;
    }

    /**
     * Default constructor for serialization.
     */
    public LoopElementModel() {
    }

    /**
     * Constructor.
     * 
     * @param name            The name id
     * @param loopElementType The type of loop element
     * @param blueprint       The blueprint defined for dcae that contains the
     *                        policy type to use
     * @param policyModel     The policy model for the policy type mentioned here
     */
    public LoopElementModel(String name, String loopElementType, String blueprint, PolicyModel policyModel) {
        this.name = name;
        this.loopElementType = loopElementType;
        this.blueprint = blueprint;
        this.policyModel = policyModel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LoopElementModel other = (LoopElementModel) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
