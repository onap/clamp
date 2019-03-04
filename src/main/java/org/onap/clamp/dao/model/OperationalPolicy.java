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

package org.onap.clamp.dao.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;
import org.onap.clamp.clds.serialization.JsonObjectAttributeConverter;

@Entity
@Table(name = "operational_policies")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class OperationalPolicy implements Serializable, Policy {
    /**
     *
     */
    private static final long serialVersionUID = 6117076450841538255L;

    @Expose
    @Id
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @Expose
    @Convert(converter = JsonObjectAttributeConverter.class)
    @Column(columnDefinition = "json", name = "configurations_json")
    private JsonObject configurationsJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loop_id", nullable = false)
    private Loop loop;

    public OperationalPolicy() {
        //Serialization
    }

    public OperationalPolicy(String name, Loop loop, JsonObject configurationsJson) {
        this.name = name;
        this.loop = loop;
        this.configurationsJson = configurationsJson;
    }

    public String getName() {
        return name;
    }

    @Override
    public JsonObject getJsonRepresentation() {
        return configurationsJson;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonObject getConfigurationsJson() {
        return configurationsJson;
    }

    public void setConfigurationsJson(JsonObject configurationsJson) {
        this.configurationsJson = configurationsJson;
    }

}
