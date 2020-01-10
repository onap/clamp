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

package org.onap.clamp.loop.template;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TemplateMicroServiceModelId implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 4089888115504914773L;

    @Expose
    @Column(name = "loop_template_name")
    private String loopTemplateName;

    @Expose
    @Column(name = "micro_service_model_name")
    private String microServiceModelName;

    /**
     * Default constructor for serialization.
     */
    public TemplateMicroServiceModelId() {

    }

    /**
     * Constructor.
     * 
     * @param loopTemplateName      The loop template name id
     * @param microServiceModelName THe micro Service name id
     */
    public TemplateMicroServiceModelId(String loopTemplateName, String microServiceModelName) {
        this.loopTemplateName = loopTemplateName;
        this.microServiceModelName = microServiceModelName;
    }

    /**
     * loopTemplateName getter.
     * 
     * @return the loopTemplateName
     */
    public String getLoopTemplateName() {
        return loopTemplateName;
    }

    /**
     * loopTemplateName setter.
     * 
     * @param loopTemplateName the loopTemplateName to set
     */
    public void setLoopTemplateName(String loopTemplateName) {
        this.loopTemplateName = loopTemplateName;
    }

    /**
     * microServiceModelName getter.
     * 
     * @return the microServiceModelName
     */
    public String getMicroServiceModelName() {
        return microServiceModelName;
    }

    /**
     * microServiceModelName setter.
     * 
     * @param microServiceModelName the microServiceModelName to set
     */
    public void setMicroServiceModelName(String microServiceModelName) {
        this.microServiceModelName = microServiceModelName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((loopTemplateName == null) ? 0 : loopTemplateName.hashCode());
        result = prime * result + ((microServiceModelName == null) ? 0 : microServiceModelName.hashCode());
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
        TemplateMicroServiceModelId other = (TemplateMicroServiceModelId) obj;
        if (loopTemplateName == null) {
            if (other.loopTemplateName != null) {
                return false;
            }
        } else if (!loopTemplateName.equals(other.loopTemplateName)) {
            return false;
        }
        if (microServiceModelName == null) {
            if (other.microServiceModelName != null) {
                return false;
            }
        } else if (!microServiceModelName.equals(other.microServiceModelName)) {
            return false;
        }
        return true;
    }

}
