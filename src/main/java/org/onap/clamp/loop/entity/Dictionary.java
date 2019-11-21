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

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * Represents Dictionary.
 */

@Entity
@Table(name = "dictionary")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
public class Dictionary implements Serializable {
	
	/**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388645L;
	
	@Id
	@Expose
    @Column(nullable = false, name = "dictionary_id", unique = true)
	@GeneratedValue(generator="system-uuid")	
	@GenericGenerator(name="system-uuid", strategy = "uuid")
    private String dictionaryId;
    
    @Expose
    @Column(nullable = false, name = "dictionary_name", unique = true)
    private String dictionaryName;

	@Expose
	@CreatedBy
    @Column(name = "created_by")
    private String createdBy;
    
	@Expose
	@LastModifiedBy
    @Column(name = "modified_by")    
    private String updatedBy;
    
    @Expose
    @Column(name = "timestamp")
    @UpdateTimestamp    
    private Timestamp lastUpdatedDate;
    
    @Expose
    @Column(name = "dictionary_sec_level")
    private int secLevelDictionary;
    
    @Expose
    @Column(name = "dictionary_type")
	private String subDictionaryType;
        
    @Expose
    @OneToMany(mappedBy="dictionary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DictionaryItem> dictionaryItems = new ArrayList<>();
    
    public Dictionary() {	
	}
        
    /**
     * Get the dictionary ID.
     * 
     * @return the dictionaryId
     */
    public String getDictionaryId() {
        return dictionaryId;
    }

    /**
     * Set the dictionary Id.
     * 
     * @param dictionaryId the dictionaryId to set
     */
    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    /**
     * Get the dictionary name.
     * 
     * @return the dictionaryName
     */
    public String getDictionaryName() {
        return dictionaryName;
    }

    /**
     * Set the dictionary name.
     * 
     * @param dictionaryName the dictionaryName to set
     */
    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
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
    
    /** 
     * @return the is2ndLevelDictionary 
     */ 
    public int get2ndLevelDictionary() { 
        return secLevelDictionary; 
    } 
 
    /** 
     * @param is2ndLevelDictionary 
     *        the is2ndLevelDictionary to set 
     */ 
    public void setSecLevelDictionary(int secLevelDictionary) { 
        this.secLevelDictionary = secLevelDictionary; 
    } 
 
    /** 
     * @return the subDictionaryType 
     */ 
    public String getSubDictionaryType() { 
        return subDictionaryType; 
    } 
 
    /** 
     * @param subDictionaryType 
     *        the subDictionaryType to set 
     */ 
    public void setSubDictionaryType(String subDictionaryType) { 
        this.subDictionaryType = subDictionaryType; 
    }

    /**
     * Get all the dictionary items.
     * 
     * @return the cldsDictionaryItems
     */
    public List<DictionaryItem> getDictionaryItems() {
        return dictionaryItems;
    } 

    /**
     * Set the whole dictionary items.
     * 
     * @param DictionaryItems the DictionaryItems to set
     */
    public void setDictionaryItems(List<DictionaryItem> DictionaryItems) {
        this.dictionaryItems = DictionaryItems;
    } 
            
}
