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
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;
import no.priv.bang.ukelonn.mocks.MockLogService;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

public class LoginServletTest extends ServletTestBase {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "1ad");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        createSubjectAndBindItToThread(request, response);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void testAdminLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("admin", "admin");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        createSubjectAndBindItToThread(request, response);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    @Ignore("Gets wrong password exception instead of unknown user exception, don't know why")
    @Test
    public void testLoginUnknownUser() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("unknown", "unknown");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        createSubjectAndBindItToThread(request, response);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Unknown account", result.getErrorMessage());
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "wrong");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        createSubjectAndBindItToThread(request, response);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Wrong password", result.getErrorMessage());
    }

    @Test
    public void testLoginWrongJson() throws Exception {
        // Set up the request
        HttpServletRequest request = buildRequestFromStringBody("xxxyzzy");

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        createSubjectAndBindItToThread(request, response);
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
        assertEquals("text/plain", response.getContentType());
    }

    /**
     * Shiro fails because there is no WebSubject bound to the thread.
     * @throws Exception
     */
    @Test
    public void testLoginInternalServerError() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "1ad");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will cause a NullPointerException when
        // trying to write the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        when(response.getWriter()).thenReturn(null);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Create the servlet and do the login
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }

    /**
     * Verify that a GET to the LoginServlet will return the current state
     * when a user is logged in
     *
     * Used to initialize webapp if the webapp is reloaded.
     *
     * @throws Exception
     */
    @Test
    public void testGetLoginStateWhenLoggedIn() throws Exception {
        // Set up the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");
        when(request.getSession()).thenReturn(session);

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        // Create the servlet and check the login state with HTTP GET
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = LoginServlet.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    /**
     * Verify that a GET to the LoginServlet will return the current state
     * when no user is logged in
     *
     * Used to initialize webapp if the webapp is reloaded.
     *
     * @throws Exception
     */
    @Test
    public void testGetLoginStateWhenNotLoggedIn() throws Exception {
        // Set up the request
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");
        when(request.getSession()).thenReturn(session);

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        // Create the servlet and check the login state with HTTP GET
        LoginServlet servlet = new LoginServlet();
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
     * Verify that a GET to the LoginServlet will return status 500
     * when Shiro is failing.
     *
     * Used to initialize webapp if the webapp is reloaded.
     *
     * @throws Exception
     */
    @Test
    public void testGetLoginWithInternalServerError() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        when(response.getWriter()).thenReturn(null);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and check the login state with HTTP GET
        LoginServlet servlet = new LoginServlet();
        servlet.setLogservice(logservice);
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }
}
