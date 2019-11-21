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

/**
 * Represents a CLDS Tosca model revision.
 */

package org.onap.clamp.loop.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tosca_model_revision")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class ToscaModelRevision implements Serializable {
    
	/**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522705701376645L;

	@Id
	@Expose
    @Column(nullable = false, name = "tosca_model_revision_id", unique = true)
	@GeneratedValue(generator="system-uuid")	
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String revisionId;
    
	@Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "tosca_model_yaml")
	private String toscaModelYaml;
	
	
	@Expose
	@Column(name = "version")
    private double version;
    
	@Expose
	@Type(type = "json")
	@Column(columnDefinition = "json", name = "tosca_model_json")
	private JsonObject toscaModelJson;
	
	@Expose
    @Column(name = "user_id")
    private String userId;
	
	@Expose
	@CreationTimestamp
	@Column(name = "created_timestamp")
    private Timestamp createdDate;
    
	@Expose
	@UpdateTimestamp
	@Column(name = "last_updated_timestamp")
	private Timestamp lastUpdatedDate;

	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tosca_model_id")
    private ToscaModelDetails toscaModelDetails;
	
    
    public ToscaModelDetails geToscaModelDetails() {
		return toscaModelDetails;
	}

	public void setToscaModelDetails(ToscaModelDetails toscaModelDetails) {
		this.toscaModelDetails = toscaModelDetails;
	}

	/**
     * Get the revision id.
     * @return the revisionId
     */
    public String getRevisionId() {
        return revisionId;
    }

    /**
     * Set the revision id.
     * @param revisionId
     *        the revisionId to set
     */
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * Get the tosca model yaml.
     * @return the toscaModelYaml
     */
    public String getToscaModelYaml() {
        return toscaModelYaml;
    }

    /**
     * Set the tosca model yaml.
     * @param toscaModelYaml
     *        the toscaModelYaml to set
     */
    public void setToscaModelYaml(String toscaModelYaml) {
        this.toscaModelYaml = toscaModelYaml;
    }

    /**
     * Get the version.
     * @return the version
     */
    public double getVersion() {
        return version;
    }

    /**
     * Set the version.
     * @param version
     *        the version to set
     */
    public void setVersion(double version) {
        this.version = version;
    }

    /**
     * Get the tosca model json.
     * @return the toscaModelJson
     */
    public JsonObject getToscaModelJson() {
        return toscaModelJson;
    }

    /**
     * Set the tosca model json.
     * @param toscaModelJson
     *        the toscaModelJson to set
     */
    public void setToscaModelJson(JsonObject toscaModelJson) {
        this.toscaModelJson = toscaModelJson;
    }

    /**
     * Get the user id.
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user id.
     * @param userId
     *        the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the created date.
     * @return the createdDate
     */
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    /**
     * Set the created date.
     * @param createdDate
     *        the createdDate to set
     */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Get the last updated date.
     * @return the lastUpdatedDate
     */
    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * Set the last updated date.
     * @param lastUpdatedDate
     *        the lastUpdatedDate to set
     */
    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    
        
    public ToscaModelRevision() {    	
		
	}
	
	public ToscaModelRevision(String toscaModelRevisionId, int version, String toscaModelYaml,
			JsonObject toscaModelJson, String userId, Timestamp timestamp, ToscaModelDetails toscaModelDetails) {
		this.revisionId = toscaModelRevisionId;
	    this.version = version;
		this.toscaModelYaml = toscaModelYaml;
		this.toscaModelJson = toscaModelJson;
		this.userId = userId;
		this.createdDate = timestamp;
		this.lastUpdatedDate = timestamp;
		this.toscaModelDetails=toscaModelDetails;
	}
}