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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "templates_microservicemodels")
public class TemplateMicroServiceModel implements Serializable, Comparable<TemplateMicroServiceModel> {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 5924989899078094245L;

    @Id
    @Expose
    @ManyToOne
    @JoinColumn(name = "loop_template_name")
    private LoopTemplate loopTemplate;

    @Id
    @Expose
    @ManyToOne
    @JoinColumn(name = "micro_service_model_name")
    private MicroServiceModel microServiceModel;

    @Expose
    @Column(nullable = false, name = "flow_order")
    private Integer flowOrder;

    /**
     * @return the loopTemplate
     */
    public LoopTemplate getLoopTemplate() {
        return loopTemplate;
    }

    /**
     * @param loopTemplate the loopTemplate to set
     */
    public void setLoopTemplate(LoopTemplate loopTemplate) {
        this.loopTemplate = loopTemplate;
    }

    /**
     * @return the microServiceModel
     */
    public MicroServiceModel getMicroServiceModel() {
        return microServiceModel;
    }

    /**
     * @param microServiceModel the microServiceModel to set
     */
    public void setMicroServiceModel(MicroServiceModel microServiceModel) {
        this.microServiceModel = microServiceModel;
    }

    /**
     * @return the flowOrder
     */
    public Integer getFlowOrder() {
        return flowOrder;
    }

    /**
     * @param flowOrder the flowOrder to set
     */
    public void setFlowOrder(Integer flowOrder) {
        this.flowOrder = flowOrder;
    }

    @Override
    public int compareTo(TemplateMicroServiceModel arg0) {
        // Reverse it, so that by default we have the latest
        if (getFlowOrder() == null) {
            return 1;
        }
        if (arg0.getFlowOrder() == null) {
            return -1;
        }
        return arg0.getFlowOrder().compareTo(this.getFlowOrder());
    }
}
