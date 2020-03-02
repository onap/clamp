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
			for (var policy in this.loopJsonCache["microServicePolicies"]) {
				if (this.loopJsonCache["microServicePolicies"][policy]["name"] === name) {
					this.loopJsonCache["microServicePolicies"][policy]["configurationsJson"] = newMsProperties;
				}
			}
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

	updateMicroServicePdpGroup(name, pdpGroup, pdpSubgroup) {
			for (var policy in this.loopJsonCache["microServicePolicies"]) {
				if (this.loopJsonCache["microServicePolicies"][policy]["name"] === name) {
					this.loopJsonCache["microServicePolicies"][policy]["pdpGroup"] = pdpGroup;
					this.loopJsonCache["microServicePolicies"][policy]["pdpSubgroup"] = pdpSubgroup;
				}
			}
	}

	updateGlobalProperties(newGlobalProperties) {
		this.loopJsonCache["globalPropertiesJson"] = newGlobalProperties;
	}

	updateOperationalPolicyProperties(name, newOpProperties) {
		for (var policy in this.loopJsonCache["operationalPolicies"]) {
				if (this.loopJsonCache["operationalPolicies"][policy]["name"] === name) {
					this.loopJsonCache["operationalPolicies"][policy]["configurationsJson"] = newOpProperties;
				}
			}
	}

	updateOperationalPolicyPdpGroup(name, pdpGroup, pdpSubgroup) {
		for (var policy in this.loopJsonCache["operationalPolicies"]) {
			if (this.loopJsonCache["operationalPolicies"][policy]["name"] === name) {
				this.loopJsonCache["operationalPolicies"][policy]["pdpGroup"] = pdpGroup;
				this.loopJsonCache["operationalPolicies"][policy]["pdpSubgroup"] = pdpSubgroup;
			}
		}
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

	getOperationalPolicyForName(name) {
		var opProperties=this.getOperationalPolicies();
		for (var policy in opProperties) {
			if (opProperties[policy]["name"] === name) {
				return opProperties[policy];
			}
		}
		return null;
	}

	getOperationalPolicyPropertiesForName(name) {
		var opConfig = this.getOperationalPolicyForName(name);
		if (opConfig !== null) {
			return opConfig["configurationsJson"];
		}
		return null;
	}

	getOperationalPolicyJsonRepresentationForName(name) {
		var opConfig = this.getOperationalPolicyForName(name);
		if (opConfig !== null) {
			return opConfig["jsonRepresentation"];
		}
		return null;
	}

	getOperationalPolicySupportedPdpGroup(name) {
		var opConfig=this.getOperationalPolicyForName(name);
		if (opConfig !== null) {
		    if (opConfig["policyModel"]["policyPdpGroup"] !== undefined && opConfig["policyModel"]["policyPdpGroup"]["supportedPdpGroups"] !== undefined) {
			    return opConfig["policyModel"]["policyPdpGroup"]["supportedPdpGroups"];
			}
		}
		return [];
	}

	getOperationalPolicyPdpGroup(name) {
		var opConfig=this.getOperationalPolicyForName(name);
		if (opConfig !== null) {
			return opConfig["pdpGroup"];
		}
		return null;
	}

	getOperationalPolicyPdpSubgroup(name) {
		var opConfig=this.getOperationalPolicyForName(name);
		if (opConfig !== null) {
			return opConfig["pdpSubgroup"];
		}
		return null;
	}

	getMicroServiceSupportedPdpGroup(name) {
		var microService=this.getMicroServiceForName(name);
		if (microService !== null) {
		    if (microService["policyModel"]["policyPdpGroup"] !== undefined && microService["policyModel"]["policyPdpGroup"]["supportedPdpGroups"] !== undefined) {
			    return microService["policyModel"]["policyPdpGroup"]["supportedPdpGroups"];
			}
		}
		return [];
	}

	getMicroServicePdpGroup(name) {
		var microService=this.getMicroServiceForName(name);
		if (microService !== null) {
			return microService["pdpGroup"];
		}
		return null;
	}

	getMicroServicePdpSubgroup(name) {
		var microService=this.getMicroServiceForName(name);
		if (microService !== null) {
			return microService["pdpSubgroup"];
		}
		return null;
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
			if (msProperties[policy]["policyModel"] && msProperties[policy]["policyModel"]["policyModelType"] === policyModelType && msProperties[policy]["name"].indexOf(name) >= 0) {
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
	
	getLoopTemplate() {
		return this.loopJsonCache["loopTemplate"];
	}
	
	getLoopElementModelsUsed() {
		var loopTemplate = this.getLoopTemplate();
		if(loopTemplate != null) {
			return loopTemplate["loopElementModelsUsed"];
		}
		return null;
	}
	
	getLoopElementModelNameForPolicyName(policyName) {
		var msConfig = this.getMicroServiceForName(policyName);
		if(msConfig != null) {
			return msConfig["loopElementModel"]["name"];
			 
		}
		return null;
	}
	
	getPolicyModelVariants(policyName) {
		var loopElementModelsUsed = this.getLoopElementModelsUsed()
		var policyModelVariantMap = new Map([]);
		for (var i=0; i< loopElementModelsUsed.length ; i++) {
			var loopElementModel = loopElementModelsUsed[i]["loopElementModel"];
			if(loopElementModel["name"] === policyName) {
				var policyModels  = loopElementModel["policyModels"];
				for(var model in policyModels) {
					policyModelVariantMap.set(policyModels[model]["policyModelType"], policyModels[model]["policyAcronym"]);
				}
				break;
			}
		}
		return policyModelVariantMap;
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
	
	isOpenLoopTemplate() {
		var loopTemplate = this.getLoopTemplate();
		if(loopTemplate != null && loopTemplate["allowedLoopType"] === "OPEN") {
			return true;
		} 
		return false;
	}
}
