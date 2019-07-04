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

import { createGlobalStyle } from 'styled-components';

export const GlobalClampStyle = createGlobalStyle`
  body {
    padding: 0;
    margin: 0;
    font-family: ${props => props.theme.fontFamily};
    font-size: ${props => props.theme.fontSize};
    font-weight: normal;
    color: ${props => props.theme.fontColor};
    background-color: ${props => (props.theme.backgroundColor)};
  }

  a {
	font-family: ${props => props.theme.fontFamily};
    font-size: ${props => props.theme.fontSize};
    font-weight: bold;
    color: ${props => props.theme.fontColor};
    background-color: ${props => (props.theme.backgroundColor)};
  }
`

export const DefaultClampTheme = {
	backgroundColor: '#ffeeee',
	fontColor: '#ffff00',
	fontFamily: 'Arial, sans-serif',
	fontSize: '15px',
	danger: '#eb238e',
	light: '#f4f4f4',
	dark: '#222'
};
