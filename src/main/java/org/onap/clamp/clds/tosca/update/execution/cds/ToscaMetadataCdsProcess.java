/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.tosca.update.execution.cds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import org.onap.clamp.clds.tosca.update.execution.ToscaMetadataProcess;
import org.onap.clamp.loop.service.Service;
import org.onap.clamp.tosca.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is there to add the JsonObject for CDS in the json Schema according to what is found in the Tosca model.
 */
public class ToscaMetadataCdsProcess extends ToscaMetadataProcess {

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public void executeProcess(String parameters, JsonObject childObject, Service serviceModel) {
        switch (parameters) {
            case "actor":
                JsonArray jsonArray = new JsonArray();
                jsonArray.add("CDS");
                addToJsonArray(childObject, "enum", jsonArray);
                break;
            case "payload":
                generatePayload(childObject, serviceModel);
                break;
            case "operation":
                generateOperation(childObject, serviceModel);
                break;
        }
    }

    private static void generatePayload(JsonObject childObject, Service serviceModel) {
        generatePayloadPerResource(childObject, "VF", serviceModel);
        generatePayloadPerResource(childObject, "PNF", serviceModel);
        addToJsonArray(childObject, "anyOf", createBlankEntry());
    }

    private static void generateOperation(JsonObject childObject, Service serviceModel) {
        generateOperationPerResource(childObject, "VF", serviceModel);
        generateOperationPerResource(childObject, "PNF", serviceModel);
    }

    private static void generateOperationPerResource(JsonObject childObject, String resourceName,
                                                     Service serviceModel) {
        JsonArray schemaEnum = new JsonArray();
        JsonArray schemaTitle = new JsonArray();
        for (Map.Entry<String, JsonElement> entry : serviceModel.getResourceDetails().getAsJsonObject(resourceName)
                .entrySet()) {
            JsonObject controllerProperties = entry.getValue().getAsJsonObject()
                    .getAsJsonObject("controllerProperties");
            if (controllerProperties != null) {
                for (String workflowsEntry : controllerProperties.getAsJsonObject("workflows").keySet()) {
                    schemaEnum.add(workflowsEntry);
                    schemaTitle.add(workflowsEntry + " (CDS operation)");
                }
            }
        }
        addToJsonArray(childObject, "enum", schemaEnum);
        if (childObject.get("options") == null) {
            JsonObject optionsSection = new JsonObject();
            childObject.add("options", optionsSection);
        }
        addToJsonArray(childObject.getAsJsonObject("options"), "enum_titles", schemaTitle);

    }

    private static void generatePayloadPerResource(JsonObject childObject, String resourceName,
                                                   Service serviceModel) {
        JsonArray schemaAnyOf = new JsonArray();

        for (Map.Entry<String, JsonElement> entry : serviceModel.getResourceDetails().getAsJsonObject(resourceName)
                .entrySet()) {
            JsonObject controllerProperties = entry.getValue().getAsJsonObject()
                    .getAsJsonObject("controllerProperties");
            if (controllerProperties != null) {
                for (Map.Entry<String, JsonElement> workflowsEntry : controllerProperties.getAsJsonObject("workflows")
                        .entrySet()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("title", workflowsEntry.getKey());
                    obj.add("properties", createPayloadProperty(workflowsEntry.getValue().getAsJsonObject(),
                            controllerProperties));
                    schemaAnyOf.add(obj);
                }
            }
        }
        addToJsonArray(childObject, "anyOf", schemaAnyOf);
    }

    private static JsonArray createBlankEntry() {
        JsonArray result = new JsonArray();
        JsonObject blankObject = new JsonObject();
        blankObject.addProperty("title", "User defined");
        blankObject.add("properties", new JsonObject());
        result.add(blankObject);
        return result;
    }

    private static JsonObject createPayloadProperty(JsonObject workFlow, JsonObject controllerProperties) {
        JsonObject payloadResult = new JsonObject();

        payloadResult.add("artifact_name",
                createAnyOfJsonProperty("artifact_name", controllerProperties.get("sdnc_model_name").getAsString()));
        payloadResult.add("artifact_version",
                createAnyOfJsonProperty("artifact_version",
                        controllerProperties.get("sdnc_model_version").getAsString()));
        payloadResult.add("mode", createAnyOfJsonProperty("mode", "async"));

        payloadResult.add("data", createAnyOfJsonObject("data", workFlow.getAsJsonObject("inputs")));
        return payloadResult;
    }

    private static JsonObject createAnyOfJsonProperty(String name, String defaultValue) {
        JsonObject result = new JsonObject();
        result.addProperty("title", name);
        result.addProperty("type", "string");
        result.addProperty("default", defaultValue);
        result.addProperty("readOnly", "True");
        return result;
    }

    private static JsonObject createAnyOfJsonObject(String name, JsonObject allProperties) {
        JsonObject result = new JsonObject();
        result.addProperty("title", name);
        result.addProperty("type", "object");
        result.add("properties", allProperties);
        return result;
    }

    private static void addToJsonArray(JsonObject childObject, String section, JsonArray value) {
        if (childObject.getAsJsonArray(section) != null) {
            childObject.getAsJsonArray(section).addAll(value);
        }
        else {
            childObject.add(section, value);
        }
    }
}