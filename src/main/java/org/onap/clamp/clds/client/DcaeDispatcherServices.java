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

package org.onap.clamp.clds.client;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.exception.dcae.DcaeDeploymentException;
import org.onap.clamp.clds.util.LoggingUtils;
import org.onap.clamp.util.HttpConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the communication with DCAE for the service
 * deployments.
 */
@Component
public class DcaeDispatcherServices {

    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(DcaeDispatcherServices.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();
    private final ClampProperties refProp;
    private final HttpConnectionManager dcaeHttpConnectionManager;
    private static final String STATUS_URL_LOG = "Status URL extracted: ";
    private static final String DCAE_URL_PREFIX = "/dcae-deployments/";
    private static final String DCAE_URL_PROPERTY_NAME = "dcae.dispatcher.url";
    public static final String DCAE_REQUESTID_PROPERTY_NAME = "dcae.header.requestId";
    private static final String DCAE_LINK_FIELD = "links";
    private static final String DCAE_STATUS_FIELD = "status";

    @Autowired
    public DcaeDispatcherServices(ClampProperties refProp, HttpConnectionManager dcaeHttpConnectionManager) {
        this.refProp = refProp;
        this.dcaeHttpConnectionManager = dcaeHttpConnectionManager;
    }

    /**
     * Get the Operation Status from a specified URL with retry.
     * @param operationStatusUrl
     *        The URL of the DCAE
     * @return The status
     * @throws InterruptedException Exception during the retry
     */
    public String getOperationStatusWithRetry(String operationStatusUrl) throws InterruptedException {
        String operationStatus = "";
        for (int i = 0; i < Integer.valueOf(refProp.getStringValue("dcae.dispatcher.retry.limit")); i++) {
            logger.info("Trying to get Operation status on DCAE for url:" + operationStatusUrl);
            operationStatus = getOperationStatus(operationStatusUrl);
            logger.info("Current Status is:" + operationStatus);
            if (!"processing".equalsIgnoreCase(operationStatus)) {
                return operationStatus;
            } else {
                Thread.sleep(Integer.valueOf(refProp.getStringValue("dcae.dispatcher.retry.interval")));
            }
        }
        logger.warn("Number of attempts on DCAE is over, stopping the getOperationStatus method");
        return operationStatus;
    }

    /**
     * Get the Operation Status from a specified URL.
     * @param statusUrl
     *        The URL provided by a previous DCAE Query
     * @return The status
     */
    public String getOperationStatus(String statusUrl) {
        // Assigning processing status to monitor operation status further
        String opStatus = "processing";
        Date startTime = new Date();
        LoggingUtils.setTargetContext("DCAE", "getOperationStatus");
        try {
            String responseStr = dcaeHttpConnectionManager.doHttpRequest(statusUrl, "GET", null,
                                                                         null, "DCAE", null,
                                                                         null);
            JSONObject jsonObj = parseResponse(responseStr);
            String operationType = (String) jsonObj.get("operationType");
            String status = (String) jsonObj.get(DCAE_STATUS_FIELD);
            logger.info("Operation Type - " + operationType + ", Status " + status);
            LoggingUtils.setResponseContext("0", "Get operation status success", this.getClass().getName());
            opStatus = status;
        } catch (Exception e) {
            LoggingUtils.setResponseContext("900", "Get operation status failed", this.getClass().getName());
            LoggingUtils.setErrorContext("900", "Get operation status error");
            logger.error("Exception occurred during getOperationStatus Operation with DCAE", e);
        } finally {
            LoggingUtils.setTimeContext(startTime, new Date());
            metricsLogger.info("getOperationStatus complete");
        }
        return opStatus;
    }

    /**
     * Returns status URL for createNewDeployment operation.
     * @param deploymentId
     *        The deployment ID
     * @param serviceTypeId
     *        Service type ID
     * @param blueprintInputJson
     *        The value for each blueprint parameters in a flat JSON
     * @return The status URL
     */
    public String createNewDeployment(String deploymentId, String serviceTypeId, JsonObject blueprintInputJson) {
        Date startTime = new Date();
        LoggingUtils.setTargetContext("DCAE", "createNewDeployment");
        try {
            JsonObject rootObject = refProp.getJsonTemplate("dcae.deployment.template").getAsJsonObject();
            rootObject.addProperty("serviceTypeId", serviceTypeId);
            if (blueprintInputJson != null) {
                rootObject.add("inputs", blueprintInputJson);
            }
            String apiBodyString = rootObject.toString();
            logger.info("Dcae api Body String - " + apiBodyString);
            String url = refProp.getStringValue(DCAE_URL_PROPERTY_NAME) + DCAE_URL_PREFIX + deploymentId;
            String statusUrl = getDcaeResponse(url, "PUT", apiBodyString, "application/json", DCAE_LINK_FIELD,
                DCAE_STATUS_FIELD);
            LoggingUtils.setResponseContext("0", "Create new deployment failed", this.getClass().getName());
            return statusUrl;
        } catch (Exception e) {
            LoggingUtils.setResponseContext("900", "Create new deployment failed", this.getClass().getName());
            LoggingUtils.setErrorContext("900", "Create new deployment error");
            logger.error("Exception occurred during createNewDeployment Operation with DCAE", e);
            throw new DcaeDeploymentException("Exception occurred during createNewDeployment Operation with DCAE", e);
        } finally {
            LoggingUtils.setTimeContext(startTime, new Date());
            metricsLogger.info("createNewDeployment complete");
        }
    }

    /**
     * Returns status URL for deleteExistingDeployment operation.
     *
     * @param deploymentId
     *        The deployment ID
     * @param serviceTypeId
     *        The service Type ID
     * @return The status URL
     */
    public String deleteExistingDeployment(String deploymentId, String serviceTypeId) {
        Date startTime = new Date();
        LoggingUtils.setTargetContext("DCAE", "deleteExistingDeployment");
        try {
            String apiBodyString = "{\"serviceTypeId\": \"" + serviceTypeId + "\"}";
            logger.info("Dcae api Body String - " + apiBodyString);
            String url = refProp.getStringValue(DCAE_URL_PROPERTY_NAME) + DCAE_URL_PREFIX + deploymentId;
            String statusUrl = getDcaeResponse(url, "DELETE", apiBodyString, "application/json", DCAE_LINK_FIELD,
                DCAE_STATUS_FIELD);
            LoggingUtils.setResponseContext("0", "Delete existing deployment success", this.getClass().getName());
            return statusUrl;

        } catch (Exception e) {
            LoggingUtils.setResponseContext("900", "Delete existing deployment failed", this.getClass().getName());
            LoggingUtils.setErrorContext("900", "Delete existing deployment error");
            logger.error("Exception occurred during deleteExistingDeployment Operation with DCAE", e);
            throw new DcaeDeploymentException("Exception occurred during deleteExistingDeployment Operation with DCAE",
                e);
        } finally {
            LoggingUtils.setTimeContext(startTime, new Date());
            metricsLogger.info("deleteExistingDeployment complete");
        }
    }

    private String getDcaeResponse(String url, String requestMethod, String payload, String contentType, String node,
        String nodeAttr) throws IOException, ParseException {
        Date startTime = new Date();
        try {
            String responseStr = dcaeHttpConnectionManager.doHttpRequest(url, requestMethod, payload, contentType,
                                                                         "DCAE", null, null);
            JSONObject jsonObj = parseResponse(responseStr);
            JSONObject linksObj = (JSONObject) jsonObj.get(node);
            String statusUrl = (String) linksObj.get(nodeAttr);
            logger.info(STATUS_URL_LOG + statusUrl);
            return statusUrl;
        } catch (IOException | ParseException e) {
            logger.error("Exception occurred getting response from DCAE", e);
            throw e;
        } finally {
            LoggingUtils.setTimeContext(startTime, new Date());
            metricsLogger.info("getDcaeResponse complete");
        }
    }

    private JSONObject parseResponse(String responseStr) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj0 = parser.parse(responseStr);
        JSONObject jsonObj = (JSONObject) obj0;
        return jsonObj;
    }
}