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
import { mount } from 'enzyme';
import { render } from 'enzyme';
import ManageDictionaries from './ManageDictionaries';
import TemplateMenuService from '../../../api/TemplateService'
import CsvToJson from '../../../utils/CsvToJson'

describe('Verify ManageDictionaries', () => {
	beforeEach(() => {
		fetch.resetMocks();
	});

	it('Test API Successful', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {
					return Promise.resolve({
						"name": "vtest",
						"secondLevelDictionary": "1",
						"subDictionaryType": "string",
						"updatedBy": "test",
						"updatedDate": "05-07-2019 19:09:42"
					});
				}
			});
		});
		const component = shallow(<ManageDictionaries />);
		expect(component).toMatchSnapshot();
	});

	it('Test API Exception', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: false,
				status: 500,
				json: () => {
					return Promise.resolve({
						"name": "vtest",
						"secondLevelDictionary": "1",
						"subDictionaryType": "string",
						"updatedBy": "test",
						"updatedDate": "05-07-2019 19:09:42"
					});
				}
			});
		});
		const component = shallow(<ManageDictionaries />);
	});

	it('Test API Rejection', () => {
		const myMockFunc  = fetch.mockImplementationOnce(() => Promise.reject('error'));
		setTimeout( () => myMockFunc().catch(e => {
			console.info(e);
		}),
		100
		);
		const component = shallow(<ManageDictionaries />);
		expect(myMockFunc.mock.calls.length).toBe(1);
	});

	it('Test Table icons', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {
					return Promise.resolve({
						"name": "vtest",
						"secondLevelDictionary": "1",
						"subDictionaryType": "string",
						"updatedBy": "test",
						"updatedDate": "05-07-2019 19:09:42"
					});
				}
			});
		});
		const component = mount(<ManageDictionaries />);
		expect(component.find('[className="MuiSelect-icon MuiTablePagination-selectIcon"]')).toBeTruthy();
	});

	test('Test get dictionaryNames/dictionaryElements, add/delete dictionary functions', async () => {
		const dictionaries = [
			{
				name: "DefaultActors",
				secondLevelDictionary: 0,
				subDictionaryType: "",
				dictionaryElements: [
      					{
        					"shortName": "SDNR",
        					"name": "SDNR Change",
        					"description": "SDNR component",
        					"type": "string",
        					"createdDate": "2020-06-07T18:57:18.130858Z",
        					"updatedDate": "2020-06-11T13:10:52.239282Z",
        					"updatedBy": "admin"
      					}
    				],
    				createdDate: "2020-06-07T22:21:08.428742Z",
    				updatedDate: "2020-06-10T00:41:49.122908Z",
    				updatedBy: "Not found"
  			}
		];
		const historyMock = { push: jest.fn() };
		TemplateMenuService.getDictionary = jest.fn().mockImplementation(() => {
			return Promise.resolve(dictionaries);
		});
		TemplateMenuService.getDictionaryElements = jest.fn().mockImplementation(() => {
			return Promise.resolve(dictionaries[0]);
		});
		TemplateMenuService.insDictionary = jest.fn().mockImplementation(() => {
			return Promise.resolve(200);
		});
		TemplateMenuService.deleteDictionary = jest.fn().mockImplementation(() => {
			return Promise.resolve(200);
		});
		const flushPromises = () => new Promise(setImmediate);
		const component = shallow(<ManageDictionaries history={historyMock} />)
		const instance = component.instance();
		instance.getDictionaryElements("DefaultActors");
		instance.clickHandler();
		instance.addReplaceDictionaryRequest();
		instance.deleteDictionaryRequest();
		await flushPromises();
		expect(component.state('dictionaries')).toEqual(dictionaries);
	});

	test('Test adding and deleting dictionaryelements', async () => {
		const historyMock = { push: jest.fn() };
		const dictionaries = [
			{
				name: "DefaultActors",
				secondLevelDictionary: 0,
				subDictionaryType: "",
				dictionaryElements: [
      					{
        					"shortName": "SDNR",
        					"name": "SDNR Change",
        					"description": "SDNR component",
        					"type": "string",
        					"createdDate": "2020-06-07T18:57:18.130858Z",
        					"updatedDate": "2020-06-11T13:10:52.239282Z",
        					"updatedBy": "admin"
      					}
    				],
    				createdDate: "2020-06-07T22:21:08.428742Z",
    				updatedDate: "2020-06-10T00:41:49.122908Z",
    				updatedBy: "Not found"
  			}
		];
		TemplateMenuService.getDictionary = jest.fn().mockImplementation(() => {
			return Promise.resolve(dictionaries);
		});
		TemplateMenuService.insDictionaryElements = jest.fn().mockImplementation(() => {
			return Promise.resolve(200);
		});
		TemplateMenuService.deleteDictionaryElements = jest.fn().mockImplementation(() => {
			return Promise.resolve(200);
		});
		const flushPromises = () => new Promise(setImmediate);
		const component = shallow(<ManageDictionaries history={historyMock}/>)
		const instance = component.instance();
		instance.addReplaceDictionaryRequest({ name: "EventDictionary", secondLevelDictionary: "0", subDictionaryType: "string"} );
		instance.deleteDictionaryRequest('EventDictionary');
		await flushPromises();
		expect(component.state('currentSelectedDictionary')).toEqual(null);
	});

	it('Test handleClose', () => {
		fetch.mockImplementationOnce(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {
					return Promise.resolve({
						"name": "vtest",
						"secondLevelDictionary": "1",
						"subDictionaryType": "string",
						"updatedBy": "test",
						"updatedDate": "05-07-2019 19:09:42"
					});
				}
			});
		});
		const historyMock = { push: jest.fn() };
		const handleClose = jest.spyOn(ManageDictionaries.prototype,'handleClose');
		const component = shallow(<ManageDictionaries history={historyMock} />)
		component.find('[variant="secondary"]').prop('onClick')();
		expect(handleClose).toHaveBeenCalledTimes(1);
		expect(component.state('show')).toEqual(false);
		expect(historyMock.push.mock.calls[0]).toEqual([ '/']);
		handleClose.mockClear();
	});

	it('Test CsvToJson', () => {

		const rawCsv = '"Element String","Element Number"\n"Some String",99\n';
		const hdrNames= [ 'Element String', 'Element Number' ];
		const jsonKeyNames = [ 'elStr', 'elnum' ];
		const mandatory = [ true, true ];
		const expectedResult = { errorMessages: '', jsonObjArray: [{elStr: 'Some String', elnum: "99"}]};

		expect(CsvToJson(rawCsv, ',', '|', hdrNames, jsonKeyNames, mandatory)).toEqual(expectedResult);
	});
});
