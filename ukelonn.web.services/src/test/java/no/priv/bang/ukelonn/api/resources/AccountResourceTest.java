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

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.ServletTestBase;

class AccountResourceTest extends ServletTestBase {

    AccountResourceTest() {
        super("/ukelonn", "/api");
    }

    @Test
    void testGetAccount() throws Exception {
        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Create the object to be tested
        var resource = new AccountResource();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());
        resource.ukelonn = ukelonn;

        // Run the method under test
        var result = resource.getAccount("jad");

        // Check the result
        assertEquals("jad", result.getUsername());
        assertEquals(673.0, result.getBalance(), 0.0);
    }

    /**
     * Test that verifies that a regular user can't access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testGetAccountOtherUsername() throws Exception {
        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        var resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test with a different username
        assertThrows(ForbiddenException.class, () -> {
                resource.getAccount("jod");
            });
    }

    /**
     * Test that verifies that an admin user can access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testGetAccountWhenLoggedInAsAdministrator() throws Exception {
        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        var resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());
        resource.ukelonn = ukelonn;

        // Run the method under test
        var result = resource.getAccount("jad");

        // Check the response
        assertEquals("jad", result.getUsername());
        assertEquals(673.0, result.getBalance(), 0.0);
    }

    @Test
    void testGetAccountNoUsername() throws Exception {
        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create the object to be tested
        var resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        assertThrows(BadRequestException.class, () -> {
                resource.getAccount(null);
            });
    }

    @Test
    void testGetAccountUsernameNotPresentInDatabase() throws Exception {
        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        var resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenThrow(UkelonnException.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        assertThrows(InternalServerErrorException.class, () -> {
                resource.getAccount("on");
            });
    }

    @Test
    void testGetAccountWhenSubjectHasNullPrincipal() {
        createSubjectWithNullPrincipalAndBindItToThread();

        // Create the object to be tested
        var resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        assertThrows(InternalServerErrorException.class, () -> {
                resource.getAccount("on");
            });
    }
}
