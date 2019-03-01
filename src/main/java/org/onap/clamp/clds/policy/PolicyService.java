package org.onap.clamp.clds.policy;

import java.util.List;
import java.util.Set;
import org.onap.clamp.clds.loop.Loop;

public interface PolicyService<T extends Policy> {

    Set<T> updatePolicies(Loop loop,
        List<T> newMicroservicePolicies);

}
