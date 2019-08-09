package org.onap.clamp.policy.operational;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.clamp.loop.Loop;

public class OperationalPolicyRepresentationBuilder {

    /*
     * "oneOf": [ { "title": "test1", "properties": { "type": { "title":
     * "Target type", "type": "string", "default": "VNF", "readOnly": "True" },
     * "resourceID": { "title": "Target Resource ID", "type": "string", "default":
     * "vnfname1", "readOnly": "True" } } }, { "title": "test2", "properties": {
     * "type": { "title": "Target type", "type": "string", "default": "VFMODULE",
     * "readOnly": "True" }, "resourceID": { "title": "Target Resource ID", "type":
     * "string", "default": "vfmodule1", "readOnly": "True" }, "modelInvariantId": {
     * "title": "Target Resource ID", "type": "string", "default":
     * "ca052563-eb92-4b5b-ad41-9111768ce043", "readOnly": "True" } } } ]
     */

    public static JsonObject generateJsonRepresentation(Loop loop) throws JsonSyntaxException, IOException {
        JsonObject jsonSchema = JsonUtils.GSON.fromJson(
                ResourceFileUtil.getResourceAsString("clds/json-schema/operational_policies/operational_policy.json"),
                JsonObject.class);
        jsonSchema.get("schema").getAsJsonObject().get("items").getAsJsonObject().get("properties").getAsJsonObject()
        .get("configurationsJson").getAsJsonObject().get("properties").getAsJsonObject()
        .get("operational_policy").getAsJsonObject() .get("properties").getAsJsonObject().get("policies")
        .getAsJsonObject().get("items").getAsJsonObject().get("properties").getAsJsonObject().get("target")
        .getAsJsonObject().get("oneOf")
    }
}
