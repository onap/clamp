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
import Modal from 'react-bootstrap/Modal';
import styled from 'styled-components';
import LoopService from '../../../api/LoopService';
import JSONEditor from '@json-editor/json-editor';

const ModalStyled = styled(Modal)`
	background-color: transparent;
`

export default class PolicyModal extends React.Component {

	state = {
		show: true,
		loopCache: this.props.loopCache,
		jsonEditor: null,
		policyName: this.props.match.params.policyName,
		// This is to indicate whether it's an operational or config policy (in terms of loop instance)
		policyInstanceType: this.props.match.params.policyInstanceType
	};

	constructor(props, context) {
		super(props, context);
		this.handleClose = this.handleClose.bind(this);
		this.handleSave = this.handleSave.bind(this);
		this.renderJsonEditor = this.renderJsonEditor.bind(this);
	}

	handleSave() {
		var errors = this.state.jsonEditor.validate();
		var editorData = this.state.jsonEditor.getValue();

		if (errors.length !== 0) {
			console.error("Errors detected during policy data validation ", errors);
			this.setState({ show: false });
			this.props.history.push('/');
		}
		else {
			console.info("NO validation errors found in policy data");
			if (this.state.policyInstanceType === 'MICRO-SERVICE-POLICY') {
                this.state.loopCache.updateMicroServiceProperties(this.state.policyName, editorData[0]);
                LoopService.setMicroServiceProperties(this.state.loopCache.getLoopName(), this.state.loopCache.getMicroServiceForName(this.state.policyName)).then(resp => {
                    this.setState({ show: false });
                    this.props.history.push('/');
                    this.props.loadLoopFunction(this.state.loopCache.getLoopName());
                });
			} else if (this.state.policyInstanceType === 'OPERATIONAL-POLICY') {
			    this.state.loopCache.updateOperationalPolicyProperties(editorData);
            	LoopService.setOperationalPolicyProperties(this.state.loopCache.getLoopName(), this.state.loopCache.getOperationalPolicyForName(this.state.policyName)).then(resp => {
            		this.setState({ show: false });
            		this.props.history.push('/');
            		this.props.loadLoopFunction(this.state.loopCache.getLoopName());
            	});
			}
		}
	}

	handleClose() {
		this.setState({ show: false });
		this.props.history.push('/');
	}

	componentDidMount() {
		this.renderJsonEditor();
	}

	renderJsonEditor() {
		console.debug("Rendering PolicyModal ", this.state.policyName);
		var toscaModel = {};
	    var editorData = {};
	    if (this.state.policyInstanceType === 'MICRO-SERVICE-POLICY') {
            toscaModel = this.state.loopCache.getMicroServiceJsonRepresentationForName(this.state.policyName);
            editorData = this.state.loopCache.getMicroServicePropertiesForName(this.state.policyName);
        } else if (this.state.policyInstanceType === 'OPERATIONAL-POLICY') {
            toscaModel = this.state.loopCache.getOperationalPolicyJsonRepresentationForName(this.state.policyName);
            editorData = this.state.loopCache.getOperationalPolicyPropertiesForName(this.state.policyName);
        }

		if (toscaModel == null) {
			return;
		}

		this.setState({
			jsonEditor: new JSONEditor(document.getElementById("editor"),
				{   schema: toscaModel,
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
		})
	}

	render() {
		return (
			<ModalStyled size="xl" show={this.state.show} onHide={this.handleClose}>
				<Modal.Header closeButton>
					<Modal.Title>Configuration policies</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					<div id="editor" />

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