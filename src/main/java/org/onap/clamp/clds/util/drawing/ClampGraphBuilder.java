/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights
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

package org.onap.clamp.clds.util.drawing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClampGraphBuilder {
    private String policy;
    private String collector;
    private List<String> microServices = new ArrayList<>();
    private final Painter painter;

    public ClampGraphBuilder(Painter p) {
        painter = p;
    }

    public ClampGraphBuilder collector(String c) {
        collector = c;
        return this;
    }

    public ClampGraphBuilder policy(String p) {
        policy = p;
        return this;
    }

    public ClampGraphBuilder microService(String ms) {
        microServices.add(ms);
        return this;
    }

    public ClampGraph build() {
        if(microServices.isEmpty()) {
            throw new InvalidStateException("At least one microservice is required");
        }
        if(Objects.isNull(policy) || policy.trim().isEmpty()) {
            throw new InvalidStateException("Policy element must be present");
        }
        return new ClampGraph(painter.doPaint(collector, microServices, policy));
    }
}
