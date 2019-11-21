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

import com.google.gson.annotations.Expose;

import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.CascadeType;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

/**
 * Represents a Dictionary Item.
 */
@Entity
@Table(name = "dictionary_elements")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
public class DictionaryItem implements Serializable{

	/**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388644L;

	@Id
    @Expose
    @Column(nullable = false, name = "dict_element_id", unique = true)
	@GeneratedValue(generator="system-uuid")	
	@GenericGenerator(name="system-uuid", strategy = "uuid")
    private String dictElementId;
                
    @Expose
    @Column(nullable = false, name = "dict_element_name", unique = true)
    private String dictElementName;
    
    @Expose
    @Column(nullable = false, name = "dict_element_short_name", unique = true)
    private String dictElementShortName;
    
    @Expose
    @Column(name = "dict_element_description")
    private String dictElementDesc;
    
    @Expose
    @Column(nullable = false, name = "dict_element_type")
    private String dictElementType;
    
    @Expose
    @Column(name = "created_by")
    private String createdBy;
    
    @Expose
    @Column(name = "modified_by")
    private String updatedBy;
    
    @Expose
    @Column(name = "timestamp")
    @UpdateTimestamp
    private Timestamp lastUpdatedDate;
    
    @Column(name = "subdictionary_id", nullable=false)
    @Expose
    private String subDictionary;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;
    
    public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
    
    /**
     * Get the dictionary element id.
     *
     * @return the dictElementId
     */
    public String getDictElementId() {
        return dictElementId;
    }

    /**
     * Set the dictionary element id.
     *
     * @param dictElementId the dictElementId to set
     */
    public void setDictElementId(String dictElementId) {
        this.dictElementId = dictElementId;
    }
    
    /**
     * Get the dictionary name.
     *
     * @return the dictElementName
     */
    public String getDictElementName() {
        return dictElementName;
    }

    /**
     * Set the dictionary name.
     *
     * @param dictElementName the dictElementName to set
     */
    public void setDictElementName(String dictElementName) {
        this.dictElementName = dictElementName;
    }

    /**
     * Get the dictionary element short name.
     *
     * @return the dictElementShortName
     */
    public String getDictElementShortName() {
        return dictElementShortName;
    }

    /**
     * Set the dictionary element short name.
     *
     * @param dictElementShortName the dictElementShortName to set
     */
    public void setDictElementShortName(String dictElementShortName) {
        this.dictElementShortName = dictElementShortName;
    }

    /**
     * Get the dictionary element description.
     *
     * @return the dictElementDesc
     */
    public String getDictElementDesc() {
        return dictElementDesc;
    }

    /**
     * Set the dictionary element description.
     *
     * @param dictElementDesc the dictElementDesc to set
     */
    public void setDictElementDesc(String dictElementDesc) {
        this.dictElementDesc = dictElementDesc;
    }

    /**
     * Get the dictionary element type.
     *
     * @return the dictElementType
     */
    public String getDictElementType() {
        return dictElementType;
    }

    /**
     * Set the dictionary element type.
     *
     * @param dictElementType the dictElementType to set
     */
    public void setDictElementType(String dictElementType) {
        this.dictElementType = dictElementType;
    }
    
    /** 
     * @return the subDictionary 
     */ 
    public String getSubDictionary() { 
        return subDictionary; 
    } 
 
    /** 
     * @param subDictionary 
     *        the subDictionary to set 
     */ 
    public void setSubDictionary(String subDictionary) { 
        this.subDictionary = subDictionary; 
    }

    /**
     * Get the createdBy info.
     *
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy info.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy info.
     *
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy info.
     *
     * @param updatedby the updatedBy to set
     */
    public void setUpdatedBy(String updatedby) {
        updatedBy = updatedby;
    }

    /**
     * Get the last updated date.
     *
     * @return the lastUpdatedDate
     */
    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * Set the last updated date.
     *
     * @param lastUpdatedDate the lastUpdatedDate to set
     */
    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    
    public DictionaryItem()
    {
    }
    
    public DictionaryItem(String dictElementName, Dictionary dictionary, String updatedBy, String createdBy, String dictElementShortName, String dictElementDesc, String dictElementType)
    {
    	this.dictElementName = dictElementName;
    	this.dictionary = dictionary;
    	this.updatedBy = updatedBy;
    	this.createdBy = createdBy;
    	this.dictElementName = dictElementName;
    	this.dictElementShortName = dictElementShortName;
    	this.dictElementType = dictElementType;
    	this.dictElementDesc = dictElementDesc;
    	this.dictionary = dictionary;
    }
        
} 