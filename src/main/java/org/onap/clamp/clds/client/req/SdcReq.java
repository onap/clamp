/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.client.req;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.onap.clamp.clds.client.SdcCatalogServices;
import org.onap.clamp.clds.model.CldsSdcResource;
import org.onap.clamp.clds.model.CldsSdcServiceDetail;
import org.onap.clamp.clds.model.prop.Global;
import org.onap.clamp.clds.model.prop.ModelProperties;
import org.onap.clamp.clds.model.prop.Tca;
import org.onap.clamp.clds.model.refprop.RefProp;

/**
 * Construct a Sdc request given CLDS objects.
 */
public class SdcReq {
    protected static final EELFLogger logger        = EELFManager.getInstance().getLogger(SdcReq.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();

    /**
     * Format the Blueprint from a Yaml
     *
     * @param refProp
     *            The RefProp instance containing the Clds config
     * @param prop
     *            The ModelProperties describing the clds model
     * @param docText
     *            The Yaml file that must be converted
     *
     * @return A String containing the BluePrint
     * @throws JsonParseException
     *             In case of issues
     * @throws JsonMappingException
     *             In case of issues
     * @throws IOException
     *             In case of issues
     */
    public static String formatBlueprint(RefProp refProp, ModelProperties prop, String docText)
            throws JsonParseException, JsonMappingException, IOException {

        Global globalProp = prop.getGlobal();
        String service = globalProp.getService();

        String yamlvalue = getYamlvalue(docText);

        String updatedBlueprint = "";
        Tca tca = prop.getType(Tca.class);
        if (tca.isFound()) {
        	updatedBlueprint = TcaRequestFormatter.updatedBlueprintWithConfiguration(refProp, prop, yamlvalue);
        }
        logger.info("value of blueprint:" + updatedBlueprint);
        return updatedBlueprint;
    }

    private static String getUpdatedBlueprintWithConfiguration(RefProp refProp, ModelProperties prop, String yamlValue,
            ObjectNode serviceConf) throws IOException {
        String blueprint = "";
        Yaml yaml = new Yaml();
        // Serialiaze Yaml file
        Map<String, Map> loadedYaml = (Map<String, Map>) yaml.load(yamlValue);
        // Get node templates information from Yaml
        Map<String, Map> nodeTemplates = loadedYaml.get("node_templates");
        logger.info("value of NodeTemplates:" + nodeTemplates);
        // Get Tca Object information from node templates of Yaml
        Map<String, Map> tcaObject = nodeTemplates.get("MTCA");
        logger.info("value of Tca:" + tcaObject);
        // Get Properties Object information from tca of Yaml
        Map<String, String> propsObject = tcaObject.get("properties");
        logger.info("value of PropsObject:" + propsObject);
        String deploymentJsonObject = propsObject.get("deployment_JSON");
        logger.info("value of deploymentJson:" + deploymentJsonObject);

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deployJsonNode = (ObjectNode) mapper.readTree(deploymentJsonObject);

        // "policyName":"example_model06.ClosedLoop_FRWL_SIG_0538e6f2_8c1b_4656_9999_3501b3c59ad7_Tca_",
        String policyNamePrefix = refProp.getStringValue("policy.ms.policyNamePrefix");
        String policyName = prop.getCurrentPolicyScopeAndFullPolicyName(policyNamePrefix);
        serviceConf.put("policyName", policyName);

        deployJsonNode.set("configuration", serviceConf);
        propsObject.put("deployment_JSON", deployJsonNode.toString());
        blueprint = yaml.dump(loadedYaml);
        logger.info("value of updated Yaml File:" + blueprint);

        return blueprint;
    }

    public static String formatSdcLocationsReq(ModelProperties prop, String artifactName) {
        ObjectMapper objectMapper = new ObjectMapper();
        Global global = prop.getGlobal();
        List<String> locationsList = global.getLocation();
        ArrayNode locationsArrayNode = objectMapper.createArrayNode();
        ObjectNode locationObject = objectMapper.createObjectNode();
        for (String currLocation : locationsList) {
            locationsArrayNode.add(currLocation);
        }
        locationObject.put("artifactName", artifactName);
        locationObject.putPOJO("locations", locationsArrayNode);
        String locationJsonFormat = locationObject.toString();
        logger.info("Value of locaation Json Artifact:" + locationsArrayNode);
        return locationJsonFormat;
    }

    public static String formatSdcReq(String payloadData, String artifactName, String artifactLabel,
            String artifactType) throws IOException {
        logger.info("artifact=" + payloadData);
        String base64Artifact = base64Encode(payloadData);
        return "{ \n" + "\"payloadData\" : \"" + base64Artifact + "\",\n" + "\"artifactLabel\" : \"" + artifactLabel
                + "\",\n" + "\"artifactName\" :\"" + artifactName + "\",\n" + "\"artifactType\" : \"" + artifactType
                + "\",\n" + "\"artifactGroupType\" : \"DEPLOYMENT\",\n" + "\"description\" : \"from CLAMP Cockpit\"\n"
                + "} \n";
    }

    public static String getSdcReqUrl(ModelProperties prop, String url) {
        Global globalProps = prop.getGlobal();
        String serviceUUID = "";
        String resourceInstanceName = "";
        if (globalProps != null) {
            List<String> resourceVf = globalProps.getResourceVf();
            if (resourceVf != null && !resourceVf.isEmpty()) {
                resourceInstanceName = resourceVf.get(0);
            }
            if (globalProps.getService() != null) {
                serviceUUID = globalProps.getService();
            }
        }
        String normalizedResourceInstanceName = normalizeResourceInstanceName(resourceInstanceName);
        return url + "/" + serviceUUID + "/resourceInstances/" + normalizedResourceInstanceName + "/artifacts";
    }

    /**
     * To get List of urls for all vfresources
     *
     * @param prop
     * @param baseUrl
     * @param sdcCatalogServices
     * @return
     */
    public static List<String> getSdcReqUrlsList(ModelProperties prop, String baseUrl,
            SdcCatalogServices sdcCatalogServices, DelegateExecution execution) {
        // TODO : refact and regroup with very similar code
        List<String> urlList = new ArrayList<>();

        Global globalProps = prop.getGlobal();
        if (globalProps != null) {
            if (globalProps.getService() != null) {
                String serviceInvariantUUID = globalProps.getService();
                execution.setVariable("serviceInvariantUUID", serviceInvariantUUID);
                List<String> resourceVfList = globalProps.getResourceVf();
                String serviceUUID = sdcCatalogServices.getServiceUuidFromServiceInvariantId(serviceInvariantUUID);
                String sdcServicesInformation = sdcCatalogServices.getSdcServicesInformation(serviceUUID);
                CldsSdcServiceDetail cldsSdcServiceDetail = sdcCatalogServices
                        .getCldsSdcServiceDetailFromJson(sdcServicesInformation);
                if (cldsSdcServiceDetail != null && resourceVfList != null) {
                    List<CldsSdcResource> cldsSdcResourcesList = cldsSdcServiceDetail.getResources();
                    if (cldsSdcResourcesList != null && !cldsSdcResourcesList.isEmpty()) {
                        for (CldsSdcResource CldsSdcResource : cldsSdcResourcesList) {
                            if (CldsSdcResource != null && CldsSdcResource.getResoucreType() != null
                                    && CldsSdcResource.getResoucreType().equalsIgnoreCase("VF")
                                    && resourceVfList.contains(CldsSdcResource.getResourceInvariantUUID())) {
                                String normalizedResourceInstanceName = normalizeResourceInstanceName(
                                        CldsSdcResource.getResourceInstanceName());
                                String svcUrl = baseUrl + "/" + serviceUUID + "/resourceInstances/"
                                        + normalizedResourceInstanceName + "/artifacts";
                                urlList.add(svcUrl);

                            }
                        }
                    }
                }
            }
        }

        return urlList;
    }

    /**
     * "Normalize" the resource instance name: - Remove spaces, underscores,
     * dashes, and periods. - make lower case This is required by SDC when using
     * the resource instance name to upload an artifact.
     *
     * @param inText
     * @return
     */
    public static String normalizeResourceInstanceName(String inText) {
        return inText.replace(" ", "").replace("-", "").replace(".", "").toLowerCase();
    }

    /**
     * from michael
     *
     * @param data
     * @return
     */
    public static String calculateMD5ByString(String data) {
        String calculatedMd5 = DigestUtils.md5Hex(data);
        // encode base-64 result
        return base64Encode(calculatedMd5.getBytes());
    }

    /**
     * Base 64 encode a String.
     *
     * @param inText
     * @return
     */
    public static String base64Encode(String inText) {
        return base64Encode(stringToByteArray(inText));
    }

    /**
     * Convert String to byte array.
     *
     * @param inText
     * @return
     */
    public static byte[] stringToByteArray(String inText) {
        return inText.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Base 64 encode a byte array.
     *
     * @param bytes
     * @return
     */
    public static String base64Encode(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    /**
     * Return SDC id and pw as a HTTP Basic Auth string (for example: Basic
     * dGVzdDoxMjM0NTY=).
     *
     * @return
     */
    public static String getSdcBasicAuth(RefProp refProp) {
        String sdcId = refProp.getStringValue("sdc.serviceUsername");
        String sdcPw = refProp.getStringValue("sdc.servicePassword");
        String idPw = base64Encode(sdcId + ":" + sdcPw);
        return "Basic " + idPw;
    }

    /**
     * Method to get yaml/template properties value from json
     *
     * @param docText
     * @return
     * @throws IOException
     */
    public static String getYamlvalue(String docText) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String yamlFileValue = "";
        ObjectNode root = objectMapper.readValue(docText, ObjectNode.class);
        Iterator<Entry<String, JsonNode>> entryItr = root.fields();
        while (entryItr.hasNext()) {
            Entry<String, JsonNode> entry = entryItr.next();
            String key = entry.getKey();
            if (key != null && key.equalsIgnoreCase("global")) {
                ArrayNode arrayNode = (ArrayNode) entry.getValue();
                for (JsonNode anArrayNode : arrayNode) {
                    ObjectNode node = (ObjectNode) anArrayNode;
                    ArrayNode arrayValueNode = (ArrayNode) node.get("value");
                    JsonNode jsonNode = arrayValueNode.get(0);
                    yamlFileValue = jsonNode.asText();
                    logger.info("value:" + yamlFileValue);
                }
                break;
            }
        }
        return yamlFileValue;
    }
}
