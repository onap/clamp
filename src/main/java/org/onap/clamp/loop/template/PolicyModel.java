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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

/**
 * This class represents the policy model tosca revision that we can have to a
 * specific microservice.
 */
@Entity
@Table(name = "policy_models")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
public class PolicyModel implements Serializable, Comparable<PolicyModel> {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522705701376645L;

    /**
     * This variable is used to store the type mentioned in the micro-service
     * blueprint.
     */
    @Id
    @Expose
    @Column(nullable = false, name = "model_type")
    private String modelType;

    /**
     * Semantic versioning on policy side.
     */
    @Id
    @Expose
    @Column(name = "version")
    private String version;

    @Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "policy_tosca")
    private String policyModelTosca;

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
     * policyModelTosca getter.
     * 
     * @return the policyModelTosca
     */
    public String getPolicyModelTosca() {
        return policyModelTosca;
    }

    /**
     * policyModelTosca setter.
     * 
     * @param policyModelTosca the policyModelTosca to set
     */
    public void setPolicyModelTosca(String policyModelTosca) {
        this.policyModelTosca = policyModelTosca;
    }

    /**
     * @return the modelType
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * @param modelType the modelType to set
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * version getter.
     * 
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * version getter as DefaultArtifactVersion.
     * 
     * @return the version
     */
    public DefaultArtifactVersion getComparableVersion() {
        return new DefaultArtifactVersion(version);
    }

    /**
     * version setter.
     * 
     * @param version the version to set
     */
    public void setVersion(String version) {
        // Try to convert it before
        new DefaultArtifactVersion(version);
        this.version = version;
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
     * Default constructor for serialization.
     */
    public PolicyModel() {
    }

    /**
     * Constructor.
     * 
     * @param policyType       The policyType (referenced in the blueprint)
     * @param policyModelTosca The policy tosca model in yaml
     * @param version          the version like 1.0.0
     * @param updatedBy        Who updates it
     * @param createdBy        Who creates it
     */
    public PolicyModel(String policyType, String policyModelTosca, String version, String updatedBy, String createdBy) {
        this.modelType = policyType;
        this.policyModelTosca = policyModelTosca;
        this.version = version;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelType == null) ? 0 : modelType.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        PolicyModel other = (PolicyModel) obj;
        if (modelType == null) {
            if (other.modelType != null) {
                return false;
            }
        } else if (!modelType.equals(other.modelType)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(PolicyModel arg0) {
        // Reverse it, so that by default we have the latest
        if (getComparableVersion() == null) {
            return 1;
        }
        if (arg0.getComparableVersion() == null) {
            return -1;
        }
        return this.getComparableVersion().compareTo(arg0.getComparableVersion());
    }
}
