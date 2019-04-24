/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Samsung. All rights reserved.
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

package org.onap.clamp.clds.it;

import com.google.common.collect.ImmutableMap;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.onap.clamp.authorization.AuthorizationController.PERM_PREFIX;
import static org.onap.clamp.clds.config.ClampProperties.CONFIG_PREFIX;

public class MockPermission {

    private final String securityPrefix = CONFIG_PREFIX + PERM_PREFIX;
    private final Map<String, Map> permission;
    private List<GrantedAuthority> authList;

    public MockPermission(){
        String[] ALL_ACTION = new String[]{"*"};
        String[] READ_UPDATE_ACTION = new String[]{"read", "update"};

        this.authList = new LinkedList<>();
        this.permission = ImmutableMap.of(
                "permission-type-cl", ImmutableMap.of(
                        "dev", ALL_ACTION
                ),
                "permission-type-cl-event", ImmutableMap.of(
                        "dev", ALL_ACTION,
                        "test", READ_UPDATE_ACTION
                ),
                "permission-type-cl-manage", ImmutableMap.of(
                        "dev", ALL_ACTION,
                        "test", READ_UPDATE_ACTION
                ),
                "permission-type-filter-vf", ImmutableMap.of(
                        "dev", ALL_ACTION,
                        "test", READ_UPDATE_ACTION
                ),
                "permission-type-template", ImmutableMap.of(
                        "dev", ALL_ACTION,
                        "test", READ_UPDATE_ACTION
                )
        );
        this.createAuthList();
    }

    public MockPermission(Map<String, Map> permission) {
        this.authList = new LinkedList<>();
        this.permission = permission;
        this.createAuthList();
    }

    public Map<String, Map> getPermission() {
        return permission;
    }

    private void createAuthList(){
        permission.forEach((type, instanceMap) -> {
            instanceMap.forEach((instance, actionList) -> {
                for(String action: (String[]) actionList) {
                    authList.add(new SimpleGrantedAuthority(type + "|" + instance + "|" + action));
                }
            });
        });
    }

    List<GrantedAuthority> getAuthList(){
        return authList;
    }

    void setupMockEnv(MockEnvironment env){
        permission.forEach((type, instanceMap) -> {
            env.withProperty(securityPrefix + type, type);
        });
    }

    void doActionOnAllPermissions(PermissionAction action){
        permission.forEach((type, instanceMap) -> {
            instanceMap.forEach((instance, actionList) -> {
                for(String actionName: (String[]) actionList) {
                    action.doAction(type, (String) instance, actionName);
                }
            });
        });
    }

    @FunctionalInterface
    public interface PermissionAction {
        void doAction(String type, String instance, String action);
    }
}
