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
import ViewLoopTemplatesModal from './ViewLoopTemplatesModal';
import { mount } from 'enzyme';
import TemplateMenuService from '../../../api/TemplateMenuService'

describe('Verify ViewLoopTemplatesModal', () => {
	beforeEach(() => {
		fetch.resetMocks();
		}
	);

	it('Test API Successful', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {
					return Promise.resolve({
						"index": "1",
						"name": "TOSCA_MTCA",
						"maximumInstancesAllowed": "1",
						"allowedLoopType" : "OPEN",
						"uniqueBlueprint":"No",
						"updatedBy":"admin",
						"updatedDate":"2019-09-06 19:09:42"
					});
				}
			});
		});
		const component = shallow(<ViewLoopTemplatesModal/>);
	});

	it('Test API Exception', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: false,
				status: 500,
				json: () => {
					return Promise.resolve({
						"index": "1",
						"name": "TOSCA_MTCA",
						"maximumInstancesAllowed": "1",
						"allowedLoopType" : "OPEN",
						"uniqueBlueprint":"No",
						"updatedBy":"admin",
						"updatedDate":"2019-09-06 19:09:42"
					});
				}
			});
		});
		const component = shallow(<ViewLoopTemplatesModal/>);
	});

	it('Test API Rejection', () => {
		const myMockFunc  = fetch.mockImplementationOnce(() => Promise.reject('error'));
		setTimeout( () => myMockFunc().catch(e => {
			console.log(e);
		}),
		100
		);
		new Promise(resolve => setTimeout(resolve, 200));
		const component = shallow(<ViewLoopTemplatesModal/>);
		expect(myMockFunc.mock.calls.length).toBe(1);
	});

	it('Test the BP template render method', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {
					return Promise.resolve({
						"index": "1",
						"name": "TOSCA_MTCA",
						"maximumInstancesAllowed": "1",
						"allowedLoopType" : "OPEN",
						"uniqueBlueprint":"No",
						"updatedBy":"admin",
						"updatedDate":"2019-09-06 19:09:42"
					});
				}
			});
		});
		const component = shallow(<ViewLoopTemplatesModal/>);
		component.setState({ loopTemplateData: {
			"index": "1",
			"name": "TOSCA_MTCA",
			"maximumInstancesAllowed": "1",
			"allowedLoopType" : "OPEN",
			"uniqueBlueprint":"No",
			"updatedBy":"admin",
			"updatedDate":"2019-09-06 19:09:42"
		  }
		});
		expect(component).toMatchSnapshot();
	});

	it('Test Table icons', () => {
			fetch.mockImplementationOnce(() => {
				return Promise.resolve({
					ok: true,
					status: 200,
					json: () => {
						return Promise.resolve({
							"index": "1",
							"name": "TOSCA_MTCA",
							"maximumInstancesAllowed": "1",
							"allowedLoopType" : "OPEN",
							"uniqueBlueprint":"No",
							"updatedBy":"admin",
							"updatedDate":"2019-09-06 19:09:42"
						});
					}
				});
			});
			const component = mount(<ViewLoopTemplatesModal/>);
			expect(component.find('[className="MuiSelect-icon MuiTablePagination-selectIcon"]')).toBeTruthy();
		});

		it('Test handleYamlContent', () => {
			fetch.mockImplementationOnce(() => {
				return Promise.resolve({
					ok: true,
					status: 200,
					json: () => {
						return Promise.resolve({
							"index": "1",
							"name": "TOSCA_MTCA",
							"maximumInstancesAllowed": "1",
							"allowedLoopType" : "OPEN",
							"uniqueBlueprint":"No",
							"updatedBy":"admin",
							"updatedDate":"2019-09-06 19:09:42"
						});
					}
				});
			});
			const yamlContent = 'Please select Blue print template to view the details';
			const component = shallow(<ViewLoopTemplatesModal/>);
			expect(component.find('[className="sc-kAzzGY fzcWwp"]')).toBeTruthy();
			expect(component.state('content')).toEqual(yamlContent);
		});

		it('Test handleClose', () => {
			fetch.mockImplementationOnce(() => {
				return Promise.resolve({
					ok: true,
					status: 200,
					json: () => {
						return Promise.resolve({
							"index": "1",
							"name": "TOSCA_MTCA",
							"maximumInstancesAllowed": "1",
							"allowedLoopType" : "OPEN",
							"uniqueBlueprint":"No",
							"updatedBy":"admin",
							"updatedDate":"2019-09-06 19:09:42"
						});
					}
				});
			});
			const historyMock = { push: jest.fn() };
			const handleClose = jest.spyOn(ViewLoopTemplatesModal.prototype,'handleClose');
			const component = shallow(<ViewLoopTemplatesModal history={historyMock} />)
			component.find('[variant="secondary"]').prop('onClick')();
			expect(handleClose).toHaveBeenCalledTimes(1);
			expect(component.state('show')).toEqual(false);
			expect(historyMock.push.mock.calls[0]).toEqual([ '/']);
			handleClose.mockClear();
		});

		test('Test getSVGRepresentation', async () => {
			const historyMock = { push: jest.fn() };
			TemplateMenuService.getLoopTemplates = jest.fn().mockImplementation(() => {
				return Promise.resolve({loopTemplateData: "TOSCA_MTCA"});
			});
			TemplateMenuService.getSVGRepresentation = jest.fn().mockImplementation(() => {
				return Promise.resolve("<svg><text test</text></svg>");
			});
			const flushPromises = () => new Promise(setImmediate);
			const component = shallow(<ViewLoopTemplatesModal history={historyMock}/>)
			const instance = component.instance();
			instance.getSVGRepresentation("TOSCA_MTCA");
			await flushPromises();
			expect(component.state('content')).toEqual("<svg><text test</text></svg>");
		})
	});
