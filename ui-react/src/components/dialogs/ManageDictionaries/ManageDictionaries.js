/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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


import React, { forwardRef } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import styled from 'styled-components';
import TemplateMenuService from '../../../api/TemplateService';
import CsvToJson from '../../../utils/CsvToJson';
import MaterialTable, {MTableToolbar} from "material-table";
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import VerticalAlignTopIcon from '@material-ui/icons/VerticalAlignTop';
import VerticalAlignBottomIcon from '@material-ui/icons/VerticalAlignBottom';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';


const ModalStyled = styled(Modal)`
	@media (min-width: 1200px) {
		.modal-xl {
			max-width: 96%;
		}
	}
	background-color: transparent;
`

const MTableToolbarStyled = styled(MTableToolbar)`
	display: flex;
	flex-direction: row;
	align-items: center;
`
const ColPullLeftStyled = styled(Col)`
	display: flex;
	flex-direction: row;
	align-items: center;
	margin-left: -40px;
`

const cellStyle = { border: '1px solid black' };
const headerStyle = { backgroundColor: '#ddd',	border: '2px solid black'	};
const rowHeaderStyle = {backgroundColor:'#ddd',  fontSize: '15pt', text: 'bold', border: '1px solid black'};
let dictList = [];

function SelectSubDictType(props) {
	const {onChange} = props;
	const selectedValues = (e) => {
		let options = e.target.options;
		let SelectedDictTypes = '';
		for (let dictType = 0, values = options.length; dictType < values; dictType++) {
			if (options[dictType].selected) {
				SelectedDictTypes = SelectedDictTypes.concat(options[dictType].value);
				SelectedDictTypes = SelectedDictTypes.concat('|');
			}
		}
		SelectedDictTypes = SelectedDictTypes.slice(0,-1);
		onChange(SelectedDictTypes);
	}
	return(
		<div>
			<select multiple={true}  onChange={selectedValues}>
				<option value="string">string</option>
				<option value="number">number</option>
				<option value="datetime">datetime</option>
				<option value="map">map</option>
				<option value="json">json</option>
			</select>
		</div>
	)
}

function SubDict(props) {
	const {onChange} = props;
	const subDicts = [];
	subDicts.push('Default');
	if (dictList !== undefined  && dictList.length > 0) {
	let item;
        for(item in dictList) {
            if(dictList[item].secondLevelDictionary === 1) {
                subDicts.push(dictList[item].name);
            }
        };
	}
	subDicts.push('');
 	let optionItems = subDicts.map(
		(item) => <option key={item}>{item}</option>
	  );
 	function selectedValue (e) {
		onChange(e.target.value);
	}
	return(
		<select onChange={selectedValue} >
			{optionItems}
		</select>
	)
}

export default class ManageDictionaries extends React.Component {
	constructor(props, context) {
		super(props, context);
		this.handleClose = this.handleClose.bind(this);
		this.clickHandler = this.clickHandler.bind(this);
		this.getDictionaries = this.getDictionaries.bind(this);
		this.getDictionaryElements = this.getDictionaryElements.bind(this);
		this.addReplaceDictionaryRequest = this.addReplaceDictionaryRequest.bind(this);
		this.deleteDictionaryRequest = this.deleteDictionaryRequest.bind(this);
		this.updateDictionaryElementsRequest = this.updateDictionaryElementsRequest.bind(this);
		this.addDictionaryRow = this.addDictionaryRow.bind(this);
		this.updateDictionaryRow = this.updateDictionaryRow.bind(this);
		this.deleteDictionaryRow = this.deleteDictionaryRow.bind(this);
		this.addDictionaryElementRow = this.addDictionaryElementRow.bind(this);
		this.deleteDictionaryElementRow = this.deleteDictionaryElementRow.bind(this);
		this.updateDictionaryElementRow = this.updateDictionaryElementRow.bind(this);
		this.fileSelectedHandler = this.fileSelectedHandler.bind(this);
		this.state = {
			show: true,
			selectedFile: '',
			currentSelectedDictionary: null,
			exportFilename: '',
			content: null,
			dictionaryElements: [],
			tableIcons: {
				Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
				Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
				Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
				Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
				DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
				Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
				Export: forwardRef((props, ref) => <VerticalAlignBottomIcon {...props} ref={ref} />),
				Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
				FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
				LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
				NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
				PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
				ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
				Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
				SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
				ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
				ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
			},
			dictColumns: [
				{
					title: "Dictionary Name", field: "name",editable: 'onAdd',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Sub Dictionary ?", field: "secondLevelDictionary", lookup: {0: 'No', 1: 'Yes'},
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Dictionary Type", field: "subDictionaryType",lookup: {string: 'string', number: 'number'},
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Updated By", field: "updatedBy", editable: 'never',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Last Updated Date", field: "updatedDate", editable: 'never',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				}
			],
			dictElementColumns: [
				{
					title: "Element Short Name", field: "shortName",editable: 'onAdd',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Element Name", field: "name",
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Element Description", field: "description",
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Element Type", field: "type",
					editComponent: props => (
						<div>
							<SelectSubDictType  value={props.value} onChange={props.onChange} />
						</div>
					),
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
				    title: "Sub-Dictionary", field: "subDictionary",
				      editComponent: props => (
						 <div>
							 <SubDict  value={props.value} onChange={props.onChange} />
						 </div>
				      ),
				    cellStyle: cellStyle,
				    headerStyle: headerStyle
				},
				{
					title: "Updated By", field: "updatedBy", editable: 'never',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				},
				{
					title: "Updated Date", field: "updatedDate", editable: 'never',
					cellStyle: cellStyle,
					headerStyle: headerStyle
				}
			]
		}
	}

	componentDidMount() {
		this.getDictionaries();
	}

	getDictionaries() {
		TemplateMenuService.getDictionary().then(arrayOfdictionaries => {
			this.setState({ dictionaries: arrayOfdictionaries, currentSelectedDictionary: null })
		});
	}

	getDictionaryElements(dictionaryName) {
		TemplateMenuService.getDictionaryElements(dictionaryName).then(dictionaryElements => {
			dictList = this.state.dictionaries;
			this.setState({ dictionaryElements: dictionaryElements.dictionaryElements} );
		});
	}

	clickHandler(rowData) {
		this.getDictionaries();
	}

	handleClose() {
		this.setState({ show: false });
		this.props.history.push('/');
	}

	addReplaceDictionaryRequest(dictionaryEntry) {
		TemplateMenuService.insDictionary(dictionaryEntry)
		.then(resp => {})
		.then(() => {this.getDictionaries()});
	}

	updateDictionaryElementsRequest(dictElements) {
		let reqData = { "name": this.state.currentSelectedDictionary, 'dictionaryElements': dictElements };
		TemplateMenuService.insDictionaryElements(reqData)
		.then(resp => {})
		.then(() => { this.getDictionaryElements(this.state.currentSelectedDictionary) });
	}

	deleteDictionaryRequest(dictionaryName) {
		TemplateMenuService.deleteDictionary(dictionaryName)
		.then(resp => { this.getDictionaries() });
	}

	deleteDictionaryElementRequest(dictionaryName, elemenetShortName) {
		TemplateMenuService.deleteDictionaryElements({ 'name': dictionaryName, 'shortName': elemenetShortName })
		.then(resp => {
			this.getDictionaryElements(dictionaryName);
		});
	}

	fileSelectedHandler = (event) => {

		if (event.target.files[0].type === 'text/csv' || event.target.files[0].type === 'application/vnd.ms-excel') {
			if (event.target.files && event.target.files[0]) {
				const reader = new FileReader();
				reader.onload = (e) => {

				const jsonKeyNames = [ 'shortName', 'name', 'description', 'type', 'subDictionary' ];
				const userHeaderNames = [ 'Element Short Name', 'Element Name', 'Element Description', 'Element Type', 'Sub-Dictionary'  ];
				const mandatory = [ true, true, true, true, false ];
				const validTypes = ['string','number','datetime','json','map'];

				let result = CsvToJson(reader.result, ',', '||||', userHeaderNames, jsonKeyNames, mandatory);

				let errorMessages = result.errorMessages;
				let jsonObjArray = result.jsonObjArray;

				let validTypesErrorMesg = '';

				for (let i=0; i < validTypes.length; ++i) {
					if (i === 0) {
						validTypesErrorMesg = validTypes[i];
					} else {
						validTypesErrorMesg += ',' + validTypes[i];
					}
				}

				if (errorMessages !== '') {
					alert(errorMessages);
					return;
				}

				// Perform further checks on data that is now in JSON form
                    		let subDictionaries = [];

				// NOTE: dictList is a global variable  maintained faithfully
				//       by the getDictionaries() method outside this import
				//       functionality.
				let item;
                    		for (item in dictList) {
					if (dictList[item].secondLevelDictionary === 1) {
						subDictionaries.push(dictList[item].name);
					}
				};

				// Check for valid Sub-Dictionary and Element Type values
				subDictionaries = subDictionaries.toString();
				let row = 2;
				let dictElem;
				for (dictElem of jsonObjArray) {
					let itemKey;
					for (itemKey in dictElem){
						let value = dictElem[itemKey].trim();
						let keyIndex = jsonKeyNames.indexOf(itemKey);
						if (itemKey === 'shortName' && /[^a-zA-Z0-9-_.]/.test(value)) {
							errorMessages += '\n' + userHeaderNames[keyIndex] +
								' at row #' + row +
								' can only contain alphanumeric characters and periods, hyphens or underscores';
						}
						if (itemKey === 'type' && validTypes.indexOf(value) < 0) {
							errorMessages += '\nInvalid value of "' + value + '" for "' + userHeaderNames[keyIndex] + '" at row #' + row;
							errorMessages += '\nValid types are: ' + validTypesErrorMesg;
						}
						if (value !== "" && itemKey === 'subDictionary' && subDictionaries.indexOf(value) < 0) {
							errorMessages += '\nInvalid Sub-Dictionary value of "' + value + '" at row #' + row;
                            }
                        }
					++row;
                    }
					if (errorMessages) {
						alert(errorMessages);
						return;
					}

					// We made it through all the checks. Send it to back end
					this.updateDictionaryElementsRequest(jsonObjArray);
				}
				reader.readAsText(event.target.files[0]);
			}
			this.setState({selectedFile: event.target.files[0]})
		} else {
			alert('Please upload .csv extention files only.');
		}
	}

	addDictionaryRow(newData) {
		let validData = true;
		return new Promise((resolve, reject) => {
			setTimeout(() => {
					if (/[^a-zA-Z0-9-_.]/.test(newData.name)) {
						validData = false;
						alert('Please enter alphanumeric input. Only allowed special characters are:(period, hyphen, underscore)');
						reject(() => {});
					}
					for (let i = 0; i < this.state.dictionaries.length; i++) {
						if (this.state.dictionaries[i].name === newData.name) {
							validData = false;
							alert(newData.name + ' dictionary name already exists')
							reject(() => {});
						}
					}
					if (validData) {
						this.addReplaceDictionaryRequest(newData);
					}
					resolve();
			}, 1000);
		});
	}


	updateDictionaryRow(oldData, newData) {
		let validData = true;
		return new Promise((resolve) => {
			setTimeout(() => {
				if (/[^a-zA-Z0-9-_.]/.test(newData.name)) {
					validData = false;
					alert('Please enter alphanumberic input. Only allowed special characters are:(period, hyphen, underscore)');
				}
				if (validData) {
					this.addReplaceDictionaryRequest(newData);
				}
				resolve();
			}, 1000);
		});
	}

	deleteDictionaryRow(oldData) {
		return new Promise((resolve) => {
			setTimeout(() => {
				this.deleteDictionaryRequest(oldData.name);
				resolve();
			}, 1000);
		});
	}

	addDictionaryElementRow(newData) {
		return new Promise((resolve, reject) => {
			setTimeout(() => {
				let dictionaryElements = this.state.dictionaryElements;
				let errorMessage = '';
				for (let i = 0; i < this.state.dictionaryElements.length; i++) {
					if (this.state.dictionaryElements[i].shortName === newData.shortName) {
						alert('Short Name "' + newData.shortName + '" already exists');
						reject(() => {});
					}
				}
				if (newData.shortName !== '' && /[^a-zA-Z0-9-_.]/.test(newData.shortName)) {
					errorMessage += '\nShort Name is limited to alphanumeric characters and also period, hyphen, and underscore';
				}
				if (!newData.shortName){
					errorMessage += '\nShort Name must be specified';
				}
				if (!newData.name){
					errorMessage += '\nElement Name must be specified';
				}
				if (!newData.type){
					errorMessage += '\nElement Type must be specified';
				}
				if (!newData.description){
					errorMessage += '\nElement Description must be specified';
				}
				if (errorMessage === '') {
					dictionaryElements.push(newData);
					this.updateDictionaryElementsRequest(dictionaryElements);
					resolve();
				} else {
					alert(errorMessage);
					reject(() => {});
				}
			}, 1000);
		});
	}

	updateDictionaryElementRow(newData, oldData) {
		return new Promise((resolve) => {
			setTimeout(() => {
				let dictionaryElements = this.state.dictionaryElements;
				let validData =  true;
				if (!newData.type) {
					validData = false;
					alert('Element Type cannot be null');
				}
				if (validData) {
					const index = dictionaryElements.indexOf(oldData);
					dictionaryElements[index] = newData;
					this.updateDictionaryElementsRequest(dictionaryElements);
				}
				resolve();
			}, 1000);
		});
	}


	deleteDictionaryElementRow(oldData) {
		return new Promise((resolve) => {
			setTimeout(() => {
				this.deleteDictionaryElementRequest(this.state.currentSelectedDictionary, oldData.shortName);
				resolve();
			}, 1000);
		});
	}

	render() {
		return (
			<ModalStyled size="xl" show={this.state.show} onHide={this.handleClose} backdrop="static" keyboard={false} >
				<Modal.Header closeButton>
					<Modal.Title>Manage Dictionaries</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					{this.state.currentSelectedDictionary === null ? <MaterialTable
                        	title={"Dictionary List"}
                        	data={this.state.dictionaries}
                        	columns={this.state.dictColumns}
                        	icons={this.state.tableIcons}
                        	onRowClick={(event, rowData) => {
								this.setState({
									currentSelectedDictionary : rowData.name,
									exportFilename: rowData.name
								})
								this.getDictionaryElements(rowData.name);
							}}
                        	options={{
                            	headerStyle: rowHeaderStyle,
                        	}}
                        	editable={{
                            	onRowAdd: this.addDictionaryRow,
                            	onRowUpdate: this.updateDictionaryRow,
                            	onRowDelete: this.deleteDictionaryRow
							}}
						/> : null
                    }
                    {this.state.currentSelectedDictionary !== null ? <MaterialTable
                        title={'Dictionary Elements List for "' + this.state.currentSelectedDictionary + '"'}
                        data={this.state.dictionaryElements}
                        columns={this.state.dictElementColumns}
                        icons={this.state.tableIcons}
                        options={{
							exportAllData: true,
                            exportButton: true,
                            exportFileName: this.state.exportFilename,
                            headerStyle:{backgroundColor:'white',  fontSize: '15pt', text: 'bold', border: '1px solid black'}
                        }}
                        components={{
                            Toolbar: props => (
								<Row>
									<Col sm="11">
                                    	<MTableToolbarStyled {...props} />
									</Col>
									<ColPullLeftStyled sm="1">
                                        <Tooltip title="Import" placement = "bottom">
                                        <IconButton aria-label="import" onClick={() => this.fileUpload.click()}>
                                        	<VerticalAlignTopIcon />
                                        </IconButton>
                                        </Tooltip>
                                		<input type="file" ref={(fileUpload) => {this.fileUpload = fileUpload;}}
											style={{ visibility: 'hidden', width: '1px' }} onChange={this.fileSelectedHandler} />
									</ColPullLeftStyled>
                                </Row>
                            )
                        }}
                        editable={{
                            onRowAdd: this.addDictionaryElementRow,
                            onRowUpdate: this.updateDictionaryElementRow,
                            onRowDelete: this.deleteDictionaryElementRow
                        }}
                        /> : null
                    }
                    {this.state.currentSelectedDictionary !== null ? <button onClick={this.clickHandler} style={{marginTop: '25px'}}>Go Back to Dictionaries List</button>:""}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" type="null" onClick={this.handleClose}>Close</Button>
                </Modal.Footer>
            </ModalStyled>
        );
    }
}
