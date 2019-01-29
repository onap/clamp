/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.util;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;

import java.util.List;
import org.junit.Test;

public class JsonUtilsTest {

    public static class TestClass extends TestObject {

        String test2;
        TestObject2 object2;

        public TestClass(String value1, String value2) {
            super(value1);
            test2 = value2;
        }

        public void setObject2(TestObject2 object2) {
            this.object2 = object2;
        }
    }

    @Test
    public void testGetObjectMapperInstance() {
        assertNotNull(JsonUtils.getObjectMapperInstance());
    }

    /**
     * This method test that the security hole in Jackson is not enabled in the default ObjectMapper.
     *
     * @throws JsonParseException In case of issues
     * @throws JsonMappingException In case of issues
     * @throws IOException In case of issues
     */
    @Test
    public void testCreateBeanDeserializer() throws JsonParseException, JsonMappingException, IOException {
        TestClass test = new TestClass("value1", "value2");
        test.setObject2(new TestObject2("test3"));
        Object testObject = JsonUtils.getObjectMapperInstance()
            .readValue("[\"org.onap.clamp.clds.util.JsonUtilsTest$TestClass\""
                + ",{\"test\":\"value1\",\"test2\":\"value2\",\"object2\":[\"org.onap.clamp.clds.util.TestObject2\","
                + "{\"test3\":\"test3\"}]}]", Object.class);
        assertNotNull(testObject);
        assertFalse(testObject instanceof TestObject);
        assertFalse(testObject instanceof TestClass);
    }


    @Test
    public void shouldReturnJsonValueByName() throws IOException {
        //given
        String modelProperties = ResourceFileUtil
            .getResourceAsString("example/model-properties/custom/modelBpmnPropertiesMultiVF.json");
        JsonElement globalElement = JsonUtils.GSON.fromJson(modelProperties, JsonObject.class).get("global");

        //when
        String locationName = JsonUtils.getValueByName(globalElement, "location");
        String timeoutValue = JsonUtils.getValueByName(globalElement, "timeout");

        //then
        assertThat(locationName).isEqualTo("SNDGCA64");
        assertThat(timeoutValue).isEqualTo("500");
    }

    @Test
    public void shouldReturnJsonObjectByPropertyName() throws IOException {
        //given
        String modelProperties = ResourceFileUtil
            .getResourceAsString("example/model-properties/custom/modelBpmnPropertiesMultiVF.json");
        JsonNode globalElement = JsonUtils.getObjectMapperInstance().readTree(modelProperties).get("global");

        //when
        JsonNode vfs = JsonUtils.getJsonObjectByName(globalElement, "deployParameters");

        //then
        assertFalse(true);
    }

    @Test
    public void shouldReturnJsonValuesByPropertyName() throws IOException {
        //given
        String modelProperties = ResourceFileUtil
            .getResourceAsString("example/model-properties/custom/modelBpmnPropertiesMultiVF.json");
        JsonElement globalElement = JsonUtils.GSON.fromJson(modelProperties, JsonObject.class).get("global");

        //when
        List<String> vfs = JsonUtils.getValuesByName(globalElement, "vf");

        //then
        assertThat(vfs).containsExactly(
            "6c7aaec2-59eb-41d9-8681-b7f976ab668d",
            "8sadsad0-a98s-6a7s-fd12-sadji9sa8d12",
            "8sfd71ad-a90d-asd9-as87-8a7sd81adsaa"
        );
    }


    @Test
    public void shouldReturnJsonValueAsInteger() throws IOException {
        //given
        String modelProperties = ResourceFileUtil
            .getResourceAsString("example/model-properties/custom/modelBpmnPropertiesMultiVF.json");
        JsonElement globalElement = JsonUtils.GSON.fromJson(modelProperties, JsonObject.class).get("global");

        //when
        Integer timeoutValue = JsonUtils.getIntValueByName(globalElement, "timeout");

        //then
        assertThat(timeoutValue).isEqualTo(500);
    }
}
