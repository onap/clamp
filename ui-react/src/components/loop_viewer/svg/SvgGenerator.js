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

const DivStyled = styled.div`
	overflow-x: scroll;
    text-align: center;
`

const emptySvg = (<svg> <text x="20" y="40">No LOOP (SVG)</text> </svg>);

class SvgGenerator extends React.Component {
    boxWidth = 200;
    boxHeight = 100;
    boxSpace = 50;

	state = {
		loopCache: new LoopCache({}),
		clickable: false,
	}

	constructor(props) {
		super(props);
		this.state.loopCache = props.loopCache;
		this.state.clickable = props.clickable;
		this.handleSvgClick = this.handleSvgClick.bind(this);
		this.createOneBox = this.createOneBox.bind(this);
		this.createAllPolicyBoxes = this.createAllPolicyBoxes.bind(this);
		this.renderOneArrow = this.renderOneArrow.bind(this);
	}

	shouldComponentUpdate(nextProps, nextState) {
		return this.state.loopCache !== nextState.loopCache;
	}

	componentWillReceiveProps(newProps) {
		if (this.state.loopCache !== newProps.loopCache) {
			this.setState({
				loopCache: newProps.loopCache,
			});
		}
	}

	handleSvgClick(event) {
	    if (this.state.clickable) {
            console.debug("svg click event received");
            var elementName = event.target.parentNode.getAttribute('policyId');
            console.info("SVG element clicked", elementName);
            if (elementName !== null) {
                this.props.history.push("/policyModal/"+event.target.parentNode.getAttribute('policyType')+"/"+elementName);
            }
		}
	}

    renderVes (xPos) {
        return this.createOneBox(xPos,null,null,'VES','VES',null);
    }

    renderOneArrow(xPos) {
        return (
         <svg width={this.boxSpace} height={this.boxHeight} x={xPos}>
           <defs>
            		<marker viewBox="0 0 20 20" markerWidth="20" markerHeight="20" orient="auto" refX="8.5" refY="5" id="arrow">
            			<path d="m 1 5 l 0 -3 l 7 3 l -7 3 z"
            				stroke-width= "1" stroke-linecap= "butt" stroke-dasharray= "10000, 1"
            				fill="#000000" stroke="#000000" />
            		</marker>
           </defs>
           <line x1="0" y1="50%" x2="100%" y2="50%" stroke-width="2" color="black" stroke="black" marker-end="url(#arrow)"/>
         </svg>
        );
    }

    createBeginCircle(xPos, text) {
            return (
            <svg width={this.boxWidth} height={this.boxHeight} x={xPos}>
                <circle cx={this.boxWidth-30} cy="50%" r="30" stroke-width="1" color="black" stroke="black" fill="#27ae60"/>
                <text x={this.boxWidth-30} y="50%" text-anchor="middle" dominant-baseline="middle" textLength="20%" lengthAdjust="spacingAndGlyphs" >{text}</text>
            </svg>
            );
    }

    createEndCircle(xPos, text) {
            return (
            <svg width={this.boxWidth} height={this.boxHeight} x={xPos}>
                <circle cx={30} cy="50%" r="30" stroke-width="2" color="black" stroke="black" fill="#27ae60"/>
                <text x={30} y="50%" text-anchor="middle" dominant-baseline="middle" textLength="20%" lengthAdjust="spacingAndGlyphs" >{text}</text>
            </svg>
            );
    }

    createOneBox(xPos, policyId, loopElementModelId , name, title, policyType) {
        return (
        <svg width={this.boxWidth} height={this.boxHeight} x={xPos} title="test">
            <g policyId={policyId} loopElementModelId={loopElementModelId} policyType={policyType}>
                <rect width="100%" height="100%" stroke-width="2" color="black" stroke="black" fill="#1abc9c"/>
                <text x="50%" y="15%" color="white" fill="white" dominant-baseline="middle" text-anchor="middle" textLength="30%" lengthAdjust="spacingAndGlyphs">{title}</text>
                <text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" textLength="40%" lengthAdjust="spacingAndGlyphs" >{name}</text>
                <text x="50%" y="80%" text-anchor="middle" dominant-baseline="middle" textLength="110%" lengthAdjust="spacingAndGlyphs" >{policyId}</text>
            </g>
        </svg>
        );
    }

    createAllPolicyBoxes() {
        if (this.state.loopCache.getLoopName() === undefined) {
            return emptySvg;
        }
        const allElements = [];
        var xPos = 0;

        allElements.push(this.createBeginCircle(xPos,"Start"))
        xPos+=(this.boxWidth+this.boxSpace);

        allElements.push(this.renderOneArrow(xPos-this.boxSpace));

        allElements.push(this.renderVes(xPos));
        xPos+=(this.boxWidth+this.boxSpace);

        allElements.push(this.renderOneArrow(xPos-this.boxSpace));

        for (var msPolicy in this.state.loopCache.getMicroServicePolicies()) {
            var loopElementModelName =  this.state.loopCache.getMicroServicePolicies()[msPolicy]['loopElementModel'];
            if (loopElementModelName !== undefined) {
                loopElementModelName = loopElementModelName['name'];
            }
            allElements.push(this.createOneBox(xPos,
                this.state.loopCache.getMicroServicePolicies()[msPolicy]['name'],
                loopElementModelName,
                this.state.loopCache.getMicroServicePolicies()[msPolicy]['policyModel']['policyAcronym'],
                'µS',
                'MICRO-SERVICE-POLICY'))
            xPos+=(this.boxWidth+this.boxSpace);
            allElements.push(this.renderOneArrow(xPos-this.boxSpace));
        }

        for (var opPolicy in this.state.loopCache.getOperationalPolicies()) {
            loopElementModelName =  this.state.loopCache.getOperationalPolicies()[opPolicy]['loopElementModel'];
            if (loopElementModelName !== undefined) {
                loopElementModelName = loopElementModelName['name'];
            }
            allElements.push(this.createOneBox(xPos,
                this.state.loopCache.getOperationalPolicies()[opPolicy]['name'],
                loopElementModelName,
                this.state.loopCache.getOperationalPolicies()[opPolicy]['policyModel']['policyAcronym'],
                'operational',
                "OPERATIONAL-POLICY"))
            xPos+=(this.boxWidth+this.boxSpace);
            allElements.push(this.renderOneArrow(xPos-this.boxSpace));
        }

        allElements.push(this.createEndCircle(xPos, "End"))
        xPos+=(this.boxWidth+this.boxSpace);

        return allElements;
    }

    render() {
        return (
            <DivStyled onClick={this.handleSvgClick}>
            <svg height="100%" width="100%">
                {this.createAllPolicyBoxes()}
            </svg>
            </DivStyled>
        );
    }
}

export default withRouter(SvgGenerator);
