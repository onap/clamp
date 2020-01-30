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

package org.onap.clamp.loop;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.onap.clamp.dao.model.jsontype.StringJsonUserType;
import org.onap.clamp.loop.common.AuditEntity;
import org.onap.clamp.loop.components.external.DcaeComponent;
import org.onap.clamp.loop.components.external.ExternalComponent;
import org.onap.clamp.loop.components.external.PolicyComponent;
import org.onap.clamp.loop.log.LoopLog;
import org.onap.clamp.loop.service.Service;
import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.policy.microservice.MicroServicePolicy;
import org.onap.clamp.policy.operational.OperationalPolicy;

@Entity
@Table(name = "loops")
@TypeDefs({ @TypeDef(name = "json", typeClass = StringJsonUserType.class) })
public class Loop extends AuditEntity implements Serializable {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -286522707701388642L;

    @Transient
    private static final EELFLogger logger = EELFManager.getInstance().getLogger(Loop.class);

    @Id
    @Expose
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @Expose
    @Column(name = "dcae_deployment_id")
    private String dcaeDeploymentId;

    @Expose
    @Column(name = "dcae_deployment_status_url")
    private String dcaeDeploymentStatusUrl;

    @Column(columnDefinition = "MEDIUMTEXT", name = "svg_representation")
    private String svgRepresentation;

    @Expose
    @Type(type = "json")
    @Column(columnDefinition = "json", name = "global_properties_json")
    private JsonObject globalPropertiesJson;

    @Expose
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "service_uuid")
    private Service modelService;

    @Expose
    @Column(nullable = false, name = "last_computed_state")
    @Enumerated(EnumType.STRING)
    private LoopState lastComputedState;

    @Expose
    @Transient
    private final Map<String, ExternalComponent> components = new HashMap<>();

    @Expose
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop", orphanRemoval = true)
    private Set<OperationalPolicy> operationalPolicies = new HashSet<>();

    @Expose
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinTable(name = "loops_to_microservicepolicies", joinColumns = @JoinColumn(name = "loop_name"),
            inverseJoinColumns = @JoinColumn(name = "microservicepolicy_name"))
    private Set<MicroServicePolicy> microServicePolicies = new HashSet<>();

    @Expose
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loop", orphanRemoval = true)
    @SortNatural
    private SortedSet<LoopLog> loopLogs = new TreeSet<>();

    @Expose
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "loop_template_name")
    private LoopTemplate loopTemplate;

    private void initializeExternalComponents() {
        this.addComponent(new PolicyComponent());
        this.addComponent(new DcaeComponent());
    }

    /**
     * Public constructor.
     */
    public Loop() {
        initializeExternalComponents();
    }

    /**
     * Constructor.
     */
    public Loop(String name, String svgRepresentation) {
        this.name = name;
        this.svgRepresentation = svgRepresentation;
        this.lastComputedState = LoopState.DESIGN;
        this.globalPropertiesJson = new JsonObject();
        initializeExternalComponents();
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDcaeDeploymentId() {
        return dcaeDeploymentId;
    }

    void setDcaeDeploymentId(String dcaeDeploymentId) {
        this.dcaeDeploymentId = dcaeDeploymentId;
    }

    public String getDcaeDeploymentStatusUrl() {
        return dcaeDeploymentStatusUrl;
    }

    void setDcaeDeploymentStatusUrl(String dcaeDeploymentStatusUrl) {
        this.dcaeDeploymentStatusUrl = dcaeDeploymentStatusUrl;
    }

    public String getSvgRepresentation() {
        return svgRepresentation;
    }

    void setSvgRepresentation(String svgRepresentation) {
        this.svgRepresentation = svgRepresentation;
    }

    public LoopState getLastComputedState() {
        return lastComputedState;
    }

    void setLastComputedState(LoopState lastComputedState) {
        this.lastComputedState = lastComputedState;
    }

    public Set<OperationalPolicy> getOperationalPolicies() {
        return operationalPolicies;
    }

    void setOperationalPolicies(Set<OperationalPolicy> operationalPolicies) {
        this.operationalPolicies = operationalPolicies;
    }

    public Set<MicroServicePolicy> getMicroServicePolicies() {
        return microServicePolicies;
    }

    void setMicroServicePolicies(Set<MicroServicePolicy> microServicePolicies) {
        this.microServicePolicies = microServicePolicies;
    }

    public JsonObject getGlobalPropertiesJson() {
        return globalPropertiesJson;
    }

    void setGlobalPropertiesJson(JsonObject globalPropertiesJson) {
        this.globalPropertiesJson = globalPropertiesJson;
    }

    public Set<LoopLog> getLoopLogs() {
        return loopLogs;
    }

    void setLoopLogs(SortedSet<LoopLog> loopLogs) {
        this.loopLogs = loopLogs;
    }

    void addOperationalPolicy(OperationalPolicy opPolicy) {
        operationalPolicies.add(opPolicy);
        opPolicy.setLoop(this);
    }

    void addMicroServicePolicy(MicroServicePolicy microServicePolicy) {
        microServicePolicies.add(microServicePolicy);
        microServicePolicy.getUsedByLoops().add(this);
    }

    public void addLog(LoopLog log) {
        log.setLoop(this);
        this.loopLogs.add(log);
    }

    public Service getModelService() {
        return modelService;
    }

    void setModelService(Service modelService) {
        this.modelService = modelService;
    }

    public Map<String, ExternalComponent> getComponents() {
        return components;
    }

    public ExternalComponent getComponent(String componentName) {
        return this.components.get(componentName);
    }

    public void addComponent(ExternalComponent component) {
        this.components.put(component.getComponentName(), component);
    }

    public LoopTemplate getLoopTemplate() {
        return loopTemplate;
    }

    public void setLoopTemplate(LoopTemplate loopTemplate) {
        this.loopTemplate = loopTemplate;
    }

    /**
     * Generate the loop name.
     *
     * @param serviceName       The service name
     * @param serviceVersion    The service version
     * @param resourceName      The resource name
     * @param blueprintFileName The blueprint file name
     * @return The generated loop name
     */
    public static String generateLoopName(String serviceName, String serviceVersion, String resourceName,
            String blueprintFilename) {
        StringBuilder buffer = new StringBuilder("LOOP_").append(serviceName).append("_v").append(serviceVersion)
                .append("_").append(resourceName).append("_").append(blueprintFilename.replaceAll(".yaml", ""));
        return buffer.toString().replace('.', '_').replaceAll(" ", "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Loop other = (Loop) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
