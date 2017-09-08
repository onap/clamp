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

package org.onap.clamp.clds.client;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.BadRequestException;

import org.onap.clamp.clds.model.prop.ModelProperties;
import org.onap.clamp.clds.model.refprop.RefProp;
import org.onap.clamp.clds.util.LoggingUtils;
import org.openecomp.policy.api.AttributeType;
import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.DeletePolicyCondition;
import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.PolicyChangeResponse;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigType;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyEngineException;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.api.PushPolicyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 * Policy utility methods - specifically, send the policy.
 */
public class PolicyClient {

    protected static final String     POLICY_PREFIX_BASE         = "Config_";
    protected static final String     POLICY_PREFIX_BRMS_PARAM   = "Config_BRMS_Param_";
    protected static final String     POLICY_PREFIX_MICROSERVICE = "Config_MS_";

    protected static final String     LOG_POLICY_PREFIX = "Response is ";

    protected static final EELFLogger logger                     = EELFManager.getInstance()
            .getLogger(PolicyClient.class);
    protected static final EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();

    @Value("${org.onap.clamp.config.files.cldsPolicyConfig:'classpath:/clds/clds-policy-config.properties'}")
    protected String                  cldsPolicyConfigFile;

    @Autowired
    protected ApplicationContext      appContext;

    @Autowired
    protected RefProp                 refProp;

    /**
     * Perform BRMS policy type.
     *
     * @param attributes
     *            A map of attributes
     * @param prop
     *            The ModelProperties
     * @param policyRequestUuid
     *            PolicyRequest UUID
     * @return The response message of policy
     * @throws IOException
     *             In case of issues with the Stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine class
     * 
     */
    public String sendBrmsPolicy(Map<AttributeType, Map<String, String>> attributes, ModelProperties prop,
            String policyRequestUuid) throws PolicyEngineException, IOException {

        PolicyParameters policyParameters = new PolicyParameters();

        // Set Policy Type(Mandatory)
        policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);

        // Set Policy Name(Mandatory)
        policyParameters.setPolicyName(prop.getPolicyScopeAndNameWithUniqueId());

        // documentation says this is options, but when tested, got the
        // following failure: java.lang.Exception: Policy send failed: PE300 -
        // Data Issue: No policyDescription given.
        policyParameters.setPolicyDescription(refProp.getStringValue("op.policyDescription"));

        policyParameters.setAttributes(attributes);

        // Set a random UUID(Mandatory)
        policyParameters.setRequestID(UUID.fromString(policyRequestUuid));
        String policyNamePrefix = refProp.getStringValue("policy.op.policyNamePrefix");
        String rtnMsg = send(policyParameters, prop, policyNamePrefix);

        String policyType = refProp.getStringValue("policy.op.type");
        push(policyType, prop);

        return rtnMsg;
    }

    /**
     * Perform send of microservice policy in JSON.
     *
     * @param policyJson
     *            The policy JSON
     * @param prop
     *            The ModelProperties
     * @param policyRequestUuid
     *            The policy Request UUID
     * @return The response message of policy
     * @throws PolicyEngineException
     *             In case of issues with the policy engine class creation
     * @throws IOException
     *             In case of issue with the Stream
     */
    public String sendMicroServiceInJson(String policyJson, ModelProperties prop, String policyRequestUuid)
            throws IOException, PolicyEngineException {

        PolicyParameters policyParameters = new PolicyParameters();

        // Set Policy Type
        policyParameters.setPolicyConfigType(PolicyConfigType.MicroService);
        policyParameters.setEcompName(refProp.getStringValue("policy.ecomp.name"));
        policyParameters.setPolicyName(prop.getCurrentPolicyScopeAndPolicyName());

        policyParameters.setConfigBody(policyJson);
        policyParameters.setConfigBodyType(PolicyType.JSON);

        policyParameters.setRequestID(UUID.fromString(policyRequestUuid));
        String policyNamePrefix = refProp.getStringValue("policy.ms.policyNamePrefix");

        // Adding this line to clear the policy id from policy name while
        // pushing to policy engine
        prop.setPolicyUniqueId("");

        String rtnMsg = send(policyParameters, prop, policyNamePrefix);
        String policyType = refProp.getStringValue("policy.ms.type");
        push(policyType, prop);

        return rtnMsg;
    }

    /**
     * Perform send of base policy in OTHER type.
     * 
     * @param configBody
     *            The config policy string body
     * @param prop
     *            The ModelProperties
     * @param policyRequestUuid
     *            The policy request UUID
     * @return
     * @throws IOException
     *             In case of issues with the policy engine class creation
     * @throws PolicyEngineException
     *             In case of issue with the Stream
     */
    public String sendBasePolicyInOther(String configBody, ModelProperties prop, String policyRequestUuid)
            throws IOException, PolicyEngineException {

        PolicyParameters policyParameters = new PolicyParameters();

        // Set Policy Type
        policyParameters.setPolicyConfigType(PolicyConfigType.Base);
        policyParameters.setEcompName(refProp.getStringValue("policy.ecomp.name"));
        policyParameters.setPolicyName(prop.getCurrentPolicyScopeAndPolicyName());

        policyParameters.setConfigBody(configBody);
        policyParameters.setConfigBodyType(PolicyType.OTHER);
        policyParameters.setConfigName("HolmesPolicy");

        policyParameters.setRequestID(UUID.fromString(policyRequestUuid));

        // Adding this line to clear the policy id from policy name while
        // pushing to policy engine
        prop.setPolicyUniqueId("");

        String rtnMsg = send(policyParameters, prop, POLICY_PREFIX_BASE);
        push(PolicyConfigType.Base.toString(), prop);

        return rtnMsg;
    }

    /**
     * Perform send of policy.
     *
     * @param policyParameters
     *            The PolicyParameters
     * @param prop
     *            The ModelProperties
     * @return THe response message of Policy
     * @throws IOException
     *             In case of issues with the Stream
     * @throws PolicyEngineException
     *             In case of issue when creating PolicyEngine class
     */
    protected String send(PolicyParameters policyParameters, ModelProperties prop, String policyNamePrefix)
            throws IOException, PolicyEngineException {
        // Verify whether it is triggered by Validation Test button from UI
        if (prop.isTest()) {
            return "send not executed for test action";
        }

        PolicyEngine policyEngine = new PolicyEngine(
                appContext.getResource(cldsPolicyConfigFile).getFile().getAbsolutePath());

        // API method to create or update Policy.
        PolicyChangeResponse response = null;
        String responseMessage = "";
        Date startTime = new Date();
        try {
            List<Integer> versions = getVersions(policyNamePrefix, prop);
            if (versions.isEmpty()) {
                LoggingUtils.setTargetContext("Policy", "createPolicy");
                logger.info("Attempting to create policy for action=" + prop.getActionCd());
                response = policyEngine.createPolicy(policyParameters);
                responseMessage = response.getResponseMessage();
            } else {
                LoggingUtils.setTargetContext("Policy", "updatePolicy");
                logger.info("Attempting to update policy for action=" + prop.getActionCd());
                response = policyEngine.updatePolicy(policyParameters);
                responseMessage = response.getResponseMessage();
            }
        } catch (Exception e) {
            logger.error("Exception occurred during policy communication", e);
        }
        logger.info(LOG_POLICY_PREFIX + responseMessage);

        LoggingUtils.setTimeContext(startTime, new Date());

        if (response != null && response.getResponseCode() == 200) {
            logger.info("Policy send successful");
            metricsLogger.info("Policy send success");
        } else {
            logger.warn("Policy send failed: " + responseMessage);
            metricsLogger.info("Policy send failure");
            throw new BadRequestException("Policy send failed: " + responseMessage);
        }

        return responseMessage;
    }

    /**
     * Format and send push of policy.
     *
     * @param policyType
     *            The policy Type
     * @param prop
     *            The ModelProperties
     * @return The response message of policy
     * @throws IOException
     *             In case of issues with the stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine creation
     */
    protected String push(String policyType, ModelProperties prop) throws PolicyEngineException, IOException {
        // Verify whether it is triggered by Validation Test button from UI
        if (prop.isTest()) {
            return "push not executed for test action";
        }

        PushPolicyParameters pushPolicyParameters = new PushPolicyParameters();

        // Parameter arguments
        if (prop.getPolicyUniqueId() != null && !prop.getPolicyUniqueId().isEmpty()) {
            pushPolicyParameters.setPolicyName(prop.getPolicyScopeAndNameWithUniqueId());
        } else {
            pushPolicyParameters.setPolicyName(prop.getCurrentPolicyScopeAndPolicyName());
        }
        logger.info("Policy Name in Push policy method - " + pushPolicyParameters.getPolicyName());

        pushPolicyParameters.setPolicyType(policyType);
        pushPolicyParameters.setPdpGroup(refProp.getStringValue("policy.pdp.group"));
        pushPolicyParameters.setRequestID(null);

        PolicyEngine policyEngine = new PolicyEngine(
                appContext.getResource(cldsPolicyConfigFile).getFile().getAbsolutePath());

        // API method to create or update Policy.
        PolicyChangeResponse response = null;
        String responseMessage = "";
        try {
            logger.info("Attempting to push policy...");
            response = policyEngine.pushPolicy(pushPolicyParameters);
            responseMessage = response.getResponseMessage();
        } catch (Exception e) {
            logger.error("Exception occurred during policy communication", e);
        }
        logger.info(LOG_POLICY_PREFIX + responseMessage);

        if (response != null && (response.getResponseCode() == 200 || response.getResponseCode() == 204)) {
            logger.info("Policy push successful");
        } else {
            logger.warn("Policy push failed: " + responseMessage);
            throw new BadRequestException("Policy push failed: " + responseMessage);
        }

        return responseMessage;
    }

    /**
     * Use Get Config Policy API to retrieve the versions for a policy. Return
     * versions in sorted order. Return empty list if none found.
     *
     * @param policyNamePrefix
     *            The Policy Name Prefix
     * @param prop
     *            The ModelProperties
     * @return The response message from policy
     * @throws IOException
     *             In case of issues with the stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine creation
     */
    protected List<Integer> getVersions(String policyNamePrefix, ModelProperties prop)
            throws PolicyEngineException, IOException {

        ArrayList<Integer> versions = new ArrayList<>();
        ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
        String policyName = "";

        if (prop.getPolicyUniqueId() != null && !prop.getPolicyUniqueId().isEmpty()) {
            policyName = prop.getCurrentPolicyScopeAndFullPolicyName(policyNamePrefix) + "_" + prop.getPolicyUniqueId();
        } else {
            policyName = prop.getCurrentPolicyScopeAndFullPolicyName(policyNamePrefix);
        }

        logger.info("policyName=" + policyName);
        configRequestParameters.setPolicyName(policyName);

        PolicyEngine policyEngine = new PolicyEngine(
                appContext.getResource(cldsPolicyConfigFile).getFile().getAbsolutePath());

        try {
            Collection<PolicyConfig> response = policyEngine.getConfig(configRequestParameters);
            Iterator<PolicyConfig> itrResp = response.iterator();

            while (itrResp.hasNext()) {
                PolicyConfig policyConfig = itrResp.next();
                try {
                    Integer version = new Integer(policyConfig.getPolicyVersion());
                    versions.add(version);
                } catch (Exception e) {
                    // just print warning - if n;o policies, version may be null
                    logger.warn("Failed to parse due to an exception policyConfig.getPolicyVersion()="
                            + policyConfig.getPolicyVersion(), e);
                }
            }
            Collections.sort(versions);
            logger.info("Policy versions.size()=" + versions.size());
        } catch (Exception e) {
            // just print warning - if no policy version found
            logger.warn("warning: policy not found...policy name - " + policyName, e);
        }

        return versions;
    }

    /**
     * Format and send delete Micro Service requests to Policy.
     *
     * @param prop
     *            The ModelProperties
     * @return The response message from Policy
     * @throws IOException
     *             In case of issues with the stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine creation
     */
    public String deleteMicrosService(ModelProperties prop) throws PolicyEngineException, IOException {
        String policyType = refProp.getStringValue("policy.ms.type");
        return deletePolicy(prop, policyType);
    }

    /**
     * This method delete the Base policy.
     *
     * @param prop The model Properties
     * @return A string with the answer from policy
     * @throws PolicyEngineException In case of issues with the policy engine
     * @throws IOException In case of issues with the stream
     */
    public String deleteBasePolicy(ModelProperties prop) throws PolicyEngineException, IOException {
        return deletePolicy(prop, PolicyConfigType.Base.toString());
    }

    /**
     * Format and send delete BRMS requests to Policy.
     *
     * @param prop
     *            The ModelProperties
     * @return The response message from policy
     * @throws IOException
     *             In case of issues with the stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine creation
     */
    public String deleteBrms(ModelProperties prop) throws PolicyEngineException, IOException {
        String policyType = refProp.getStringValue("policy.op.type");
        return deletePolicy(prop, policyType);
    }

    /**
     * Format and send delete PAP and PDP requests to Policy.
     *
     * @param prop
     *            The ModelProperties
     *
     * @return The response message from policy
     * @throws IOException
     *             in case of issues with the Stream
     * @throws PolicyEngineException
     *             In case of issues with the PolicyEngine class creation
     */
    protected String deletePolicy(ModelProperties prop, String policyType) throws PolicyEngineException, IOException {
        DeletePolicyParameters deletePolicyParameters = new DeletePolicyParameters();

        if (prop.getPolicyUniqueId() != null && !prop.getPolicyUniqueId().isEmpty()) {
            deletePolicyParameters.setPolicyName(prop.getPolicyScopeAndNameWithUniqueId());
        } else {
            deletePolicyParameters.setPolicyName(prop.getCurrentPolicyScopeAndPolicyName());
        }
        logger.info("Policy Name in delete policy method - " + deletePolicyParameters.getPolicyName());
        deletePolicyParameters.setPolicyComponent("PDP");
        deletePolicyParameters.setDeleteCondition(DeletePolicyCondition.ALL);
        deletePolicyParameters.setPdpGroup(refProp.getStringValue("policy.pdp.group"));
        deletePolicyParameters.setPolicyType(policyType);
        // send delete request
        String responseMessage = sendDeletePolicy(deletePolicyParameters, prop);

        logger.info("Deleting policy from PAP...");
        deletePolicyParameters.setPolicyComponent("PAP");
        deletePolicyParameters.setDeleteCondition(DeletePolicyCondition.ALL);

        // send delete request
        responseMessage = sendDeletePolicy(deletePolicyParameters, prop);

        return responseMessage;
    }

    /**
     * Send delete request to Policy.
     *
     * @param deletePolicyParameters
     *            The DeletePolicyParameters
     * @param prop
     *            The ModelProperties
     * @return The response message from policy
     * @throws IOException
     *             In case of issues with the stream
     * @throws PolicyEngineException
     *             In case of issues with PolicyEngine class creation
     */
    protected String sendDeletePolicy(DeletePolicyParameters deletePolicyParameters, ModelProperties prop)
            throws PolicyEngineException, IOException {
        // Verify whether it is triggered by Validation Test button from UI
        if (prop.isTest()) {
            return "delete not executed for test action";
        }
        PolicyEngine policyEngine = new PolicyEngine(
                appContext.getResource(cldsPolicyConfigFile).getFile().getAbsolutePath());

        // API method to create or update Policy.
        PolicyChangeResponse response = null;
        String responseMessage = "";
        try {
            logger.info("Attempting to delete policy...");
            response = policyEngine.deletePolicy(deletePolicyParameters);
            responseMessage = response.getResponseMessage();
        } catch (Exception e) {
            logger.error("Exception occurred during policy communnication", e);
        }
        logger.info(LOG_POLICY_PREFIX + responseMessage);

        if (response != null && response.getResponseCode() == 200) {
            logger.info("Policy delete successful");
        } else {
            logger.warn("Policy delete failed: " + responseMessage);
            throw new BadRequestException("Policy delete failed: " + responseMessage);
        }

        return responseMessage;
    }
}