require('jquery/dist/jquery.min.js');
require('angular/angular.min.js');
require('angular-mocks/angular-mocks.js');
require('angular-route/angular-route.min.js');
require('angular-resource/angular-resource.min.js');
require('angular-cookies/angular-cookies.min.js');
require('angular-animate/angular-animate.min.js');
require('angular-sanitize/angular-sanitize.min.js');
require('angular-touch/angular-touch.min.js');
require('popper.js/dist/umd/popper.min.js');
require('bootstrap/dist/js/bootstrap.min.js');
require('angular-ui-bootstrap/dist/ui-bootstrap-tpls.js');
require('angular-loading-bar/src/loading-bar.js');
require('angular-dialog-service/dist/dialogs.js');
require('scripts/app.js');
require('scripts/propertyController.js');

describe('Property controller tests', function() {
	//var clModel = '{"name": "ClosedLoopTest","lastComputedState":"DESIGN","svgRepresentation": "representation","globalPropertiesJson": [{"name":"service","value":["4cc5b45a-1f63-4194-8100-cd8e14248c92"]},{"name":"vf","value":["07e266fc-49ab-4cd7-8378-ca4676f1b9ec"]},{"name":"actionSet","value":["vnfRecipe"]},{"name":"location","value":["DC1"]},{"name":"deployParameters","value":{"location_id":"","service_id":"","policy_id":"AUTO_GENERATED_POLICY_ID_AT_SUBMIT"}}], "blueprint": "yaml","lastComputedState": "DESIGN","operationalPolicies": [ {"name": "OpPolicyTest", "configurationsJson": { "policy1": [{"name": "pname","value": "policy1"},{"name": "pid","value": "0"},{"name": "timeout","value": "345"},{"policyConfigurations": [[{"name": "recipe","value": ["restart"]},{"name": "maxRetries","value": ["3"]},{"name": "retryTimeLimit","value": ["180"]},{"name": "_id","value": ["6TtHGPq"]},{"name": "parentPolicy","value": [""]},{"name": "actor","value": ["APPC"]},{"name": "recipeInput","value": [""]},{"name": "recipeLevel","value": ["VM"]},{"name": "targetResourceId","value": ["07e266fc-49ab-4cd7-8378-ca4676f1b9ec"]},{"name": "targetResourceIdOther","value": [""]},{"name": "enableGuardPolicy","value": ["on"]},{"name": "guardPolicyType","value": ["GUARD_YAML"]},{"name": "guardTargets","value": [".*"]},{"name": "minGuard","value": ["1"]},{"name": "maxGuard","value": ["1"]},{"name": "limitGuard","value": ["1"]},{"name": "timeUnitsGuard","value": ["minute"]},{"name": "timeWindowGuard","value": ["10"]},{"name": "guardActiveStart","value": ["00:00:01-05:00"]},{"name": "guardActiveEnd","value": ["00:00:00-05:00"]}]]}]} }],"microServicePolicies": [{"name": "tca","properties": "", "shared": true,"policyTosca": "tosca","jsonRepresentation": {"schema":{"title":"DCAE TCA Config","type":"object","required":["name","eventName"],"properties":{"name":{"propertyOrder":101,"title":"Name","type":"string","default":"New_Set"},"eventName":{"propertyOrder":102,"title":"EventName","type":"string","enum":["event1","event2"]},"clSchemaType":{"propertyOrder":103,"title":"Control Loop Schema Type","type":"string","enum":["","type1","type2"]},"threshold":{"propertyOrder":104,"title":"Threshold","format":"tabs","type":"array","items":{"type":"object","title":"Threshold","required":["metric","operator"],"properties":{"metric":{"propertyOrder":1001,"title":"Metric","type":"string","enum":["metric1","metric2"]},"operator":{"propertyOrder":1003,"default":">","title":"Operator","type":"string","enum":[">","<","=","<=",">="]}, "clEventStatus":{"propertyOrder":1004,"title":"Closed Loop Event Status","type":"string","enum":["","ONSET","ABATED"]}}}}}}}}],"loopLogs": [{ } ] }';
	var clModel = '{"name": "ClosedLoopTest","lastComputedState":"DESIGN"}';
	cl_props = JSON.parse(clModel);

	test('getStatus', () => {
		  expect(getStatus()).toBe('DESIGN');
	});

});