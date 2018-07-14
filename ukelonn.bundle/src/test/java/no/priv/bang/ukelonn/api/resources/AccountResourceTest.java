/*
 * Copyright 2018 Steinar Bang
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class AccountResourceTest extends ServletTestBase {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetAccount() throws Exception {
        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create the object to be tested
        AccountResource resource = new AccountResource();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        Account result = resource.getAccount("jad");

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
    @Test(expected=ForbiddenException.class)
    public void testGetAccountOtherUsername() throws Exception {
        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        AccountResource resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test with a different username
        resource.getAccount("jod");

        // Expect a ForbiddenException to be thrown
    }

    /**
     * Test that verifies that an admin user can access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    public void testGetAccountWhenLoggedInAsAdministrator() throws Exception {
        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        AccountResource resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        Account result = resource.getAccount("jad");

        // Check the response
        assertEquals("jad", result.getUsername());
        assertEquals(673.0, result.getBalance(), 0.0);
    }

    @Test(expected=BadRequestException.class)
    public void testGetAccountNoUsername() throws Exception {
        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create the object to be tested
        AccountResource resource = new AccountResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        resource.getAccount(null);

        // Check the response
        assertEquals(500, response.getStatus());
    }

}
