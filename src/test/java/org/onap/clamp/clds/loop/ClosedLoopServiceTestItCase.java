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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;
import java.util.Set;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.policy.microservice.MicroServicePolicy;
import org.onap.clamp.clds.policy.operational.OperationalPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClosedLoopServiceTestItCase {

    private static final String EXAMPLE_LOOP_NAME = "ClosedLoopTest";
    private static final String EXAMPLE_JSON = "{\"testName\":\"testValue\"}";

    @Autowired
    ClosedLoopService closedLoopService;

    @Autowired
    LoopsRepository loopsRepository;

    @After
    public void tearDown() {
        loopsRepository.deleteAll();
    }

    @Test
    public void shouldCreateEmptyLoop() {
        //given
        String loopBlueprint = "blueprint";
        String loopSvg = "representation";
        Loop testLoop = createTestLoop(EXAMPLE_LOOP_NAME, loopBlueprint, loopSvg);
        testLoop.setGlobalPropertiesJson(JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class));
        testLoop.setLastComputedState(LoopState.DESIGN);

        //when
        Loop actualLoop = closedLoopService.addNewLoop(testLoop);

        //then
        assertThat(actualLoop).isNotNull();
        assertThat(actualLoop).isEqualTo(loopsRepository.findById(actualLoop.getName()).get());
        assertThat(actualLoop.getName()).isEqualTo(EXAMPLE_LOOP_NAME);
        assertThat(actualLoop.getBlueprint()).isEqualTo(loopBlueprint);
        assertThat(actualLoop.getSvgRepresentation()).isEqualTo(loopSvg);
        assertThat(actualLoop.getGlobalPropertiesJson().getAsJsonPrimitive("testName").getAsString())
            .isEqualTo("testValue");
    }

    @Test
    public void shouldAddOperationalPolicyToLoop() {
        //given
        Loop testLoop = createTestLoop(EXAMPLE_LOOP_NAME, "blueprint", "representation");
        testLoop.setGlobalPropertiesJson(JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class));
        closedLoopService.addNewLoop(testLoop);
        JsonObject confJson = JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class);
        String policyName = "policyName";
        OperationalPolicy operationalPolicy = new OperationalPolicy(policyName, null, confJson);

        //when
        Loop actualLoop = closedLoopService.updateOperationalPolicies(EXAMPLE_LOOP_NAME, Lists.newArrayList(operationalPolicy));

        //then
        assertThat(actualLoop).isNotNull();
        assertThat(actualLoop.getName()).isEqualTo(EXAMPLE_LOOP_NAME);
        Set<OperationalPolicy> savedPolicies = actualLoop.getOperationalPolicies();
        assertThat(savedPolicies).hasSize(1);
        assertThat(savedPolicies)
            .usingElementComparatorIgnoringFields("loop")
            .contains(operationalPolicy);
        assertThat(savedPolicies).extracting("loop")
            .containsExactly(EXAMPLE_LOOP_NAME);

    }

    @Test
    public void shouldAddMicroservicePolicyToLoop() {
        //given
        Loop testLoop = createTestLoop(EXAMPLE_LOOP_NAME, "blueprint", "representation");
        testLoop.setGlobalPropertiesJson(JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class));
        closedLoopService.addNewLoop(testLoop);
        JsonObject confJson = JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class);
        String policyName = "policyName";
        String policyTosca = "policyTosca";
        MicroServicePolicy microServicePolicy = new MicroServicePolicy(policyName, policyTosca, false, confJson, null);

        //when
        Loop actualLoop = closedLoopService.updateMicroservicePolicies(EXAMPLE_LOOP_NAME, Lists.newArrayList(microServicePolicy));

        //then
        assertThat(actualLoop).isNotNull();
        assertThat(actualLoop.getName()).isEqualTo(EXAMPLE_LOOP_NAME);
        Set<MicroServicePolicy> savedPolicies = actualLoop.getMicroServicePolicies();
        assertThat(savedPolicies).hasSize(1);
        assertThat(savedPolicies).usingElementComparatorIgnoringFields("usedByLoops")
            .contains(microServicePolicy);
        assertThat(savedPolicies).extracting("usedByLoops")
            .hasSize(1);

    }

//    @Test
//    public void shouldCreateOperationalPolicyOnlyIfNew() {
//        //given
//        Loop testLoop = createTestLoop(EXAMPLE_LOOP_NAME, "blueprint", "representation");
//        testLoop.setGlobalPropertiesJson(JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class));
//        closedLoopService.addNewLoop(testLoop);
//        JsonObject confJson = JsonUtils.GSON.fromJson(EXAMPLE_JSON, JsonObject.class);
//        String policyName = "policyName";
//        String policyTosca = "policyTosca";
//        MicroServicePolicy microServicePolicy = new MicroServicePolicy(policyName, policyTosca, false, confJson, null);
//        Loop actualLoop = closedLoopService.updateMicroservicePolicies(EXAMPLE_LOOP_NAME, Lists.newArrayList(microServicePolicy));
//
//        //when
//
//        //then
//        assertThat(actualLoop).isNotNull();
//        assertThat(actualLoop.getName()).isEqualTo(EXAMPLE_LOOP_NAME);
//        Set<MicroServicePolicy> savedPolicies = actualLoop.getMicroServicePolicies();
//        assertThat(savedPolicies).hasSize(1);
//        assertThat(savedPolicies).usingElementComparatorIgnoringFields("usedByLoops")
//            .contains(microServicePolicy);
//        assertThat(savedPolicies).extracting("usedByLoops")
//            .hasSize(1);
//
//    }

    private Loop createTestLoop(String loopName, String loopBlueprint, String loopSvg) {
        return new Loop(loopName, loopBlueprint, loopSvg);
    }
}