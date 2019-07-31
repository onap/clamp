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
import styled from 'styled-components';
import MenuBar from './components/menu/MenuBar';
import Navbar from 'react-bootstrap/Navbar';
import logo from './logo.png';
import { GlobalClampStyle } from './theme/globalStyle.js';

import LoopSvg from './components/loop_viewer/svg/LoopSvg';
import LoopLogs from './components/loop_viewer/logs/LoopLogs';
import LoopStatus from './components/loop_viewer/status/LoopStatus';
import UserService from './api/UserService';
import LoopCache from './api/LoopCache';

import { Route, Redirect } from 'react-router-dom'
import OpenLoopModal from './components/dialogs/OpenLoop/OpenLoopModal';
import OperationalPolicyModal from './components/dialogs/OperationalPolicy/OperationalPolicyModal';
import ConfigurationPolicyModal from './components/dialogs/ConfigurationPolicy/ConfigurationPolicyModal';
import LoopProperties from './components/dialogs/LoopProperties';
import UserInfo from './components/dialogs/UserInfo';

const ProjectNameStyled = styled.a`
	vertical-align: middle;
	padding-left: 30px;
	font-size: 30px;

`
const LoopViewDivStyled = styled.div`
	height: 100%;
	overflow: hidden;
	margin-left: 10px;
	margin-right: 10px;
	margin-bottom: 10px;
	color: ${props => props.theme.loopViewerFontColor};
	background-color: ${props => props.theme.loopViewerBackgroundColor};
	border: 1px solid transparent;
	border-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
`

const LoopViewHeaderDivStyled = styled.div`
	background-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
	padding: 10px 10px;
	color: ${props => props.theme.loopViewerHeaderFontColor};
`

const LoopViewBodyDivStyled = styled.div`
	background-color: ${props => (props.theme.loopViewerBackgroundColor)};
	padding: 10px 10px;
	color: ${props => (props.theme.loopViewerHeaderFontColor)};
	height: 95%;
`

const LoopViewLoopNameSpanStyled = styled.span`
	font-weight: bold;
	color: ${props => (props.theme.loopViewerHeaderFontColor)};
	background-color: ${props => (props.theme.loopViewerHeaderBackgroundColor)};
`

export default class LoopUI extends React.Component {

	static defaultLoopName="Empty (NO loop loaded yet)";

	state = {
		userName: null,
		loopName: LoopUI.defaultLoopName,
		loopCache: new LoopCache({}),
	};

	constructor() {
		super();
		this.getUser = this.getUser.bind(this);
		this.updateLoopCache = this.updateLoopCache.bind(this);
	}

	componentWillMount() {
		this.getUser();
	}

	getUser() {
		UserService.login().then(user => {
			this.setState({ userName: user })
		});
	}

	renderMenuNavBar() {
		return (
			<MenuBar loopCache={this.state.loopCache}/>
		);
	}

	renderUserLoggedNavBar() {
		return (
			<Navbar.Text>
				Signed in as: <a href="/login">{this.state.userName}</a>
			</Navbar.Text>
		);
	}

	renderLogoNavBar() {
		return (
			<Navbar.Brand>
				<img height="50px" width="234px" src={logo} alt="" />
				<ProjectNameStyled>CLAMP</ProjectNameStyled>
			</Navbar.Brand>
		);
	}

	renderNavBar() {
		return (
			<Navbar expand="lg">
				{this.renderLogoNavBar()}
				{this.renderMenuNavBar()}
				{this.renderUserLoggedNavBar()}
			</Navbar>
		);
	}

	renderLoopViewHeader() {
		return (
			<LoopViewHeaderDivStyled>
				Loop Viewer - {this.state.loopName}
			</LoopViewHeaderDivStyled>
		);
	}

	renderLoopViewBody() {
		return (
			<LoopViewBodyDivStyled>
				<LoopSvg loopCache={this.state.loopCache} />
				<LoopStatus loopCache={this.state.loopCache}/>
				<LoopLogs loopCache={this.state.loopCache} />
			</LoopViewBodyDivStyled>
		);
	}

	getLoopCache() {
		return this.state.loopCache;

	}
	renderLoopViewer() {
		return (
			<LoopViewDivStyled>
				{this.renderLoopViewHeader()}
				{this.renderLoopViewBody()}
			</LoopViewDivStyled>
		);
	}

	updateLoopCache(loopJson) {
		this.setState({ loopCache: new LoopCache(loopJson) });
		this.setState({ loopName: this.state.loopCache.getLoopName() });
	}

	render() {
		return (
			<div id="main_div">
				<GlobalClampStyle />
				{this.renderNavBar()}
				{this.renderLoopViewer()}
				<Route path="/operationalPolicyModal"
					render={(routeProps) => (<OperationalPolicyModal {...routeProps} loopCache={this.state.loopCache} />)} />
				<Route path="/configurationPolicyModal/:componentName" render={(routeProps) => (<ConfigurationPolicyModal {...routeProps} loopCache={this.state.loopCache} />)} />
				<Route path="/openLoop" render={(routeProps) => (<OpenLoopModal {...routeProps} updateLoopCacheFunction={this.updateLoopCache} />)} />
				<Route path="/loopProperties" render={(routeProps) => (<LoopProperties {...routeProps} loopCache={this.state.loopCache} />)} />
				<Route path="/userInfo" render={(routeProps) => (<UserInfo {...routeProps} />)} />
				<Route path="/closeLoop" render={(routeProps) => (<Redirect to='/'/>)} />
			</div>
		);
	}
}
