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
package no.priv.bang.ukelonn.api;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class AccountServletTest extends ServletTestBase {

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
        // Create the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/account");
        when(request.getServletPath()).thenReturn("/api/account");
        when(request.getPathInfo()).thenReturn("/jad");
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the object to be tested
        ApiServletBase servlet = new AccountServlet();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ApiServletBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
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
    public void testGetAccountOtherUsername() throws Exception {
        // Create the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/account");
        when(request.getServletPath()).thenReturn("/api/account");
        when(request.getPathInfo()).thenReturn("/jod");
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        ApiServletBase servlet = new AccountServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    /**
     * Test that verifies that an admin user can access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    public void testGetAccountWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/account");
        when(request.getServletPath()).thenReturn("/api/account");
        when(request.getPathInfo()).thenReturn("/jad");
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        ApiServletBase servlet = new AccountServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ApiServletBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertEquals(673.0, result.getBalance(), 0.0);
    }

    @Test
    public void testGetAccountNoUsername() throws Exception {
        // Create the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/account");
        when(request.getServletPath()).thenReturn("/api/account");
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the object to be tested
        ApiServletBase servlet = new AccountServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }

}
