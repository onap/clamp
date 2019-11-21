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

package org.onap.clamp.loop.template;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "loop_templates")
public class LoopTemplate implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388642L;

    @Id
    @Expose
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    /**
     * This field is used when we have a blueprint defining all microservices. The
     * other option would be to have independent blueprint for each microservices.
     * In that case they are stored in each MicroServiceModel
     */
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false, name = "blueprint_yaml")
    private String blueprint;

    @Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "svg_representation")
    private String svgRepresentation;

    @Expose
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loopTemplate")
    @SortNatural
    private SortedSet<TemplateMicroServiceModel> microServiceModelUsed = new TreeSet<>();

    @Expose
    @Column(name = "maximum_instances_allowed")
    private Integer maximumInstancesAllowed;

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
     * svgRepresentation getter.
     * 
     * @return the svgRepresentation
     */
    public String getSvgRepresentation() {
        return svgRepresentation;
    }

    /**
     * svgRepresentation setter.
     * 
     * @param svgRepresentation the svgRepresentation to set
     */
    public void setSvgRepresentation(String svgRepresentation) {
        this.svgRepresentation = svgRepresentation;
    }

    /**
     * createdDate getter.
     * 
     * @return the createdDate
     */
    public Instant getCreatedDate() {
        return createdDate;
    }

    /**
     * createdDate setter.
     * 
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * updatedDate getter.
     * 
     * @return the updatedDate
     */
    public Instant getUpdatedDate() {
        return updatedDate;
    }

    /**
     * updatedDate setter.
     * 
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * updatedBy getter.
     * 
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * updatedBy setter.
     * 
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * createdBy getter.
     * 
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * createdBy setter.
     * 
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * microServiceModelUsed getter.
     * 
     * @return the microServiceModelUsed
     */
    public SortedSet<TemplateMicroServiceModel> getMicroServiceModelUsed() {
        return microServiceModelUsed;
    }

    /**
     * microServiceModelUsed setter.
     * 
     * @param microServiceModelUsed the microServiceModelUsed to set
     */
    public void setMicroServiceModelUsed(SortedSet<TemplateMicroServiceModel> microServiceModelUsed) {
        this.microServiceModelUsed = microServiceModelUsed;
    }

    /**
     * maximumInstancesAllowed getter.
     * 
     * @return the maximumInstancesAllowed
     */
    public Integer getMaximumInstancesAllowed() {
        return maximumInstancesAllowed;
    }

    /**
     * maximumInstancesAllowed setter.
     * 
     * @param maximumInstancesAllowed the maximumInstancesAllowed to set
     */
    public void setMaximumInstancesAllowed(Integer maximumInstancesAllowed) {
        this.maximumInstancesAllowed = maximumInstancesAllowed;
    }

    /**
     * Constructor.
     * 
     * @param name                        The loop template name id
     * @param blueprint                   The blueprint containing all microservices
     *                                    (legacy case)
     * @param svgRepresentation           The svg representation of that loop
     *                                    template
     * @param templatesMicroServiceModels The set of TemplateMicroServiceModel
     * @param createdBy                   Who created it
     */
    public LoopTemplate(String name, String blueprint, String svgRepresentation,
            SortedSet<TemplateMicroServiceModel> templatesMicroServiceModels, String createdBy,
            Integer maxInstanceAllowed) {
        this.name = name;
        this.blueprint = blueprint;
        this.svgRepresentation = svgRepresentation;
        this.microServiceModelUsed = templatesMicroServiceModels;
        this.updatedBy = createdBy;
        this.createdBy = createdBy;
        this.maximumInstancesAllowed = maxInstanceAllowed;
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
        LoopTemplate other = (LoopTemplate) obj;
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
