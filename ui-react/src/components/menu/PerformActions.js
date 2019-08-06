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
import LoopActionService from '../../api/LoopActionService';

export default class PerformActions extends React.Component {
	state = {
		loopCache: this.props.loopCache,
		loopName: this.props.loopCache.getLoopName(),
		loopAction: '',
		show:true
	};

	componentWillReceiveProps(newProps) {
		this.setState({
			loopCache: newProps.loopCache,
			loopName: newProps.loopCache.getLoopName(),
			loopAction: newProps.loopAction,
			show:true
		});
	}

	componentDidMount() {
		const action = this.state.loopAction;

		console.log("Perform action:" + action);
		LoopActionService.performAction(this.state.loopName, action).then(pars => {
			alert("Action " + action + " successfully performed");
		})
		.catch(function(error) {
			alert("Action " + action + " failed");
 		});;

		// refresh status and update loop logs
		LoopActionService.refreshStatus(this.state.loopName).then(data => {
			this.props.updateLoopFunction(data);
		});
		this.setState({ show: false });
		this.props.history.push('/');
	}


	render() {
		return (
			<div>
					<img alt='' data-scr={ require('../../React-Spinner.jpg')} />
				</div>
		);
	}
}
