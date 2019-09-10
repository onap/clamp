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

import java.io.IOException;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.model.dcae.DcaeInventoryResponse;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.util.LoggingUtils;
import org.onap.clamp.util.HttpConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the communication with DCAE for the service inventory.
 */
@Component
public class DcaeInventoryServices {

    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(DcaeInventoryServices.class);
    protected static final EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();
    public static final String DCAE_INVENTORY_URL = "dcae.inventory.url";
    public static final String DCAE_INVENTORY_RETRY_INTERVAL = "dcae.intentory.retry.interval";
    public static final String DCAE_INVENTORY_RETRY_LIMIT = "dcae.intentory.retry.limit";
    private final ClampProperties refProp;
    private final HttpConnectionManager httpConnectionManager;

    /**
     * Constructor.
     */
    @Autowired
    public DcaeInventoryServices(ClampProperties refProp, HttpConnectionManager httpConnectionManager) {
        this.refProp = refProp;
        this.httpConnectionManager = httpConnectionManager;
    }

    private int getTotalCountFromDcaeInventoryResponse(String responseStr) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj0 = parser.parse(responseStr);
        JSONObject jsonObj = (JSONObject) obj0;
        Long totalCount = (Long) jsonObj.get("totalCount");
        return totalCount.intValue();
    }

    private DcaeInventoryResponse getItemsFromDcaeInventoryResponse(String responseStr) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj0 = parser.parse(responseStr);
        JSONObject jsonObj = (JSONObject) obj0;
        JSONArray itemsArray = (JSONArray) jsonObj.get("items");
        JSONObject dcaeServiceType0 = (JSONObject) itemsArray.get(0);
        return JsonUtils.GSON.fromJson(dcaeServiceType0.toString(), DcaeInventoryResponse.class);
    }

    /**
     * DO a query to DCAE to get some Information.
     *
     * @param artifactName The artifact Name
     * @param serviceUuid  The service UUID
     * @param resourceUuid The resource UUID
     * @return The DCAE inventory for the artifact in DcaeInventoryResponse
     * @throws IOException    In case of issues with the stream
     * @throws ParseException In case of issues with the Json parsing
     */
    public DcaeInventoryResponse getDcaeInformation(String artifactName, String serviceUuid, String resourceUuid)
            throws IOException, ParseException, InterruptedException {
        LoggingUtils.setTargetContext("DCAE", "getDcaeInformation");
        String queryString = "?asdcResourceId=" + resourceUuid + "&asdcServiceId=" + serviceUuid + "&typeName="
                + artifactName;
        String fullUrl = refProp.getStringValue(DCAE_INVENTORY_URL) + "/dcae-service-types" + queryString;
        logger.info("Dcae Inventory Service full url - " + fullUrl);
        DcaeInventoryResponse response = queryDcaeInventory(fullUrl);
        LoggingUtils.setResponseContext("0", "Get Dcae Information success", this.getClass().getName());
        Date startTime = new Date();
        LoggingUtils.setTimeContext(startTime, new Date());
        return response;
    }

    private DcaeInventoryResponse queryDcaeInventory(String fullUrl)
            throws IOException, InterruptedException, ParseException {
        int retryInterval = 0;
        int retryLimit = 1;
        if (refProp.getStringValue(DCAE_INVENTORY_RETRY_LIMIT) != null) {
            retryLimit = Integer.valueOf(refProp.getStringValue(DCAE_INVENTORY_RETRY_LIMIT));
        }
        if (refProp.getStringValue(DCAE_INVENTORY_RETRY_INTERVAL) != null) {
            retryInterval = Integer.valueOf(refProp.getStringValue(DCAE_INVENTORY_RETRY_INTERVAL));
        }
        for (int i = 0; i < retryLimit; i++) {
            metricsLogger.info("Attempt n°" + i + " to contact DCAE inventory");
            String response = httpConnectionManager.doHttpRequest(fullUrl, "GET", null, null, "DCAE", null, null);
            int totalCount = getTotalCountFromDcaeInventoryResponse(response);
            metricsLogger.info("getDcaeInformation complete: totalCount returned=" + totalCount);
            if (totalCount > 0) {
                logger.info("getDcaeInformation, answer from DCAE inventory:" + response);
                return getItemsFromDcaeInventoryResponse(response);
            }
            logger.info(
                    "Dcae inventory totalCount returned is 0, so waiting " + retryInterval + "ms before retrying ...");
            // wait for a while and try to connect to DCAE again
            Thread.sleep(retryInterval);
        }
        logger.warn("Dcae inventory totalCount returned is still 0, after " + retryLimit + " attempts, returning NULL");
        return null;
    }
}
