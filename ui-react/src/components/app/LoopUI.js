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
import ClosedLoopView from '../loop_view/ClosedLoopView';
import styled from 'styled-components';
import MenuBar from '../menu/MenuBar';
import Navbar from 'react-bootstrap/Navbar';
import logo from './logo.png';
import { GlobalClampStyle } from '../../theme/globalStyle.js';

const ProjectNameStyle = styled.a`
	padding: 20px;
	font-size: 30px;

`

export default class LoopUI extends React.Component {
	
	user = "testuser";
		
	renderMenuNavBar() {
		return (
			<MenuBar />
		);
	}
	
	renderUserLoggedNavBar() {
		return (
			<Navbar.Text>
				<a href="index.html">Signed in as: {this.user}</a>
			</Navbar.Text>
		);
	}
	
	renderLogoNavBar() {
		return (
			<Navbar.Brand>
			<img height="50px" width="234px" src={logo} alt=""/>
				<Navbar.Text>
					<ProjectNameStyle>
						CLAMP
					</ProjectNameStyle>
				</Navbar.Text>
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
	
	renderLoopView() {
		return (<ClosedLoopView />);
	}
	
	render() {
		return (
				<div>
				 	<GlobalClampStyle />
					{this.renderNavBar()}
					{this.renderLoopView()}
				</div>
		);
	}
}


