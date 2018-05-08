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
 */

package org.onap.clamp.clds.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.springframework.stereotype.Component;

/**
 * User service used for authorization verification at the login page. Do not
 * remove this class.
 */
@Component
@Path("/user")
@Produces({
        MediaType.TEXT_PLAIN
})
public class UserService {
    @Context
    private SecurityContext           securityContext;

    /**
     * REST service that returns the username.
     *
     * @param userName
     * @return the user name
     */
    @GET
    @Path("/getUser")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser() {
        UserNameHandler    userNameHandler = new DefaultUserNameHandler();
        String userName = userNameHandler.retrieveUserName(securityContext);
        return userName;
    }
}