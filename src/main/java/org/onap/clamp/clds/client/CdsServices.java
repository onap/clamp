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
 * Modifications copyright (c) 2018 Nokia
 * ===================================================================
 *
 */

package org.onap.clamp.clds.client;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.util.Json;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.model.cds.CdsBpWorkFlowListResponse;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.LoggingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * This class implements the communication with DCAE for the service inventory.
 */
@Component
public class CdsServices {

    @Autowired
    CamelContext camelContext;

    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(CdsServices.class);
    protected static final EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();
    public static final String CDS_INVENTORY_URL = "cds.inventory.url";
    public static final String CDS_INVENTORY_RETRY_INTERVAL = "cds.intentory.retry.interval";
    public static final String CDS_INVENTORY_RETRY_LIMIT = "cds.intentory.retry.limit";
    private final ClampProperties refProp;

    /**
     * Constructor.
     */
    @Autowired
    public CdsServices(ClampProperties refProp) {
        this.refProp = refProp;
    }


    /**
     * Do a query to CDS Blueprint workflow list.
     *
     */
    public CdsBpWorkFlowListResponse getBlueprintWorkflowList(String blueprintName, String blueprintVersion) {
        LoggingUtils.setTargetContext("CDS", "getBlueprintWorkflowList");

        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext)
                    .withProperty("blueprintName", blueprintName).withProperty("blueprintVersion", blueprintVersion)
                    .build();

        Exchange exchangeResponse = camelContext.createProducerTemplate()
                    .send("direct:get-blueprint-workflow-list", myCamelExchange);

        if (Integer.valueOf(200).equals(exchangeResponse.getIn().getHeader("CamelHttpResponseCode"))) {
            String cdsResponse = (String) exchangeResponse.getIn().getBody();
            logger.info("getBlueprintWorkflowList, answer from CDS:" + cdsResponse);
            LoggingUtils.setResponseContext("0", "Get Blueprint workflow list", this.getClass().getName());
            Date startTime = new Date();
            LoggingUtils.setTimeContext(startTime, new Date());
            return JsonUtils.GSON_JPA_MODEL.fromJson(cdsResponse, CdsBpWorkFlowListResponse.class);
        }
        return null;
    }

    public JsonObject getWorkflowInputProperties(String blueprintName, String blueprintVersion,
                                                 String workflow) {
        LoggingUtils.setTargetContext("CDS", "getWorkflowInputProperties");

        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext)
                .withBody(getCdsPayloadForWorkFlow(blueprintName, blueprintVersion, workflow))
                .build();

        Exchange exchangeResponse = camelContext.createProducerTemplate()
                .send("direct:get-blueprint-workflow-input-properties", myCamelExchange);

        if (Integer.valueOf(200).equals(exchangeResponse.getIn().getHeader("CamelHttpResponseCode"))) {
            String cdsResponse = (String) exchangeResponse.getIn().getBody();
            logger.info("getBlueprintWorkflowList, answer from CDS:" + cdsResponse);
            LoggingUtils.setResponseContext("0", "Get Blueprint workflow list", this.getClass().getName());
            Date startTime = new Date();
            LoggingUtils.setTimeContext(startTime, new Date());
            return parseCdsResponse(cdsResponse);
        }
        return null;
    }

    protected JsonObject parseCdsResponse(String response) {
        JsonObject root = JsonParser.parseString(response).getAsJsonObject();
        JsonObject inputs = root.getAsJsonObject("workFlowData").getAsJsonObject("inputs");
        JsonObject dataTypes = root.getAsJsonObject("workFlowData").getAsJsonObject("dataTypes");

        JsonObject workFlowProperties = new JsonObject();
        workFlowProperties.add("inputs", getInputProperties(inputs, dataTypes));
        return workFlowProperties;
    }

    protected JsonObject getInputProperties(JsonObject inputs, JsonObject dataTypes) {
        JsonObject inputObject = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : inputs.entrySet()) {
            String key = entry.getKey();
            JsonObject inputProperty = inputs.getAsJsonObject(key);
            String type = inputProperty.get("type").toString();
            if (isComplexType(type, dataTypes)) {
                inputObject.add(key, handleComplexType(key, dataTypes));
            } else {
                inputObject.addProperty(key, "");
            }
        }
        return inputObject;
    }

    protected JsonObject handleComplexType(String key, JsonObject dataTypes) {
        JsonObject properties = dataTypes.get(key).getAsJsonObject().get("properties").getAsJsonObject();
        return getInputProperties(properties, dataTypes);
    }

    protected boolean isComplexType(String type, JsonObject dataTypes) {
        if (dataTypes.get(type) == null) {
            return false;
        }
        return true;
    }

    public String getCdsPayloadForWorkFlow(String blueprintName, String version, String workflow) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("blueprintName", blueprintName);
        jsonObject.addProperty("version", version);
        jsonObject.addProperty("returnContent", "json");
        jsonObject.addProperty("workflowName", workflow);
        jsonObject.addProperty("specType", "TOSCA");
        return jsonObject.toString();
    }
}
