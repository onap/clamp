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

package org.onap.clamp.loop;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.loop.template.LoopTemplatesService;
import org.onap.clamp.policy.microservice.MicroServicePolicy;
import org.onap.clamp.policy.microservice.MicroServicePolicyService;
import org.onap.clamp.policy.operational.OperationalPolicy;
import org.onap.clamp.policy.operational.OperationalPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoopService {

    @Autowired
    private LoopsRepository loopsRepository;

    @Autowired
    private LoopTemplatesService loopTemplateService;

    @Autowired
    private MicroServicePolicyService microservicePolicyService;

    @Autowired
    private OperationalPolicyService operationalPolicyService;

    Loop saveOrUpdateLoop(Loop loop) {
        return loopsRepository.save(loop);
    }

    List<String> getClosedLoopNames() {
        return loopsRepository.getAllLoopNames();
    }

    /**
     * Creates a Loop Instance from Loop Template Name
     *
     * @param loopName Name of the Loop to be created
     * @param templateName Loop Template to used for Loop
     * @return Loop Instance
     */
    public Loop createLoop(String loopName, String templateName) {
        LoopTemplate loopTemplate = loopTemplateService.getLoopTemplate(templateName);

        Loop newLoop = new Loop();
        newLoop.setName(loopName);
        newLoop.setLastComputedState(LoopState.DESIGN);
        newLoop.setLoopTemplate(loopTemplate);
        newLoop.setSvgRepresentation(loopTemplate.getSvgRepresentation());

        return loopsRepository.save(newLoop);
    }

    public Loop getLoop(String loopName) {
        return loopsRepository.findById(loopName).orElse(null);
    }

    public void deleteLoop(String loopName) {
        loopsRepository.deleteById(loopName);
    }

    /**
     * This method is used to refresh the DCAE deployment status fields.
     *
     * @param loop The loop instance to be modified
     * @param deploymentId The deployment ID as returned by DCAE
     * @param deploymentUrl The Deployment URL as returned by DCAE
     */
    public void updateDcaeDeploymentFields(Loop loop, String deploymentId, String deploymentUrl) {
        loop.setDcaeDeploymentId(deploymentId);
        loop.setDcaeDeploymentStatusUrl(deploymentUrl);
        loopsRepository.save(loop);
    }

    public void updateLoopState(Loop loop, String newState) {
        loop.setLastComputedState(LoopState.valueOf(newState));
        loopsRepository.save(loop);
    }

    Loop updateAndSaveOperationalPolicies(String loopName,
        List<OperationalPolicy> newOperationalPolicies) {
        Loop loop = findClosedLoopByName(loopName);
        Set<OperationalPolicy> newPolicies =
            operationalPolicyService.updatePolicies(loop, newOperationalPolicies);
        loop.setOperationalPolicies(newPolicies);
        return loopsRepository.save(loop);
    }

    Loop updateAndSaveMicroservicePolicies(String loopName,
        List<MicroServicePolicy> newMicroservicePolicies) {
        Loop loop = findClosedLoopByName(loopName);
        Set<MicroServicePolicy> newPolicies =
            microservicePolicyService.updatePolicies(loop, newMicroservicePolicies);
        loop.setMicroServicePolicies(newPolicies);
        return loopsRepository.save(loop);
    }

    Loop updateAndSaveGlobalPropertiesJson(String loopName, JsonObject newGlobalPropertiesJson) {
        Loop loop = findClosedLoopByName(loopName);
        loop.setGlobalPropertiesJson(newGlobalPropertiesJson);
        return loopsRepository.save(loop);
    }

    MicroServicePolicy updateMicroservicePolicy(String loopName,
        MicroServicePolicy newMicroservicePolicy) {
        Loop loop = findClosedLoopByName(loopName);
        newMicroservicePolicy =
            microservicePolicyService.getAndUpdateMicroServicePolicy(loop, newMicroservicePolicy);
        if (!loop.getMicroServicePolicies().contains(newMicroservicePolicy)) {
            loop.addMicroServicePolicy(newMicroservicePolicy);
            loopsRepository.save(loop);
        }
        return newMicroservicePolicy;

    }

    Loop updateMicroservicePolicyProperties(String loopName,
        List<MicroServicePolicy> newMicroservicePolicies) {
        Loop loop = findClosedLoopByName(loopName);

        if (newMicroservicePolicies != null && !newMicroservicePolicies.isEmpty()) {
            Set<MicroServicePolicy> updatedMicroservicePolicies = newMicroservicePolicies.stream()
                .map(newMicroservicePolicy -> microservicePolicyService
                    .getAndUpdateMicroServicePolicy(loop, newMicroservicePolicy))
                .collect(Collectors.toSet());
            MicroServicePolicy tempPolicy =
                updatedMicroservicePolicies.stream().findFirst().orElse(null);
            // For multiple policies for same mS config, only save what's coming from Json Editor
            if (tempPolicy != null) {
                loop.getMicroServicePolicies().forEach(microservicePolicy -> {
                    if (!updatedMicroservicePolicies.contains(microservicePolicy)
                        && !microservicePolicy.getModelType()
                            .equalsIgnoreCase(tempPolicy.getModelType())) {
                        updatedMicroservicePolicies.add(microservicePolicy);
                    }
                });
            }

            loop.setMicroServicePolicies(updatedMicroservicePolicies);
        }
        return loopsRepository.save(loop);
    }

    private Loop findClosedLoopByName(String loopName) {
        return loopsRepository.findById(loopName).orElseThrow(
            () -> new EntityNotFoundException("Couldn't find closed loop named: " + loopName));
    }

    /**
     * Api to refresh the Operational Policy UI window.
     *
     * @param loopName The loop Name
     * @return The refreshed loop object
     */
    public Loop refreshOpPolicyJsonRepresentation(String loopName) {
        Loop loop = findClosedLoopByName(loopName);
        Set<OperationalPolicy> policyList = loop.getOperationalPolicies();
        for (OperationalPolicy policy : policyList) {
            policy.updateJsonRepresentation();
        }
        loop.setOperationalPolicies(policyList);
        return loopsRepository.save(loop);
    }
}
