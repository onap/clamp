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
import DeployLoop from './DeployLoop';
import LoopCache from '../../api/LoopCache';
import LoopActionService from '../../api/LoopActionService';
import LoopService from '../../api/LoopService';

describe('Verify DeployLoop', () => {
	const loopCache = new LoopCache({
		"name": "LOOP_Jbv1z_v1_0_ResourceInstanceName1_tca",
		"globalPropertiesJson": {
			"dcaeDeployParameters": {
				"location_id": "",
				"policy_id": "TCA_h2NMX_v1_0_ResourceInstanceName1_tca"
			}
		}
	});

	it('Test the render method', () => {
		const component = shallow(
			<DeployLoop loopCache={loopCache}/>
		)

	expect(component).toMatchSnapshot();
	});
	
	it('Test handleClose', () => {
		const historyMock = { push: jest.fn() };
		const handleClose = jest.spyOn(DeployLoop.prototype,'handleClose');
		const component = shallow(<DeployLoop history={historyMock} loopCache={loopCache}/>)

		component.find('[variant="secondary"]').prop('onClick')();

		expect(handleClose).toHaveBeenCalledTimes(1);
		expect(historyMock.push.mock.calls[0]).toEqual([ '/', ]);
	});

	it('Test handleSave successful', async () => {
		const flushPromises = () => new Promise(setImmediate);
		const historyMock = { push: jest.fn() };
		const updateLoopFunction = jest.fn();
		const handleSave = jest.spyOn(DeployLoop.prototype,'handleSave');
		LoopService.updateGlobalProperties = jest.fn().mockImplementation(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				text: () => "OK"
			});
		});
		LoopActionService.performAction = jest.fn().mockImplementation(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {}
			});
		});
		LoopActionService.refreshStatus = jest.fn().mockImplementation(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				json: () => {}
			});
		});
		const jsdomAlert = window.alert;
		window.alert = () => {};
		const component = shallow(<DeployLoop history={historyMock} loopCache={loopCache} updateLoopFunction={updateLoopFunction} />)

		component.find('[variant="primary"]').prop('onClick')();
		await flushPromises();
		component.update();

		expect(handleSave).toHaveBeenCalledTimes(1);
		expect(component.state('show')).toEqual(false);
		expect(historyMock.push.mock.calls[0]).toEqual([ '/', ]);
		window.alert = jsdomAlert;
		handleSave.mockClear();
	});

	it('Onchange event', () => {
		const event = { target: { name: "location_id", value: "testLocation"} };
		const component = shallow(<DeployLoop loopCache={loopCache}/>);
		const forms = component.find('StateManager');

		component.find('[name="location_id"]').simulate('change', event);
		component.update();
		expect(component.state('temporaryPropertiesJson').dcaeDeployParameters.location_id).toEqual("testLocation");
	});
});