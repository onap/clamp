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

package org.onap.clamp.loop.deploy;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.onap.clamp.clds.sdc.controller.installer.BlueprintArtifact;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.loop.Loop;
import org.onap.clamp.policy.microservice.MicroServicePolicy;
import org.yaml.snakeyaml.Yaml;

public class DeployParameters  {

    private LinkedHashMap<String, JsonObject> deploymentParamMap = new LinkedHashMap<String, JsonObject>();

    public DeployParameters(HashSet<BlueprintArtifact> blueprintArtifactList, Loop loop) {
        this.init(blueprintArtifactList, loop);
    }

    public DeployParameters(BlueprintArtifact blueprintArtifact, Loop loop) {
        HashSet<BlueprintArtifact> blueprintArtifactList = new HashSet<BlueprintArtifact>();
        blueprintArtifactList.add(blueprintArtifact);
        this.init(blueprintArtifactList, loop);
    }

    private void init (HashSet<BlueprintArtifact> blueprintArtifactList, Loop loop) {
        String microServiceName = ((MicroServicePolicy) loop.getMicroServicePolicies().toArray()[0]).getName();
        int i = 0;
        for (BlueprintArtifact blueprintArtifact: blueprintArtifactList) {
            if (i > 0) {
                deploymentParamMap.put(microServiceName+i, generateDeployParameter(blueprintArtifact, microServiceName));
            } else {
                deploymentParamMap.put(microServiceName, generateDeployParameter(blueprintArtifact, microServiceName));
            }
            i++;
        }
    }

    private JsonObject generateDeployParameter(BlueprintArtifact blueprintArtifact, String microServiceName) {
        JsonObject deployJsonBody = new JsonObject();
        Yaml yaml = new Yaml();
        Map<String, Object> inputsNodes = ((Map<String, Object>) ((Map<String, Object>) yaml
            .load(blueprintArtifact.getDcaeBlueprint())).get("inputs"));
        inputsNodes.entrySet().stream().filter(e -> !e.getKey().contains("policy_id")).forEach(elem -> {
            Object defaultValue = ((Map<String, Object>) elem.getValue()).get("default");
            if (defaultValue != null) {
                addPropertyToNode(deployJsonBody, elem.getKey(), defaultValue);
            } else {
                deployJsonBody.addProperty(elem.getKey(), "");
            }
        });
        // For Dublin only one micro service is expected
        deployJsonBody.addProperty("policy_id", microServiceName);
        return deployJsonBody;
    }

    private void addPropertyToNode(JsonObject node, String key, Object value) {
        if (value instanceof String) {
            node.addProperty(key, (String) value);
        } else if (value instanceof Number) {
            node.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            node.addProperty(key, (Boolean) value);
        } else if (value instanceof Character) {
            node.addProperty(key, (Character) value);
        } else {
            node.addProperty(key, JsonUtils.GSON.toJson(value));
        }
    }

    /**
     * Convert the object in Json.
     * 
     * @return The dcae object provisioned
     */
    public JsonObject getDeploymentParametersinJson() {
        JsonObject globalProperties = new JsonObject();
        JsonObject deployParamJson = new JsonObject();
        for (Map.Entry<String, JsonObject> mapElement: deploymentParamMap.entrySet()) {
            deployParamJson.add(mapElement.getKey(), mapElement.getValue());
        } 
        globalProperties.add("dcaeDeployParameters", deployParamJson);
        return globalProperties;
    }

}
