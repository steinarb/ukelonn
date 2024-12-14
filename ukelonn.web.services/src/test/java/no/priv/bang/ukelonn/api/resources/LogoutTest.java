/*
 * Copyright 2018-2024 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.api.ServletTestBase;

class LogoutTest extends ServletTestBase {

    LogoutTest() {
        super("/ukelonn", "/api");
    }

    @Test
    void testLogoutOk() {
        // Set up the request
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        var logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        loginUser(request, response, "jad", "1ad");

        // Create the resource and do the logout
        var resource = new Logout();
        resource.logservice = logservice;
        var result = resource.doLogout();

        // Check the response
        assertThat(result.roles()).isEmpty();
        assertEquals("", result.errorMessage());
    }

    /**
     * Verify that logging out a not-logged in shiro, is harmless.
     *
     * @throws Exception
     */
    @Test
    void testLogoutNotLoggedIn() {
        // Set up the request and response used to do the login
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        var logservice = new MockLogService();

        // Set up shiro
        createSubjectAndBindItToThread(request, response);

        // Create the resource and do the logout
        var resource = new Logout();
        resource.logservice = logservice;
        var result = resource.doLogout();

        // Check the response
        assertThat(result.roles()).isEmpty();
        assertEquals("", result.errorMessage());
    }
}
