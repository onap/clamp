/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
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

package org.onap.clamp.clds.service;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.util.Date;
import javax.ws.rs.NotAuthorizedException;

import org.onap.clamp.clds.util.LoggingUtils;
import org.onap.clamp.clds.util.PrincipalUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base/abstract Service class. Implements shared security methods.
 */
public abstract class SecureServiceBase {
    protected static final EELFLogger logger          = EELFManager.getInstance().getLogger(SecureServiceBase.class);
    protected static final EELFLogger auditLogger     = EELFManager.getInstance().getAuditLogger();
    protected static final EELFLogger securityLogger  = EELFManager.getInstance().getSecurityLogger();

    // By default we'll set it to a default handler
    private static UserNameHandler    userNameHandler = new DefaultUserNameHandler();


    private SecurityContext           securityContext = SecurityContextHolder.getContext();

    /**
     * Check if user is authorized for the given the permission. Allow matches
     * if user has a permission with an "*" in permission instance or permission
     * action even if the permission to check has a specific value in those
     * fields. For example: if the user has this permission: app-perm-type|*|*
     * it will be authorized if the inPermission to check is:
     * app-perm-type|dev|read
     *
     * @param inPermission
     *            The permission to validate
     * @return A boolean to indicate if the user has the permission to do
     *         execute the inPermission
     * @throws NotAuthorizedException
     *             In case of issues with the permission test, error is returned
     *             in this exception
     */
    public boolean isAuthorized(SecureServicePermission inPermission) throws NotAuthorizedException {
        String principalName = PrincipalUtils.getPrincipalName();
        Date startTime = new Date();
        LoggingUtils.setTargetContext("CLDS", "isAuthorized");
        LoggingUtils.setTimeContext(startTime, new Date());
        securityLogger.debug("checking if {} has permission: {}", principalName, inPermission);
        try {
            return isUserPermitted(inPermission);
        } catch (NotAuthorizedException nae) {
            String msg = principalName + " does not have permission: " + inPermission;
            LoggingUtils.setErrorContext("100", "Authorization Error");
            securityLogger.warn(msg);
            throw new NotAuthorizedException(msg);
        }
    }

    /**
     * Check if user is authorized for the given aaf permission. Allow matches
     * if user has a permission with an "*" in permission instance or permission
     * action even if the permission to check has a specific value in those
     * fields. For example: if the user has this permission: app-perm-type|*|*
     * it will be authorized if the inPermission to check is:
     * app-perm-type|dev|read
     *
     * @param inPermission
     *            The permission to validate
     * @return A boolean to indicate if the user has the permission to do
     *         execute the inPermission
     */
    public boolean isAuthorizedNoException(SecureServicePermission inPermission) {
        String principalName = PrincipalUtils.getPrincipalName();
        securityLogger.debug("checking if {} has permission: {}", principalName, inPermission);
        Date startTime = new Date();
        LoggingUtils.setTargetContext("CLDS", "isAuthorizedNoException");
        LoggingUtils.setTimeContext(startTime, new Date());
        try {
            return isUserPermitted(inPermission);
        } catch (NotAuthorizedException nae) {
            String msg = principalName + " does not have permission: " + inPermission;
            LoggingUtils.setErrorContext("100", "Authorization Error");
            securityLogger.warn(msg);
        }
        return false;
    }

    /**
     * This method can be used by the Application.class to set the
     * UserNameHandler that must be used in this class. The UserNameHandler
     * where to get the User name
     *
     * @param handler
     *            The Handler impl to use
     */
    public static final void setUserNameHandler(UserNameHandler handler) {
        if (handler != null) {
            userNameHandler = handler;
        }
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    private boolean isUserPermitted(SecureServicePermission inPermission) {
        String principalName = PrincipalUtils.getPrincipalName();
        boolean authorized = false;
        // check if the user has the permission key or the permission key with a
        // combination of  all instance and/or all action.
        if (hasRole(inPermission.getKey())) {
            securityLogger.info("{} authorized for permission: {}", principalName, inPermission.getKey());
            authorized = true;
            // the rest of these don't seem to be required - isUserInRole method
            // appears to take * as a wildcard
        } else if (hasRole(inPermission.getKeyAllInstance())) {
            securityLogger.info("{} authorized because user has permission with * for instance: {}", principalName, inPermission.getKey());
            authorized = true;
        } else if (hasRole(inPermission.getKeyAllInstanceAction())) {
            securityLogger.info("{} authorized because user has permission with * for instance and * for action: {}", principalName, inPermission.getKey());
            authorized = true;
        } else if (hasRole(inPermission.getKeyAllAction())) {
            securityLogger.info("{} authorized because user has permission with * for action: {}", principalName, inPermission.getKey());
            authorized = true;
        } else {
            throw new NotAuthorizedException("");
        }
        return authorized;
    }

    protected boolean hasRole(String role) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (role.equals(auth.getAuthority()))
                return true;
        }

        return false;
    }
}