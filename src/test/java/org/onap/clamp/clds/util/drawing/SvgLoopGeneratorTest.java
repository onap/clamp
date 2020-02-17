package org.onap.clamp.clds.util.drawing;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.onap.clamp.loop.Loop;
import org.onap.clamp.loop.template.PolicyModel;
import org.onap.clamp.policy.microservice.MicroServicePolicy;
import org.onap.clamp.policy.operational.OperationalPolicy;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;

public class SvgLoopGeneratorTest {
    private Loop getLoop() {
        MicroServicePolicy ms1 = new MicroServicePolicy("ms1", "org.onap.ms1", "", false, new HashSet<Loop>());
        MicroServicePolicy ms2 = new MicroServicePolicy("ms2", "org.onap.ms2", "", false, new HashSet<Loop>());
        OperationalPolicy opPolicy = new OperationalPolicy("OperationalPolicy", new Loop(), new JsonObject(),
                new PolicyModel("org.onap.opolicy", null, "1.0.0", "opolicy1"));
        Loop loop = new Loop();
        loop.addMicroServicePolicy(ms1);
        loop.addMicroServicePolicy(ms2);
        loop.addOperationalPolicy(opPolicy);
        return loop;
    }

    @Test
    public void getAsSvgTest() throws IOException, ParserConfigurationException, SAXException {
        String xml = SvgLoopGenerator.getSvgImage(getLoop());
        assertThat(xml).contains("ms1");
        assertThat(xml).contains("ms2");
        assertThat(xml).contains("OperationalPolicy");
    }
}
