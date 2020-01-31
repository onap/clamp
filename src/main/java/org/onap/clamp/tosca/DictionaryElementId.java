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

import com.google.gson.annotations.Expose;

public class DictionaryElementId implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 4089888115504914773L;

    @Expose
    private String name;

    @Expose
    private String shortName;

    @Expose
    private String description;

    /**
     * Default constructor for serialization.
     */
    public DictionaryElementId() {

    }

    /**
     * Constructor.
     * 
     * @param name The dictionary name id
     * @param shortName The dictionary element short name id
     * @param description The dictionary element description id
     */
    public DictionaryElementId(String name, String shortName, String description) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
    }

    /**
     * dictionary element name getter
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * dictionary element name setter
     * 
     * @param name the dictionary element Name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * dictionary element short Name getter
     * 
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * dictionary element short Name setter
     * 
     * @param shortName the dictionary element short Name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * dictionary element description getter
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * dictionary element description setter
     * 
     * @param description the dictionary element description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
