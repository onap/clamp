/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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


//When element is first created it should have a red box because it hasn't been edited
function newElementProcessor(id) {
  if ($('g[data-element-id="' + id + '"]').length > 0) {

    var _idNode = $('g[data-element-id="' + id + '"]')
    _idNode.children("rect").each(function() {
      if ($(this).attr('class') === 'djs-outline') {
        $(this).attr('class', "djs-outline-no-property-saved")
        $(this).attr('fill', 'red')
      }
    });

  }
}

//Typically used when opening a new model/template
function reloadDefaultVariables(isTemp) {
  isTemplate = isTemp;

}
