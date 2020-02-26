/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
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
import org.onap.clamp.loop.common.AuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name="cds_blueprints")
public class CdsBlueprintInfo extends AuditEntity implements Serializable {

    @Id
    @Expose
    @Column(name = "blueprint_id")
    String blueprint_id;

    @Expose
    @Column(name = "blueprint_name")
    String blueprintName;

    @Expose
    @Column(name = "blueprint_version")
    String blueprintVersion;

    @OneToMany(mappedBy = "cdsBlueprintInfo")
    List<CdsBlueprintWorkflowInfo> blueprintWorkflowInfoList;

    @ManyToOne
    @MapsId("loopTemplateName")
    @JoinColumn(name = "loop_template_name")
    private LoopTemplate loopTemplate;

    public String getBlueprintName() {
        return blueprintName;
    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public String getBlueprintVersion() {
        return blueprintVersion;
    }

    public void setBlueprintVersion(String blueprintVersion) {
        this.blueprintVersion = blueprintVersion;
    }

    public List<CdsBlueprintWorkflowInfo> getBlueprintWorkflowInfoList() {
        return blueprintWorkflowInfoList;
    }

    public void setBlueprintWorkflowInfoList(List<CdsBlueprintWorkflowInfo> blueprintWorkflowInfoList) {
        this.blueprintWorkflowInfoList = blueprintWorkflowInfoList;
    }

    public String getBlueprint_id() {
        return blueprint_id;
    }

    public void setBlueprint_id(String blueprint_id) {
        this.blueprint_id = blueprint_id;
    }

    public void addWorkFlow(CdsBlueprintWorkflowInfo workflow) {
        if (blueprintWorkflowInfoList == null) {
            blueprintWorkflowInfoList = new LinkedList<CdsBlueprintWorkflowInfo>();
        }
        blueprintWorkflowInfoList.add(workflow);
        workflow.setCdsBlueprintInfo(this);
    }

    public LoopTemplate getLoopTemplate() {
        return loopTemplate;
    }

    public void setLoopTemplate(LoopTemplate loopTemplate) {
        this.loopTemplate = loopTemplate;
    }
}
