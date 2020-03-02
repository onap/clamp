export default class LoopComponentConverter {

	static buildMapOfComponents(loopCache) {
		var componentsMap = new Map([]);
		if (typeof (loopCache.getMicroServicePolicies()) !== "undefined") {
			loopCache.getMicroServicePolicies().forEach(ms => {
				componentsMap.set(ms.name, "/policyModal/MICRO-SERVICE-POLICY/"+ms.name);
			})
			
		}
		if (typeof (loopCache.getOperationalPolicies()) !== "undefined") {
			loopCache.getOperationalPolicies().forEach(op => {
				componentsMap.set(op.name, "/policyModal/OPERATIONAL-POLICY/"+op.name);
			})
		}
		var loopElementModelsUsed = loopCache.getLoopElementModelsUsed();
		if(loopElementModelsUsed != null) {
			for (var i=0; i< loopElementModelsUsed.length ; i++) {
				var loopElementModel = loopElementModelsUsed[i]["loopElementModel"];
				if(loopElementModel["loopElementType"] === "MICRO_SERVICE_TYPE") {
					componentsMap.set(loopElementModel["name"], "/policyModal/MICRO-SERVICE-POLICY/"+ loopElementModel["name"]);
				} else if(loopElementModel["loopElementType"] === "OPERATIONAL_POLICY_TYPE") {
					componentsMap.set(loopElementModel["name"], "/policyModal/OPERATIONAL-POLICY/"+ loopElementModel["name"]);
				}
			}
		}

		return componentsMap;
	}
}
