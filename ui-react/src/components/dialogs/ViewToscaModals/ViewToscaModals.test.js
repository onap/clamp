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
import React from 'react';
import { shallow } from 'enzyme';
import ViewToscaModals from './ViewToscaModals';

describe('Verify ViewToscaModals', () => {
	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockImplementation(() => {
			return Promise.resolve({
			ok: true,
			status: 200,
			json: () => {
				return Promise.resolve({
					"index": "1",
  				"toscaModelYaml":"tosca_definitions_version: tosca_simple_yaml_1_0_0\r\nnode_types:\r\n  policy.nodes.DCAE_MTCA_Config:\r\n    derived_from: policy.nodes.Tosca\r\n    properties:\r\n      DCAE_MTCAConfig:\r\n        type: policy.data.DCAE_MTCA_Context\r\n        description: MTCA Config\r\n\r\ndata_types:\r\n  policy.data.DCAE_MTCA_Context:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:\r\n      context:\r\n        type: list\r\n        description: Context\r\n        required: true\r\n        entry_schema: \r\n          type: String \r\n          constraints: \r\n            - valid_values: [ \"Dictionary:mtcaContext\" ]\r\n      signatures:\r\n        type: list\r\n        description: Signatures\r\n        required: true\r\n        entry_schema:\r\n          type: policy.data.signatures\r\n          \r\n  policy.data.signatures:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:              \r\n      closedLoopControlName:\r\n        type: string\r\n        description: Policy\r\n        required: true\r\n        constraints:\r\n          - min_length: 1                               \r\n          - valid_values: [ \"Dictionary:userDefined\" ]\r\n          \r\n      useCaseName:\r\n        type: string\r\n        description: Name\r\n        min_length: 1\r\n        \r\n      nfNamingCode:\r\n        type: string\r\n        description: nfNaming Code\r\n        constraints:\r\n          - valid_values: [ \"Dictionary:NfNamingCode\" ] \r\n        \r\n      severity:\r\n        type: string\r\n        description: Severity\r\n        required: true\r\n        constraints:\r\n          - min_length: 1\r\n          - valid_values: [ \"NORMAL\", \"CRITICAL\", \"MAJOR\", \"MINOR\", \"WARNING\" ]\r\n          \r\n      ageLimit:\r\n        type: integer \r\n        description: Age limit (0- 4500 secs)\r\n        default: 3600\r\n        required: true\r\n        constraints:\r\n          - in_range: [ 0, 4500 ]\r\n          \r\n      maxInterval:\r\n        type: integer\r\n        description: Max Intervals\r\n        default: 0\r\n        required: true\r\n        constraints:\r\n        - in_range: [ 0, 9999 ]\r\n        \r\n      minMessageViolations:\r\n        type: integer\r\n        description: Min Violations\r\n        default: 0\r\n        required: true\r\n        constraints:\r\n        - in_range: [ 0, 9999 ]\r\n                \r\n      thresholds:\r\n        type: list\r\n        description: Threshold\r\n        constraints: \r\n          - min_length: 1          \r\n        required: true\r\n        entry_schema:\r\n           type: policy.data.threshold\r\n\r\n  policy.data.threshold:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:\r\n      thresholdName:\r\n        type: string\r\n        description: Metric\r\n        constraints:\r\n          - valid_values: [ \"Dictionary:ThresholdName\" ]\r\n      direction:\r\n        type: string\r\n        description: Operator\r\n        required: true\r\n        constraints:\r\n          - valid_values: [ \"GREATER\", \"LESS\", \"EQUAL\" ]                  \r\n      thresholdValue:\r\n        type: integer \r\n        description: Threshold\r\n        required: true\r\n        constraints:\r\n          - in_range: [ 0, 9999 ]","version":1.0,"toscaModelJson":"{\"schema\":{\"type\":\"object\",\"title\":\"MTCA Config\",\"required\":[\"context\",\"signatures\"],\"properties\":{\"context\":{\"propertyOrder\":1001,\"uniqueItems\":\"true\",\"format\":\"select\",\"title\":\"Context\",\"type\":\"array\",\"items\":{\"options\":{\"enum_titles\":[\"NONE\",\"PROD\"]},\"type\":\"string\",\"enum\":[\"NONE\",\"PROD\"]}},\"signatures\":{\"propertyOrder\":1002,\"uniqueItems\":\"true\",\"format\":\"tabs-top\",\"title\":\"Signatures\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"required\":[\"closedLoopControlName\",\"severity\",\"ageLimit\",\"maxInterval\",\"minMessageViolations\",\"thresholds\"],\"properties\":{\"severity\":{\"propertyOrder\":1006,\"minLength\":1,\"title\":\"Severity\",\"type\":\"string\",\"enum\":[\"NORMAL\",\"CRITICAL\",\"MAJOR\",\"MINOR\",\"WARNING\"]},\"ageLimit\":{\"propertyOrder\":1007,\"default\":3600,\"maximum\":4500,\"title\":\"Age limit (0- 4500 secs)\",\"type\":\"integer\",\"minimum\":0},\"useCaseName\":{\"propertyOrder\":1004,\"title\":\"Name\",\"type\":\"string\"},\"thresholds\":{\"minItems\":1,\"propertyOrder\":1010,\"uniqueItems\":\"true\",\"format\":\"tabs-top\",\"title\":\"Threshold\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"required\":[\"direction\",\"thresholdValue\"],\"properties\":{\"thresholdValue\":{\"propertyOrder\":1013,\"maximum\":9999,\"title\":\"Threshold\",\"type\":\"integer\",\"minimum\":0},\"thresholdName\":{\"propertyOrder\":1011,\"options\":{\"enum_titles\":[\"PMRAATTCBRA\",\"PMCELLDOWNTIMEAUTO\",\"PMCELLDOWNTIMEMAN\",\"BS_Reset_Needed_UC2\",\"PMRRCCONNESTABATT\",\"PMRRCCONNESTABSUCC\",\"PMRRCCONNESTABFLCELLLATENCY\"]},\"title\":\"Metric\",\"type\":\"string\",\"enum\":[\"PMRAATTCBRA\",\"PMCELLDOWNTIMEAUTO\",\"PMCELLDOWNTIMEMAN\",\"BS_Reset_Needed_UC2\",\"PMRRCCONNESTABATT\",\"PMRRCCONNESTABSUCC\",\"PMRRCCONNESTABFLCELLLATENCY\"]},\"direction\":{\"propertyOrder\":1012,\"title\":\"Operator\",\"type\":\"string\",\"enum\":[\"GREATER\",\"LESS\",\"EQUAL\"]}}}},\"minMessageViolations\":{\"propertyOrder\":1009,\"default\":0,\"maximum\":9999,\"title\":\"Min Violations\",\"type\":\"integer\",\"minimum\":0},\"maxInterval\":{\"propertyOrder\":1008,\"default\":0,\"maximum\":9999,\"title\":\"Max Intervals\",\"type\":\"integer\",\"minimum\":0},\"nfNamingCode\":{\"propertyOrder\":1005,\"options\":{\"enum_titles\":[\"ENBE\",\"MEAP\"]},\"title\":\"nfNaming Code\",\"type\":\"string\",\"enum\":[\"ENBE\",\"MEAP\"]},\"closedLoopControlName\":{\"propertyOrder\":1003,\"minLength\":1,\"options\":{\"enum_titles\":[]},\"title\":\"Policy\",\"type\":\"string\",\"enum\":[]}}}}}}}",
					"toscaModelName":"DCAE_MTCAConfig",
					"version":"16",
					"userId":"aj928f",
					"policyType":"mtca",
					"lastUpdatedDate":"05-07-2019 19:09:42"
				});
			}
		});
	});
});

	it('Test the tosca model view render method', () => {
		const component = shallow(<ViewToscaModals/>);
		component.setState({
			ToscaNames: {
				"index": "1",
				"toscaModelYaml": "tosca_definitions_version: tosca_simple_yaml_1_0_0\r\nnode_types:\r\n  policy.nodes.DCAE_MTCA_Config:\r\n    derived_from: policy.nodes.Tosca\r\n    properties:\r\n      DCAE_MTCAConfig:\r\n        type: policy.data.DCAE_MTCA_Context\r\n        description: MTCA Config\r\n\r\ndata_types:\r\n  policy.data.DCAE_MTCA_Context:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:\r\n      context:\r\n        type: list\r\n        description: Context\r\n        required: true\r\n        entry_schema: \r\n          type: String \r\n          constraints: \r\n            - valid_values: [ \"Dictionary:mtcaContext\" ]\r\n      signatures:\r\n        type: list\r\n        description: Signatures\r\n        required: true\r\n        entry_schema:\r\n          type: policy.data.signatures\r\n          \r\n  policy.data.signatures:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:              \r\n      closedLoopControlName:\r\n        type: string\r\n        description: Policy\r\n        required: true\r\n        constraints:\r\n          - min_length: 1                               \r\n          - valid_values: [ \"Dictionary:userDefined\" ]\r\n          \r\n      useCaseName:\r\n        type: string\r\n        description: Name\r\n        min_length: 1\r\n        \r\n      nfNamingCode:\r\n        type: string\r\n        description: nfNaming Code\r\n        constraints:\r\n          - valid_values: [ \"Dictionary:NfNamingCode\" ] \r\n        \r\n      severity:\r\n        type: string\r\n        description: Severity\r\n        required: true\r\n        constraints:\r\n          - min_length: 1\r\n          - valid_values: [ \"NORMAL\", \"CRITICAL\", \"MAJOR\", \"MINOR\", \"WARNING\" ]\r\n          \r\n      ageLimit:\r\n        type: integer \r\n        description: Age limit (0- 4500 secs)\r\n        default: 3600\r\n        required: true\r\n        constraints:\r\n          - in_range: [ 0, 4500 ]\r\n          \r\n      maxInterval:\r\n        type: integer\r\n        description: Max Intervals\r\n        default: 0\r\n        required: true\r\n        constraints:\r\n        - in_range: [ 0, 9999 ]\r\n        \r\n      minMessageViolations:\r\n        type: integer\r\n        description: Min Violations\r\n        default: 0\r\n        required: true\r\n        constraints:\r\n        - in_range: [ 0, 9999 ]\r\n                \r\n      thresholds:\r\n        type: list\r\n        description: Threshold\r\n        constraints: \r\n          - min_length: 1          \r\n        required: true\r\n        entry_schema:\r\n           type: policy.data.threshold\r\n\r\n  policy.data.threshold:\r\n    derived_from: tosca.nodes.Root        \r\n    properties:\r\n      thresholdName:\r\n        type: string\r\n        description: Metric\r\n        constraints:\r\n          - valid_values: [ \"Dictionary:ThresholdName\" ]\r\n      direction:\r\n        type: string\r\n        description: Operator\r\n        required: true\r\n        constraints:\r\n          - valid_values: [ \"GREATER\", \"LESS\", \"EQUAL\" ]                  \r\n      thresholdValue:\r\n        type: integer \r\n        description: Threshold\r\n        required: true\r\n        constraints:\r\n          - in_range: [ 0, 9999 ]","version":1.0,"toscaModelJson":"{\"schema\":{\"type\":\"object\",\"title\":\"MTCA Config\",\"required\":[\"context\",\"signatures\"],\"properties\":{\"context\":{\"propertyOrder\":1001,\"uniqueItems\":\"true\",\"format\":\"select\",\"title\":\"Context\",\"type\":\"array\",\"items\":{\"options\":{\"enum_titles\":[\"NONE\",\"PROD\"]},\"type\":\"string\",\"enum\":[\"NONE\",\"PROD\"]}},\"signatures\":{\"propertyOrder\":1002,\"uniqueItems\":\"true\",\"format\":\"tabs-top\",\"title\":\"Signatures\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"required\":[\"closedLoopControlName\",\"severity\",\"ageLimit\",\"maxInterval\",\"minMessageViolations\",\"thresholds\"],\"properties\":{\"severity\":{\"propertyOrder\":1006,\"minLength\":1,\"title\":\"Severity\",\"type\":\"string\",\"enum\":[\"NORMAL\",\"CRITICAL\",\"MAJOR\",\"MINOR\",\"WARNING\"]},\"ageLimit\":{\"propertyOrder\":1007,\"default\":3600,\"maximum\":4500,\"title\":\"Age limit (0- 4500 secs)\",\"type\":\"integer\",\"minimum\":0},\"useCaseName\":{\"propertyOrder\":1004,\"title\":\"Name\",\"type\":\"string\"},\"thresholds\":{\"minItems\":1,\"propertyOrder\":1010,\"uniqueItems\":\"true\",\"format\":\"tabs-top\",\"title\":\"Threshold\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"required\":[\"direction\",\"thresholdValue\"],\"properties\":{\"thresholdValue\":{\"propertyOrder\":1013,\"maximum\":9999,\"title\":\"Threshold\",\"type\":\"integer\",\"minimum\":0},\"thresholdName\":{\"propertyOrder\":1011,\"options\":{\"enum_titles\":[\"PMRAATTCBRA\",\"PMCELLDOWNTIMEAUTO\",\"PMCELLDOWNTIMEMAN\",\"BS_Reset_Needed_UC2\",\"PMRRCCONNESTABATT\",\"PMRRCCONNESTABSUCC\",\"PMRRCCONNESTABFLCELLLATENCY\"]},\"title\":\"Metric\",\"type\":\"string\",\"enum\":[\"PMRAATTCBRA\",\"PMCELLDOWNTIMEAUTO\",\"PMCELLDOWNTIMEMAN\",\"BS_Reset_Needed_UC2\",\"PMRRCCONNESTABATT\",\"PMRRCCONNESTABSUCC\",\"PMRRCCONNESTABFLCELLLATENCY\"]},\"direction\":{\"propertyOrder\":1012,\"title\":\"Operator\",\"type\":\"string\",\"enum\":[\"GREATER\",\"LESS\",\"EQUAL\"]}}}},\"minMessageViolations\":{\"propertyOrder\":1009,\"default\":0,\"maximum\":9999,\"title\":\"Min Violations\",\"type\":\"integer\",\"minimum\":0},\"maxInterval\":{\"propertyOrder\":1008,\"default\":0,\"maximum\":9999,\"title\":\"Max Intervals\",\"type\":\"integer\",\"minimum\":0},\"nfNamingCode\":{\"propertyOrder\":1005,\"options\":{\"enum_titles\":[\"ENBE\",\"MEAP\"]},\"title\":\"nfNaming Code\",\"type\":\"string\",\"enum\":[\"ENBE\",\"MEAP\"]},\"closedLoopControlName\":{\"propertyOrder\":1003,\"minLength\":1,\"options\":{\"enum_titles\":[]},\"title\":\"Policy\",\"type\":\"string\",\"enum\":[]}}}}}}}",
				"toscaModelName": "DCAE_MTCAConfig",
				"version" : "16",
				"userId" : "aj928f",
				"policyType" : "mtca",
				"lastUpdatedDate" : "05-07-2019 19:09:42"
			}
		});
    expect(component).toMatchSnapshot();
  });

	it('Test onRowClick', () => {
		const component = shallow(<ViewToscaModals/>);
		component.find('ModalBody').simulate('onRowClick');
    expect(component.state('selectedRow')).toEqual(-1);
  });

	it('Test handleYamlContent', () => {
		const yamlContent = 'MTCA Tosca model details';
		const component = shallow(<ViewToscaModals/>);
		component.find('[value="Please select Tosca model to view the details"]').prop('onChange')({ target: { value: yamlContent }});
    expect(component.state('show')).toEqual(true);
	});

	it('Test handleClose', () => {
		const historyMock = { push: jest.fn() };
    const handleClose = jest.spyOn(ViewToscaModals.prototype,'handleClose');
    const component = shallow(<ViewToscaModals history={historyMock} />)
    component.find('[variant="secondary"]').prop('onClick')();
    expect(handleClose).toHaveBeenCalledTimes(1);
    expect(component.state('show')).toEqual(false);
    expect(historyMock.push.mock.calls[0]).toEqual([ '/']);
    handleClose.mockClear();
	});
});
