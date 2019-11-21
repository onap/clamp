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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;

@Entity
@Table(name = "loop_template")
@TypeDefs({@TypeDef(name = "json", typeClass = StringJsonUserType.class)})
public class LoopTemplate implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388642L;


    @Expose
    @Column(nullable = false, name = "template_name", unique = true)
    private String templateName;

    @Id
    @Expose
    @Column(nullable = false, name = "template_id", unique = true)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String templateId;

    @Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "template_yaml")
    private String templateYaml;

    @Expose
    @Column(columnDefinition = "MEDIUMTEXT", name = "svg_image")
    private String templateImage;

    @Expose
    @Column(name = "updated_by")
    private String updatedBy;

    @Expose
    @UpdateTimestamp
    @Column(name = "timestamp")
    private Timestamp timestamp;


    @Expose
    @OneToMany(mappedBy = "loopTemplate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<TemplatePolicy> templatePolicy = new HashSet<>();

    public Set<TemplatePolicy> getTemplatePolicy() {
        return templatePolicy;
    }

    public void setTemplatePolicy(Set<TemplatePolicy> templatePolicy) {
        this.templatePolicy = templatePolicy;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateYaml() {
        return templateYaml;
    }

    public void setTemplateYaml(String templateYaml) {
        this.templateYaml = templateYaml;
    }

    public String getTemplateImage() {
        return templateImage;
    }

    public void setTemplateImage(String templateImage) {
        this.templateImage = templateImage;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public LoopTemplate() {

    }

    /**
     * Constructor with parameters.
     *
     * @param templateName template name
     * @param templateId template id
     * @param templateYaml template yaml
     * @param templateImage template image
     * @param updatedBy updated by user
     * @param timestamp timestamp
     */
    public LoopTemplate(String templateName, String templateId, String templateYaml, String templateImage,
            String updatedBy, Timestamp timestamp) {
        super();
        this.templateName = templateName;
        this.templateId = templateId;
        this.templateYaml = templateYaml;
        this.templateImage = templateImage;
        this.updatedBy = updatedBy;
        this.timestamp = timestamp;
    }

}
