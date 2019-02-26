/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 Nokia Intellectual Property. All rights
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.clamp.authorization.AuthorizationController;
import org.onap.clamp.clds.model.CldsInfo;
import org.onap.clamp.clds.util.PrincipalUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;


public class CldsInfoProviderTest {
    private Authentication authentication;
    private List<GrantedAuthority> authList = new LinkedList<GrantedAuthority>();
    private static final String TEST_USERNAME = "admin";
    /**
     * Setup the variable before the tests execution.
     *
     * @throws IOException
     *         In case of issues when opening the files
     */
    @Before
    public void setupBefore() throws IOException {
        authList.add(new SimpleGrantedAuthority("null|null|read"));
        authList.add(new SimpleGrantedAuthority("null|null|update"));
        authList.add(new SimpleGrantedAuthority("null|null|read"));
        authList.add(new SimpleGrantedAuthority("null|null|update"));
        authList.add(new SimpleGrantedAuthority("null|null|*"));
        authList.add(new SimpleGrantedAuthority("null|null|read"));
        authList.add(new SimpleGrantedAuthority("null|null|update"));
        authentication = new UsernamePasswordAuthenticationToken(new User("admin", "", authList), "", authList);


    }
    @Test
    public void shouldProvideCldsInfoFromContext() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        PrincipalUtils util = new PrincipalUtils();
        util.setSecurityContext(securityContext);
        AuthorizationController auth = mock(AuthorizationController.class);
        when(auth.isUserPermittedNoException(any())).thenReturn(true);

        CldsInfoProvider cldsInfoProvider = new CldsInfoProvider();
        cldsInfoProvider.setAuthorizationDelegate(auth);
        // when
        CldsInfo cldsInfo = cldsInfoProvider.getCldsInfo();

        // then
        assertThat(cldsInfo.getUserName()).isEqualTo(TEST_USERNAME);
        assertThat(cldsInfo.isPermissionReadCl()).isTrue();
        assertThat(cldsInfo.isPermissionReadTemplate()).isTrue();
        assertThat(cldsInfo.isPermissionUpdateCl()).isTrue();
        assertThat(cldsInfo.isPermissionUpdateTemplate()).isTrue();
    }
}