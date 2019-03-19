/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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
app
.controller(
'operationalPolicyCtrl',
[
'$scope',
'$rootScope',
'$uibModalInstance',
'data',
'operationalPolicyService',
'dialogs',
function($scope, $rootScope, $uibModalInstance, data, operationalPolicyService, 
         dialogs) {
	console.log("//////operationalPolicyCtrl");
	$rootScope.serviceInfo;
	$rootScope.serviceInput;
	$rootScope.serviceOutput;
	$rootScope.serviceFault;
	$rootScope.serviceInputPartInfo;
	$rootScope.schemElemant1;
	$rootScope.updateServiceInfo;
	$rootScope.updateServiceInput;
	$rootScope.updateServiceOutput;
	$rootScope.updateServiceFault;
	$rootScope.updateServiceInputPartInfo;
	$rootScope.updateSchemElemant1;
	// Below code is added to get the policyNames
	var policies = getOperationalPolicyProperty();
	for ( var obj in policies) {
			if (!($.isEmptyObject(obj))) {
				allPolicies = jQuery.extend({}, obj);
				$scope.policyNames = [];
				for ( var policy in allPolicies) {
					$scope.policyNames.push(policy);
				}
			}
	}
	setTimeout(function() {
		console.log("setTimeout");
		setMultiSelect();
	}, 100);
	$scope.close = function() {
		console.log("close");
		$uibModalInstance.close("closed");
	};
	$rootScope.file_path;

	$scope.setFile = function(element) {
		console.log("setFile");
		$scope.$apply(function($scope) {
			console.log("apply");
			$scope.theFile = element.files[0];
			$rootScope.fileName = $scope.theFile.name;
			var file = element.files[0];
			$rootScope.file_path = file;
			// $uibModalInstance.close("closed");
			angular.element(document.getElementById('fileUpload')).val(null);
		});
	};
	$scope.setUpgradeFile = function(element) {
		console.log("setUpgradeFile");
		$scope.$apply(function($scope) {
			console.log("apply");
			$scope.theUpgradeFile = element.files[0];
			$rootScope.upgradeFileName = $scope.theUpgradeFile.name;
			// alert("fname1"+$rootScope.upgradeFileName);
			var file = element.files[0];
			$rootScope.file_path = file;
			// $uibModalInstance.close("closed");
			angular.element(document.getElementById('fileUpload')).val(null);
		});
	};
	$scope.reset = function() {
		console.log("reset");
		document.getElementById("fileUpload").disabled = false;
		document.getElementById("schemaLocation").disabled = false;
		document.getElementById("userID").disabled = false;
		document.getElementById("password").disabled = false;
		document.getElementById("schemaLocation").value = '';
		document.getElementById("userID").value = '';
		document.getElementById("password").value = '';
		$scope.theFile = null;
		angular.element(document.getElementById('fileUpload')).val(null);
	};

	$scope.submitForm = function(obj) {
		var operationalPolicies = JSON.parse(JSON.stringify(getOperationalPolicies()));
		if (obj !== null) {
			operationalPolicies[0]["configurationsJson"] = obj;
		}
		operationalPolicyService.saveOpPolicyProperties(operationalPolicies).then(function(pars) {
 			updateOpPolicyProperties(operationalPolicies);
 		}, function(data) {
 		});
     };

} ]);