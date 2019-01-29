/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.model.properties;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.type.TypeReference;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.onap.clamp.clds.util.JsonUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * Parse policyConfigurations from Policy json properties.
 * <p>
 * Example json:
 * "Policy_005sny1":[[{"name":"timeout","value":"5"}],{"policyConfigurations":[[
 * {"name":"recipe","value":["restart"]},{"name":"maxRetries","value":["3"]},{
 * "name":"retryTimeLimit","value":["180"]},{"name":"_id","value":["vf3RtPi"]},{
 * "name":"location","value":["san_diego"]},{"name":"resource","value":["vCTS"]}
 * ,{"name":"onMaxRetriesLimit","value":[""]},{"name":"onTimeLimit","value":[""]
 * },{"name":"onOtherFailure","value":[""]},{"name":"policy_parent","value":[""]
 * }],[{"name":"recipe","value":["rebuild"]},{"name":"maxRetries","value":["3"]}
 * ,{"name":"retryTimeLimit","value":["180"]},{"name":"_id","value":["89z8Ncl"]}
 * ,{"name":"location","value":["san_diego"]},{"name":"resource","value":["vCTS"
 * ]},{"name":"onMaxRetriesLimit","value":[""]},{"name":"onTimeLimit","value":[
 * ""]},{"name":"onOtherFailure","value":[""]},{"name":"policy_parent","value":[
 * "vf3RtPi"]},{"name":
 * "targetResourceId","value":["Eace933104d443b496b8.nodes.heat.vpg"]}]]}]
 */
public class PolicyItem implements Cloneable {
    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(PolicyItem.class);
    protected static final EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();

    private String id;
    private String recipe;
    private int maxRetries;
    private int retryTimeLimit;
    private String parentPolicy;
    private List<String> parentPolicyConditions;
    private String actor;
    private String targetResourceId;
    private String recipeInfo;
    private String recipeLevel;
    private String recipeInput;
    private Map<String, String> recipePayload;
    private String oapRop;
    private String oapLimit;

    private String enableGuardPolicy;
    private String guardPolicyType;
    private String guardTargets;
    private String minGuard;
    private String maxGuard;
    private String limitGuard;
    private String timeUnitsGuard;
    private String timeWindowGuard;
    private String guardActiveStart;
    private String guardActiveEnd;

    /**
     * Parse Policy given json node.
     *
     * @param item
     * @throws IOException
     */
    public PolicyItem(JsonElement item) throws IOException {
        id = JsonUtils.getValueByName(item, "_id");
        recipe = JsonUtils.getValueByName(item, "recipe");
        maxRetries = JsonUtils.getIntValueByName(item, "maxRetries");
        retryTimeLimit = JsonUtils.getIntValueByName(item, "retryTimeLimit");
        parentPolicy = JsonUtils.getValueByName(item, "parentPolicy");
//        parentPolicyConditions = JsonUtils.getValuesByName(item, "parentPolicyConditions");
        targetResourceId = JsonUtils.getValueByName(item, "targetResourceId");
        if (targetResourceId != null && targetResourceId.isEmpty()) {
            this.setTargetResourceId(null);
        }
        recipeInfo = JsonUtils.getValueByName(item, "recipeInfo");
        recipeLevel = JsonUtils.getValueByName(item, "recipeLevel");
        recipeInput = JsonUtils.getValueByName(item, "recipeInput");
        String payload = JsonUtils.getValueByName(item, "recipePayload");

        if (payload != null && !payload.isEmpty()) {
            if (payload.trim().startsWith("{") && payload.trim().endsWith("}")) {
                // Seems to be a JSON
                recipePayload = JsonUtils.GSON.fromJson(payload, new TypeToken<Map<String, String>>() {}.getType());
            } else {
                // SHould be a YAML then
                recipePayload = new Yaml().load(payload);
            }
        }
        oapRop = JsonUtils.getValueByName(item, "oapRop");
        oapLimit = JsonUtils.getValueByName(item, "oapLimit");
        actor = JsonUtils.getValueByName(item, "actor");

        enableGuardPolicy = JsonUtils.getValueByName(item, "enableGuardPolicy");
        guardPolicyType = JsonUtils.getValueByName(item, "guardPolicyType");
        guardTargets = JsonUtils.getValueByName(item, "guardTargets");
        minGuard = JsonUtils.getValueByName(item, "minGuard");
        maxGuard = JsonUtils.getValueByName(item, "maxGuard");
        limitGuard = JsonUtils.getValueByName(item, "limitGuard");
        timeUnitsGuard = JsonUtils.getValueByName(item, "timeUnitsGuard");
        timeWindowGuard = JsonUtils.getValueByName(item, "timeWindowGuard");
        guardActiveStart = JsonUtils.getValueByName(item, "guardActiveStart");
        guardActiveEnd = JsonUtils.getValueByName(item, "guardActiveEnd");
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the recipe
     */
    public String getRecipe() {
        return recipe;
    }

    /**
     * @set the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @set the recipe
     */
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    /**
     * @set the parentPolicy
     */
    public void setParentPolicy(String parentPolicy) {
        this.parentPolicy = parentPolicy;
    }

    /**
     * @set the maxRetries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * @set the retryTimeLimit
     */
    public void setRetryTimeLimit(int retryTimeLimit) {
        this.retryTimeLimit = retryTimeLimit;
    }

    /**
     * @set the parentPolicyConditions
     */
    public void setParentPolicyConditions(List<String> parentPolicyConditions) {
        this.parentPolicyConditions = parentPolicyConditions;
    }

    /**
     * @return the maxRetries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * @return the retryTimeLimit
     */
    public int getRetryTimeLimit() {
        return retryTimeLimit;
    }

    /**
     * @return the parentPolicy
     */
    public String getParentPolicy() {
        return parentPolicy;
    }

    /**
     * @return the parentPolicyConditions
     */
    public List<String> getParentPolicyConditions() {
        return parentPolicyConditions;
    }

    /**
     * @return the actor
     */
    public String getActor() {
        return actor;
    }

    /**
     * @set the actor
     */
    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(String targetResourceId) {
        this.targetResourceId = targetResourceId;
    }

    public String getRecipeInfo() {
        return recipeInfo;
    }

    public String getRecipeLevel() {
        return recipeLevel;
    }

    public String getRecipeInput() {
        return recipeInput;
    }

    public Map<String, String> getRecipePayload() {
        return recipePayload;
    }

    public String getOapRop() {
        if (oapRop == null) {
            oapRop = "0m";
        }
        return oapRop;
    }

    public String getOapLimit() {
        if (oapLimit == null) {
            oapLimit = "0";
        }
        return oapLimit;
    }

    public String getEnableGuardPolicy() {
        return enableGuardPolicy;
    }

    public String getGuardPolicyType() {
        return guardPolicyType;
    }

    public String getGuardTargets() {
        return guardTargets;
    }

    public String getMinGuard() {
        return minGuard;
    }

    public String getMaxGuard() {
        return maxGuard;
    }

    public String getLimitGuard() {
        return limitGuard;
    }

    public String getTimeUnitsGuard() {
        return timeUnitsGuard;
    }

    public String getTimeWindowGuard() {
        return timeWindowGuard;
    }

    public String getGuardActiveStart() {
        return guardActiveStart;
    }

    public String getGuardActiveEnd() {
        return guardActiveEnd;
    }

}
