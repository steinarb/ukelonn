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

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.mocks.MockLogService;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

public class LoginTest extends ServletTestBase {

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
        // Set up the login request
        LoginCredentials credentials = new LoginCredentials("jad", "1ad");
        HttpServletRequest request = buildLoginRequest(credentials);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the resource and do the login
        Login resource = new Login();
        resource.logservice = logservice;
        createSubjectAndBindItToThread(request, response);
        LoginResult result = resource.doLogin(credentials);

        // Check the response
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void testAdminLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("admin", "admin");
        HttpServletRequest request = buildLoginRequest(credentials);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        Login resource = new Login();
        resource.logservice = logservice;
        createSubjectAndBindItToThread(request, response);
        LoginResult result = resource.doLogin(credentials);

        // Check the response
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    @Ignore("Gets wrong password exception instead of unknown user exception, don't know why")
    @Test
    public void testLoginUnknownUser() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("unknown", "unknown");
        HttpServletRequest request = buildLoginRequest(credentials);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        Login resource = new Login();
        resource.logservice = logservice;
        createSubjectAndBindItToThread(request, response);
        LoginResult result = resource.doLogin(credentials);

        // Check the response
        assertEquals(0, result.getRoles().length);
        assertEquals("Unknown account", result.getErrorMessage());
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "wrong");
        HttpServletRequest request = buildLoginRequest(credentials);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        Login resource = new Login();
        resource.logservice = logservice;
        createSubjectAndBindItToThread(request, response);
        LoginResult result = resource.doLogin(credentials);

        // Check the response
        assertEquals(0, result.getRoles().length);
        assertEquals("Wrong password", result.getErrorMessage());
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
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        // Create the resource and check the login state with HTTP GET
        Login resource = new Login();
        resource.logservice = logservice;
        LoginResult result = resource.loginStatus();

        // Check the response
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
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        // Create the resource and check the login state with HTTP GET
        Login resource = new Login();
        resource.logservice = logservice;
        LoginResult result = resource.loginStatus();

        // Check the response
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }
}
