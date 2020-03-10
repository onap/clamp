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

package org.onap.clamp.clds.tosca.update;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.yaml.snakeyaml.Yaml;

public class Extractor {
    private LinkedHashMap<String, ToscaElement> allItems;
    private String source;
    private String nativeComponent;

    @SuppressWarnings("unchecked")
    public Extractor(String toParse, String nativeComponent) throws IOException {

        this.source = toParse;
        this.nativeComponent = nativeComponent;
        allItems = new LinkedHashMap<String, ToscaElement>();
        getAllAsMaps();

    }

    public LinkedHashMap<String, ToscaElement> getAllItems() {
        return allItems;
    }

    public String getSource() {
        return source;
    }

    /**
     * Yaml Parse gets raw policies and datatypes, in different sections : necessary to extract
     * all entities and put them at the same level.
     *
     * @return an object
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Object> getAllAsMaps() {
        Yaml yaml = new Yaml();
        Object contentFile = yaml.load(source);
        LinkedHashMap<String, LinkedHashMap<String, Object>> file =
                (LinkedHashMap<String, LinkedHashMap<String, Object>>) contentFile;
        // Get DataTypes
        LinkedHashMap<String, Object> dataTypes = file.get("data_types");
        dataTypes = (dataTypes == null) ? (new LinkedHashMap<>()) : dataTypes;
        // Get Policies : first, get topology and after extract policies from it
        LinkedHashMap<String, Object> policyTypes = file.get("policy_types");
        // Put the policies and datatypes in the same collection
        dataTypes.putAll(policyTypes);

        Object contentNativeFile = yaml.load(nativeComponent);
        LinkedHashMap<String, Object> dataTypesEmbedded =
                ((LinkedHashMap<String, LinkedHashMap<String, Object>>) contentNativeFile).get("data_types");
        dataTypes.putAll(dataTypesEmbedded);

        parseInComponent(dataTypes);
        return dataTypes;
    }

    /**
     * With all the component, get as Map, Components and Components properties are created.
     *
     * @param allMaps maps
     */
    @SuppressWarnings("unchecked")
    public void parseInComponent(LinkedHashMap<String, Object> allMaps) {
        //Component creations, from the file maps
        for (Entry<String, Object> itemToParse : allMaps.entrySet()) {
            LinkedHashMap<String, Object> componentBody = (LinkedHashMap<String, Object>) itemToParse.getValue();
            ToscaElement toscaElement = new ToscaElement(itemToParse.getKey(), (String) componentBody.get("derived_from"),
                    (String) componentBody.get("description"));
            //If policy, version and type_version :
            if (componentBody.get("type_version") != null) {
                toscaElement.setVersion((String) componentBody.get("type_version"));
                toscaElement.setTypeVersion((String) componentBody.get("type_version"));
            }
            //Properties creation, from the map
            if (componentBody.get("properties") != null) {
                LinkedHashMap<String, Object> properties =
                        (LinkedHashMap<String, Object>) componentBody.get("properties");
                for (Entry<String, Object> itemToProperty : properties.entrySet()) {
                    Property property = new Property(itemToProperty.getKey(),
                            (LinkedHashMap<String, Object>) itemToProperty.getValue());
                    toscaElement.addProperties(property);
                }
            }
            this.allItems.put(toscaElement.getName(), toscaElement);
        }
    }
}
