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

package org.onap.clamp.loop.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

import com.google.gson.annotations.Expose;

/**
 * Represents a Tosca model.
 */

@Entity
@Table(name = "tosca_model")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })

public class ToscaModelDetails implements Serializable {
	
	/**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701376645L;
    
    @Id
	@Expose
    @Column(nullable = false, name = "tosca_model_id", unique = true)	 
	@GeneratedValue(generator="system-uuid")	
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String toscaModelId;
		
    @Expose
    @Column(nullable = false, name = "tosca_model_name", unique = true)
    private String toscaModelName;
			
    @Expose
    @Column(nullable = false, name = "policy_type", unique = true)
    private String policyType;
        
    
    @Expose
    @Column(name = "user_id")
    private String userId;
    
    @Expose
    @UpdateTimestamp
    @Column(name = "timestamp")
    private Timestamp lastUpdatedDate;
            
	@Expose
    @OneToMany(mappedBy="toscaModelDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   private List<ToscaModelRevision> toscaModelRevisions = new ArrayList<>();

    
    /**
     * Get the id.
     * @return the id
     */
    public String getToscaModelId() {
        return toscaModelId;
    }

    /**
     * Set the id.
     * @param id
     *        the id to set
     */
    public void setToscaModelId(String toscaModelId) {
        this.toscaModelId = toscaModelId;
    }

    /**
     * Get the tosca model name.
     * @return the toscaModelName
     */
    public String getToscaModelName() {
        return toscaModelName;
    }

    /**
     * Set the tosca model name.
     * @param toscaModelName
     *        the toscaModelName to set
     */
    public void setToscaModelName(String toscaModelName) {
        this.toscaModelName = toscaModelName;
    }

    /**
     * Get the policy type.
     * @return the policyType
     */
    public String getPolicyType() {
        return policyType;
    }

    /**
     * Set the policy type.
     * @param policyType
     *        the policyType to set
     */
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    /**
     * Get the list of tosca model revisions.
     * @return the toscaModelRevisions
     */
    public List<ToscaModelRevision> getToscaModelRevisions() {
        return toscaModelRevisions;
    }

    /**
     * Set the list of tosca model revisions.
     * @param toscaModelRevisions
     *        the toscaModelRevisions to set
     */
    public void setToscaModelRevisions(List<ToscaModelRevision> toscaModelRevisions) {
        this.toscaModelRevisions = toscaModelRevisions;
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
          
    
    public ToscaModelDetails() {		
	}
	
	public ToscaModelDetails(String toscaModelId, String toscaModelName, String policyType, String userId,
			Timestamp timestamp) {
		super();
		this.toscaModelId = toscaModelId;
		this.toscaModelName = toscaModelName;
		this.policyType = policyType;
		this.userId = userId;
		this.lastUpdatedDate = timestamp;
	} 
		
}
