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

package org.onap.clamp.clds.client.req.policy;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.model.properties.ModelProperties;
import org.onap.clamp.clds.model.properties.PolicyChain;
import org.onap.clamp.clds.model.properties.PolicyItem;
import org.onap.policy.api.AttributeType;
import org.onap.policy.controlloop.policy.builder.BuilderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Construct an Operational Policy request given CLDS objects.
 */
@Component
public class OperationalPolicyRequestAttributesConstructor {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(OperationalPolicyRequestAttributesConstructor.class);
    static final String TEMPLATE_NAME = "templateName";
    static final String CLOSED_LOOP_CONTROL_NAME = "closedLoopControlName";
    static final String NOTIFICATION_TOPIC = "notificationTopic";
    static final String OPERATION_TOPIC = "operationTopic";
    static final String CONTROL_LOOP_YAML = "controlLoopYaml";
    static final String CONTROLLER = "controller";
    static final String RECIPE = "Recipe";
    static final String MAX_RETRIES = "MaxRetries";
    static final String RETRY_TIME_LIMIT = "RetryTimeLimit";
    static final String RESOURCE_ID = "ResourceId";
    static final String RECIPE_TOPIC = "RecipeTopic";
    private OperationalPolicyYamlFormatter policyYamlFormatter;

    @Autowired
    protected OperationalPolicyRequestAttributesConstructor(OperationalPolicyYamlFormatter policyYamlFormatter) {
        this.policyYamlFormatter = policyYamlFormatter;
    }

    /**
     * Format Operational Policy attributes.
     *
     * @param refProp
     * @param modelProperties
     * @param modelElementId
     * @param policyChain
     * @return
     * @throws BuilderException
     * @throws UnsupportedEncodingException
     */
    public Map<AttributeType, Map<String, String>> formatAttributes(ClampProperties refProp,
                                                                           ModelProperties modelProperties, String modelElementId, PolicyChain policyChain)
            throws BuilderException, UnsupportedEncodingException {
        modelProperties.setCurrentModelElementId(modelElementId);
        modelProperties.setPolicyUniqueId(policyChain.getPolicyId());

        String globalService = modelProperties.getGlobal().getService();

        Map<String, String> ruleAttributes = prepareRuleAttributes(refProp, modelProperties, modelElementId, policyChain, globalService);
        Map<String, String> matchingAttributes = prepareMatchingAttributes(refProp, globalService);

        return createAttributesMap(matchingAttributes, ruleAttributes);
    }

    private Map<String, String> prepareRuleAttributes(ClampProperties refProp, ModelProperties modelProperties,
                                                             String modelElementId, PolicyChain policyChain, String globalService) throws BuilderException, UnsupportedEncodingException {
        logger.info("Preparing rule attributes...");
        String templateName = refProp.getStringValue("op.templateName", globalService);
        String operationTopic = refProp.getStringValue("op.operationTopic", globalService);
        String notificationTopic = refProp.getStringValue("op.notificationTopic", globalService);

        Map<String, String> ruleAttributes = new HashMap<>();
        ruleAttributes.put(TEMPLATE_NAME, templateName);
        ruleAttributes.put(CLOSED_LOOP_CONTROL_NAME, modelProperties.getControlNameAndPolicyUniqueId());
        ruleAttributes.put(NOTIFICATION_TOPIC, notificationTopic);

        if (operationTopic == null || operationTopic.isEmpty()) {
            // if no operationTopic, then don't format yaml - use first policy
            String recipeTopic = refProp.getStringValue("op.recipeTopic", globalService);
            fillRuleAttributesFromPolicyItem(ruleAttributes, policyChain.getPolicyItems().get(0), recipeTopic);
        } else {
            fillRuleAttributesFromPolicyChain(ruleAttributes, policyChain, modelProperties, modelElementId, operationTopic);
        }
        logger.info("Prepared: " + ruleAttributes);
        return ruleAttributes;
    }

    private Map<String, String> prepareMatchingAttributes(ClampProperties refProp, String globalService) {
        logger.info("Preparing matching attributes...");
        String controller = refProp.getStringValue("op.controller", globalService);
        Map<String, String> matchingAttributes = new HashMap<>();
        matchingAttributes.put(CONTROLLER, controller);
        logger.info("Prepared: " + matchingAttributes);
        return matchingAttributes;
    }

    private Map<AttributeType, Map<String, String>> createAttributesMap(Map<String, String> matchingAttributes, Map<String, String> ruleAttributes) {
        Map<AttributeType, Map<String, String>> attributes = new HashMap<>();
        attributes.put(AttributeType.RULE, ruleAttributes);
        attributes.put(AttributeType.MATCHING, matchingAttributes);
        return attributes;
    }

    private void fillRuleAttributesFromPolicyItem(Map<String, String> ruleAttributes, PolicyItem policyItem, String recipeTopic) {
        logger.info("recipeTopic=" + recipeTopic);
        ruleAttributes.put(RECIPE_TOPIC, recipeTopic);
        ruleAttributes.put(RECIPE, policyItem.getRecipe());
        ruleAttributes.put(MAX_RETRIES, String.valueOf(policyItem.getMaxRetries()));
        ruleAttributes.put(RETRY_TIME_LIMIT, String.valueOf(policyItem.getRetryTimeLimit()));
        ruleAttributes.put(RESOURCE_ID, String.valueOf(policyItem.getTargetResourceId()));
    }

    private void fillRuleAttributesFromPolicyChain(Map<String, String> ruleAttributes, PolicyChain policyChain,
                                                          ModelProperties modelProperties, String modelElementId, String operationTopic)
            throws BuilderException, UnsupportedEncodingException {
        logger.info("operationTopic=" + operationTopic);
        String yaml = policyYamlFormatter.formatYaml(modelProperties, modelElementId, policyChain);
        ruleAttributes.put(OPERATION_TOPIC, operationTopic);
        ruleAttributes.put(CONTROL_LOOP_YAML, yaml);
    }
}