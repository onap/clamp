/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights
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
 * Modifications copyright (c) 2019 AT&T
 * ===================================================================
 *
 */

package org.onap.clamp.clds.sdc.controller.installer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class BlueprintParser {

    static final String TCA = "TCA";
    static final String HOLMES = "Holmes";
    private static final String TCA_POLICY = "tca_policy";
    private static final String HOLMES_PREFIX = "holmes";
    private static final String NODE_TEMPLATES = "node_templates";
    private static final String DCAE_NODES = "dcae.nodes.";
    private static final String TYPE = "type";
    private static final String PROPERTIES = "properties";
    private static final String NAME = "name";
    private static final String INPUT = "inputs";
    private static final String GET_INPUT = "get_input";
    private static final String POLICY_MODELID = "policy_model_id";
    private static final String RELATIONSHIPS = "relationships";
    private static final String CLAMP_NODE_RELATIONSHIPS_GETS_INPUT_FROM = "clamp_node.relationships.gets_input_from";
    private static final String TARGET = "target";

    /**
     * Get all micro services from blueprint.
     * 
     * @param blueprintString the blueprint in a String
     * @return A set of MircoService
     */
    public Set<MicroService> getMicroServices(String blueprintString) {
        Set<MicroService> microServices = new HashSet<>();
        JsonObject blueprintJson = BlueprintParser.convertToJson(blueprintString);
        JsonObject nodeTemplateList = blueprintJson.get(NODE_TEMPLATES).getAsJsonObject();
        JsonObject inputList = blueprintJson.get(INPUT).getAsJsonObject();

        for (Entry<String, JsonElement> entry : nodeTemplateList.entrySet()) {
            JsonObject nodeTemplate = entry.getValue().getAsJsonObject();
            if (nodeTemplate.get(TYPE).getAsString().contains(DCAE_NODES)) {
                MicroService microService = getNodeRepresentation(entry, nodeTemplateList, inputList);
                microServices.add(microService);
            }
        }
        microServices.removeIf(ms -> TCA_POLICY.equals(ms.getName()));
        return microServices;
    }

    /**
     * Does a fallback to TCA or Holmes.
     * 
     * @param blueprintString the blueprint in a String
     * @return The list of microservices
     */
    public List<MicroService> fallbackToOneMicroService(String blueprintString) {
        JsonObject jsonObject = BlueprintParser.convertToJson(blueprintString);
        JsonObject results = jsonObject.get(NODE_TEMPLATES).getAsJsonObject();
        String theBiggestMicroServiceContent = "";
        String theBiggestMicroServiceKey = "";
        for (Entry<String, JsonElement> entry : results.entrySet()) {
            String msAsString = entry.getValue().toString();
            int len = msAsString.length();
            if (len > theBiggestMicroServiceContent.length()) {
                theBiggestMicroServiceContent = msAsString;
                theBiggestMicroServiceKey = entry.getKey();
            }
        }
        String msName = theBiggestMicroServiceKey.toLowerCase().contains(HOLMES_PREFIX) ? HOLMES : TCA;
        return Collections
                .singletonList(new MicroService(msName, "onap.policies.monitoring.cdap.tca.hi.lo.app", "", ""));
    }

    String getName(Entry<String, JsonElement> entry) {
        String microServiceYamlName = entry.getKey();
        JsonObject ob = entry.getValue().getAsJsonObject();
        if (ob.has(PROPERTIES)) {
            JsonObject properties = ob.get(PROPERTIES).getAsJsonObject();
            if (properties.has(NAME)) {
                return properties.get(NAME).getAsString();
            }
        }
        return microServiceYamlName;
    }

    String getInput(Entry<String, JsonElement> entry) {
        JsonObject ob = entry.getValue().getAsJsonObject();
        if (ob.has(RELATIONSHIPS)) {
            JsonArray relationships = ob.getAsJsonArray(RELATIONSHIPS);
            for (JsonElement element : relationships) {
                String target = getTarget(element.getAsJsonObject());
                if (!target.isEmpty()) {
                    return target;
                }
            }
        }
        return "";
    }

    String findModelTypeInTargetArray(JsonArray jsonArray, JsonObject nodeTemplateList, JsonObject inputList) {
        for (JsonElement elem : jsonArray) {
            String modelType = getModelType(
                    new AbstractMap.SimpleEntry<String, JsonElement>(elem.getAsJsonObject().get(TARGET).getAsString(),
                            nodeTemplateList.get(elem.getAsJsonObject().get(TARGET).getAsString()).getAsJsonObject()),
                    nodeTemplateList, inputList);
            if (!modelType.isEmpty()) {
                return modelType;
            }
        }
        return "";
    }

    String getModelType(Entry<String, JsonElement> entry, JsonObject nodeTemplateList, JsonObject inputList) {
        JsonObject ob = entry.getValue().getAsJsonObject();
        // Search first in this node template
        if (ob.has(PROPERTIES)) {
            JsonObject properties = ob.get(PROPERTIES).getAsJsonObject();
            if (properties.has(POLICY_MODELID)) {
                if (properties.get(POLICY_MODELID).isJsonObject()) {
                    // it's a blueprint parameter
                    return inputList.get(properties.get(POLICY_MODELID).getAsJsonObject().get(GET_INPUT).getAsString())
                            .getAsJsonObject().get("default").getAsString();
                } else {
                    // It's a direct value
                    return properties.get(POLICY_MODELID).getAsString();
                }
            }
        }
        // Or it's may be defined in a relationship
        if (ob.has(RELATIONSHIPS)) {
            return findModelTypeInTargetArray(ob.get(RELATIONSHIPS).getAsJsonArray(), nodeTemplateList, inputList);
        }
        return "";
    }

    MicroService getNodeRepresentation(Entry<String, JsonElement> entry, JsonObject nodeTemplateList,
            JsonObject inputList) {
        String name = getName(entry);
        String getInputFrom = getInput(entry);
        String modelType = getModelType(entry, nodeTemplateList, inputList);
        return new MicroService(name, modelType, getInputFrom, "");
    }

    private String getTarget(JsonObject elementObject) {
        if (elementObject.has(TYPE) && elementObject.has(TARGET)
                && elementObject.get(TYPE).getAsString().equals(CLAMP_NODE_RELATIONSHIPS_GETS_INPUT_FROM)) {
            return elementObject.get(TARGET).getAsString();
        }
        return "";
    }

    private static JsonObject convertToJson(String yamlString) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlString);

        JSONObject jsonObject = new JSONObject(map);
        return new Gson().fromJson(jsonObject.toString(), JsonObject.class);
    }
}
