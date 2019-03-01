package org.onap.clamp.clds.loop;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;
import java.util.Set;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.Application;
import org.onap.clamp.clds.util.JsonUtils;
import org.onap.clamp.clds.policy.microservice.MicroServicePolicy;
import org.onap.clamp.clds.policy.operational.OperationalPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ClosedLoopServiceTest {

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

    private Loop createTestLoop(String loopName, String loopBlueprint, String loopSvg) {
        return new Loop(loopName, loopBlueprint, loopSvg);
    }
}