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

	constructor(loopJson) {
		this.loopJsonCache=loopJson;
	}

	updateMicroServiceProperties(name, newMsProperties) {
			for (var policy in this.loopJsonCache["microServicePolicies"]) {
				if (this.loopJsonCache["microServicePolicies"][policy]["name"] === name) {
					this.loopJsonCache["microServicePolicies"][policy]["properties"] = newMsProperties;
				}
			}
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
			return opConfig["properties"];
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
			return msConfig["properties"];
		}
		return null;
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
}
