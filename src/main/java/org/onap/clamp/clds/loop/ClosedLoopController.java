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

package org.onap.clamp.clds.loop;

import java.util.List;
import org.onap.clamp.clds.policy.microservice.MicroServicePolicy;
import org.onap.clamp.clds.policy.operational.OperationalPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//This will be replaced with camel

@RestController
@RequestMapping("loop")
class ClosedLoopController {

    private final ClosedLoopService closedLoopService;

    @Autowired
    public ClosedLoopController(ClosedLoopService closedLoopService) {
        this.closedLoopService = closedLoopService;
    }

    @PostMapping("createEmptyLoop")
    Loop createEmptyLoop() {
        return closedLoopService.addNewLoop(new Loop("testLoopName", "testLoopBluepritn", "testLoopSVG"));
    }

    @GetMapping("getSVG")
    String getClosedLoopModelSVG(String closedLoopName) {
        return closedLoopService.getClosedLoopModelSVG(closedLoopName);
    }

    @GetMapping("getAllNames")
    List<String> getLoopNames() {
        return closedLoopService.getClosedLoopNames();
    }

    @GetMapping
    Loop getLoop(@RequestParam String loopName) {
        return closedLoopService.getLoop(loopName);
    }

    @PostMapping("updateOperationalPolicies/{loopName}")
    Loop updateOperationalPolicies(@PathVariable String loopName,
        @RequestBody List<OperationalPolicy> operationalPolicies) {
        return closedLoopService.updateOperationalPolicies(loopName, operationalPolicies);
    }

    @PostMapping("updateMicroservicePolicies/{loopName}")
    Loop updateMicroservicePolicies(@PathVariable String loopName,
        @RequestBody List<MicroServicePolicy> microServicePolicies) {
        return closedLoopService.updateMicroservicePolicies(loopName, microServicePolicies);
    }
}
