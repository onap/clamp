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

package org.onap.clamp.clds.tosca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.clamp.clds.dao.CldsDao;
import org.onap.clamp.clds.model.CldsDictionaryItem;
import org.yaml.snakeyaml.Yaml;

/**
 * Tosca Model Yaml parser and convertor to JSON Schema consumable for JSON
 * Editor
 *
 */
public class ToscaYamlToJsonConvertor {

    private CldsDao cldsDao;
    private int simpleTypeOrder = 1000;
    private int complexTypeOrder = 10000;
    private int complexSimpleTypeOrder = 1;

    public ToscaYamlToJsonConvertor(CldsDao cldsDao) {
        this.cldsDao = cldsDao;
    }

    private int incrementSimpleTypeOrder() {
        return simpleTypeOrder++;
    }

    private int incrementComplexTypeOrder() {
        return complexTypeOrder = complexTypeOrder + 10000;
    }

    private int incrementComplexSimpleTypeOrder() {
        complexSimpleTypeOrder++;
        return complexTypeOrder + complexSimpleTypeOrder;
    }

    /**
     * @return the cldsDao
     */
    public CldsDao getCldsDao() {
        return cldsDao;
    }

    /**
     * @param cldsDao
     *        the cldsDao to set
     */
    public void setCldsDao(CldsDao cldsDao) {
        this.cldsDao = cldsDao;
    }

    @SuppressWarnings("unchecked")
    public String parseToscaYaml(String yamlString) {

        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> loadedYaml =  yaml.load(yamlString);
        LinkedHashMap<String, Object> nodeTypes = new LinkedHashMap<>();
        LinkedHashMap<String, Object> dataNodes = new LinkedHashMap<>();
        JSONObject jsonEditorObject = new JSONObject();
        JSONObject jsonParentObject = new JSONObject();
        JSONObject jsonTempObject = new JSONObject();
        parseNodeAndDataType(loadedYaml, nodeTypes, dataNodes);
        populateJsonEditorObject(nodeTypes, dataNodes, jsonParentObject, jsonTempObject);
        if (jsonTempObject.length() > 0) {
            jsonParentObject = jsonTempObject;
        }
        jsonEditorObject.put(JsonEditorSchemaConstants.SCHEMA, jsonParentObject);
        return jsonEditorObject.toString();
    }

    // Parse node_type and data_type
    @SuppressWarnings("unchecked")
    private void parseNodeAndDataType(LinkedHashMap<String, Object> map, LinkedHashMap<String, Object> nodeTypes,
        LinkedHashMap<String, Object> dataNodes) {
        map.entrySet().stream().forEach(node -> parseNodeBasedOnType(nodeTypes, dataNodes, node));
    }

    private void parseNodeBasedOnType(LinkedHashMap<String, Object> nodeTypes, LinkedHashMap<String, Object> dataNodes, Map.Entry<String, Object> n) {
        String nodeName = n.getKey();
        if (nodeName.contains(ToscaSchemaConstants.NODE_TYPES) || nodeName.contains(ToscaSchemaConstants.DATA_TYPES)) {
            parseNodeAndDataType((LinkedHashMap<String, Object>) n.getValue(), nodeTypes, dataNodes);
        }
        else if (nodeName.contains(ToscaSchemaConstants.POLICY_NODE)) {
            nodeTypes.put(nodeName, n.getValue());
        } else if (isPolicyDataComposedType(nodeName)) {
            dataNodes.put(nodeName, n.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void populateJsonEditorObject(LinkedHashMap<String, Object> nodeTypes,
        LinkedHashMap<String, Object> dataNodes, JSONObject jsonParentObject, JSONObject jsonTempObject) {

        Map<String, JSONObject> jsonEntrySchema = new HashMap();
        jsonParentObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_OBJECT);

        nodeTypes.entrySet()
                .stream()
                .filter(this::hasMapAsValue)
                .map(el -> ((LinkedHashMap<String, Object>) el.getValue()).entrySet())
                .flatMap(Collection::stream)
                .filter(entry -> isPropertiesNode(entry) && hasMapAsValue(entry))
                .forEach(propertyElement ->
                        parseNodeTypeProperties(propertyElement, dataNodes, jsonParentObject, jsonTempObject, jsonEntrySchema));
    }

    @SuppressWarnings("unchecked")
    private void parseNodeTypeProperties(Map.Entry<String, Object> ntElement, LinkedHashMap<String, Object> dataNodes, JSONObject jsonParentObject, JSONObject jsonTempObject, Map<String, JSONObject> jsonEntrySchema) {
        JSONArray rootNodeArray = new JSONArray();
        ((LinkedHashMap<String, Object>) ntElement.getValue()).entrySet()

                .forEach((ntPropertiesElement) -> {
                    parseDescription((LinkedHashMap<String, Object>) ntPropertiesElement.getValue(), jsonParentObject);
                    LinkedHashMap<String, Object> parentPropertiesMap = (LinkedHashMap<String, Object>) ntPropertiesElement.getValue();
                    if (isTypeOfListAndHasEntrySchema(parentPropertiesMap)) {
                        parentPropertiesMap = (LinkedHashMap<String, Object>) parentPropertiesMap.get(ToscaSchemaConstants.ENTRY_SCHEMA);
                        populatePropertiesOfListNode(jsonParentObject, jsonTempObject, ntPropertiesElement);
                    }
                    if (isTypeOfPolicyData(parentPropertiesMap)) {
                        populateDataNodes(dataNodes, jsonParentObject, jsonEntrySchema, rootNodeArray, parentPropertiesMap);
                    }
                });
    }

    private boolean isTypeOfListAndHasEntrySchema(Map<String, Object> parentPropertiesMap) {
        return parentPropertiesMap.containsKey(ToscaSchemaConstants.TYPE) && ((String) parentPropertiesMap.get(ToscaSchemaConstants.TYPE)).contains(ToscaSchemaConstants.TYPE_LIST)
                && parentPropertiesMap.containsKey(ToscaSchemaConstants.ENTRY_SCHEMA);
    }

    private boolean isTypeOfPolicyData(Map<String, Object> parentPropertiesMap) {
        return parentPropertiesMap.containsKey(ToscaSchemaConstants.TYPE)
                && isPolicyDataComposedType((String) parentPropertiesMap.get(ToscaSchemaConstants.TYPE));
    }

    @SuppressWarnings("unchecked")
    private void populatePropertiesOfListNode(JSONObject jsonParentObject, JSONObject jsonTempObject, Map.Entry<String, Object> ntPropertiesElement) {
        jsonTempObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_ARRAY);
        parseDescription((LinkedHashMap<String, Object>) ntPropertiesElement.getValue(), jsonTempObject);
        jsonTempObject.put(JsonEditorSchemaConstants.ITEMS, jsonParentObject);
        jsonTempObject.put(JsonEditorSchemaConstants.FORMAT, JsonEditorSchemaConstants.CUSTOM_KEY_FORMAT_TABS_TOP);
        jsonTempObject.put(JsonEditorSchemaConstants.UNIQUE_ITEMS, JsonEditorSchemaConstants.TRUE);
    }

    @SuppressWarnings("unchecked")
    private void populateDataNodes(LinkedHashMap<String, Object> dataNodes, JSONObject jsonParentObject,
                                   Map<String, JSONObject> jsonEntrySchema, JSONArray rootNodeArray,
                                   Map<String, Object> parentPropertiesMap) {
        ((LinkedHashMap<String, Object>) dataNodes.get(parentPropertiesMap.get(ToscaSchemaConstants.TYPE)))
                .entrySet()
                .stream()
                .filter(this::isPropertiesNode)
                .map(entry -> (LinkedHashMap<String, Object>) entry.getValue())
                .forEach(map -> {
                    incrementSimpleTypeOrder();
                    parseToscaProperties(map, jsonParentObject, rootNodeArray, jsonEntrySchema, dataNodes);});

    }

    @SuppressWarnings("unchecked")
    private void parseToscaProperties(LinkedHashMap<String, Object> propertiesMap,
        JSONObject jsonDataNode, JSONArray requiredPropertyNames, Map<String, JSONObject> jsonEntrySchema,
        LinkedHashMap<String, Object> dataNodes) {
        JSONObject jsonPropertyNode = new JSONObject();
        propertiesMap.entrySet()
                .stream()
                .filter(this::hasMapAsValue)
                .forEach(entry -> {
                    LinkedHashMap<String, Object> nodeMap = (LinkedHashMap<String, Object>) entry.getValue();
                    parseToscaChildNodeMap(entry.getKey(), entry.getValue(), jsonPropertyNode, jsonEntrySchema, dataNodes, incrementSimpleTypeOrder());
                    if (isPropertyRequired(nodeMap)) {
                        requiredPropertyNames.put(entry.getKey());
                    }
                });
        addRequiredPropertiesToDataNode(jsonDataNode, requiredPropertyNames, jsonPropertyNode);
    }

    @SuppressWarnings("unchecked")
    private void parseToscaPropertiesForType(LinkedHashMap<String, Object> propertiesMap,
        JSONObject jsonDataNode, JSONArray requiredPropertyNames, Map<String, JSONObject> jsonEntrySchema,
        LinkedHashMap<String, Object> dataNodes, int order) {
        JSONObject jsonPropertyNode = new JSONObject();
        propertiesMap.entrySet()
                .stream()
                .filter(this::hasMapAsValue)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (LinkedHashMap<String, Object>) e.getValue()))
                .entrySet()
                .forEach(el -> {
                    parseToscaChildNodeMap(el.getKey(), el.getValue(), jsonPropertyNode, jsonEntrySchema, dataNodes, order);
                    if (isPropertyRequired(el.getValue())) {
                        requiredPropertyNames.put(el.getKey());
                    }
                });
        addRequiredPropertiesToDataNode(jsonDataNode, requiredPropertyNames, jsonPropertyNode);
    }
    private boolean isPropertyRequired(LinkedHashMap<String, Object> nodeMap) {
        return nodeMap.containsKey(ToscaSchemaConstants.REQUIRED) && ((boolean) nodeMap.get(ToscaSchemaConstants.REQUIRED));
    }

    private void addRequiredPropertiesToDataNode(JSONObject jsonDataNode, JSONArray requiredPropertyNames, JSONObject jsonPropertyNode){
        jsonDataNode.put(JsonEditorSchemaConstants.REQUIRED, requiredPropertyNames);
        jsonDataNode.put(JsonEditorSchemaConstants.PROPERTIES, jsonPropertyNode);
    }

    private void parseToscaChildNodeMap(String childObjectKey, Object childNodeMap,
        JSONObject jsonPropertyNode, Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes, int order) {
        JSONObject childObject = new JSONObject();
        LinkedHashMap<String, Object> childNodeHashMap = (LinkedHashMap<String, Object>) childNodeMap;
        parseDescription(childNodeHashMap, childObject);
        parseTypes(childNodeHashMap, childObject, jsonEntrySchema, dataNodes, order);
        parseConstraints(childNodeHashMap, childObject);
        parseEntrySchema(childNodeHashMap, childObject, jsonEntrySchema, dataNodes);

        jsonPropertyNode.put(childObjectKey, childObject);
    }

    private void parseEntrySchema(LinkedHashMap<String, Object> childNodeMap, JSONObject childObject, Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes) {
            Object entrySchema = childNodeMap.get(ToscaSchemaConstants.ENTRY_SCHEMA);
            if (entrySchema instanceof Map) {
                Map<String, Object>  entrySchemaWithDefinedType = ((LinkedHashMap<String, Object>) entrySchema).entrySet()
                        .stream()
                        .filter(this::hasPropertyDefinedType).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                entrySchemaWithDefinedType.entrySet()
                        .stream()
                        .filter(entry -> isPolicyDataComposedType((String) entry.getValue()))
                        .map(entry -> (String) entry.getValue())
                        .forEach(entrySchemaType -> {
                            if (jsonEntrySchema.get(entrySchemaType) != null) {
                                // Already traversed
                                attachEntrySchemaJsonObject(childObject, jsonEntrySchema.get(entrySchemaType), JsonEditorSchemaConstants.TYPE_OBJECT);
                            }
                            else if (dataNodes.containsKey(entrySchemaType)) {
                                // Need to traverse
                                ((LinkedHashMap<String, Object>) dataNodes.get(entrySchemaType)).entrySet()
                                        .stream()
                                        .filter(this::isPropertiesNode)
                                        .map(el -> (LinkedHashMap<String, Object>) el.getValue())
                                        .forEach(propertyMap -> processDataTypeProperty(childObject, jsonEntrySchema, dataNodes, entrySchemaType, propertyMap));
                            }
                        });

                entrySchemaWithDefinedType.entrySet()
                        .stream()
                        .map(Map.Entry::getValue)
                        .filter(this::isPrimitiveType)
                        .map(el -> indicatesNumericType(el) ? JsonEditorSchemaConstants.TYPE_INTEGER: JsonEditorSchemaConstants.TYPE_STRING)
                        .forEach(jsonType -> {
                            JSONObject entrySchemaObject = new JSONObject();
                            parseConstraints(entrySchemaWithDefinedType, entrySchemaObject);
                            if (hasNodeListType(childNodeMap)) {
                                // Only known value of type is String for now// Custom key for JSON Editor and UI rendering
                                childObject.put(JsonEditorSchemaConstants.CUSTOM_KEY_FORMAT, JsonEditorSchemaConstants.FORMAT_SELECT);
                            }
                            attachEntrySchemaJsonObject(childObject, entrySchemaObject, jsonType);
                        });
            }
    }


    private boolean hasPropertyDefinedType(Map.Entry<String, Object> entry) {
        return entry.getKey().equalsIgnoreCase(ToscaSchemaConstants.TYPE) && entry.getValue() != null;
    }

    private boolean isPrimitiveType(Object entrySchemaType) {
        return indicatesNumericType(entrySchemaType) || ((String) entrySchemaType).equalsIgnoreCase(ToscaSchemaConstants.TYPE_STRING);
    }

    private boolean hasNodeListType(Map<String, Object> childNodeMap) {
        Object type = childNodeMap.get(ToscaSchemaConstants.TYPE);
        return type instanceof String && isListType((String) type);
    }

    private boolean indicatesNumericType(Object entrySchemaType) {
        if(!(entrySchemaType instanceof String)){
            return false;
        }
        String entrySchemaTypeStr = (String) entrySchemaType;
        return entrySchemaTypeStr.equalsIgnoreCase(ToscaSchemaConstants.TYPE_INTEGER)
                || entrySchemaTypeStr.equalsIgnoreCase(ToscaSchemaConstants.TYPE_FLOAT);
    }

    private void processDataTypeProperty(JSONObject childObject, Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes, String entrySchemaType, LinkedHashMap<String, Object> propertyMap) {
        JSONObject entrySchemaObject = new JSONObject();
        incrementComplexTypeOrder();
        parseToscaProperties(propertyMap, entrySchemaObject, new JSONArray(), jsonEntrySchema, dataNodes);
        jsonEntrySchema.put(entrySchemaType, entrySchemaObject);
        dataNodes.remove(entrySchemaType);
        attachEntrySchemaJsonObject(childObject, entrySchemaObject, JsonEditorSchemaConstants.TYPE_OBJECT);
    }

    private void attachEntrySchemaJsonObject(JSONObject childObject, JSONObject entrySchemaObject, String dataType) {

        entrySchemaObject.put(JsonEditorSchemaConstants.TYPE, dataType);
        childObject.put(JsonEditorSchemaConstants.ITEMS, entrySchemaObject);
    }

    @SuppressWarnings("unchecked")
    private void attachTypeJsonObject(JSONObject childObject, JSONObject typeObject) {
        Iterator<String> keys = typeObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            childObject.put(key, typeObject.get(key));
        }
    }

    private void parseDescription(LinkedHashMap<String, Object> childNodeMap, JSONObject childObject) {
        if (childNodeMap.get(ToscaSchemaConstants.DESCRIPTION) != null) {
            childObject.put(JsonEditorSchemaConstants.TITLE, childNodeMap.get(ToscaSchemaConstants.DESCRIPTION));
        }
    }

    private void parseTypes(LinkedHashMap<String, Object> childNodeMap, JSONObject childObject,
        Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes, int order) {
            // Only known value of type is String for now
            if (isTypeInYamlProperlyDefined(childNodeMap)) {
                childObject.put(JsonEditorSchemaConstants.PROPERTY_ORDER, order);
                String typeValue = (String) childNodeMap.get(ToscaSchemaConstants.TYPE);
                addTypeBasedOnValue(typeValue, childObject, jsonEntrySchema, dataNodes);
            }
            addDefaultValueIfPresent(childNodeMap, childObject);
    }

    private void addTypeBasedOnValue(String typeValue, JSONObject childObject, Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes) {
        if (isNumberType(typeValue)) {
            childObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_INTEGER);
        } else if (isListType(typeValue)) {
            childObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_ARRAY);
            // Custom key for JSON Editor and UI rendering
            childObject.put(JsonEditorSchemaConstants.CUSTOM_KEY_FORMAT,
                JsonEditorSchemaConstants.CUSTOM_KEY_FORMAT_TABS_TOP);
            childObject.put(JsonEditorSchemaConstants.UNIQUE_ITEMS, JsonEditorSchemaConstants.TRUE);
        } else if (isMapType(typeValue)) {
            childObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_OBJECT);
        } else if (isPolicyDataComposedType(typeValue)) {
            processComposedDataType(childObject, jsonEntrySchema, dataNodes, typeValue);
        } else {
            addDefaultTypeIfNotRecognized(childObject);
        }
    }

    private boolean isPropertiesNode(Map.Entry<String, Object> el) {
        return el.getKey().equalsIgnoreCase(ToscaSchemaConstants.PROPERTIES);
    }

    private void processComposedDataType(JSONObject childObject, Map<String, JSONObject> jsonEntrySchema, LinkedHashMap<String, Object> dataNodes, String typeValue) {
        JSONArray childArray = new JSONArray();

        if (jsonEntrySchema.get(typeValue) != null) {
            // Already traversed
            attachTypeJsonObject(childObject, jsonEntrySchema.get(typeValue));
        } else if (dataNodes.containsKey(typeValue)) {
            JSONObject entrySchemaObject = new JSONObject();
            ((LinkedHashMap<String, Object>) dataNodes.get(typeValue)).entrySet()
                    .stream()
                    .filter(this::isPropertiesNode)
                    .map(entry -> (LinkedHashMap<String, Object>) entry.getValue())
                    .forEach(propertiesMap -> {
                        parseToscaPropertiesForType(propertiesMap, entrySchemaObject, childArray,
                                jsonEntrySchema, dataNodes, incrementComplexSimpleTypeOrder());
                        jsonEntrySchema.put(typeValue, entrySchemaObject);
                        dataNodes.remove(typeValue);
                        attachTypeJsonObject(childObject, entrySchemaObject);
                    });
        }
    }

    private void processConstraint(Map<String, Object> childNodeMap, JSONObject childObject, String constraintName, Object constraintValue) {
        if (isMinLengthOrEqOrGtConstraint(constraintName)) {
            // For String min_lenghth is minimum length whereas for number, it will be
            // minimum or greater than to the defined value
            if (hasStringType(childNodeMap)) {
                childObject.put(JsonEditorSchemaConstants.MIN_LENGTH, constraintValue);
            } else {
                childObject.put(JsonEditorSchemaConstants.MINIMUM, constraintValue);
            }
        }
        else if (isMaxLengthOrLtOrEqConstraint(constraintName)) {
            // For String max_lenghth is maximum length whereas for number, it will be
            // maximum or less than the defined value
            if (hasStringType(childNodeMap)) {
                childObject.put(JsonEditorSchemaConstants.MAX_LENGTH, constraintValue);
            } else {
                childObject.put(JsonEditorSchemaConstants.MAXIMUM, constraintValue);
            }
        }
        else if (is(constraintName,ToscaSchemaConstants.LESS_THAN)) {
            childObject.put(JsonEditorSchemaConstants.EXCLUSIVE_MAXIMUM, constraintValue);
        }
        else if (is(constraintName, ToscaSchemaConstants.GREATER_THAN)) {
            childObject.put(JsonEditorSchemaConstants.EXCLUSIVE_MINIMUM, constraintValue);
        }
        else if (isInRangeConstraint(constraintName, constraintValue)) {
            processInRangeConstraint(childNodeMap, childObject, (ArrayList<?>) constraintValue);
        }
        else if (isValidValuesConstraint(constraintName, constraintValue)) {
            processValidValuesConstraint(childObject, (ArrayList<?>)constraintValue);
        }
    }

    private void processValidValuesConstraint(JSONObject childObject, ArrayList<?> constraintValue) {
        JSONArray validValuesArray = new JSONArray();
        boolean noDictionaryToProcess =  constraintValue
                .stream()
                .noneMatch(value -> (value instanceof String && ((String) value).contains(ToscaSchemaConstants.DICTIONARY)));
        if (noDictionaryToProcess) {
            constraintValue.stream().forEach(validValuesArray::put);
            childObject.put(JsonEditorSchemaConstants.ENUM, validValuesArray);
        }
        else {
            constraintValue
                    .stream()
                    .filter(value -> value instanceof String)
                    .map(value -> (String) value)
                    .filter(value -> value.contains(ToscaSchemaConstants.DICTIONARY))
                    .forEach(value -> processDictionaryElements(childObject, value));
        }
    }

    private void processInRangeConstraint(Map<String, Object> childNodeMap, JSONObject childObject, ArrayList<?> constraintValue) {
        if (hasStringType(childNodeMap)) {
            childObject.put(JsonEditorSchemaConstants.MIN_LENGTH,
                    constraintValue.get(0));
            childObject.put(JsonEditorSchemaConstants.MAX_LENGTH,
                    constraintValue.get(1));
        }
        else {
            childObject.put(JsonEditorSchemaConstants.MINIMUM,
                    constraintValue.get(0));
            childObject.put(JsonEditorSchemaConstants.MAXIMUM,
                    constraintValue.get(1));
        }
    }

    private void parseConstraints(Map<String, Object> childNodeMap, JSONObject childObject) {
        if (hasConstraints(childNodeMap)) {
            List<LinkedHashMap<String, Object>> constraintsList = (List<LinkedHashMap<String, Object>>) childNodeMap
                .get(ToscaSchemaConstants.CONSTRAINTS);
            constraintsList.stream()
                    .map(LinkedHashMap::entrySet)
                    .flatMap(Collection::stream)
                    .forEach(constraint -> processConstraint(childNodeMap, childObject, constraint.getKey(), constraint.getValue()));
        }
    }

    private boolean hasConstraints(Map<String, Object> childNodeMap) {
        return childNodeMap.containsKey(ToscaSchemaConstants.CONSTRAINTS)
            && childNodeMap.get(ToscaSchemaConstants.CONSTRAINTS) != null;
    }

    private void addDefaultTypeIfNotRecognized(JSONObject childObject) {
        childObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_STRING);
    }

    private boolean isPolicyDataComposedType(String typeValue) {
        return typeValue.contains(ToscaSchemaConstants.POLICY_DATA);
    }

    private boolean isMapType(String typeValue) {
        return typeValue.equalsIgnoreCase(ToscaSchemaConstants.TYPE_MAP);
    }

    private boolean isListType(String typeValue) {
        return typeValue.equalsIgnoreCase(ToscaSchemaConstants.TYPE_LIST);
    }

    private boolean isTypeInYamlProperlyDefined(LinkedHashMap<String, Object> childNodeMap) {
        return childNodeMap.get(ToscaSchemaConstants.TYPE) instanceof String;
    }

    private boolean isIntegerType(String typeValue) {
        return typeValue.equalsIgnoreCase(ToscaSchemaConstants.TYPE_INTEGER);
    }

    private boolean isNumberType(String typeValue) {
        return typeValue.equalsIgnoreCase(ToscaSchemaConstants.TYPE_INTEGER) || typeValue.equalsIgnoreCase(ToscaSchemaConstants.TYPE_FLOAT);
    }

    private void addDefaultValueIfPresent(LinkedHashMap<String, Object> childNodeMap, JSONObject childObject) {
        if (childNodeMap.get(ToscaSchemaConstants.DEFAULT) != null) {
            childObject.put(JsonEditorSchemaConstants.DEFAULT, childNodeMap.get(ToscaSchemaConstants.DEFAULT));
        }
    }

    private boolean hasMapAsValue(Map.Entry<String,Object> obj){
        return obj.getValue() instanceof Map;
    }

    private boolean isInRangeConstraint(String constraintName, Object constraintValue) {
        return is(constraintName, ToscaSchemaConstants.IN_RANGE) && constraintValue instanceof ArrayList<?>;
    }

    private boolean isValidValuesConstraint(String constraintName, Object constraintValue) {
        return constraintName.equalsIgnoreCase(ToscaSchemaConstants.VALID_VALUES) && constraintValue instanceof ArrayList<?>;
    }

    private boolean is(String propertyName, String keyword) {
        return propertyName.equalsIgnoreCase(keyword);
    }

    private boolean isMaxLengthOrLtOrEqConstraint(String constraintNameInYaml) {
        return constraintNameInYaml.equalsIgnoreCase(ToscaSchemaConstants.MAX_LENGTH) || constraintNameInYaml.equalsIgnoreCase(ToscaSchemaConstants.LESS_OR_EQUAL);
    }

    private boolean hasStringType(Map<String, Object> childNodeMap) {
        return childNodeMap.containsKey(ToscaSchemaConstants.TYPE) && (childNodeMap.get(ToscaSchemaConstants.TYPE) instanceof String) && ((String) childNodeMap.get(ToscaSchemaConstants.TYPE)).equalsIgnoreCase(ToscaSchemaConstants.TYPE_STRING);
    }

    private boolean isMinLengthOrEqOrGtConstraint(String constraintNameInYaml) {
        return constraintNameInYaml.equalsIgnoreCase(ToscaSchemaConstants.MIN_LENGTH) || constraintNameInYaml.equalsIgnoreCase(ToscaSchemaConstants.GREATER_OR_EQUAL);
    }

    private void processDictionaryElements(JSONObject childObject, String dictionaryReference) {
        if (dictionaryReference.contains("#")) {// We support only one # as of now.
            String[] dictionaryKeyArray = dictionaryReference
                    .substring(dictionaryReference.indexOf(ToscaSchemaConstants.DICTIONARY) + 11).split("#");
            processDictionaryElementsWithHashInReference(childObject, dictionaryKeyArray);
        } else {
            String dictionaryKey = dictionaryReference.substring(dictionaryReference.indexOf(ToscaSchemaConstants.DICTIONARY) + 11);
            processDictionaryElementsWithoutHashInReference(childObject, dictionaryKey);
        }
    }

    private void processDictionaryElementsWithHashInReference(JSONObject childObject, String[] dictionaryKeyArray ) {
        if (dictionaryKeyArray != null && dictionaryKeyArray.length == 2) {
            List<CldsDictionaryItem> cldsDictionaryElements =  getCldsDao().getDictionaryElements(dictionaryKeyArray[0], null, null);
            List<CldsDictionaryItem> subDictionaryElements = getCldsDao().getDictionaryElements(dictionaryKeyArray[1], null, null);

            if (cldsDictionaryElements != null) {
                List<String> subCldsDictionaryNames = subDictionaryElements.stream()
                    .map(CldsDictionaryItem::getDictElementShortName).collect(Collectors.toList());
                JSONArray jsonArray = new JSONArray();

                cldsDictionaryElements.stream()
                        .forEach(c -> {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(JsonEditorSchemaConstants.TYPE, getJsonType(c.getDictElementType()));
                            if (c.getDictElementType() != null && c.getDictElementType().equalsIgnoreCase(ToscaSchemaConstants.TYPE_STRING)) {
                                jsonObject.put(JsonEditorSchemaConstants.MIN_LENGTH, 1);
                            }
                            jsonObject.put(JsonEditorSchemaConstants.ID, c.getDictElementName());
                            jsonObject.put(JsonEditorSchemaConstants.LABEL, c.getDictElementShortName());
                            jsonObject.put(JsonEditorSchemaConstants.OPERATORS, subCldsDictionaryNames);
                            jsonArray.put(jsonObject);
                        });
                JSONObject filterObject = new JSONObject();
                filterObject.put(JsonEditorSchemaConstants.FILTERS, jsonArray);

                childObject.put(JsonEditorSchemaConstants.TYPE, JsonEditorSchemaConstants.TYPE_QBLDR);
                // TO invoke validation on such parameters
                childObject.put(JsonEditorSchemaConstants.MIN_LENGTH, 1);
                childObject.put(JsonEditorSchemaConstants.QSSCHEMA, filterObject);

            }
        }
    }

    private void processDictionaryElementsWithoutHashInReference(JSONObject childObject, String dictionaryKey) {
        if (dictionaryKey != null) {
            List<CldsDictionaryItem> cldsDictionaryElements = getCldsDao().getDictionaryElements(dictionaryKey,
                null, null);
            if (cldsDictionaryElements != null) {
                List<String> cldsDictionaryNames = getDictionaryNames(cldsDictionaryElements);
                List<String> cldsDictionaryFullNames = getDictionaryFullNames(cldsDictionaryElements);
                if(cldsDictionaryFullNames.isEmpty()){
                    childObject.put(JsonEditorSchemaConstants.ENUM, cldsDictionaryNames);
                }
                else{
                    childObject.put(JsonEditorSchemaConstants.ENUM, cldsDictionaryFullNames);
                    // Add Enum titles for generated translated values during JSON instance generation
                    JSONObject enumTitles = new JSONObject();
                    enumTitles.put(JsonEditorSchemaConstants.ENUM_TITLES, cldsDictionaryNames);
                    childObject.put(JsonEditorSchemaConstants.OPTIONS, enumTitles);
                }

            }
        }
    }

    List<String> getDictionaryNames(List<CldsDictionaryItem> cldsDictionaryElements){
        return cldsDictionaryElements
                .stream()
                .map(CldsDictionaryItem::getDictElementShortName)
                .collect(Collectors.toList());
    }

    List<String> getDictionaryFullNames(List<CldsDictionaryItem> cldsDictionaryElements){
        return cldsDictionaryElements
                .stream()
                .filter(el -> el.getDictElementType() != null && !el.getDictElementType().equalsIgnoreCase("json"))
                .map(CldsDictionaryItem::getDictElementName)
                .collect(Collectors.toList());
    }

    private String getJsonType(String toscaType) {
        String jsonType = JsonEditorSchemaConstants.TYPE_STRING;
        if (isIntegerType(toscaType)) {
            jsonType = JsonEditorSchemaConstants.TYPE_INTEGER;
        } else if (isListType(toscaType)) {
            jsonType = JsonEditorSchemaConstants.TYPE_ARRAY;
        }
        return jsonType;
    }

}