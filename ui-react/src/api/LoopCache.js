/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
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

export default class LoopCache {
	loopJsonCache;
	jsonRepresentationMap;
	

	constructor(loopJson) {
		this.loopJsonCache=loopJson;
		this.jsonRepresentationMap = new Map([])
	}

	updateMicroServiceProperties(name, newMsProperties) {
		var existingMSProperty = false;
		for (var policy in this.loopJsonCache["microServicePolicies"]) {
			if (this.loopJsonCache["microServicePolicies"][policy]["name"] === name) {
				this.loopJsonCache["microServicePolicies"][policy]["configurationsJson"] = newMsProperties;
				existingMSProperty = true;
				break;
			}
		}
		return existingMSProperty;
	}
	
	updateMicroServicePropertiesByPolicyModelType(modelType, newMsProperties) {
		var existingMSProperty = false;
		for (var policy in this.loopJsonCache["microServicePolicies"]) {
			if (this.loopJsonCache["microServicePolicies"][policy]["modelType"] === modelType 
					&& this.loopJsonCache["microServicePolicies"][policy]["name"].indexOf(newMsProperties["name"]) >= 0) {
				this.loopJsonCache["microServicePolicies"][policy]["configurationsJson"] = newMsProperties;
				existingMSProperty = true;
				break;
			}
		}
		return existingMSProperty;
	}

	updateGlobalProperties(newGlobalProperties) {
		this.loopJsonCache["globalPropertiesJson"] = newGlobalProperties;
	}

	updateOperationalPolicyProperties(newOpProperties) {
		this.loopJsonCache["operationalPolicies"] = newOpProperties;
	}

	getLoopName() {
		return this.loopJsonCache["name"];
	}

	getOperationalPolicyConfigurationJson() {
		return this.loopJsonCache["operationalPolicies"]["0"]["configurationsJson"];
	}
	
	getOperationalPolicyJsonSchema() {
		return this.loopJsonCache["operationalPolicies"]["0"]["jsonRepresentation"];
	}

	getOperationalPolicies() {
		return this.loopJsonCache["operationalPolicies"];
	}

	getOperationalPoliciesNoJsonSchema() {
		var operationalPolicies = JSON.parse(JSON.stringify(this.loopJsonCache["operationalPolicies"]));
		delete operationalPolicies[0]["jsonRepresentation"];
		return operationalPolicies;
	}

	getGlobalProperties() {
		return this.loopJsonCache["globalPropertiesJson"];
	}

	getDcaeDeploymentProperties() {
		return this.loopJsonCache["globalPropertiesJson"]["dcaeDeployParameters"];
	}

	getMicroServicePolicies() {
		return this.loopJsonCache["microServicePolicies"];
	}
	
	getLoopTemplate() {
		return this.loopJsonCache["loopTemplate"];
	}
	
	getLoopElementModelsUsed() {
		return this.loopJsonCache["loopTemplate"]["loopElementModelsUsed"];
	}
	
	getPolicyModelVariants(componentName) {
		var loopElementModelsUsed = this.getLoopElementModelsUsed()
		var policyModels = null;
		for (var i=0; i< loopElementModelsUsed.length ; i++) {
			var loopElementModel = loopElementModelsUsed[i]["loopElementModel"];
			if(loopElementModel["name"] === componentName) {
				policyModels = loopElementModel["policyModels"];
				break;
			}
		}
		return policyModels;
	}

	updateJsonRepresentationMap(policyModelType, jsonRepresentation) {
		this.jsonRepresentationMap.set(policyModelType, jsonRepresentation);
	}
	
	getJsonRepresentationFromMap(policyModelType) {
		if(this.jsonRepresentationMap.has(policyModelType)) {
			return this.jsonRepresentationMap.get(policyModelType);
		} else {
			return null;
		}
	}
	
	getMicroServiceForName(name) {
		var msProperties=this.getMicroServicePolicies();
		for (var policy in msProperties) {
			if (msProperties[policy]["name"] === name) {
				return msProperties[policy];
			}
		}
		return null;
	}

	getMicroServicePropertiesForName(name) {
		var msConfig = this.getMicroServiceForName(name);
		if (msConfig !== null) {
			return msConfig["configurationsJson"];
		}
		return null;
	}
	
	getMicroServiceByModelType(policyModelType, name) {
		var msProperties = this.getMicroServicePolicies();
		var matchingMicroservices = [];
		for (var policy in msProperties) {
			if (msProperties[policy]["modelType"] === policyModelType && msProperties[policy]["name"].indexOf(name) >= 0) {
				matchingMicroservices.push(msProperties[policy]);
			}
		}
		return matchingMicroservices;
	}
	
	getMicroServicePropertiesForModelType(policyModelType, name) {
		var msConfig = this.getMicroServiceByModelType(policyModelType, name);
		var msConfigArray = [];
		if (msConfig !== [] && msConfig !== null) {
			for(var config in msConfig) {
				msConfigArray.push(msConfig[config]["configurationsJson"])
			}
		}
		return msConfigArray;
	}
	
	getMicroServiceJsonRepresentationForName(name) {
		var msConfig = this.getMicroServiceForName(name);
		if (msConfig !== null) {
			return msConfig["jsonRepresentation"];
		}
		return null;
	}

	getResourceDetailsVfProperty() {
		return this.loopJsonCache["modelService"]["resourceDetails"]["VF"];
	}

	getResourceDetailsVfModuleProperty() {
		return this.loopJsonCache["modelService"]["resourceDetails"]["VFModule"];
	}

	getLoopLogsArray() {
		return this.loopJsonCache.loopLogs;
	}

	getComputedState() {
		return this.loopJsonCache.lastComputedState;
	}

	getComponentStates() {
		return this.loopJsonCache.components;
	}
	
	isOpenLoopTemplate() {
		var loopTemplate = this.getLoopTemplate();
		if(loopTemplate != null && loopTemplate["allowedLoopType"] === "OPEN") {
			return true;
		} else {
			return false;
		}
	}
}
