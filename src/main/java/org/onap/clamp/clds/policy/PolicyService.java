package org.onap.clamp.clds.policy;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.onap.clamp.dao.model.Loop;
import org.onap.clamp.dao.model.Policy;

public interface PolicyService<T extends Policy> {

    Set<T> updatePolicies(Loop loop,
        List<T> newMicroservicePolicies);

    Consumer<T> getDeletePolicyConsumer();

    default void clearOldPolicies(Set<T> savedPolicies, Set<T> oldPolicies) {
        Set<String> savedPoliciesNames = extractPoliciesNames(savedPolicies).collect(Collectors.toSet());
        oldPolicies
            .stream()
            .filter(policy -> !savedPoliciesNames.contains(policy.getName()))
            .forEach(getDeletePolicyConsumer());
    }

    default Stream<String> extractPoliciesNames(Set<? extends Policy> savedPolicies) {
        return savedPolicies
            .stream()
            .map(Policy::getName);
    }
}
