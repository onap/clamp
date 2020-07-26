/*
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * ============LICENSE_END=========================================================
 *
 */

import React from 'react'
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';
import styled from 'styled-components';

import LoopService from '../../../api/LoopService';
import LoopCache from '../../../api/LoopCache';

import { forwardRef }  from 'react';

import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
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

import MaterialTable from "material-table";

const mclfRegularCellStyle = { border: '1px solid black', padding: '-2em'  };

const ModalStyled = styled(Modal)`
    background-color: transparent;
`
const TextModal = styled.textarea`
    margin-top: 20px;
    white-space:pre;
    background-color: ${props => props.theme.toscaTextareaBackgroundColor};
    text-align: justify;
    font-size: ${props => props.theme.toscaTextareaFontSize};
    width: 100%;
    height: 300px;
`

export default class ImportPolicyJsonModal extends React.Component {
    
	constructor(props, context) {
		super(props, context);
		this.fileSelectedHandler = this.fileSelectedHandler.bind(this);
        this.getLoopNames = this.getLoopNames.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.importLoop = this.importLoop.bind(this);
        this.state = {
            show: true,
            importCL: this.props.importCL,
            loopName: '',
            loopCache: this.props.loopCache,
            content: "Select Import button below to import Policy JSON",
            selectedFile: '',
            CLnames: [],
            selectedRows: [],
            importCache: null,
            opPolicies: false
        }
        
        this.tableColumns = [
            {
                title: "Policy Name",
                field: "configurationsJson.name",
                editable: 'onUpdate',
                cellStyle: mclfRegularCellStyle,
                render: (rowData) => {
                        if (rowData.configurationsJson.name == null) {
                            return(rowData.name);
                        } else {
                            return(rowData.configurationsJson.name);
                        }
                }
            },
            {
                title: "Type",
                field: "policyModel.policyAcronym",
                editable: 'never',
                cellStyle: mclfRegularCellStyle
            },
            {
                title: "Configuration",
                field: "configurationsJson",
                editable: 'never',
                cellStyle: mclfRegularCellStyle,
                render: (rowData) => {
                    return (
                        (JSON.stringify(rowData.configurationsJson))
                    )
                }
            }
        ];
        
        this.tableIcons = {
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
        };

	}
    
    componentWillMount() {
        this.getLoopNames();
    }
    
    getLoopNames() {
        LoopService.getLoopNames().then(loopNames => {
            if (Object.entries(loopNames).length !== 0) {
                this.setState({ CLnames: loopNames })
            }
        });
    }
    
    handleClose() {
        this.setState({ show: false });
        this.props.history.push('/');
    }

    fileSelectedHandler = (event) => {

        if (event.target.files && event.target.files[0]) {
            const reader = new FileReader();
                
            // file reading started
            reader.addEventListener('loadstart', function() {
                console.log('File reading started');
            });
            
            // file reading finished successfully
            reader.addEventListener('load', function(e) {    
                var text = e.target.result;
                
                // clear old data
                this.setState({content: ""});
                this.setState({importCache: null});
                this.setState({loopName: ''});
                this.setState({opPolicies: false});
                this.setState({selectedRows: []});
       
                // validate - import file should be in json format     
                try {
                    this.setState({ importCache: JSON.parse(text) });
                } catch(e) {
                    alert('Error: Invalid import file. Expecting a json file with required attributes.');
                    this.setState({selectedFile: ''});
                    return;
                }
                    
                // check if file contains policies
                if (this.state.importCache.microServicePolicies == null || this.state.importCache.microServicePolicies.length <= 0) {
                    alert('Error: Invalid import file - no microservice config policies found');
                    this.setState({selectedFile: ''});
                    this.setState({importCache: null});
                    return;
                }
                    
                if (!(this.state.importCache.operationalPolicies == null) && (this.state.importCache.operationalPolicies.length > 0)) {
                    this.setState({opPolicies: true});
                }
                    
                // file seems valid
                this.setState({content: e.target.result});
                // set loop name to CL being imported or existing CL
                if (this.props.importCL) {
                    this.setState({loopName: this.state.importCache.name});
                } else {
                    this.setState({loopName: this.state.loopCache.getLoopName()});
                }
                // console.log(text);
                console.log('File read complete');
            }.bind(this));
            
            // file reading failed
            reader.addEventListener('error', function() {
                alert('Error : Failed to read file');
            });
            
            // read as text file
            reader.readAsText(event.target.files[0]);
            this.setState({selectedFile: event.target.files[0]});
        }
    }
    
    handleImportPolicy(e) {
        e.preventDefault();
        if (this.state.selectedRows.length <= 0) {
            alert('Error: Select one or more microservice policies for import');
            return;
        }
        
        if (this.state.importCache) { 
            if (this.props.importCL) {
                // ---- Import CL and its policies ----
                // check if a loop already exists with the same name
                var loopName = this.state.loopName;
                if (this.state.CLnames.includes(loopName)) {
                    console.log('Error: CL exists with name ' + loopName);
                    alert('Import CL Error: CL exists with name ' + loopName + '\nEnter a new name below or cancel');
                } else {
                    this.importLoop(loopName);
                }
                return;               
            } else {
                // TODO: Import policies into existing CL
                // This will be implemented when such use cases are supported
                console.log('Importing policies into existing CL is not supported');
            }
        }
    } 
    
    // import a CL and all its policies (mS and Operational)
    importLoop(loopName) {
        var templateName = this.state.importCache.loopTemplate.name;
        console.log('Importing CL with name: ' + loopName + ', Template: ' + templateName);
        this.setState({ show: false });
        
        // CL does not exist. First, create an empty CL.
        console.log('Creating loop ' + loopName);
        LoopService.createLoop(loopName, templateName).then(loopJson => {
            console.debug("CreateLoop response received: ", loopJson);
            var localCache = new LoopCache(loopJson);
                
            // Import microservice policies. Only 1 policy will be imported
            // TODO: Import of multiple policies for a mS type as well as multiple mS types
            //   in a single CL. This will be implmented when such use cases are supported.
            var msProperties=localCache.getMicroServicePolicies();
            var policyName = msProperties[0]["name"];
            var selectedMS = this.state.selectedRows[0];
            localCache.updateMicroServiceProperties(policyName, selectedMS.configurationsJson);
            localCache.updateMicroServicePdpGroup(policyName,selectedMS.pdpGroup, selectedMS.pdpSubgroup);
            console.log('Update microservices for loop ' + loopName);
            LoopService.setMicroServiceProperties(loopName, localCache.getMicroServiceForName(policyName)).then(resp => {
                // Import Operational policies
                if (this.state.opPolicies) {
                    var opArray = this.state.importCache.operationalPolicies;
                    // first add operational policy types to loop
                    console.log('Add operational policies for loop ' + loopName);
                    for (var idx in opArray) {
                        LoopService.addOperationalPolicyType(loopName,opArray[idx]["policyModel"]["policyModelType"],
                          opArray[idx]["policyModel"]["version"]).then(resp => {
                            LoopService.getLoop(loopName).then(loop => {
                                loopJson = loop;
                                opArray[idx]["name"] = loopJson.operationalPolicies[idx]["name"];
                                LoopService.setOperationalPolicyProperties(loopName, opArray).then(resp => {
                                    console.debug('Update operational policies response received');
                                    this.setState({ show: false });
                                    this.props.history.push('/');
                                    this.props.loadLoopFunction(loopName);
                                });
                            });
                        });
                    }
                }
            });
        }).catch(function (error) {
            console.warn(error);
            alert(error);
            this.props.history.push('/');
        });
    } 
    
    handleModelName = event => {
        this.setState({loopName: event.target.value});
    }
    
    handleSelection = event => {
        //this.setState({selectedRows: event});
        this.state.selectedRows = event;
    }
    
	render() {
        return (
            <ModalStyled size="xl" show={this.state.show} onHide={this.handleClose} backdrop="static" keyboard={false} >
                <Modal.Header closeButton>
                    {this.state.importCL?<Modal.Title>Import CL</Modal.Title>:
                        <Modal.Title>Import Policy JSON</Modal.Title>}
                </Modal.Header>
                <Modal.Body>
                    <Form.Group as={Row} controlId="formPlaintextEmail">
                        <Col sm="10">
                        <input style={{display: 'none'}} type="file" name="file" accept=".json,.js" onChange={this.fileSelectedHandler}
                            ref={fileInput => this.fileInput = fileInput}/>
                        <button onClick={() => this.fileInput.click()}>Pick Import File</button>
                            <Alert variant="secondary">
                                <p>{this.state.selectedFile.name}</p>
                            </Alert>
                        </Col>
                    </Form.Group>
                    <div>
                        <TextModal value={this.state.content} />
                     </div>

                    {this.state.importCache? <MaterialTable
                        onSelectionChange={this.handleSelection}
                        //onSelectionChange={(rowData) => this.setState({selectedRows: rowData})}
                        data={this.state.importCache.microServicePolicies}
                        columns={this.tableColumns}
                        title="Microservice Config Policies"
                        icons={this.tableIcons}
                        options={{
                            selection: true,
                            toolbar: true,
                            headerStyle: { backgroundColor: '#ddd', border: '2px solid black' },
                            padding: "dense"
                        }}
                        
                    />:""}
                    
                    {this.state.opPolicies? <MaterialTable
                        onSelectionChange={this.handleSelection}
                        data={this.state.importCache.operationalPolicies}
                        columns={this.tableColumns}
                        title="Operational Policies"
                        icons={this.tableIcons}
                        options={{
                            //selection: true,
                            toolbar: true,
                            //showTitle: true,
                            headerStyle: { backgroundColor: '#ddd', border: '2px solid black' },
                            padding: "dense"
                        }}
                        />:""}
                        
                    <br></br>
                    <Form.Group disabled={!this.state.importCL} as={Row} controlId="formPlaintextEmail">
                        <Form.Label column sm="2">Model Name:</Form.Label>
                        <input disabled={!this.state.importCL} type="text" style={{width: '40%', marginLeft: '1em' }}
                            value={this.state.loopName}
                            onChange={this.handleModelName}
                        />
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>             
                    {this.state.importCL?<Button disabled={!this.state.selectedFile.name} variant="primary" type="submit" onClick={this.handleImportPolicy.bind(this)}>Import CL</Button>:
                    <Button disabled={!this.state.selectedFile.name} variant="primary" type="submit" onClick={this.handleImportPolicy.bind(this)}>Import Policies</Button>}
                    <Button variant="secondary" type="null" onClick={this.handleClose}>Cancel</Button>
                </Modal.Footer>
            </ModalStyled>

        );
    }
}
