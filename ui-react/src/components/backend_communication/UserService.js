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

export default class UserService {

	static LOGIN() {
		return fetch('/restservices/clds/v1/user/getUser', {
				method: 'GET',
				credentials: 'include',
			})
		.then(function (response) {
			if (response.ok) {
				console.log("getUser response received: ", response.status);
				return response.text();
			} else {
				console.error("getUser failed with status code: ",response.status);
				return "Anonymous";
			}
		})
		.then(function (data) {
			console.log ("User connected:",data)
			return data;
		})
		.catch(function(error) {
			console.error("getUser error received",error);
			return "Anonymous";
		});
	}
}

