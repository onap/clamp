/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights
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

package org.onap.clamp.policy.microservice;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.loop.Loop;
import org.onap.clamp.policy.Policy;
import org.onap.clamp.policy.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MicroServicePolicyService implements PolicyService<MicroServicePolicy> {

    private final MicroServicePolicyRepository repository;
    private final ClampProperties refProp;

    private static final String USE_POLICYID_PREFIX_SCOPE = "policyid.useprefixScope";
    private static final String POLICYID_PREFIX_SCOPE = "policyid.prefixScope";
    private static final String USE_POLICYID_CONFIG_MS_PREFIX = "policyid.useprefix";
    private static final String POLICYID_CONFIG_MS_PREFIX = "policyid.prefix";

    @Autowired
    public MicroServicePolicyService(MicroServicePolicyRepository repository,
        ClampProperties refProp) {
        this.repository = repository;
        this.refProp = refProp;
    }

    @Override
    public Set<MicroServicePolicy> updatePolicies(Loop loop,
        List<MicroServicePolicy> newMicroservicePolicies) {
        return newMicroservicePolicies.stream()
            .map(policy -> getAndUpdateMicroServicePolicy(loop, policy))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isExisting(String policyName) {
        return repository.existsById(policyName);
    }

    /**
     * Get and update the MicroService policy properties.
     *
     * @param loop The loop
     * @param policy The new MicroService policy
     * @return The updated MicroService policy
     */
    public MicroServicePolicy getAndUpdateMicroServicePolicy(Loop loop, MicroServicePolicy policy) {
        return repository.save(repository.findById(policy.getName())
            .map(p -> updateMicroservicePolicyProperties(p, policy, loop))
            .orElse(createMicroservicePolicy(policy, loop)));
    }

    private MicroServicePolicy updateMicroservicePolicyProperties(MicroServicePolicy oldPolicy,
        MicroServicePolicy newPolicy, Loop loop) {
        oldPolicy.setConfigurationsJson(newPolicy.getConfigurationsJson());
        oldPolicy.setPolicyModel(newPolicy.getPolicyModel());
        if (!oldPolicy.getUsedByLoops().contains(loop)) {
            oldPolicy.getUsedByLoops().add(loop);
        }
        return oldPolicy;
    }

    /**
     * Update the MicroService policy deployment related parameters.
     *
     * @param microServicePolicy The micro service policy
     * @param deploymentId The deployment ID as returned by DCAE
     * @param deploymentUrl The Deployment URL as returned by DCAE
     * @throws MicroServicePolicy doesn't exist in DB
     */
    public void updateDcaeDeploymentFields(MicroServicePolicy microServicePolicy,
        String deploymentId, String deploymentUrl) {
        microServicePolicy.setDcaeDeploymentId(deploymentId);
        microServicePolicy.setDcaeDeploymentStatusUrl(deploymentUrl);
        repository.save(microServicePolicy);
    }

    private MicroServicePolicy createMicroservicePolicy(MicroServicePolicy policy, Loop loop) {
        MicroServicePolicy newPolicy = new MicroServicePolicy(
            generateMicroservicePolicyNameWithPrefix(policy, loop), policy.getPolicyModel(),
            policy.getShared(), policy.getJsonRepresentation(), Sets.newHashSet(loop));
        newPolicy.setConfigurationsJson(policy.getConfigurationsJson());
        return newPolicy;

    }

    private String generateMicroservicePolicyNameWithPrefix(MicroServicePolicy policy, Loop loop) {
        // if the policy name does not contain the loop name, it is assumed to be a policy
        // type. Generate a policy name which includes the loop name and an index (the next
        // policy for a type)
        if (!policy.getName().contains(loop.getName())) {
            StringBuilder sb = new StringBuilder();
            if (refProp.getStringValue(USE_POLICYID_PREFIX_SCOPE).equalsIgnoreCase("true")) {
                sb.append(refProp.getStringValue(POLICYID_PREFIX_SCOPE));
            }
            if (refProp.getStringValue(USE_POLICYID_CONFIG_MS_PREFIX).equalsIgnoreCase("true")) {
                sb.append(refProp.getStringValue(POLICYID_CONFIG_MS_PREFIX));
            }

            sb.append(policy.getPolicyModel().getPolicyAcronym()).append("_")
                .append(loop.getName());
            updateMicroservicePolicyNameFromJson(policy, sb);
            return Policy.normalizePolicyScopeName(sb.toString());

        }
        return policy.getName();
    }

    private void updateMicroservicePolicyNameFromJson(MicroServicePolicy microServicePolicy,
        StringBuilder sb) {
        JsonObject jsonObject =
            JsonUtils.GSON.fromJson(microServicePolicy.getConfigurationsJson(), JsonObject.class);
        if (jsonObject != null && jsonObject.has("name") && jsonObject.get("name") != null) {
            sb.append("_").append(jsonObject.get("name").getAsString());
        }
    }

}
