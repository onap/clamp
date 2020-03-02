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

import React from 'react'
import Button from 'react-bootstrap/Button';
import Select from 'react-select';
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import styled from 'styled-components';
import LoopService from '../../../api/LoopService';
import LoopCache from '../../../api/LoopCache';
import PolicyToscaService from '../../../api/PolicyToscaService';
import JSONEditor from '@json-editor/json-editor';
import IsEqual from 'react-fast-compare';

const ModalStyled = styled(Modal)`
	background-color: transparent;
`

export default class PolicyModal extends React.Component {

	state = {
		show: true,
		loopCache: this.props.loopCache,
		jsonEditor: null,
		oldJsonEditorData: null,
		policyName: this.props.match.params.policyName,
		// This is to indicate whether it's an operational or config policy (in terms of loop instance)
		policyInstanceType: this.props.match.params.policyInstanceType,
		pdpGroup: null,
		pdpGroupList: [],
		pdpSubgroupList: [],
		chosenPdpGroup: '',
		chosenPdpSubgroup: '',
		policyModelVariants: [],
		chosenPolicyModelVariant: null,
		hidePolicyTypeDropdown: true
	};

	constructor(props, context) {
		super(props, context);
		this.handleClose = this.handleClose.bind(this);
		this.handleSave = this.handleSave.bind(this);
		this.renderJsonEditor = this.renderJsonEditor.bind(this);
		this.handlePdpGroupChange = this.handlePdpGroupChange.bind(this);
		this.handlePdpSubgroupChange = this.handlePdpSubgroupChange.bind(this);
		this.createJsonEditor = this.createJsonEditor.bind(this);
		this.configureJsonEditor = this.configureJsonEditor.bind(this);
		this.getPolicyModelVariants = this.getPolicyModelVariants.bind(this);
		this.handlePolicyModelVariantListChange = this.handlePolicyModelVariantListChange.bind(this);
		this.getPolicyModelVariants();
	}

	handleSave() {
		this.saveJsonEditorData(false);
	}
	
	saveJsonEditorData(policyTypeChangeEvent) {
		if(policyTypeChangeEvent === 'undefined' || policyTypeChangeEvent === null ) {
			policyTypeChangeEvent = false;
		}
		var errors = this.state.jsonEditor.validate();
		var editorData = this.state.jsonEditor.getValue();

		if (errors.length !== 0) {
			console.error("Errors detected during policy data validation ", errors);
			return;
		}
		else {
			console.info("NO validation errors found in config policy data");
			
			if (this.state.policyInstanceType === 'MICRO-SERVICE-POLICY') {
				if(editorData !== null) {
					var editorArray = [];
					if(Array.isArray(editorData)) {
						editorArray = editorData;
					} else {
						editorArray.push(editorData);
					}
					this.setMicroServiceProps(this.getMicroserviceArray(editorArray), policyTypeChangeEvent);
				}
			} else if (this.state.policyInstanceType === 'OPERATIONAL-POLICY') {
				this.state.loopCache.updateOperationalPolicyProperties(this.state.policyName, editorData);
				this.state.loopCache.updateOperationalPolicyPdpGroup(this.state.policyName, this.state.chosenPdpGroup, this.state.chosenPdpSubgroup);
				LoopService.setOperationalPolicyProperties(this.state.loopCache.getLoopName(), this.state.loopCache.getOperationalPolicies()).then(resp => {
					this.setState({ show: false });
				this.props.history.push('/');
					this.props.loadLoopFunction(this.state.loopCache.getLoopName());
				});
			}
		}
	}
	
	setMicroServiceProps(microServicePolicyArray, policyTypeChangeEvent) {
		LoopService.setMicroServiceProperties(this.state.loopCache.getLoopName(), 
				microServicePolicyArray).then(resp => {
				this.setState({ oldJsonEditorData: null });
				if(!policyTypeChangeEvent) {
					this.setState({ show: false });
					this.props.history.push('/');
					this.props.loadLoopFunction(this.state.loopCache.getLoopName());
				}  else {
					console.debug("setMicroServiceProps: Updating loopCache");
					this.setState({ loopCache: new LoopCache(JSON.parse(resp)) });
				}
			});
	}
	
    getMicroserviceArray(editorArray) {
    	var microservicesArray = [];
    	for(var item in editorArray) {
    		var existingMsProperty = false;
    		if(this.state.hidePolicyTypeDropdown 
    				&& this.state.loopCache.updateMicroServiceProperties(this.state.policyName, editorArray[item])) {
				// hidePolicyTypeDropdown is true only when there are more than
				// one policy variants
				existingMsProperty = true;
				var mS = this.state.loopCache.getMicroServiceForName(this.state.policyName);
				microservicesArray.push(this.getMicroserviceObject(mS["name"],
						mS["configurationsJson"],
						mS["modelType"],
						mS["jsonRepresentation"]
	    			));
    				
			} else if(this.state.loopCache.updateMicroServicePropertiesByPolicyModelType(
					this.state.chosenPolicyModelVariant.value, editorArray[item])){
				existingMsProperty = true;
				var msArray = this.state.loopCache.getMicroServiceByModelType(this.state.chosenPolicyModelVariant.value, editorArray[item]["name"]);
				console.info("getMicroserviceArray: Retrieved existing policies: ", msArray);
				for(var i in msArray) {
					microservicesArray.push(this.getMicroserviceObject(msArray[i]["name"],
						msArray[i]["configurationsJson"],
						msArray[i]["modelType"],
						msArray[i]["jsonRepresentation"]
	    			));
				}
			}

    		// existingMsProperty is false then it is a new microservice config
			// policy
    		if(!existingMsProperty) {
    			//Set name as the policy model type for new policies
    			microservicesArray.push(this.getMicroserviceObject(this.state.chosenPolicyModelVariant.value,
    					editorArray[item],
    					this.state.chosenPolicyModelVariant.value,
    					this.state.loopCache.getJsonRepresentationFromMap(this.state.chosenPolicyModelVariant.value)));
    			
    		}
    		this.state.loopCache.updateMicroServicePdpGroup(this.state.policyName, this.state.chosenPdpGroup, this.state.chosenPdpSubgroup);
    	}
    	console.info("getMicroserviceArray: microservicesArray to update: ", microservicesArray);
    	return microservicesArray;
    }
    
    getMicroserviceObject(name, configurationsJson, modelType, jsonRepresentation) {
    	return {"name": name,
			"configurationsJson": configurationsJson,
			"shared": false,
			"modelType": modelType,
			"jsonRepresentation": jsonRepresentation};
    }
	
	getPolicyModelVariants() {
		if (typeof this.state.policyName === "undefined" || this.state.policyName === null) {
			return null;
		} else {
			var policyVariants = this.state.loopCache.getPolicyModelVariants(this.state.policyName);
			if(policyVariants !== null) {
				var policyVariantOptions = [];
				policyVariants.forEach((key, value) => { 
					policyVariantOptions.push({ label: key, value: value }); 
				});
				if(policyVariantOptions === [] || policyVariantOptions.length === 1) {
					// Do not show dropdown for 1 policy variant
					this.setState({ hidePolicyTypeDropdown : true });
					if(policyVariantOptions.length === 1) {
						this.setState({chosenPolicyModelVariant : policyVariantOptions[0] }, () => this.renderJsonEditor());
					} else{
						this.renderJsonEditor();
					}
				} else {
					this.setState({ hidePolicyTypeDropdown : false });
					this.setState({ policyModelVariants : policyVariantOptions }, () => this.renderJsonEditor());
				}
			} else {
				return null;
			}
		}
	}
	
	handlePolicyModelVariantListChange(e) {
		if(this.state.jsonEditor !== null && this.state.oldJsonEditorData !== null 
				&& !IsEqual(this.state.oldJsonEditorData, this.state.jsonEditor.getValue())) {
			this.saveJsonEditorData(true);
		}
		this.setState({chosenPolicyModelVariant: e}, () => this.renderJsonEditor());
	}

	handleClose() {
		this.setState({ show: false });
		this.props.history.push('/');
		this.props.loadLoopFunction(this.state.loopCache.getLoopName());
	}

	componentDidMount() {
		this.getPolicyModelVariants();
	}

    createJsonEditor(toscaModel, editorData) {
		var schema = toscaModel;
    	if(toscaModel.schema) {
    		schema = toscaModel.schema;
    	} 
    	
    	return new JSONEditor(document.getElementById("editor"),
        {   schema: schema,
              startval: editorData,
              theme: 'bootstrap4',
              object_layout: 'grid',
              disable_properties: true,
              disable_edit_json: false,
              disable_array_reorder: true,
              disable_array_delete_last_row: true,
              disable_array_delete_all_rows: false,
              no_additional_properties: true,
              show_errors: 'always',
              display_required_only: false,
              show_opt_in: true,
              prompt_before_delete: true,
              required_by_default: true
        })
    }

	renderJsonEditor() {
		if((this.state.policyInstanceType === 'OPERATIONAL-POLICY' && this.state.loopCache.isOpenLoopTemplate())) {
			console.debug("Skipping rendering PolicyModal for Operational Policy as Template is Open Loop");
			return;
		}
		console.debug("Rendering PolicyModal ", this.state.policyName);
		var toscaModel = {};
		var editorData = {};
		var pdpGroupValues = {};
		var chosenPdpGroupValue, chosenPdpSubgroupValue;
		if (this.state.policyInstanceType === 'MICRO-SERVICE-POLICY') {
			toscaModel = this.state.loopCache.getMicroServiceJsonRepresentationForName(this.state.policyName);
			editorData = this.state.loopCache.getMicroServicePropertiesForName(this.state.policyName);
			pdpGroupValues = this.state.loopCache.getMicroServiceSupportedPdpGroup(this.state.policyName);
			chosenPdpGroupValue = this.state.loopCache.getMicroServicePdpGroup(this.state.policyName);
			chosenPdpSubgroupValue = this.state.loopCache.getMicroServicePdpSubgroup(this.state.policyName);
            console.debug("Json Schema representation from microservice config ", toscaModel);
            //If tosca policy model is null then it has more than one variant as it is intentionally set to null at backend
            if((!toscaModel || toscaModel === null) &&  this.state.chosenPolicyModelVariant !== null){
            	console.debug("Json Schema representation from microservice config ", toscaModel);
            	if((!editorData || editorData === null) && this.state.chosenPolicyModelVariant.value !== null) {
        			// Retrieve existing mS config policies by policyModelype and acronym
        			editorData = this.state.loopCache.getMicroServicePropertiesForModelType(this.state.chosenPolicyModelVariant.value, 
        					this.state.chosenPolicyModelVariant.label);
        		}
				PolicyToscaService.getPolicyModelTypeJsonSchema(this.state.chosenPolicyModelVariant.value).then(resp => {
					toscaModel = JSON.parse(resp);
					console.debug("Json Schema representation from API response ", toscaModel);
					// Update the Map with jsonSchema
					this.state.loopCache.updateJsonRepresentationMap(this.state.chosenPolicyModelVariant.value, toscaModel);
					this.configureJsonEditor(toscaModel, editorData, pdpGroupValues, chosenPdpGroupValue, chosenPdpSubgroupValue);
				});
			} else {
				this.configureJsonEditor(toscaModel, editorData, pdpGroupValues, chosenPdpGroupValue, chosenPdpSubgroupValue);
			}
        } else if (this.state.policyInstanceType === 'OPERATIONAL-POLICY') {
			toscaModel = this.state.loopCache.getOperationalPolicyJsonRepresentationForName(this.state.policyName);
			editorData = this.state.loopCache.getOperationalPolicyPropertiesForName(this.state.policyName);
			pdpGroupValues = this.state.loopCache.getOperationalPolicySupportedPdpGroup(this.state.policyName);
			chosenPdpGroupValue = this.state.loopCache.getOperationalPolicyPdpGroup(this.state.policyName);
			chosenPdpSubgroupValue = this.state.loopCache.getOperationalPolicyPdpSubgroup(this.state.policyName);
			this.configureJsonEditor(toscaModel, editorData, pdpGroupValues, chosenPdpGroupValue, chosenPdpSubgroupValue);
        }
	    
	}
	
	configureJsonEditor(toscaModel, editorData, pdpGroupValues, chosenPdpGroupValue, chosenPdpSubgroupValue) {
		if (toscaModel == null) {
			return;
		}
		var pdpGroupListValues = [];
		if(pdpGroupValues && pdpGroupValues !== null) {
			pdpGroupListValues = pdpGroupValues.map(entry => {
				return { label: Object.keys(entry)[0], value: Object.keys(entry)[0] };
			});
		}
        var pdpSubgroupValues = [];
		if (chosenPdpGroupValue !== null && typeof(chosenPdpGroupValue) !== "undefined" && pdpGroupValues !== null && typeof(pdpGroupValues) !== "undefined") {
			var selectedPdpGroup =	pdpGroupValues.filter(entry => (Object.keys(entry)[0] === chosenPdpGroupValue));
			pdpSubgroupValues = selectedPdpGroup[0][chosenPdpGroupValue].map((pdpSubgroup) => { return { label: pdpSubgroup, value: pdpSubgroup } });
		}

		if(this.state.jsonEditor !== null) {
			this.state.jsonEditor.destroy();
		}
		this.setState({oldJsonEditorData : editorData});
		this.setState({
        				jsonEditor: this.createJsonEditor(toscaModel,editorData),
        				pdpGroup: pdpGroupValues,
        				pdpGroupList: pdpGroupListValues,
        				pdpSubgroupList: pdpSubgroupValues,
        				chosenPdpGroup: chosenPdpGroupValue,
        				chosenPdpSubgroup: chosenPdpSubgroupValue
        			})
	}

	handlePdpGroupChange(e) {
		var selectedPdpGroup =	this.state.pdpGroup.filter(entry => (Object.keys(entry)[0] === e.value));
		const pdpSubgroupValues = selectedPdpGroup[0][e.value].map((pdpSubgroup) => { return { label: pdpSubgroup, value: pdpSubgroup } });
		if (this.state.chosenPdpGroup !== e.value) {
			this.setState({
				chosenPdpGroup: e.value,
				chosenPdpSubgroup: '',
				pdpSubgroupList: pdpSubgroupValues
			});
		}
	}

	handlePdpSubgroupChange(e) {
		this.setState({ chosenPdpSubgroup: e.value });
	}
	
	renderPolicyTypeDrodown() {
		if(!this.state.hidePolicyTypeDropdown) {
			return (
					<Form.Group as={Row} controlId="formPlaintextEmail">
						<Form.Label column sm="2">Policy Type</Form.Label>
						<Col sm="10">
						<Select onSelectResetsInput={false} value={this.state.chosenPolicyModelVariant} onChange={this.handlePolicyModelVariantListChange} options={this.state.policyModelVariants} />
						</Col>
					</Form.Group>
			);			
		}
	}
	
	renderOpenLoopMessage() {
		if(this.state.policyInstanceType === 'OPERATIONAL-POLICY' && this.state.loopCache.isOpenLoopTemplate()) {
			return (
				"Operational Policy cannot be configured as only Open Loop is supported for this Template!"
			);	
		}
	}
	
	renderButton() {
		if(this.state.policyInstanceType === 'OPERATIONAL-POLICY' && this.state.loopCache.isOpenLoopTemplate()) {
			return (
				<Button variant="secondary" onClick={this.handleClose}>
				Close
				</Button>
			);
		} else {
			return (
				<>
					<Button variant="secondary" onClick={this.handleClose}>
					Close
					</Button>
					<Button variant="primary" onClick={this.handleSave}>
					Save Changes
					</Button>
				</>
			);
		}
	}

	render() {
		return (
			<ModalStyled size="xl" backdrop="static" keyboard={false} show={this.state.show} onHide={this.handleClose}>
				<Modal.Header closeButton>
					<Modal.Title>Edit the policy</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					{this.renderOpenLoopMessage()}
					{this.renderPolicyTypeDrodown()}
					<div id="editor" />
					<Form.Group as={Row} controlId="formPlaintextEmail">
						<Form.Label column sm="2">Pdp Group Info</Form.Label>
						<Col sm="3">
							<Select value={{ label: this.state.chosenPdpGroup, value: this.state.chosenPdpGroup }} onChange={this.handlePdpGroupChange} options={this.state.pdpGroupList} />
						</Col>
						<Col sm="3">
							<Select value={{ label: this.state.chosenPdpSubgroup, value: this.state.chosenPdpSubgroup }} onChange={this.handlePdpSubgroupChange} options={this.state.pdpSubgroupList} />
						</Col>
					</Form.Group>
				</Modal.Body>
				<Modal.Footer>
					{this.renderButton()}
				</Modal.Footer>
			</ModalStyled>

		);
	}
}