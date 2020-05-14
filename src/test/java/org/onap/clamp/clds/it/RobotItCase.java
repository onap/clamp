/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.it;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:https/https-test.properties")
public class RobotItCase {

    @Value("${server.port:8080}")
    private String springServerPort;
    // timeout in seconds
    private static final int TIMEOUT_S = 120;

    @BeforeClass
    public static void setUp() {
        try {
            // setup ssl context to ignore certificate errors
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLContext.setDefault(ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void robotTest() throws InterruptedException {
        File robotFolder = new File(getClass().getClassLoader().getResource("robotframework").getFile());
        Volume reportsVolume = new Volume("/opt/robotframework/reports");
        Volume testsVolume = new Volume("/opt/robotframework/tests");

        DockerClient client = DockerClientBuilder
                .getInstance()
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();
        CreateContainerResponse createContainerResponse = client.createContainerCmd("ppodgorsek/robot-framework")
                .withVolumes(reportsVolume, testsVolume)
                .withBinds(
                        new Bind(robotFolder.getAbsolutePath() + "/tests/", testsVolume, AccessMode.rw),
                        new Bind(robotFolder.getAbsolutePath() + "/reports/", reportsVolume, AccessMode.rw))
                .withEnv("CLAMP_PORT=" + springServerPort)
                .withStopTimeout(TIMEOUT_S)
                .withNetworkMode("host")
                .exec();

        String id = createContainerResponse.getId();
        client.startContainerCmd(id).exec();
        InspectContainerResponse exec;

        int tries = 0;
        do {
            Thread.sleep(1000);
            exec = client.inspectContainerCmd(id).exec();
            tries++;
        } while (exec.getState().getRunning() && tries < TIMEOUT_S);

        Assert.assertEquals(exec.getState().getError(), 0L, Objects.requireNonNull(exec.getState().getExitCodeLong()).longValue());
    }

}
