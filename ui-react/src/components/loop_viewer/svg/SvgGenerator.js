/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import React from 'react'
import styled from 'styled-components';
import { withRouter } from "react-router-dom";
import LoopCache from '../../../api/LoopCache';

class Testos extends React.Component {

    boxWidth = 250;
    boxHeight = 150;

	state = {
		loopCache: new LoopCache({}),
	}

	constructor(props) {
		super(props);
		this.state.loopCache = props.loopCache;
		this.handleSvgClick = this.handleSvgClick.bind(this);
		this.createOneBox = this.createOneBox.bind(this);
		this.createAllPolicyBoxes = this.createAllPolicyBoxes.bind(this);
	}

	componentWillReceiveProps(newProps) {
		if (this.state.loopCache !== newProps.loopCache) {
			this.setState({
				loopCache: newProps.loopCache,
			});
		}
	}

	handleSvgClick(event) {
		console.debug("svg click event received");
		var elementName = event.target.parentNode.getAttribute('policyId');
		console.info("SVG element clicked", elementName);
		if (elementName !== null) {
		    this.props.history.push("/policyModal/"+event.target.parentNode.getAttribute('policyType')+"/"+elementName);
		}
	}

    renderVes () {
        return (
        <></>
        );
    }

    createOneCircle() {
            return (
            <svg width="50" height="50">
                <circle width="100%" height="100%" stroke-width="3" color="black" stroke="black" fill="#E8E8E8"/>
            </svg>
            );
        }

    createOneBox(xPos, policyId, loopElementModelId , name, title, policyType) {
        return (
        <svg width={this.boxWidth} height={this.boxHeight} x={xPos} title="test">
            <g policyId={policyId} loopElementModelId={loopElementModelId} policyType={policyType}>
                <rect width="100%" height="100%" stroke-width="3" color="black" stroke="black" fill="#E8E8E8"/>
                <text x="50%" y="10%" color="white" fill="white" dominant-baseline="middle" text-anchor="middle" textLength="40%" lengthAdjust="spacingAndGlyphs">{title}</text>
                <text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" textLength="50%" lengthAdjust="spacingAndGlyphs" >{name}</text>
                <text x="50%" y="70%" text-anchor="middle" dominant-baseline="middle" textLength="100%" lengthAdjust="spacingAndGlyphs" >{policyId}</text>
            </g>
        </svg>
        );
    }

    createAllPolicyBoxes() {
        const allBoxes = [];
        var xPos = 0;
        for (var policy in this.state.loopCache.getMicroServicePolicies()) {
            var loopElementModelName =  this.state.loopCache.getMicroServicePolicies()[policy]['loopElementModel'];
            if (loopElementModelName !== null) {
                loopElementModelName = loopElementModelName['name'];
            }
            allBoxes.push(this.createOneBox(xPos,
                this.state.loopCache.getMicroServicePolicies()[policy]['name'],
                loopElementModelName,
                this.state.loopCache.getMicroServicePolicies()[policy]['policyModel']['policyAcronym'],
                'µS',
                'MICRO-SERVICE-POLICY'))
            xPos+=(this.boxWidth+30);
        }

        for (var policy in this.state.loopCache.getOperationalPolicies()) {
            var loopElementModelName =  this.state.loopCache.getMicroServicePolicies()[policy]['loopElementModel'];
            if (loopElementModelName !== null) {
                loopElementModelName = loopElementModelName['name'];
            }
            allBoxes.push(this.createOneBox(xPos,
                this.state.loopCache.getOperationalPolicies()[policy]['name'],
                loopElementModelName,
                this.state.loopCache.getOperationalPolicies()[policy]['policyModel']['policyAcronym'],
                'operational',
                "OPERATIONAL-POLICY"))
            xPos+=(this.boxWidth+30);
        }
        return allBoxes;
    }

    render() {
        return (
            <div onClick={this.handleSvgClick}>
              <svg>
                {this.createOneCircle()}
                {this.createAllPolicyBoxes()}

              </svg>
            </div>
        );
    }

 /* render() {
    return (
    <div onClick={this.handleSvgClick}>
      <svg>
        <defs>
          <g id="box" >
              <RectStyled x="0" y="0" width="120" height="75"/>
          </g>
        </defs>
        <use xlinkHref="#box" x="50" y="50" data-element-id="testos"/>
        <use xlinkHref="#box" x="300" y="50" data-element-id="testos2" />
      </svg>
      </div>
    );
  }*/
}

export default withRouter(Testos);
