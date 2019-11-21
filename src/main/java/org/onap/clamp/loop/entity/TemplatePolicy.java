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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;


@Entity
@Table(name = "loop_template_policy")
@TypeDefs({@TypeDef(name = "json", typeClass = StringJsonUserType.class)})
public class TemplatePolicy implements Serializable {
    /**
     * The serial version ID.
     */
    private static final long serialVersionUID = 6117076450841538255L;

    @Transient
    private static final EELFLogger logger = EELFManager.getInstance().getLogger(TemplatePolicy.class);

    @Id
    @Expose
    @Column(nullable = false, name = "template_policy_id", unique = true)
    private String templatePolicyId;

    // @Id
    @Expose
    @Column(name = "policy_model_id")
    private String policyModelId;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private LoopTemplate loopTemplate;


    public LoopTemplate getLoopTemplate() {
        return loopTemplate;
    }

    public void setLoopTemplate(LoopTemplate loopTemplate) {
        this.loopTemplate = loopTemplate;
    }

    public String getPolicyModelId() {
        return policyModelId;
    }

    public void setPolicyModelId(String policyModelId) {
        this.policyModelId = policyModelId;
    }

    public TemplatePolicy() {
        // Serialization
    }

    /**
     * Constructor with parameters.
     *
     * @param loopTemplate loop template object
     * @param policyModelId policy model id
     * @param templatePolicyId template policy id
     */
    public TemplatePolicy(LoopTemplate loopTemplate, String policyModelId, String templatePolicyId) {
        this.loopTemplate = loopTemplate;
        this.policyModelId = policyModelId;
        this.templatePolicyId = templatePolicyId;
    }


}
