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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;
import no.priv.bang.ukelonn.mocks.MockLogService;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

public class LogoutServletTest extends ServletTestBase {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testLogoutOk() throws Exception {
        // Set up the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");
        when(request.getSession()).thenReturn(session);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        
        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        // Create the servlet and do the logout
        LogoutServlet servlet = new LogoutServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    /**
     * Verify that logging out a not-logged in shiro, is harmless.
     *
     * @throws Exception
     */
    @Test
    public void testLogoutNotLoggedIn() throws Exception {
        // Set up the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");
        when(request.getSession()).thenReturn(session);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        
        // Set up shiro 
        createSubjectAndBindItToThread(request, response);

        // Create the servlet and do the logout
        LogoutServlet servlet = new LogoutServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    /**
     * Verify that all exceptions results in a 500 error return
     *
     * @throws Exception
     */
    @Test
    public void testLogoutGetException() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        when(response.getWriter()).thenReturn(null);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the logout
        LogoutServlet servlet = new LogoutServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }
}
