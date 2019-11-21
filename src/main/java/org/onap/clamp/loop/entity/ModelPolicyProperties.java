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


package org.onap.clamp.loop.entity;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;
import org.onap.clamp.loop.Loop;


/**
 * Represents ModelPolicyProperties model.
 *
 */
@Entity
@Table(name = "model_policy_properties")
@TypeDefs({@TypeDef(name = "json", typeClass = StringJsonUserType.class)})
public class ModelPolicyProperties implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522607701388645L;

    @Transient
    private static final EELFLogger logger = EELFManager.getInstance().getLogger(ModelPolicyProperties.class);

    @Id
    @Expose
    @Column(nullable = false, name = "policy_name", unique = true)
    private String policyName;

    @Expose
    @Column(name = "name")
    private String name;

    @Expose
    @Column(name = "policy_type")
    private String policyType;

    @Expose
    @Column(nullable = false, name = "context")
    private String context;

    @Expose
    @Column(nullable = false, name = "device_type_scope")
    private String deviceTypeScope;

    @Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "config_json")
    private String policyJson;

    @Expose
    @Column(name = "user_id")
    private String userId;

    @Expose
    @UpdateTimestamp
    @Column(name = "timestamp")
    private Timestamp lastUpdatedDate;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Loop loop;

    public void setLoop(Loop loop) {
        this.loop = loop;
    }

    public Loop getLoop() {
        return loop;
    }


    /**
     * Returns the policy name.
     *
     * @return the policyName
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Set the policy name.
     *
     * @param policyName the policyName to set
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * Get name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get policy type.
     *
     * @return the policyType
     */
    public String getPolicyType() {
        return policyType;
    }

    /**
     * Set policy type.
     *
     * @param policyType the policyType to set
     */
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    /**
     * Get context.
     *
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * Set context.
     *
     * @param context the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Get the device type scope.
     *
     * @return the deviceTypeScope
     */
    public String getDeviceTypeScope() {
        return deviceTypeScope;
    }

    /**
     * Sets the device type scope.
     *
     * @param deviceTypeScope the deviceTypeScope to set
     */
    public void setDeviceTypeScope(String deviceTypeScope) {
        this.deviceTypeScope = deviceTypeScope;
    }

    /**
     * Get the policy json.
     *
     * @return the policyJson
     */
    public String getPolicyJson() {
        return policyJson;
    }

    /**
     * Sets the policy json.
     *
     * @param policyJson the policyJson to set
     */
    public void setPolicyJson(String policyJson) {
        this.policyJson = policyJson;
    }

    /**
     * Get user id.
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set user id.
     *
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get last updated date.
     *
     * @return the lastUpdatedDate
     */
    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * Set last updated date.
     *
     * @param lastUpdatedDate the lastUpdatedDate to set
     */
    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public ModelPolicyProperties() {

    }

    /**
     * Constructor with parameters.
     *
     * @param policyName policy name
     * @param name name
     * @param policyType policy type
     * @param policyJson policy json
     * @param userId user id
     * @param deviceTypeScope device type scope
     * @param context context
     * @param loop loop object
     */
    public ModelPolicyProperties(String policyName, String name, String policyType, String policyJson, String userId,
            String deviceTypeScope, String context, Loop loop) {
        this.policyName = policyName;
        this.name = name;
        this.policyType = policyType;
        this.policyJson = policyJson;
        this.userId = userId;
        this.deviceTypeScope = deviceTypeScope;
        this.context = context;
        this.loop = loop;
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    /*
     * @Override public String toString() { StringBuilder builder = new StringBuilder();
     * builder.append("ModelPolicyProperties [modelId="); builder.append(modelId); builder.append(", policyName=");
     * builder.append(policyName); builder.append(", name="); builder.append(name); builder.append(", policyType=");
     * builder.append(policyType); builder.append(", context="); builder.append(context);
     * builder.append(", deviceTypeScope="); builder.append(deviceTypeScope); builder.append(", policyJson=");
     * builder.append(policyJson); builder.append(", userId="); builder.append(userId);
     * builder.append(", lastUpdatedDate="); builder.append(lastUpdatedDate); builder.append("]"); return
     * builder.toString(); }
     */


}

