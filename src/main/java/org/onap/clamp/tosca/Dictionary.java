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

package org.onap.clamp.tosca;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

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
    @Column(nullable = false, name = "name", unique = true)
    private String name;

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

    @Expose
    @Column(name = "dictionary_second_level")
    private int secondLevelDictionary;

    @Expose
    @Column(name = "dictionary_type")
    private String subDictionaryType;

    @Expose
    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DictionaryElement> dictionaryElements = new ArrayList<>();

}
