/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
 *                             reserved.
 * Modifications Copyright (C) 2020 Huawei Technologies Co., Ltd.
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

package org.onap.clamp.policy.operational;

import static org.onap.clamp.clds.tosca.update.execution.cds.ToscaMetadataCdsProcess.createInputPropertiesForPayload;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map.Entry;

import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.clamp.loop.service.Service;

public class OperationalPolicyRepresentationBuilder {

    private static final EELFLogger logger =
            EELFManager.getInstance().getLogger(OperationalPolicyRepresentationBuilder.class);

    /**
     * This method generates the operational policy json representation that will be
     * used by ui for rendering. It uses the model (VF and VFModule) defined in the
     * loop object to do so, so it's dynamic. It also uses the operational policy
     * schema template defined in the resource folder.
     *
     * @param modelJson The loop model json
     * @return The json representation
     */
    public static JsonObject generateOperationalPolicySchema(Service modelJson) {

        JsonObject jsonSchema = null;
        try {
            jsonSchema = JsonUtils.GSON.fromJson(
                    ResourceFileUtil
                            .getResourceAsString("clds/json-schema/operational_policies/operational_policy.json"),
                    JsonObject.class);
            jsonSchema.get("properties").getAsJsonObject()
                    .get("operational_policy").getAsJsonObject().get("properties").getAsJsonObject().get("policies")
                    .getAsJsonObject().get("items").getAsJsonObject().get("properties").getAsJsonObject().get("target")
                    .getAsJsonObject().get("anyOf").getAsJsonArray().addAll(createAnyOfArray(modelJson, true));

            // update CDS recipe and payload information to schema
            JsonArray actors = jsonSchema.get("properties").getAsJsonObject()
                    .get("operational_policy").getAsJsonObject().get("properties").getAsJsonObject().get("policies")
                    .getAsJsonObject().get("items").getAsJsonObject().get("properties").getAsJsonObject().get("actor")
                    .getAsJsonObject().get("anyOf").getAsJsonArray();

            for (JsonElement actor : actors) {
                if ("CDS".equalsIgnoreCase(actor.getAsJsonObject().get("title").getAsString())) {
                    actor.getAsJsonObject().get("properties").getAsJsonObject().get("recipe").getAsJsonObject()
                            .get("anyOf").getAsJsonArray()
                            .addAll(createAnyOfArrayForCdsRecipe(modelJson));
                }
            }
            return jsonSchema;
        } catch (IOException e) {
            logger.error("Unable to generate the json schema because of an exception", e);
            return new JsonObject();
        }
    }

    private static JsonObject createSchemaProperty(String title, String type, String defaultValue, String readOnlyFlag,
                                                   String[] enumArray) {
        JsonObject property = new JsonObject();
        property.addProperty("title", title);
        property.addProperty("type", type);
        property.addProperty("default", defaultValue);
        property.addProperty("readOnly", readOnlyFlag);

        if (enumArray != null) {
            JsonArray jsonArray = new JsonArray();
            property.add("enum", jsonArray);
            for (String val : enumArray) {
                jsonArray.add(val);
            }
        }
        return property;
    }

    private static JsonArray createVnfSchema(Service modelService, boolean generateType) {
        JsonArray vnfSchemaArray = new JsonArray();
        JsonObject modelVnfs = modelService.getResourceByType("VF");

        for (Entry<String, JsonElement> entry : modelVnfs.entrySet()) {
            JsonObject vnfOneOfSchema = new JsonObject();
            vnfOneOfSchema.addProperty("title", "VNF" + "-" + entry.getKey());
            JsonObject properties = new JsonObject();
            if (generateType) {
                properties.add("type", createSchemaProperty("Type", "string", "VNF", "True", null));
            }
            properties.add("resourceID", createSchemaProperty("Resource ID", "string",
                    modelVnfs.get(entry.getKey()).getAsJsonObject().get("name").getAsString(), "True", null));

            vnfOneOfSchema.add("properties", properties);
            vnfSchemaArray.add(vnfOneOfSchema);
        }
        return vnfSchemaArray;
    }

    private static JsonArray createVfModuleSchema(Service modelService, boolean generateType) {
        JsonArray vfModuleOneOfSchemaArray = new JsonArray();
        JsonObject modelVfModules = modelService.getResourceByType("VFModule");

        for (Entry<String, JsonElement> entry : modelVfModules.entrySet()) {
            JsonObject vfModuleOneOfSchema = new JsonObject();
            vfModuleOneOfSchema.addProperty("title", "VFMODULE" + "-" + entry.getKey());
            JsonObject properties = new JsonObject();
            if (generateType) {
                properties.add("type", createSchemaProperty("Type", "string", "VFMODULE", "True", null));
            }
            properties.add("resourceID",
                    createSchemaProperty("Resource ID", "string",
                            modelVfModules.get(entry.getKey()).getAsJsonObject().get("vfModuleModelName").getAsString(),
                            "True", null));
            properties.add("modelInvariantId",
                    createSchemaProperty("Model Invariant Id (ModelInvariantUUID)", "string",
                            modelVfModules.get(entry.getKey()).getAsJsonObject().get("vfModuleModelInvariantUUID")
                                    .getAsString(),
                            "True", null));
            properties.add("modelVersionId",
                    createSchemaProperty("Model Version Id (ModelUUID)", "string",
                            modelVfModules.get(entry.getKey()).getAsJsonObject().get("vfModuleModelUUID").getAsString(),
                            "True", null));
            properties.add("modelName",
                    createSchemaProperty("Model Name", "string",
                            modelVfModules.get(entry.getKey()).getAsJsonObject().get("vfModuleModelName").getAsString(),
                            "True", null));
            properties.add("modelVersion", createSchemaProperty("Model Version", "string",
                    modelVfModules.get(entry.getKey()).getAsJsonObject().get("vfModuleModelVersion").getAsString(),
                    "True", null));
            properties
                    .add("modelCustomizationId",
                            createSchemaProperty("Customization ID", "string",
                                    modelVfModules.get(entry.getKey()).getAsJsonObject()
                                            .get("vfModuleModelCustomizationUUID").getAsString(), "True",
                                    null));

            vfModuleOneOfSchema.add("properties", properties);
            vfModuleOneOfSchemaArray.add(vfModuleOneOfSchema);
        }
        return vfModuleOneOfSchemaArray;
    }

    /**
     * Create an anyOf array of possible structure we may have for Target.
     *
     * @param modelJson The service object
     * @return A JsonArray with everything inside
     */
    public static JsonArray createAnyOfArray(Service modelJson, boolean generateType) {
        JsonArray targetOneOfStructure = new JsonArray();
        targetOneOfStructure.addAll(createVnfSchema(modelJson, generateType));
        targetOneOfStructure.addAll(createVfModuleSchema(modelJson, generateType));
        return targetOneOfStructure;
    }

    private static JsonArray createAnyOfArrayForCdsRecipe(Service modelJson) {
        JsonArray anyOfStructure = new JsonArray();
        anyOfStructure.addAll(createAnyOfCdsRecipe(modelJson.getResourceDetails().getAsJsonObject("VF")));
        anyOfStructure.addAll(createAnyOfCdsRecipe(modelJson.getResourceDetails().getAsJsonObject("PNF")));
        return anyOfStructure;
    }

    private static JsonArray createAnyOfCdsRecipe(JsonObject jsonObject) {
        JsonArray schemaArray = new JsonArray();
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonObject controllerProperties = entry.getValue().getAsJsonObject()
                    .getAsJsonObject("controllerProperties");

            if (controllerProperties != null) {
                JsonObject workflows = controllerProperties.getAsJsonObject("workflows");
                for (Entry<String, JsonElement> workflowsEntry : workflows.entrySet()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("title", workflowsEntry.getKey());
                    obj.addProperty("type", "object");
                    obj.add("properties", createPayloadProperty(workflowsEntry.getValue().getAsJsonObject(),
                            controllerProperties, workflowsEntry.getKey()));
                    schemaArray.add(obj);
                }

            }
        }
        return schemaArray;
    }

    private static JsonObject createPayloadProperty(JsonObject workFlow,
                                                    JsonObject controllerProperties, String workFlowName) {
        JsonObject payload = new JsonObject();
        payload.addProperty("title", "Payload (YAML)");
        payload.addProperty("type", "object");
        payload.add("properties", createInputPropertiesForPayload(workFlow,
                                                                  controllerProperties, true));
        JsonObject properties = new JsonObject();
        properties.add("recipe", createRecipeForCdsWorkflow(workFlowName));
        properties.add("payload", payload);
        return properties;
    }

    private static JsonObject createRecipeForCdsWorkflow(String workflow) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("title", "recipe");
        recipe.addProperty("type", "string");
        recipe.addProperty("default", workflow);
        JsonObject options = new JsonObject();
        options.addProperty("hidden", true);
        recipe.add("options", options);
        return recipe;
    }
}
