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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ParserToJson {
    private LinkedHashMap<String, Component> components;
    private LinkedHashMap<String, Template> templates;

    public ParserToJson(LinkedHashMap<String, Component> components, LinkedHashMap<String, Template> templates) {
        this.components = components;
        this.templates = templates;
    }

    /**
     * For a given component, launch process to parse it in Json.
     *
     * @param nameComponent name components
     * @return return
     */
    public JsonObject getJsonProcess(String nameComponent, String typeComponent) {
        JsonObject glob = new JsonObject();

        if (typeComponent.equals("object")) {
            glob = this.getFieldAsObject(matchComponent(nameComponent));
        }
        else {
            /*glob = this.getFieldAsArray(matchComponent(nameComponent));*/
        }

        return glob;
    }

    /**
     * Return the classical/general fields of the component, & launch the properties deployment.
     *
     * @param component the compo
     * @return a json object
     */
    public JsonObject getFieldAsObject(Component component) {

        JsonObject globalFields = new JsonObject();
        if (templates.get("object").hasFields("title")) {
            globalFields.addProperty("title", component.getName());
        }
        if (templates.get("object").hasFields("type")) {
            globalFields.addProperty("type", "object");
        }
        if (templates.get("object").hasFields("description")) {
            if (component.getDescription() != null) {
                globalFields.addProperty("description", component.getDescription());
            }
        }
        if (templates.get("object").hasFields("required")) {
            globalFields.add("required", this.getRequirements(component.getName()));
        }
        if (templates.get("object").hasFields("properties")) {
            globalFields.add("properties", this.deploy(component.getName()));
        }
        return globalFields;
    }

    /**
     * Get the required properties of the Component, including the parents properties requirements.
     *
     * @param nameComponent name component
     * @return a json array
     */
    public JsonArray getRequirements(String nameComponent) {
        JsonArray requirements = new JsonArray();
        Component toParse = components.get(nameComponent);
        //Check for a father component, and launch the same process
        if (!toParse.getDerivedFrom().equals("tosca.datatypes.Root")
                && !toParse.getDerivedFrom().equals("tosca.policies.Root")) {
            requirements.addAll(getRequirements(toParse.getDerivedFrom()));
        }
        //Each property is checked, and add to the requirement array if it's required
        Collection<Property> properties = toParse.getProperties().values();
        for (Property property : properties) {
            if (property.getItems().containsKey("required")
                    && property.getItems().get("required").equals(true)) {
                requirements.add(property.getName());
            }
        }
        return requirements;
    }

    /**
     * The beginning of the recursive process. Get the parents (or not) to launch the same process, and otherwise
     * deploy and parse the properties.
     *
     * @param nameComponent name component
     * @return a json object
     */
    public JsonObject deploy(String nameComponent) {
        JsonObject jsonSchema = new JsonObject();
        Component toParse = components.get(nameComponent);
        //Check for a father component, and launch the same process
        if (!toParse.getDerivedFrom().equals("tosca.datatypes.Root")
                && !toParse.getDerivedFrom().equals("tosca.policies.Root")) {
            jsonSchema = this.getParent(toParse.getDerivedFrom());
        }
        //For each component property, check if its a complex properties (a component) or not. In that case,
        //launch the analyse of the property.
        for (Entry<String, Property> property : toParse.getProperties().entrySet()) {
            if (matchComponent((String) property.getValue().getItems().get("type")) != null) {
                jsonSchema.add(property.getValue().getName(),
                        this.getJsonProcess((String) property.getValue().getItems().get("type"), "object"));
            }
            else {
                jsonSchema.add(property.getValue().getName(), this.complexParse(property.getValue()));
            }
        }
        return jsonSchema;
    }

    /**
     * If a component has a parent, it is deploy in the same way.
     *
     * @param nameComponent name component
     * @return a json object
     */
    public JsonObject getParent(String nameComponent) {
        return deploy(nameComponent);
    }

    /**
     * to be done.
     *
     * @param property property
     * @return a json object
     */
    @SuppressWarnings("unchecked")
    public JsonObject complexParse(Property property) {
        JsonObject propertiesInJson = new JsonObject();
        Template currentPropertyTemplate;
        String typeProperty = (String) property.getItems().get("type");
        if (typeProperty.toLowerCase().equals("list") || typeProperty.toLowerCase().equals("map")) {
            currentPropertyTemplate = templates.get("object");
        }
        else {
            String propertyType = (String) property.getItems().get("type");
            currentPropertyTemplate = templates.get(propertyType.toLowerCase());
        }
        //Each "special" field is analysed, and has a specific treatment
        for (String propertyField : property.getItems().keySet()) {
            switch (propertyField) {
                case "type":
                    if (currentPropertyTemplate.hasFields(propertyField)) {
                        String fieldtype = (String) property.getItems().get(propertyField);
                        switch (fieldtype.toLowerCase()) {
                            case "list":
                                propertiesInJson.addProperty("type", "array");
                                break;
                            case "map":
                                propertiesInJson.addProperty("type", "object");
                                break;
                            case "tosca.datatypes.TimeInterval":
                            case "tosca.datatypes.scalar-unit.time":
                            case "tosca.datatypes.scalar-unit.frequency":
                            case "tosca.datatypes.scalar-unit.size":
                                propertiesInJson.addProperty("type", "string");
                                break;
                            case "timestamp":
                                propertiesInJson.addProperty("type", "string");
                                propertiesInJson.addProperty("format", "date-time");
                                break;
                            case "range":
                                propertiesInJson.addProperty("type", "integer");
                                if (!checkConstraintPresence(property, "greater_than")
                                        && currentPropertyTemplate.hasFields("exclusiveMinimum")) {
                                    propertiesInJson.addProperty("exclusiveMinimum", false);
                                }
                                if (!checkConstraintPresence(property, "less_than")
                                        && currentPropertyTemplate.hasFields("exclusiveMaximum")) {
                                    propertiesInJson.addProperty("exclusiveMaximum", false);
                                }
                                break;
                            default:
                                propertiesInJson.addProperty("type", currentPropertyTemplate.getName());
                                break;
                        }
                    }
                    break;
                case "metadata":
                    break;
                case "constraints":
                    property.addConstraintsAsJson(propertiesInJson,
                            (ArrayList<Object>) property.getItems().get("constraints"),
                            currentPropertyTemplate);
                    break;
                case "entry_schema":
                    String typeToParse = (String) property.getItems().get("type");
                    switch (typeToParse.toLowerCase()) {
                        case "list":
                        case "map":
                            if (matchComponent(extractSpecificFieldFromMap(property, "entry_schema")) != null) {
                                propertiesInJson.add("items",
                                        this.getJsonProcess(extractSpecificFieldFromMap(property, "entry_schema"),
                                                "object"));
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default://Each classical field : type, description, default..
                    if (currentPropertyTemplate.hasFields(propertyField) && !propertyField.equals("required")) {
                        property.addFieldToJson(propertiesInJson, propertyField,
                                property.getItems().get(propertyField));
                    }
                    break;
            }
        }
        return propertiesInJson;
    }

    /**
     * Look for a matching Component for the name paramater, in the components list.
     *
     * @param name the name
     * @return a component
     */
    public Component matchComponent(String name) {
        Component correspondingComponent = null;
        if (components == null) {
            return null;
        }
        for (Component component : components.values()) {
            if (component.getName().equals(name)) {
                correspondingComponent = component;
            }
        }
        return correspondingComponent;
    }

    /**
     * Simple method to extract quickly a type field from particular property item.
     *
     * @param property  the property
     * @param fieldName the fieldname
     * @return a string
     */
    @SuppressWarnings("unchecked")
    public String extractSpecificFieldFromMap(Property property, String fieldName) {
        LinkedHashMap<String, String> entrySchemaFields =
                (LinkedHashMap<String, String>) property.getItems().get(fieldName);
        return entrySchemaFields.get("type");
    }

    /**
     * Check if a constraint, for a specific property, is there.
     *
     * @param property       property
     * @param nameConstraint name constraint
     * @return a flag boolean
     */
    public boolean checkConstraintPresence(Property property, String nameConstraint) {
        boolean presentConstraint = false;
        if (property.getItems().containsKey("constraints")) {
            ArrayList<Object> constraints = (ArrayList) property.getItems().get("constraints");
            for (Object constraint : constraints) {
                if (constraint instanceof LinkedHashMap) {
                    if (((LinkedHashMap) constraint).containsKey(nameConstraint)) {
                        presentConstraint = true;
                    }
                }
            }
        }
        return presentConstraint;
    }
}
