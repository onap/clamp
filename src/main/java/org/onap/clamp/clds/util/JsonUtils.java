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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.onap.clamp.clds.model.properties.AbstractModelElement;

/**
 * This class is used to access the jackson with restricted type access.
 */
public class JsonUtils {

    private static ObjectMapper objectMapper;
    protected static final EELFLogger logger = EELFManager.getInstance().getLogger(AbstractModelElement.class);
    private static final String LOG_ELEMENT_NOT_FOUND = "Value '{}' for key 'name' not found in JSON";
    private static final String LOG_ELEMENT_NOT_FOUND_IN_JSON = "Value '{}' for key 'name' not found in JSON {}";


    public static final Gson GSON = new Gson();

    private JsonUtils() {
    }

    /**
     * Call this method to retrieve a secure ObjectMapper.
     *
     * @return an ObjectMapper instance (same for clamp)
     */
    public static synchronized ObjectMapper getObjectMapperInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            // This is to disable the security hole that could be opened for
            // json deserialization, if needed do this
            // objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
            objectMapper.disableDefaultTyping();
        }
        return objectMapper;
    }

    /**
     * Return the value field of the json node element that has a name field equals to the given name.
     */
    public static String getValueByName(JsonNode nodeIn, String name) {
        String value = null;
        if (nodeIn != null) {
            for (JsonNode node : nodeIn) {
                if (node.path("name").asText().equals(name)) {
                    JsonNode vnode = node.path("value");
                    if (vnode.isArray()) {
                        // if array, assume value is in first element
                        value = vnode.path(0).asText();
                    } else {
                        // otherwise, just return text
                        value = vnode.asText();
                    }
                }
            }
        }
        if (value == null || value.length() == 0) {
            logger.warn(LOG_ELEMENT_NOT_FOUND, name);
        } else {
            logger.debug(LOG_ELEMENT_NOT_FOUND_IN_JSON, name, nodeIn.toString());
        }
        return value;
    }

    /**
     * Return the value field of the json node element that has a name field equals to the given name.
     */
    public static String getValueByName(JsonElement jsonElement, String name) {
        Optional<String> value = Optional.empty();
        if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (hasMatchingParameterName(name, element)) {
                    value = extractValueFromElement(element);
                    break;
                }
            }
        } else if (hasMatchingParameterName(name, jsonElement)) {
            value = extractValueFromElement(jsonElement);
        }
        return value.orElseGet(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug(LOG_ELEMENT_NOT_FOUND_IN_JSON, name, jsonElement.toString());
            } else {
                logger.warn(LOG_ELEMENT_NOT_FOUND, name);
            }
            return null;
        });
    }

    /**
     * Return the int value field of the json node element that has a name field equals to the given name.
     */
    public static Integer getIntValueByName(JsonElement element, String name) {
        String value = getValueByName(element, name);
        return Integer.valueOf(value);
    }

    /**
     * Return an array of values for the field of the json node element that has a name field equals to the given name.
     */
    public static List<String> getValuesByName(JsonElement jsonElement, String name) {
        List<String> values = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (hasMatchingParameterName(name, element)) {
                    values.addAll(extractValuesFromElement(element));
                    break;
                }
            }
        } else if (hasMatchingParameterName(name, jsonElement)) {
            values.addAll(extractValuesFromElement(jsonElement));
        }
        if(values.isEmpty()){
            if (logger.isDebugEnabled()) {
                logger.debug(LOG_ELEMENT_NOT_FOUND_IN_JSON, name, jsonElement.toString());
            } else {
                logger.warn(LOG_ELEMENT_NOT_FOUND, name);
            }
        }
        return values;
    }


    /**
     * Return the Json value field of the json node element that has a name field
     * equals to the given name.
     */
    public static JsonNode getJsonObjectByName(JsonNode nodeIn, String name) {
        JsonNode vnode = null;
        if (nodeIn != null) {
            for (JsonNode node : nodeIn) {
                if (node.path("name").asText().equals(name)) {
                    vnode = node.path("value");
                }
            }
        }
        if (vnode == null) {
            logger.warn(LOG_ELEMENT_NOT_FOUND, name);
        } else {
            logger.debug(LOG_ELEMENT_NOT_FOUND_IN_JSON, name, nodeIn.toString());
        }
        return vnode;
    }

    private static boolean hasMatchingParameterName(String name, JsonElement element) {
        return element.isJsonObject()
            && element.getAsJsonObject().has("name")
            && name.equals(element.getAsJsonObject().get("name").getAsString());
    }
    private static Optional<String> extractValueFromElement(JsonElement element) {
        JsonElement valueJsonElement = element.getAsJsonObject().get("value");
        if (valueJsonElement.isJsonArray()) {
            return Optional.ofNullable(valueJsonElement.getAsJsonArray().get(0).getAsString());
        } else if (valueJsonElement.isJsonPrimitive()) {
            return Optional.ofNullable(valueJsonElement.getAsJsonPrimitive().getAsString());
        } else {
            return Optional.ofNullable(GSON.toJson(valueJsonElement));
        }
    }

    private static List<String> extractValuesFromElement(JsonElement element) {
        JsonElement valueJsonElement = element.getAsJsonObject().get("value");
        if (valueJsonElement.isJsonArray()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(valueJsonElement.getAsJsonArray().iterator(), Spliterator.ORDERED), false)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isString)
                .map(JsonPrimitive::getAsString)
                .collect(Collectors.toList());
        } else {
            String value = extractValueFromElement(element).orElse(null);
            return Lists.newArrayList(value);
        }

    }
}
