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

import React from 'react'
import Select from 'react-select';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import styled from 'styled-components';
import LoopService from '../../../api/LoopService';
import LoopCache from '../../../api/LoopCache';
import JSONEditor from '@json-editor/json-editor';
import IsEqual from 'react-fast-compare';

const ModalStyled = styled(Modal)`
	background-color: transparent;
`

export default class ConfigurationPolicyModal extends React.Component {
	state = {
			show: true,
			loopCache: this.props.loopCache,
			jsonEditor: null,
			oldJsonEditorData: null,
			componentName: this.props.match.params.componentName,
			policyModelVariants: [],
			chosenPolicyModelVariant: null,
			hidePolicyTypeDropdown: true,
	};
	
	constructor(props, context) {
		super(props, context);
		this.handleClose = this.handleClose.bind(this);
		this.handleSave = this.handleSave.bind(this);
		this.renderJsonEditor = this.renderJsonEditor.bind(this);
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
			console.error("Errors detected during config policy data validation ", errors);
			this.setState({ show: false });
			this.setState({ oldJsonEditorData: null });
			this.props.history.push('/');
		}
		else {
			console.info("NO validation errors found in config policy data");
			if(editorData !== null) {
				var editorArray = [];
				if(Array.isArray(editorData)) {
					editorArray = editorData;
				} else {
					editorArray.push(editorData);
				}
				this.setMicroServiceProps(this.getMicroserviceArray(editorArray), policyTypeChangeEvent);
			}
		}
	}
	
    getMicroserviceArray(editorArray) {
    	var microservicesArray = [];
    	for(var item in editorArray) {
    		var existingMsProperty = false;
    		if(this.state.hidePolicyTypeDropdown 
    				&& this.state.loopCache.updateMicroServiceProperties(this.state.componentName, editorArray[item])) {
				// hidePolicyTypeDropdown is true only when there are more than
				// one policy variants
				existingMsProperty = true;
				var mS = this.state.loopCache.getMicroServiceForName(this.state.componentName);
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
    			microservicesArray.push(this.getMicroserviceObject(this.state.componentName,
    					editorArray[item],
    					this.state.chosenPolicyModelVariant.value,
    					this.state.loopCache.getJsonRepresentationFromMap(this.state.chosenPolicyModelVariant.value)));
    			
    		}
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
	
	setMicroServiceProps(microServicePolicyArray, policyTypeChangeEvent) {
		LoopService.setMicroServiceProperties(this.state.loopCache.getLoopName(), 
				microServicePolicyArray).then(resp => {
				if(!policyTypeChangeEvent) {
					this.setState({ show: false });
					this.setState({ oldJsonEditorData: null });
					this.props.history.push('/');
					this.props.loadLoopFunction(this.state.loopCache.getLoopName());
				}  else {
					console.debug("setMicroServiceProps: Updating loopCache");
					this.setState({ loopCache: new LoopCache(resp) });
				}
			});
	}
	
	getPolicyModelVariants() {
		if (typeof this.state.componentName === "undefined" || this.state.componentName === null) {
			return null;
		} else {
			var policyVariants = this.state.loopCache.getPolicyModelVariants(this.state.componentName);
			if(policyVariants !== null) {
				var policyVariantOptions = policyVariants.map((variant) => { 
					return { label: variant.policyAcronym, value: variant.policyModelType } 
				});
				if(policyVariantOptions === null || policyVariantOptions.length === 1) {
					// Do not show dropdown for 1 policy variant
					this.setState({ hidePolicyTypeDropdown : true });
					if(policyVariantOptions !== null && policyVariantOptions.length === 1) {
						this.setState({chosenPolicyModelVariant : policyVariantOptions[0] }, () => this.renderJsonEditor());
					}
				} else {
					this.setState({ hidePolicyTypeDropdown : false });
					this.setState({ policyModelVariants : policyVariantOptions });
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
		this.renderJsonEditor();
	}
	
	renderJsonEditor() {
		console.debug("Rendering ConfigurationPolicyModal ", this.state.componentName);
		console.debug("Rendering ConfigurationPolicyModal with chosenPolicyModelVariant ", this.state.chosenPolicyModelVariant);
		var toscaModel = this.state.loopCache.getMicroServiceJsonRepresentationForName(this.state.componentName);
		
		if (toscaModel === null && this.state.chosenPolicyModelVariant !== null) {
			// retrieve from LoopCache Map
			toscaModel = this.state.loopCache.getJsonRepresentationFromMap(this.state.chosenPolicyModelVariant.value);
			// If Map does not contain the jsonSchema then invoke the service
			if(toscaModel === null){
				LoopService.getPolicyModelTypeJsonSchema(this.state.chosenPolicyModelVariant.value).then(resp => {
					toscaModel = JSON.parse(resp);
					// Update the Map with jsonSchema
					this.state.loopCache.updateJsonRepresentationMap(this.state.chosenPolicyModelVariant.value, toscaModel);
					this.configureJsonEditor(toscaModel);
				});
			} else {
				this.configureJsonEditor(toscaModel);
			}
		} else {
			this.configureJsonEditor(toscaModel);
		}
	}
	
	configureJsonEditor(toscaModel) {
		
		if (toscaModel == null) {
			return;
		}
		var editorData = this.state.loopCache.getMicroServicePropertiesForName(this.state.componentName);	
		if(editorData === null && this.state.chosenPolicyModelVariant !== null && this.state.chosenPolicyModelVariant.value !== null) {
			// Retrieve existing mS config policies by policyModelype and Acronym
			editorData = this.state.loopCache.getMicroServicePropertiesForModelType(this.state.chosenPolicyModelVariant.value, 
					this.state.chosenPolicyModelVariant.label);
		}
		console.debug(" ConfigurationPolicyModal: Retrieved mS config policies ", editorData);

		JSONEditor.defaults.options.theme = 'bootstrap4';
		// JSONEditor.defaults.options.iconlib = 'bootstrap2';
		JSONEditor.defaults.options.object_layout = 'grid';
		JSONEditor.defaults.options.disable_properties = true;
		JSONEditor.defaults.options.disable_edit_json = true;
		JSONEditor.defaults.options.disable_array_reorder = true;
		JSONEditor.defaults.options.disable_array_delete_last_row = true;
		JSONEditor.defaults.options.disable_array_delete_all_rows = false;
		JSONEditor.defaults.options.show_errors = 'always';
		
		if(this.state.jsonEditor !== null) {
			this.state.jsonEditor.destroy();
		}

		this.setState({oldJsonEditorData : editorData});
		this.setState({
			jsonEditor: new JSONEditor(document.getElementById("editor"),
				{ schema: toscaModel.schema, startval: editorData })
		})
	}

	render() {
		var policyTypeDropdown;
		if(!this.state.hidePolicyTypeDropdown) {
			policyTypeDropdown = <Form.Group as={Row} controlId="formPlaintextEmail">
			<Form.Label column sm="2">Policy Type</Form.Label>
			<Col sm="10">
			<Select onSelectResetsInput={false} value={this.state.chosenPolicyModelVariant} onChange={this.handlePolicyModelVariantListChange} options={this.state.policyModelVariants} />
			</Col>
		</Form.Group>;
		}
		return (
			<ModalStyled size="xl" show={this.state.show} onHide={this.handleClose}>
				<Modal.Header closeButton>
					<Modal.Title>Configuration policies</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					{policyTypeDropdown}
					<div id="editor" onChange={(e) => this.setState({jsonEditorValuesChanged : true})} />
				</Modal.Body>
				<Modal.Footer>
					<Button variant="secondary" onClick={this.handleClose}>
						Close
	            </Button>
					<Button variant="primary" onClick={this.handleSave}>
						Save Changes
	            </Button>
				</Modal.Footer>
			</ModalStyled>

		);
	}
}