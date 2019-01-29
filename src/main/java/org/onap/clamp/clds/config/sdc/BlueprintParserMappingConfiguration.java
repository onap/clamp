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

package org.onap.clamp.clds.config.sdc;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.onap.clamp.clds.util.JsonUtils;

/**
 * This class is used to decode the configuration found in
 * application.properties, this is related to the blueprint mapping
 * configuration that is used to create data in database, according to the
 * blueprint content coming from SDC.
 */
public class BlueprintParserMappingConfiguration {

    private String blueprintKey;
    private boolean dcaeDeployable;
    private BlueprintParserFilesConfiguration files;

    public String getBlueprintKey() {
        return blueprintKey;
    }

    public void setBlueprintKey(String blueprintKey) {
        this.blueprintKey = blueprintKey;
    }

    public BlueprintParserFilesConfiguration getFiles() {
        return files;
    }

    public void setFiles(BlueprintParserFilesConfiguration filesConfig) {
        this.files = filesConfig;
    }

    public boolean isDcaeDeployable() {
        return dcaeDeployable;
    }

    public static List<BlueprintParserMappingConfiguration> createFromJson(InputStream json) throws IOException {
        TypeReference<List<BlueprintParserMappingConfiguration>> mapType = new TypeReference<List<BlueprintParserMappingConfiguration>>() {
        };
        return JsonUtils.GSON.fromJson(json, mapType);
    }
}
