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
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import 'bootstrap-css-only/css/bootstrap.min.css';
import styled from 'styled-components';
import { Link } from 'react-router-dom'

const StyledNavDropdownItem = styled(NavDropdown.Item)`
	color: ${props => props.theme.fontNormal};
	:hover {
			background-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
			color:  ${props => props.theme.loopViewerHeaderFontColor}
	}
`;
const StyledLink = styled(Link)`
	color: ${props => props.theme.menuColor};
	background-color: ${props => props.theme.menuBackgroundColor};
	font-weight: normal;
	display: block;
	width: 100%;
	padding: .25rem 1.5rem;
	clear: both;
	text-align: inherit;
	white-space: nowrap;
	border: 0;
	:hover {
			text-decoration: none;
			background-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
			color:  ${props => props.theme.loopViewerHeaderFontColor};
	}
`;

export default class MenuBar extends React.Component {

	render () {
		return (
				<Navbar.Collapse>
					<NavDropdown title="Closed Loop">
						<StyledNavDropdownItem as={StyledLink} to="/openLoop">Open CL</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/loopProperties">Properties CL</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/closeLoop">Close Model</StyledNavDropdownItem>
					</NavDropdown>
					<NavDropdown title="Manage">
						<StyledNavDropdownItem as={StyledLink} to="/submit">Submit</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/stop">Stop</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/restart">Restart</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/delete">Delete</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="#action/3.3">Deploy</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/undeploy">UnDeploy</StyledNavDropdownItem>
					</NavDropdown>
					<NavDropdown title="View">
						<StyledNavDropdownItem as={StyledLink} to="/refreshStatus">Refresh Status</StyledNavDropdownItem>
					</NavDropdown>
					<NavDropdown title="Help" id="basic-nav-dropdown">
						<StyledNavDropdownItem href="https://wiki.onap.org/" target="_blank">Wiki</StyledNavDropdownItem>
						<StyledNavDropdownItem href="mailto:onap-discuss@lists.onap.org?subject=CLAMP&body=Please send us suggestions or feature enhancements or defect. If possible, please send us the steps to replicate any defect.">Contact Us</StyledNavDropdownItem>
						<StyledNavDropdownItem as={StyledLink} to="/userInfo">User Info</StyledNavDropdownItem>
					</NavDropdown>
				</Navbar.Collapse>
		);
	}
}
