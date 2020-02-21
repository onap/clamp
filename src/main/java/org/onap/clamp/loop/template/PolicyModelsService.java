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

package org.onap.clamp.loop.template;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import org.onap.clamp.clds.tosca.ToscaSchemaConstants;
import org.onap.clamp.clds.tosca.ToscaYamlToJsonConvertor;
import org.onap.clamp.util.SemanticVersioning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyModelsService {
    private final PolicyModelsRepository policyModelsRepository;
    private ToscaYamlToJsonConvertor toscaYamlToJsonConvertor;

    @Autowired
    public PolicyModelsService(PolicyModelsRepository policyModelrepo,
                               ToscaYamlToJsonConvertor convertor) {
        policyModelsRepository = policyModelrepo;
        toscaYamlToJsonConvertor = convertor;
    }

    public PolicyModel saveOrUpdatePolicyModel(PolicyModel policyModel) {
        return policyModelsRepository.save(policyModel);
    }

    /**
     * Creates the Tosca Policy Model from a policy tosca file,
     * if the same type already exists in database, it increases the version automatically.
     *
     * @param policyModelType  The policyModeltype in Tosca yaml
     * @param policyModelTosca The Policymodel object
     * @return The Policy Model created
     */
    public PolicyModel createNewPolicyModelFromTosca(String policyModelType,
                                                     String policyModelTosca) {
        JsonObject jsonObject = toscaYamlToJsonConvertor.validateAndConvertToJson(policyModelTosca);
        String policyModelTypeFromTosca = toscaYamlToJsonConvertor.getValueFromMetadata(jsonObject,
                ToscaSchemaConstants.METADATA_POLICY_MODEL_TYPE);
        String policyModelTypeToUse = policyModelTypeFromTosca != null ? policyModelTypeFromTosca : policyModelType;
        String policyAcronym = toscaYamlToJsonConvertor.getValueFromMetadata(jsonObject,
                ToscaSchemaConstants.METADATA_ACRONYM);

        Iterable<PolicyModel> models = getAllPolicyModelsByType(policyModelTypeToUse);
        Collections.sort((List<PolicyModel>) models);
        String latestVersion = ((List<PolicyModel>) models).get(0) != null ?
                ((List<PolicyModel>) models).get(0).getVersion() : null;

        PolicyModel newPolicyModel = new PolicyModel(policyModelTypeToUse, policyModelTosca,
                SemanticVersioning.incrementMajorVersion(latestVersion), policyAcronym);
        return saveOrUpdatePolicyModel(newPolicyModel);
    }

    /**
     * Update an existing Tosca Policy Model.
     *
     * @param policyModelType    The policy Model type in Tosca yaml
     * @param policyModelVersion The policy Version to update
     * @param policyModelTosca   The Policy Model tosca
     * @return The Policy Model updated
     */
    public PolicyModel updatePolicyModelTosca(String policyModelType, String policyModelVersion,
                                              String policyModelTosca) {
        JsonObject jsonObject = toscaYamlToJsonConvertor.validateAndConvertToJson(policyModelTosca);
        PolicyModel thePolicyModel = getPolicyModelByType(policyModelType, policyModelVersion);
        thePolicyModel.setPolicyAcronym(toscaYamlToJsonConvertor.getValueFromMetadata(jsonObject,
                ToscaSchemaConstants.METADATA_ACRONYM));
        thePolicyModel.setPolicyModelTosca(policyModelTosca);
        return saveOrUpdatePolicyModel(thePolicyModel);
    }

    public List<String> getAllPolicyModelTypes() {
        return policyModelsRepository.getAllPolicyModelType();
    }

    public Iterable<PolicyModel> getAllPolicyModels() {
        return policyModelsRepository.findAll();
    }

    public PolicyModel getPolicyModel(String type, String version) {
        return policyModelsRepository.findById(new PolicyModelId(type, version)).orElse(null);
    }

    public Iterable<PolicyModel> getAllPolicyModelsByType(String type) {
        return policyModelsRepository.findByPolicyModelType(type);
    }

    public PolicyModel getPolicyModelByType(String type, String version) {
        return policyModelsRepository.findById(new PolicyModelId(type, version)).orElse(null);
    }

    /**
     * Retrieves the Tosca model Yaml string.
     *
     * @param type The PolicyModelType
     * @return The Tosca model Yaml string
     */
    public String getPolicyModelTosca(String type, String version) {
        return policyModelsRepository.findById(new PolicyModelId(type, version)).orElse(new PolicyModel())
                .getPolicyModelTosca();
    }
}
