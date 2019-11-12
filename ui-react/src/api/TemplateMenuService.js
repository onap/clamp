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

export default class TemplateMenuService {
  static getToscaPolicyModels() {
    return fetch('restservices/clds/v2/loop/tosca/models', { method: 'GET', credentials: 'same-origin' })
      .then(function (response) {
        console.debug("getToscaPolicyModels response received: ", response.status);
        if (response.ok) {
          return response.json();
        } else {
          console.error("getToscaPolicyModels query failed");
          return {};
        }
      })
      .catch(function (error) {
        console.error("getToscaPolicyModels error received", error);
        return {};
      });
  }

  static getBlueprintMicroServiceTemplates() {
    return fetch('restservices/clds/v2/loop/templates', { method: 'GET', credentials: 'same-origin', })
      .then(function (response) {
        console.debug("getBlueprintMicroServiceTemplates response received: ", response.status);
        if (response.ok) {
          return response.json();
        } else {
          console.error("getBlueprintMicroServiceTemplates query failed");
          return {};
        }
      })
      .catch(function (error) {
        console.error("getBlueprintMicroServiceTemplates error received", error);
        return {};
      });
  }

  static getDictionary() {
    return fetch('restservices/clds/v2/loop/dictionary/', { method: 'GET', credentials: 'same-origin', })
      .then(function (response) {
        console.debug("getDictionary response received: ", response.status);
        if (response.ok) {
          return response.json();
        } else {
          console.error("getDictionary query failed");
          return {};
        }
      })
      .catch(function (error) {
        console.error("getDictionary error received", error);
        return {};
      });
  }

  static getDictionaryItems(dictionaryName) {
    return fetch('restservices/clds/v2/loop/dictionary/' + dictionaryName + '/items', {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
      },
      credentials: 'same-origin',
    })
      .then(function (response) {
        console.debug("getDictionaryItems response received: ", response.status);
        if (response.ok) {
          return response.json();
        } else {
          console.error("getDictionaryItems query failed");
          return {};
        }
      })
      .catch(function (error) {
        console.error("getDictionaryItems error received", error);
        return {};
      });
  }

  static uploadToscaModal(toscaModelName, jsonData) {
    var svcRequest = {toscaModelName: toscaModelName, toscaModelRevisions: [{toscaModelYaml: jsonData}]};
    return fetch('/restservices/clds/v2/loop/tosca/models/' + toscaModelName, {
      method: 'PUT',
      credentials: 'same-origin',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(svcRequest)
    })
      .then(function (response) {
        console.debug("uploadToscaModal response received: ", response.status);
        if (response.ok) {
          var message = {status: response.status, message: 'Tosca Model successfully uploaded'};
          return message;
        } else {
        	console.error("uploadToscaModel failed");
			    return response.text();
        }
      })
      .catch(function (error) {
        console.error("uploadToscaModal error received", error);
        return "";
      });
  }

  static uploadBPTempl(templateName, jsonData) {
    var svcRequest = {templateName: templateName, templateYaml: jsonData};
    return fetch('/restservices/clds/v2/loop/createLoopTemplate/' + templateName, {
      method: 'PUT',
      credentials: 'same-origin',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(svcRequest)
    })
      .then(function (response) {
        console.debug("uploadBPTempl response received: ", response.status);
        if (response.ok) {
          var message = {status: response.status, message: 'BP template successfully uploaded'};
          return message;
        } else {
          console.error("uploadBPTempl query failed");
          return response.text();
        }
      })
      .catch(function (error) {
        console.error("uploadBPTempl error received", error);
        return "";
      });
  }

  static insDictionary(jsonData) {
    return fetch('/restservices/clds/v2/loop/dictionary', {
      method: 'PUT',
      credentials: 'same-origin',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(jsonData)
    })
      .then(function (response) {
        console.debug("insDictionary response received: ", response.status);
        if (response.ok) {
          return response.status;
        } else {
          var errorMessage = response.status;
          console.error("insDictionary query failed", response.status);
          return errorMessage;
        }
      })
      .catch(function (error) {
        console.error("insDictionary error received", error);
        return "";
      });
  }
}
