/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
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
 * Modifications copyright (c) 2018 Nokia
 * ===================================================================
 *
 */

package org.onap.clamp.clds.service;

import org.onap.clamp.authorization.AuthorizationController;
import org.onap.clamp.clds.model.CldsInfo;
import org.onap.clamp.clds.util.ClampVersioning;
import org.onap.clamp.clds.util.PrincipalUtils;
import org.springframework.beans.factory.annotation.Value;

public class CldsInfoProvider {

    final SecureServicePermission permissionReadCl;
    final SecureServicePermission permissionUpdateCl;
    final SecureServicePermission permissionReadTemplate;
    final SecureServicePermission permissionUpdateTemplate;
    final SecureServicePermission permissionReadTosca;
    final SecureServicePermission permissionUpdateTosca;
    private AuthorizationController auth = new AuthorizationController();

    @Value("${clamp.config.security.permission.type.cl:permission-type-cl}")
    private String cldsPersmissionTypeCl;
    @Value("${clamp.config.security.permission.type.template:permission-type-template}")
    private String cldsPermissionTypeTemplate;
    @Value("${clamp.config.security.permission.type.tosca:permission-type-tosca}")
    private String cldsPermissionTypeTosca;
    @Value("${clamp.config.security.permission.instance:dev}")
    private String cldsPermissionInstance;


    public CldsInfoProvider() {
        permissionReadCl = SecureServicePermission.create(cldsPersmissionTypeCl, cldsPermissionInstance, "read");
        permissionUpdateCl = SecureServicePermission.create(cldsPersmissionTypeCl, cldsPermissionInstance, "update");
        permissionReadTemplate = SecureServicePermission.create(cldsPermissionTypeTemplate, cldsPermissionInstance,
            "read");
        permissionUpdateTemplate = SecureServicePermission.create(cldsPermissionTypeTemplate, cldsPermissionInstance,
            "update");
        permissionReadTosca = SecureServicePermission.create(cldsPermissionTypeTosca, cldsPermissionInstance, "read");
        permissionUpdateTosca = SecureServicePermission.create(cldsPermissionTypeTosca, cldsPermissionInstance,"update");
    }

    public CldsInfo getCldsInfo() {
        CldsInfo cldsInfo = new CldsInfo();
        cldsInfo.setUserName(PrincipalUtils.getUserName());
        cldsInfo.setCldsVersion(ClampVersioning.getCldsVersionFromProps());
        cldsInfo.setPermissionReadCl(auth.isUserPermittedNoException(permissionReadCl));
        cldsInfo.setPermissionUpdateCl(auth.isUserPermittedNoException(permissionUpdateCl));
        cldsInfo.setPermissionReadTemplate(auth.isUserPermittedNoException(permissionReadTemplate));
        cldsInfo.setPermissionUpdateTemplate(auth.isUserPermittedNoException(permissionUpdateTemplate));
        cldsInfo.setPermissionReadTosca(auth.isUserPermittedNoException(permissionReadTosca));
        cldsInfo.setPermissionUpdateTosca(auth.isUserPermittedNoException(permissionUpdateTosca));

        return cldsInfo;
    }
    // created for testing purpose
    public void setAuthorizationDelegate (AuthorizationController auth) {
        this.auth = auth;
    }
}
