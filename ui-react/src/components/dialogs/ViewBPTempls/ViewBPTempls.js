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

import React, { forwardRef } from 'react'
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import styled from 'styled-components';
import TemplateMenuService from '../../../api/TemplateMenuService';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Search from '@material-ui/icons/Search';
import MaterialTable from "material-table";

const ModalStyled = styled(Modal)`
	background-color: transparent;
`
const StyledBPTemplView = styled.textarea`
`
const StyledBPTemplDiv = styled.div`
`

const vtmCellStyle = { border: '1px solid black' };
const vtmHeaderStyle = { backgroundColor: '#ddd',	border: '2px solid black'	};
const vtmRowHeaderStyle = {backgroundColor:'#ddd',  fontSize: '15pt', text: 'bold', border: '1px solid black'};

export default class ViewBPTempl extends React.Component {
	state = {
		show: true,
		content: 'Please select Blue print template to view the details',
		selectedRow: -1,
		bpTemplNames: [],
		bpTemplColumns: [
							{ title: "#", field: "index", render: rowData => rowData.tableData.id + 1,
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle 
							},

							{ title: "Template Name", field: "templateName",
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle
							 },
							{ title: "Policy Model", field: "templatePolicy[0].policyModelId",
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle
							},
							{ title: "Template ID", field: "templateId",
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle
							},
							{ title: "Uploaded By", field: "updatedBy",
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle
							},
							{ title: "Uploaded Date", field: "timestamp", editable: 'never',
								cellStyle: vtmCellStyle,
								headerStyle: vtmHeaderStyle
							}
				],
		tableIcons: {
			FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
			LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
			NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
			PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
			ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
			Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
			SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />)
		}
	};

	constructor(props, context) {
		super(props, context);
		this.handleClose = this.handleClose.bind(this);
		this.getBPTempls = this.getBPTempls.bind(this);
		this.handleYamlContent = this.handleYamlContent.bind(this);
	}

	componentWillMount() {
		this.getBPTempls();
	}

	getBPTempls() {
		TemplateMenuService.getBPTempls().then(bpTemplNames => {
		this.setState({ bpTemplNames: bpTemplNames })
		});
	}

	handleYamlContent = event => {
		console.log('inside handleYamlContent');
		this.setState({
			content: event.target.value
		})
	}

	handleClose() {
		this.setState({ show: false });
		this.props.history.push('/')
	}

	render() {
		return (
			<ModalStyled size="xl" show={this.state.show} onHide={this.handleClose}>
				<Modal.Header closeButton>
					<Modal.Title className="title">View BP Templates</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					<MaterialTable
					title={"View BP Templates"}
					data={this.state.bpTemplNames}
					columns={this.state.bpTemplColumns}
					icons={this.state.tableIcons}
					onRowClick={(event, rowData) => {this.setState({content: rowData.templateYaml, selectedRow: rowData.tableData.id})}}
					options={{
					headerStyle:vtmRowHeaderStyle,
					rowStyle: rowData => ({
					 backgroundColor: (this.state.selectedRow !== -1 && this.state.selectedRow === rowData.tableData.id) ? '#EEE' : '#FFF'
				 })
				}}
				/>
				<StyledBPTemplDiv>
					<StyledBPTemplView value={this.state.content} onChange={this.handleYamlContent} />
				</StyledBPTemplDiv>
				</Modal.Body>
				<Modal.Footer>
					<Button variant="secondary" onClick={this.handleClose}>
						Close
	            </Button>
				</Modal.Footer>
			</ModalStyled>

		);
	}
}
