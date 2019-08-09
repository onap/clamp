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
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import styled from 'styled-components';
import LoopService from '../../../api/LoopService';
import JSONEditor from '@json-editor/json-editor';

const ModalStyled = styled(Modal)`
	background-color: transparent;
`

export default class OperationalPolicyModal extends React.Component {

	state = {
		show: true,
		loopCache: this.props.loopCache,
		jsonEditor: null,
		componentName: this.props.match.params.componentName,
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
			console.error("Errors detected during config policy data validation ", errors);
			this.setState({ show: false });
			this.props.history.push('/');
		}
		else {
			console.info("NO validation errors found in config policy data");
			this.state.loopCache.updateMicroServiceProperties(this.state.componentName, editorData[0]);
			LoopService.setMicroServiceProperties(this.state.loopCache.getLoopName(), this.state.loopCache.getMicroServiceForName(this.state.componentName)).then(resp => {
				this.setState({ show: false });
				this.props.history.push('/');
				this.props.loadLoopFunction(this.state.loopCache.getLoopName());
			});
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
		console.debug("Rendering OperationalPolicyModal ", this.state.componentName);
		fetch("/schema/operational_policies/operational_policy.json")
		  .then(response => response.json())
		  .then(schema_json => {
			  if (schema_json == null) {
					return;
				}
			  var editorData = this.state.loopCache.getMicroServicePropertiesForName(this.state.componentName);

				JSONEditor.defaults.options.theme = 'bootstrap4';
				//JSONEditor.defaults.options.iconlib = 'bootstrap2';

				JSONEditor.defaults.options.object_layout = 'grid';
				JSONEditor.defaults.options.disable_properties = true;
				JSONEditor.defaults.options.disable_edit_json = false;
				JSONEditor.defaults.options.disable_array_reorder = true;
				JSONEditor.defaults.options.disable_array_delete_last_row = true;
				JSONEditor.defaults.options.disable_array_delete_all_rows = false;
				JSONEditor.defaults.options.array_controls_top=true;
				JSONEditor.defaults.options.show_errors = 'always';
				JSONEditor.defaults.options.keep_oneof_values=false;
				JSONEditor.defaults.options.ajax=true;
				JSONEditor.defaults.options.collapsed=true;

				this.setState({
					jsonEditor: new JSONEditor(document.getElementById("editor"),
						{ schema: schema_json.schema, startval: editorData })
				})
		  });
		//var toscaModel = require("/schema/operational_policies/json_editor_template.json");
		
		
	}

	render() {
		return (
			<ModalStyled size="xl" show={this.state.show} onHide={this.handleClose}>
				<Modal.Header closeButton>
					<Modal.Title>Operational policies</Modal.Title>
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