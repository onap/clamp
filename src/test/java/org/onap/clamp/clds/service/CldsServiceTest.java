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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedList;

import javax.ws.rs.NotAuthorizedException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "clamp-default,clamp-default-user,clamp-sdc-controller")
public class CldsServiceTest {

	@Autowired
    private CldsService cldsService;

    private SecurityContext securityContext = mock(SecurityContext.class);
    private Authentication auth =  mock(Authentication.class);
    private UserDetails userDetails =  mock(UserDetails.class);
    private Collection<GrantedAuthority> authorityList = new LinkedList<GrantedAuthority>();

    private void initTest() {
        when(userDetails.getUsername()).thenReturn("testName");
        when(auth.getPrincipal()).thenReturn(userDetails);
    }

    @Test(expected = NotAuthorizedException.class)
    public void isAuthorizedForVfTestNotAuthorized1() throws Exception {
        initTest();
        when(securityContext.getAuthentication()).thenReturn(auth);
        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test(expected = NotAuthorizedException.class)
    public void isAuthorizedForVfTestNotAuthorized2() throws Exception {
        initTest();
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|prod|*"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);
        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test(expected = NotAuthorizedException.class)
    public void isAuthorizedForVfTestNotAuthorized3() throws Exception {
        initTest();
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|dev|testId2"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);
        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test(expected = NotAuthorizedException.class)
    public void isAuthorizedForVfTestNotAuthorized4() throws Exception {
        initTest();
        when(securityContext.getAuthentication()).thenReturn(null);
        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test
    public void isAuthorizedForVfTest1() throws Exception {
        initTest();
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|*|*"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);

        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test
    public void isAuthorizedForVfTest2() throws Exception {
        initTest();
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|dev|*"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);

        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test
    public void isAuthorizedForVfTest3() throws Exception {
        initTest();	
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|dev|testId"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);

        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test
    public void isAuthorizedForVfTest4() throws Exception {
        initTest();	
        authorityList.add(new SimpleGrantedAuthority("permission-type-filter-vf|*|testId"));
        when((Collection<GrantedAuthority>)auth.getAuthorities()).thenReturn(authorityList);
        when(securityContext.getAuthentication()).thenReturn(auth);

        cldsService.setSecurityContext(securityContext);
        boolean res = cldsService.isAuthorizedForVf("testId");
        assertThat(res).isTrue();
    }

    @Test
    public void getUserIdTest() throws Exception {
        initTest();	
        when(securityContext.getAuthentication()).thenReturn(auth);

        cldsService.setSecurityContext(securityContext);
        assertThat(cldsService.getUserId()).isEqualTo("testName");
    }
}