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
import LoopUI from './LoopUI';
import LoopCache from './api/LoopCache';
import UserService from './api/UserService';

describe('Verify LoopUI', () => {
	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockImplementation(() => {
			return Promise.resolve({
				ok: true,
				status: 200,
				text: () => "testUser"

			});
		});
	})

	it('Test the render method', async () => {
		const flushPromises = () => new Promise(setImmediate);

		const component = shallow(<LoopUI />)
		component.setState({
			loopName: "testLoopName",
			showAlert: false 
		});
		await flushPromises();
		expect(component).toMatchSnapshot();
	});
});
