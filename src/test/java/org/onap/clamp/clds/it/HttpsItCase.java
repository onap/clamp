/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test HTTP and HTTPS settings + redirection of HTTP to HTTPS.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:https/https-test.properties")
public class HttpsItCase {

    @Value("${server.port}")
    private String httpsPort;
    @Value("${server.http-to-https-redirection.port}")
    private String httpPort;
    // timeout in seconds
    private static final int TIMEOUT_S = 400;
    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(HttpsItCase.class);

    /**
     * Setup the variable before tests execution.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // setup ssl context to ignore certificate errors
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLContext.setDefault(ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testDesignerIndex() throws Exception {
        RestTemplate template = new RestTemplate();
        final MySimpleClientHttpRequestFactory factory = new MySimpleClientHttpRequestFactory(new HostnameVerifier() {

            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
        template.setRequestFactory(factory);
        ResponseEntity<String> entity = template.getForEntity("http://localhost:" + this.httpPort + "/swagger.html",
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        ResponseEntity<String> httpsEntity = template
                .getForEntity("https://localhost:" + this.httpsPort + "/swagger.html", String.class);
        assertThat(httpsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpsEntity.getBody()).contains("Clamp Rest API");
    }

    @Test
    public void testSwaggerJson() throws Exception {
        RestTemplate template = new RestTemplate();
        final MySimpleClientHttpRequestFactory factory = new MySimpleClientHttpRequestFactory(new HostnameVerifier() {

            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
        template.setRequestFactory(factory);
        ResponseEntity<String> httpsEntity = template
                .getForEntity("https://localhost:" + this.httpsPort + "/restservices/clds/api-doc", String.class);
        assertThat(httpsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpsEntity.getBody()).contains("swagger");
        FileUtils.writeStringToFile(new File("docs/swagger/swagger.json"), httpsEntity.getBody(),
                Charset.defaultCharset());
    }

    @Test
    public void testRobot() throws Exception {
        File robotFolder = new File(getClass().getClassLoader().getResource("robotframework").getFile());
        Volume testsVolume = new Volume("/opt/robotframework/tests");
        DockerClient client = DockerClientBuilder
                .getInstance()
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();



        BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println("XXX ITEM " + item);
                super.onNext(item);
            }
        };

        String imageId = client.buildImageCmd(robotFolder).exec(callback).awaitImageId();
        CreateContainerResponse createContainerResponse = client.createContainerCmd(imageId)
                .withVolumes(testsVolume)
                .withBinds(
                        new Bind(robotFolder.getAbsolutePath() + "/tests/", testsVolume, AccessMode.rw))
                .withEnv("CLAMP_PORT=" + httpsPort)
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
        LogContainerCmd logContainerCmd = client.logContainerCmd(id);
        logContainerCmd.withStdOut(true).withStdErr(true);
        try {
            logContainerCmd.exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    logger.info(item.toString());
                }
            }).awaitCompletion();
        } catch (InterruptedException e) {
            throw new Exception("Failed to retrieve logs of container " + id, e);
        }
        client.stopContainerCmd(id);
    }

    /**
     * Http Request Factory for ignoring SSL hostname errors. Not for production
     * use!
     */
    class MySimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

        private final HostnameVerifier verifier;

        public MySimpleClientHttpRequestFactory(final HostnameVerifier verifier) {
            this.verifier = verifier;
        }

        @Override
        protected void prepareConnection(final HttpURLConnection connection, final String httpMethod)
                throws IOException {
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setHostnameVerifier(this.verifier);
            }
            super.prepareConnection(connection, httpMethod);
        }
    }
}
