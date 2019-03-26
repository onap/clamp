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
    function($scope, $rootScope, $uibModalInstance, data, operationalPolicyService, dialogs) {

	    console.log("//////operationalPolicyCtrl");
	    var policy_ids = {}
	    var allPolicies = {};
	    $scope.guardType = "GUARD_MIN_MAX";
	    $scope.targetResource = "";
	    $scope.number = 0;
	    function getAllFormId() {

		    return Array.from(document.getElementsByClassName("formId"));
	    }
	    function searchActiveFormId() {

		    var formArray = getAllFormId();
		    for (var i = 0, max = formArray.length; i < max; i++) {
			    console.log("Search active FormId, current element " + formArray[i].id);
			    if (formArray[i].style.display !== "none") {
				    console.log("Active form is:" + formArray[i].id);
				    return formArray[i];
			    }
		    }
		    console.log("No active formId found !");
	    }
	    function initTargetResourceId() {

		    if (vf_Services !== null && vf_Services !== undefined) {
			    // Set all the Resource Invariant UUID in the target resource ID
			    // list (+Empty and other)
			    Object.keys(vf_Services["shared"]["byVf"]).forEach(function(key) {

				    $("#targetResourceId").append($('<option></option>').val(key).html(key));
			    });
		    }
	    }
	    function add_one_more() {

		    console.log("add one more");
		    // setPolicyOptions();
		    $("#nav_Tabs li").removeClass("active");
		    // FormSpan contains a block of the form that is not being
		    // displayed. We will create clones of that and add them to tabs
		    var form = $("#formSpan").clone(true,true)
		    var count = 0;
		    // Each new tab will have the formId class attached to it. This way
		    // we can track how many forms we currently have out there and
		    // assign listeners to them
		    if ($(".formId").length > 0) {
			    var greatest = 0;
			    var s = $(".formId");
			    for (var i = 0; i < s.length; i++) {
				    if (parseInt($(s[i]).attr("id").substring(6)) > greatest) {
					    greatest = parseInt($(s[i]).attr("id").substring(6))
				    }
			    }
			    count = greatest + 1;
			    $("#properties_tab").append(('<span class="formId" id="formId' + count + '"></span>'));
		    } else {
			    count++;
			    $("#properties_tab").append('<span class="formId" id="formId1"></span>');
		    }
		    $("#add_one_more")
		    .parent()
		    .before(
		    ' <li class="active"><a id="go_properties_tab'
		    + count
		    + '">Policy</a><button id="tab_close'
		    + count
		    + '" type="button" class="close tab-close-popup" aria-hidden="true" style="margin-top: -30px;margin-right: 5px">&times;</button></li>');
		    $("#formId" + count).append(form.children());
		    $(".formId").not($("#formId" + count)).css("display", "none");
		    addCustListen(count);
		    // This is for when the process is not loading from map but being
		    // created
		    // if (!loadingId) {
		    // var l = makeid()
		    // $(form).find("#id").val(l)
		    // policy_ids["#formId" + count] = l
		    // var answers = {}
		    // for (var i = 1; i <= greatestIdNum(); i++) {
		    // if ($("#formId" + i).length > 0) {
		    // answers["#formId" + i + " #parentPolicy"] = $("#formId" + i + "
			// #parentPolicy").val();
		    // $("#formId" + i + " #parentPolicy").empty();
		    // for (k in policy_ids) {
		    // if (($("#formId" + i + " #id").val() !==
			// policy_ids[k].toString())
		    // && $(k + " #recipe").val() !== undefined && $(k + "
			// #recipe").val() !== "") {
		    // $("#formId" + i + " #parentPolicy").append(
		    // "<option value='" + policy_ids[k] + "'>" + $(k + "
			// #recipe").val() + "</option>")
		    // }
		    // }
		    // $("#formId" + i + " #parentPolicy").prepend("<option
			// value=''></option>")
		    // }
		    // }
		    // // re-populate parent policy with chosen options
		    // for (k in answers) {
		    // $(k).val(answers[k])
		    // }
		    // }
		    return count;
	    }
	    function addCustListen(count) {

		    $('#go_properties_tab' + count).click(function(event) {

			    $("#nav_Tabs li").removeClass("active");
			    $(this).parent().addClass("active");
			    $("#formId" + count).css("display", "")
			    $(".formId").not($("#formId" + count)).css("display", "none")
		    })
		    $('#tab_close' + count).click(
		    function(event) {

			    if (document.getElementById("timeout").disabled) {
				    return false;
			    }
			    $(this).parent().remove();
			    for (var i = 1; i <= greatestIdNum(); i++) {
				    if ($("#formId" + i).length > 0) {
					    if (i !== count) {
						    if ($("#formId" + i + " #parentPolicy").val() === $("#formId" + count + " #id").val()
						    .toString())
							    $("#formId" + i + " #parentPolicy").val("")
						    $(
						    "#formId" + i + " #parentPolicy option[value='"
						    + $("#formId" + count + " #id").val().toString() + "']").remove();
					    }
				    }
			    }
			    delete policy_ids["#formId" + count + " #id"]
			    $("#formId" + count).remove();
		    })
	    }
	    function greatestIdNum() {

		    var greatest = 0;
		    var s = $(".formId");
		    for (var i = 0; i < s.length; i++) {
			    if (parseInt($(s[i]).attr("id").substring(6)) > greatest) {
				    greatest = parseInt($(s[i]).attr("id").substring(6))
			    }
		    }
		    return greatest;
	    }
	    function serializeElement(element) {

		    var o = {};
		    var a = element.serializeArray();
		    $.each(a, function() {

			    if (o[this.name]) {
				    if (!o[this.name].push) {
					    o[this.name] = [ o[this.name] ];
				    }
				    o[this.name].push(this.value || '');
			    } else {
				    o[this.name] = this.value || '';
			    }
		    });
		    return o;
	    }
	    function savePolicyLocally(policyId) {

		    console.log("Saving policy id:" + policyId);
		    var polForm = {}
		    polForm = serializeElement($("#operationalPolicyHeaderForm"));
		    var policiesArray = []
		    $.each($(".formId"), function() {

		    	var policyProperties=serializeElement($("#"+this.id+" .policyProperties"));
		    	policyProperties["target"]=serializeElement($("#"+this.id+" .policyTarget"))
		    	policiesArray.push(policyProperties);

		    	// Now get the Guard
		    	
		    });

		    polForm['policies'] = policiesArray;
		    allPolicies[policyId] = polForm;
	    }
	    function add_new_policy(issueNewNames) {

		    console.log("add new policy");
		    // remove old gui forms
		    for (var i = 1; i < ($(".formId").length + 1); i++) {
			    $("#go_properties_tab" + i).parent().remove();
		    }
		    $(".formId").remove();
		    $("#add_one_more").click();
	    }
	    function configureComponents(allPolicies, policyId) {

		    console.log("load properties to op policy");
		    // Set the header
		    $.each($('#operationalPolicyHeaderForm').find('.form-control'), function() {

			    $(this).val(allPolicies[policyId][this.id]);
		    });
		    // Set the sub-policies
		    $.each(allPolicies[policyId]['policies'], function() {

			    var formNum = add_one_more();
			    $.each($('.policyProperties').find('.form-control'), function() {

				    $("#formId" + formNum + " #" + this.id)
				    .val(allPolicies[policyId]['policies'][formNum - 1][this.id]);
			    });
			    // update the current tab label
			    $("#go_properties_tab" + formNum).text(allPolicies[policyId]['policies'][formNum - 1]['id'])
		    });
	    }
	    function setPolicyOptions() {

		    console.log("reset policy default options");
		    try {
			    var actor_values = defaults_props['policy']['actor'];
			    if (actor_values) {
				    for (key in actor_values) {
					    if ($("#actor").length > 0) {
						    $("#actor").append("<option value=\"" + key + "\">" + actor_values[key] + "</option>")
					    }
				    }
				    // $("#actor").multiselect("rebuild");
			    }
			    var recipe_values = defaults_props['policy']['vnfRecipe'];
			    if (recipe_values) {
				    for (key in recipe_values) {
					    if ($("#recipe").length > 0) {
						    $("#recipe").append("<option value=\"" + key + "\">" + recipe_values[key] + "</option>")
					    }
				    }
				    // $("#recipe").multiselect("rebuild");
			    }
			    var timeUnitsGuard_values = defaults_props['policy']['timeUnitsGuard'];
			    if (timeUnitsGuard_values) {
				    for (key in timeUnitsGuard_values) {
					    if ($("#timeUnitsGuard").length > 0) {
						    $("#timeUnitsGuard").append(
						    "<option value=\"" + key + "\">" + timeUnitsGuard_values[key] + "</option>")
					    }
				    }
				    // $("#timeUnitsGuard").multiselect("rebuild");
			    }
		    } catch (e) {
			    console.log(e)
		    }
	    }
	    $scope.refreshOperationalPoliciesList = function(event) {
	    	var previousValue = $(event.target).val();
	    	// Clean everything
	    	$(event.target).children().remove()
	    	// Add op policies to the list
	    	$.each($(".formId"),function() {
	    		var policyName = $($("#"+this.id+ " #id")[0]).val();
	    		$(event.target).append(
						    "<option value=\"" + policyName + "\">" + policyName + "</option>")
	    	})
	    	// Set it to previous value
	    	$(event.target).val(previousValue);
	    }
	    
	    $scope.changeTargetResourceIdOther = function() {

		    var formItemActive = searchActiveFormId();
		    if (formItemActive === undefined)
			    return;
		    if ($("#" + formItemActive.id + " #targetResourceId").val() === "Other:") {
			    $("#" + formItemActive.id + " #targetResourceIdOther").show();
		    } else {
			    $("#" + formItemActive.id + " #targetResourceIdOther").hide();
			    $("#" + formItemActive.id + " #targetResourceIdOther").val("");
		    }
	    }
	    $scope.changeGuardPolicyType = function() {

		    var formItemActive = searchActiveFormId();
		    if (formItemActive === undefined)
			    return;
		    if ($("#" + formItemActive.id + " #guardPolicyType").val() === "GUARD_MIN_MAX") {
			    $("#" + formItemActive.id + " #minMaxGuardPolicyDiv").show();
			    $("#" + formItemActive.id + " #frequencyLimiterGuardPolicyDiv").hide();
		    } else if ($("#" + formItemActive.id + " #guardPolicyType").val() === "GUARD_YAML") {
			    $("#" + formItemActive.id + " #minMaxGuardPolicyDiv").hide();
			    $("#" + formItemActive.id + " #frequencyLimiterGuardPolicyDiv").show();
		    }
	    }
	    $scope.init = function() {

		    $(function() {

			    setPolicyOptions();
			    $("#add_one_more").click(function(event) {

				    console.log("add one more");
				    event.preventDefault();
				    add_one_more();
				    // setMultiSelect();
			    });
			    allPolicies['operationalPolicy'] = getOperationalPolicyProperty();
			    if (allPolicies['operationalPolicy'] !== undefined && allPolicies['operationalPolicy'] !== null) {
				    // load properties
				    console.log("load properties");
				    configureComponents(allPolicies, 'operationalPolicy');
			    } else {
				    console.log("create new policy");
				    add_new_policy();
			    }
			    $("#savePropsBtn").click(
			    function(event) {

				    console.log("save properties triggered");
				    if ($("#targetResourceIdOther").is(":visible")) {
					    $('#targetResourceId').append(
					    $('<option></option>').val($("#targetResourceIdOther").val()).html(
					    $("#targetResourceIdOther").val()))
					    $("#targetResourceId").val($("#targetResourceIdOther").val());
				    }
				    savePolicyLocally('operationalPolicy');
				    var finalSaveList = allPolicies['operationalPolicy'];
				    angular.element(document.getElementById('formSpan')).scope().submitForm(finalSaveList);
				    $("#close_button").click();
			    });
			    initTargetResourceId();
		    });
	    }
	    $scope.init();

	    $scope.updateTabLabel = function(event) {
		    var formNum = $(event.target).closest('.formId').attr('id').substring(6);
		    $("#go_properties_tab" + formNum).text($(event.target).val());
	    }
	    $scope.close = function() {

		    console.log("close");
		    $uibModalInstance.close("closed");
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