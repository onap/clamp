/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 NokiaIntellectual Property. All rights
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
package org.onap.clamp.clds.serialization;

import static org.onap.clamp.clds.util.JsonUtils.GSON;

import com.google.gson.JsonObject;
import javax.persistence.AttributeConverter;

public class JsonObjectAttributeConverter implements AttributeConverter<JsonObject, String> {

    @Override
    public String convertToDatabaseColumn(JsonObject jsonObject) {
        return GSON.toJson(jsonObject);
    }

    @Override
    public JsonObject convertToEntityAttribute(String s) {
        return GSON.fromJson(s, JsonObject.class);
    }
}
