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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

@Entity
@Table(name = "loop_templates")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
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
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "templates_microservicemodels", joinColumns = @JoinColumn(name = "loop_template_id"), inverseJoinColumns = @JoinColumn(name = "micro_service_model_id"))
    @SortNatural
    private SortedSet<MicroServiceModel> microServiceModels = new TreeSet<>();

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
     * @return the svgRepresentation
     */
    public String getSvgRepresentation() {
        return svgRepresentation;
    }

    /**
     * @param svgRepresentation the svgRepresentation to set
     */
    public void setSvgRepresentation(String svgRepresentation) {
        this.svgRepresentation = svgRepresentation;
    }

    /**
     * @return the microServiceModels
     */
    public Set<MicroServiceModel> getMicroServiceModels() {
        return microServiceModels;
    }

    /**
     * @param microServiceModels the microServiceModels to set
     */
    public void setMicroServiceModels(SortedSet<MicroServiceModel> microServiceModels) {
        this.microServiceModels = microServiceModels;
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
     * Constructor.
     * 
     * @param id
     * @param name
     * @param blueprint
     * @param svgRepresentation
     * @param microServiceModels
     * @param updatedBy
     * @param createdBy
     */
    public LoopTemplate(String name, String blueprint, String svgRepresentation,
            SortedSet<MicroServiceModel> microServiceModels, String updatedBy, String createdBy) {
        this.name = name;
        this.blueprint = blueprint;
        this.svgRepresentation = svgRepresentation;
        this.microServiceModels = microServiceModels;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
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
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
