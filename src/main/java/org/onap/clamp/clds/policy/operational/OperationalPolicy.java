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

package org.onap.clamp.clds.policy.operational;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.TypeDef;
import org.onap.clamp.clds.serialization.JsonObjectAttributeConverter;
import org.onap.clamp.clds.policy.Policy;

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

    @Column(name = "loop_id", nullable = false, updatable = false)
    private String loop;

    public OperationalPolicy() {
        //Serialization
    }

    public OperationalPolicy(String name, String loop, JsonObject configurationsJson) {
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

    public JsonObject getConfigurationsJson() {
        return configurationsJson;
    }

    public void setConfigurationsJson(JsonObject configurationsJson) {
        this.configurationsJson = configurationsJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationalPolicy that = (OperationalPolicy) o;
        return Objects.equal(name, that.name) &&
            Objects.equal(configurationsJson, that.configurationsJson) &&
            Objects.equal(loop, that.loop);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, configurationsJson);
    }
}
