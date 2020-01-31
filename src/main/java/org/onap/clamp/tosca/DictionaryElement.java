/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
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

package org.onap.clamp.tosca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.onap.clamp.loop.common.AuditEntity;

import com.google.gson.annotations.Expose;

/**
 * Represents a Dictionary Item.
 */
@Entity
@Table(name = "dictionary_elements")
public class DictionaryElement extends AuditEntity implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388644L;

    @Id
    @Expose
    @Column(nullable = false, name = "short_name")
    private String shortName;

    @Expose
    @Column(nullable = false, name = "name")
    private String name;

    @Expose
    @Column(nullable = false, name = "description")
    private String description;

    @Expose
    @Column(nullable = false, name = "type")
    private String type;

    @Expose
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subdictionary_name", referencedColumnName = "name", nullable = true)
    private Dictionary subDictionary;

    @ManyToMany(mappedBy = "dictionaryElements", fetch = FetchType.EAGER)
    private List<Dictionary> usedByDictionaries = new ArrayList<>();

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
     * shortName getter.
     * 
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * shortName setter.
     * 
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * description getter.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * description setter.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * type getter.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * type setter.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * subDictionary getter.
     * 
     * @return the subDictionary
     */
    public Dictionary getSubDictionary() {
        return subDictionary;
    }

    /**
     * subDictionary setter.
     * 
     * @param subDictionary the subDictionary to set
     */
    public void setSubDictionary(Dictionary subDictionary) {
        this.subDictionary = subDictionary;
    }

    /**
     * @return the usedByDictionaries
     */
    public List<Dictionary> getUsedByDictionaries() {
        return usedByDictionaries;
    }

    /**
     * Default Constructor.
     */
    public DictionaryElement() {
    }

    /**
     * Constructor.
     * 
     * @param name The Dictionary element name
     * @param shortName The short name
     * @param description The description
     * @param type The type of element
     * @param subDictionary The sub type
     */
    public DictionaryElement(String name, String shortName, String description, String type, Dictionary subDictionary) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.type = type;
        this.subDictionary = subDictionary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
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
        DictionaryElement other = (DictionaryElement) obj;
        if (shortName == null) {
            if (other.shortName != null) {
                return false;
            }
        } else if (!shortName.equals(other.shortName)) {
            return false;
        }
        return true;
    }
}
