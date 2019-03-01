/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 NokiaIntellectual Property. All rights
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

package org.onap.clamp.clds.policy.microservice;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.onap.clamp.clds.policy.PolicyService;
import org.onap.clamp.dao.MicroServicePolicyRepository;
import org.onap.clamp.dao.model.Loop;
import org.onap.clamp.dao.model.MicroServicePolicy;
import org.springframework.stereotype.Service;

@Service
public class MicroservicePolicyService implements PolicyService<MicroServicePolicy> {

    private final MicroServicePolicyRepository repository;

    public MicroservicePolicyService(MicroServicePolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<MicroServicePolicy> updatePolicies(Loop loop,
        List<MicroServicePolicy> newMicroservicePolicies) {
        return newMicroservicePolicies
            .stream()
            .map(policy ->
                repository
                    .findById(policy.getName())
                    .map(p -> setJsonRepresentation(p, policy.getJsonRepresentation()))
                    .orElse(new MicroServicePolicy(policy.getName(), policy.getShared(), policy.getProperties(),
                        Sets.newHashSet(loop))))
            .map(repository::save)
            .collect(Collectors.toSet());
    }


    public Consumer<MicroServicePolicy> getDeletePolicyConsumer(){
        return repository::delete;
    }

    private MicroServicePolicy setJsonRepresentation(MicroServicePolicy p, JsonObject propertiesJson) {
        p.setProperties(propertiesJson);
        return p;
    }
}
