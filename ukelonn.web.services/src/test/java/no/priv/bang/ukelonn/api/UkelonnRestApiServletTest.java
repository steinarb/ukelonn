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

import static no.priv.bang.ukelonn.backend.CommonDatabaseMethods.getAccountInfoFromDatabase;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;

/**
 * The tests in this test class mirrors the tests for the Jersey
 * resources.  The purpose of the tests in this test class is
 * to verify that the resources are found on the expected paths
 * and gets the expected HK2 injections and accept the
 * expected request data and returns the expected responses.
 *
 *  Sort of a lightweight integration test.
 *
 */
public class UkelonnRestApiServletTest extends ServletTestBase {

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

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());
        createSubjectAndBindItToThread(request, response);

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());
        createSubjectAndBindItToThread(request, response);

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());
        createSubjectAndBindItToThread(request, response);

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());
        createSubjectAndBindItToThread(request, response);

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());
        createSubjectAndBindItToThread(request, response);

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
        assertEquals("text/plain", response.getContentType());
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/login"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/login");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
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

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/login"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/login");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void testLogoutOk() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/logout"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/logout");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        loginUser(request, response, "jad", "1ad");

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/logout"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/logout");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create the response that will receive the login result
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up shiro
        createSubjectAndBindItToThread(request, response);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void testGetJobtypes() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/jobtypes"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/jobtypes");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> jobtypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        assertThat(jobtypes.size()).isGreaterThan(0);
    }

    @Test
    public void testGetAccounts() throws Exception {
        // Create the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/accounts"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/accounts");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Account> accounts = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<Account>>() {});
        assertEquals(2, accounts.size());
    }

    @Test
    public void testGetAccount() throws Exception {
        // Create the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/account/jad"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/account/jad");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        double expectedAccountBalance = getUkelonnServiceSingleton().getAccount("jad").getBalance();
        Account result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertEquals(expectedAccountBalance, result.getBalance(), 0.0);
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/account/jod"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/account/jod");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

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
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/account/jad"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/account/jad");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        double expectedAccountBalance = getUkelonnServiceSingleton().getAccount("jad").getBalance();
        Account result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertEquals(expectedAccountBalance, result.getBalance(), 0.0);
    }

    @Test
    public void testGetAccountNoUsername() throws Exception {
        // Create the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/account"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/account");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        // (Looks like Jersey enforces the pathinfo element so the response is 404 "Not Found"
        // rather than the expected 400 "Bad request" (that the resource would send if reached))
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetAccountUsernameNotPresentInDatabase() throws Exception {
        // Create the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/account/unknownuser"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/account/unknownuser");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        // (Looks like Jersey enforces the pathinfo element so the response is 404 "Not Found"
        // rather than the expected 400 "Bad request" (that the resource would send if reached))
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testRegisterJob() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    /**
     * Test that verifies that a regular user can't update the job list of
     * other users than the one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterJobOtherUsername() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jod");
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    /**
     * Test that verifies that an admin user register a job on the behalf
     * of a different user.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterJobtWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());


        Account result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    @Test
    public void testRegisterJobNoUsername() throws Exception {
        // Create the request
        Account account = new Account();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    @Test
    public void testRegisterJobUnparsablePostData() throws Exception {
        // Create the request
        HttpServletRequest request = buildRequestFromStringBody("this is not json");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
    }

    /**
     * To provoked the internal server error, the user isn't logged in.
     * This causes a NullPointerException in the user check.
     *
     * (In a production environment this request without a login,
     * will be stopped by Shiro)
     *
     * @throws Exception
     */
    @Test
    public void testRegisterJobInternalServerError() throws Exception {
        // Create the request
        Account account = new Account();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetJobs() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), "jad");
        String requestURL = String.format("http://localhost:8181/ukelonn/api/jobs/%d", account.getAccountId());
        String requestURI = String.format("/ukelonn/api/jobs/%d", account.getAccountId());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Transaction> jobs = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<Transaction>>() {});
        assertEquals(10, jobs.size());
    }

    @Test
    public void testDeleteJobs() throws Exception {
        try {
            // Set up the request
            Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), "jod");
            List<Transaction> jobs = getUkelonnServiceSingleton().getJobs(account.getAccountId());
            List<Integer> jobIds = Arrays.asList(jobs.get(0).getId(), jobs.get(1).getId());
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, jobIds);
            String accountWithJobIdsAsJson = ServletTestBase.mapper.writeValueAsString(accountWithJobIds);
            HttpServletRequest request = buildRequestFromStringBody(accountWithJobIdsAsJson);
            when(request.getMethod()).thenReturn("POST");
            String requestURL = "http://localhost:8181/ukelonn/api/admin/jobs/delete";
            String requestURI = "/ukelonn/api/admin/jobs/delete";
            when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
            when(request.getRequestURI()).thenReturn(requestURI);

            // Create a response object that will receive and hold the servlet output
            MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

            // Create the servlet that is to be tested
            UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

            // Create mock OSGi services to inject and inject it
            MockLogService logservice = new MockLogService();
            servlet.setLogservice(logservice);

            // Inject fake OSGi service UkelonnService
            servlet.setUkelonnService(getUkelonnServiceSingleton());

            // Activate the servlet DS component
            servlet.activate();

            // When the servlet is activated it will be plugged into the http whiteboard and configured
            ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
            servlet.init(config);

            // Call the method under test
            servlet.service(request, response);

            // Check the output
            assertEquals(200, response.getStatus());
            assertEquals("application/json", response.getContentType());
            List<Transaction> jobsAfterDelete = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<Transaction>>() { });
            assertEquals(0, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testUpdateJob() throws Exception {
        try {
            // Find the job that is to be modified
            Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), "jod");
            Transaction job = getUkelonnServiceSingleton().getJobs(account.getAccountId()).get(0);
            Integer originalTransactionTypeId = job.getTransactionType().getId();
            double originalTransactionAmount = job.getTransactionAmount();

            // Find a different job type that has a different amount than the
            // job's original type
            TransactionType newJobType = findJobTypeWithDifferentIdAndAmount(getUkelonnServiceSingleton(), originalTransactionTypeId, originalTransactionAmount);

            // Create a new job object with a different jobtype and the same id
            Date now = new Date();
            UpdatedTransaction editedJob = new UpdatedTransaction(job.getId(), account.getAccountId(), newJobType.getId(), now, newJobType.getTransactionAmount());

            // Build the HTTP request
            String editedJobAsJson = ServletTestBase.mapper.writeValueAsString(editedJob);
            HttpServletRequest request = buildRequestFromStringBody(editedJobAsJson);
            when(request.getMethod()).thenReturn("POST");
            String requestURL = "http://localhost:8181/ukelonn/api/job/update";
            String requestURI = "/ukelonn/api/job/update";
            when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
            when(request.getRequestURI()).thenReturn(requestURI);

            // Create a response object that will receive and hold the servlet output
            MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

            // Create the servlet that is to be tested
            UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

            // Create mock OSGi services to inject and inject it
            MockLogService logservice = new MockLogService();
            servlet.setLogservice(logservice);

            // Inject fake OSGi service UkelonnService
            servlet.setUkelonnService(getUkelonnServiceSingleton());

            // Activate the servlet DS component
            servlet.activate();

            // When the servlet is activated it will be plugged into the http whiteboard and configured
            ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
            servlet.init(config);

            // Call the method under test
            servlet.service(request, response);

            // Check the output (compare the updated job against the edited job values)
            assertEquals(200, response.getStatus());
            assertEquals("application/json", response.getContentType());
            List<Transaction> updatedJobs = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<Transaction>>() { });
            Transaction editedJobFromDatabase = updatedJobs.stream().filter(t->t.getId() == job.getId()).collect(Collectors.toList()).get(0);

            assertEquals(editedJob.getTransactionTypeId(), editedJobFromDatabase.getTransactionType().getId().intValue());
            assertThat(editedJobFromDatabase.getTransactionTime().getTime()).isGreaterThan(job.getTransactionTime().getTime());
            assertEquals(editedJob.getTransactionAmount(), editedJobFromDatabase.getTransactionAmount(), 0.0);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testGetPayments() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), "jad");
        String requestURL = String.format("http://localhost:8181/ukelonn/api/payments/%d", account.getAccountId());
        String requestURI = String.format("/ukelonn/api/payments/%d", account.getAccountId());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Transaction> payments = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<Transaction>>() {});
        assertEquals(10, payments.size());
    }

    @Test
    public void testGetPaymenttypes() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/paymenttypes"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/paymenttypes");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> paymenttypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        assertEquals(2, paymenttypes.size());
    }

    @Test
    public void testRegisterPayments() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> paymentTypes = getUkelonnServiceSingleton().getPaymenttypes();
        PerformedTransaction payment = new PerformedTransaction(account, paymentTypes.get(0).getId(), account.getBalance(), new Date());
        String paymentAsJson = ServletTestBase.mapper.writeValueAsString(payment);
        HttpServletRequest request = buildRequestFromStringBody(paymentAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/registerpayment"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/registerpayment");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ServletTestBase.mapper.readValue(response.getOutput().toString(StandardCharsets.UTF_8.toString()), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @Test
    public void testModifyJobtype() throws Exception {
        // Find a jobtype to modify
        List<TransactionType> jobtypes = getUkelonnServiceSingleton().getJobTypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype.setTransactionAmount(originalAmount + 1);

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        HttpServletRequest request = buildRequestFromStringBody(jobtypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/jobtype/modify"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/jobtype/modify");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated amount is larger than the original amount
        List<TransactionType> updatedJobtypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    public void testCreateJobtype() throws Exception {
        // Save the jobtypes before adding a new jobtype
        List<TransactionType> originalJobtypes = getUkelonnServiceSingleton().getJobTypes();


        // Create new jobtyoe
        TransactionType jobtype = new TransactionType(-1, "Skrubb badegolv", 200.0, true, false);

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        HttpServletRequest request = buildRequestFromStringBody(jobtypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/jobtype/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/jobtype/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        List<TransactionType> updatedJobtypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @Test
    public void testModifyPaymenttype() throws Exception {
        // Find a payment type to modify
        List<TransactionType> paymenttypes = getUkelonnServiceSingleton().getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(0);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype.setTransactionAmount(originalAmount + 1);

        // Create the request
        String paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        HttpServletRequest request = buildRequestFromStringBody(paymenttypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/jobtype/modify"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/paymenttype/modify");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated amount is larger than the original amount
        List<TransactionType> updatedPaymenttypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    public void testCreatePaymenttype() throws Exception {
        // Save the payment types before adding a new payment type
        List<TransactionType> originalPaymenttypes = getUkelonnServiceSingleton().getPaymenttypes();

        // Create new payment type
        TransactionType paymenttype = new TransactionType(-2, "Vipps", 0.0, false, true);

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        HttpServletRequest request = buildRequestFromStringBody(jobtypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/paymenttype/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/paymenttype/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        List<TransactionType> updatedPaymenttypes = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedPaymenttypes.size()).isGreaterThan(originalPaymenttypes.size());
    }

    @Test
    public void testGetUsers() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/users"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/users");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<User> users = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<User>>() {});
        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testModifyUser() throws Exception {
        // Get a user and modify all properties except id
        int userToModify = 1;
        List<User> users = getUkelonnServiceSingleton().getUsers();
        User user = users.get(userToModify);
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        user.setUsername(modifiedUsername);
        user.setEmail(modifiedEmailaddress);
        user.setFirstname(modifiedFirstname);
        user.setLastname(modifiedLastname);

        // Create the request
        String userAsJson = ServletTestBase.mapper.writeValueAsString(user);
        HttpServletRequest request = buildRequestFromStringBody(userAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/modify"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/modify");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        List<User> updatedUsers = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<User>>() {});
        User firstUser = updatedUsers.get(userToModify);
        assertEquals(modifiedUsername, firstUser.getUsername());
        assertEquals(modifiedEmailaddress, firstUser.getEmail());
        assertEquals(modifiedFirstname, firstUser.getFirstname());
        assertEquals(modifiedLastname, firstUser.getLastname());
    }

    @Test
    public void testCreateUser() throws Exception {
        // Save the number of users before adding a user
        int originalUserCount = getUkelonnServiceSingleton().getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        HttpServletRequest request = buildRequestFromStringBody(passwordsAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        List<User> updatedUsers = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<User>>() {});

        // Verify that the last user has the expected values
        assertThat(updatedUsers.size()).isGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @Test
    public void testChangePassword() throws Exception {
        // Save the number of users before adding a user
        int originalUserCount = getUkelonnServiceSingleton().getUsers().size();

        // Get a user with a valid username
        List<User> users = getUkelonnServiceSingleton().getUsers();
        User user = users.get(2);

        // Create a passwords object containing the user and with valid passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        HttpServletRequest request = buildRequestFromStringBody(passwordsAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/password"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/password");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Activate the servlet DS component
        servlet.activate();

        // When the servlet is activated it will be plugged into the http whiteboard and configured
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        List<User> updatedUsers = mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<User>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(originalUserCount, updatedUsers.size());
    }

    @Test
    public void testNotifications() throws Exception {
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = new UkelonnServiceProvider();
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUkelonnService(ukelonn);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // A request for notifications to a user
        HttpServletRequest requestGetNotifications = buildGetRequest();
        when(requestGetNotifications.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/notificationsto/jad"));
        when(requestGetNotifications.getRequestURI()).thenReturn("/ukelonn/api/notificationsto/jad");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse notificationsResponse = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Do a REST API call
        servlet.service(requestGetNotifications, notificationsResponse);

        // Check the REST API response (no notifications expected)
        assertEquals(200, notificationsResponse.getStatus());
        assertEquals("application/json", notificationsResponse.getContentType());
        List<User> notificationsToJad = mapper.readValue(notificationsResponse.getOutput().toByteArray(), new TypeReference<List<Notification>>() {});
        assertThat(notificationsToJad).isEmpty();

        // Send a notification to user "jad" over the REST API
        Notification utbetalt = new Notification("Ukelønn", "150 kroner betalt til konto");
        String utbetaltAsJson = mapper.writeValueAsString(utbetalt);
        HttpServletRequest sendNotificationRequest = buildRequestFromStringBody(utbetaltAsJson);
        when(sendNotificationRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/notificationto/jad"));
        when(sendNotificationRequest.getRequestURI()).thenReturn("/ukelonn/api/notificationto/jad");
        MockHttpServletResponse sendNotificationResponse = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        servlet.service(sendNotificationRequest, sendNotificationResponse);

        if (sendNotificationResponse.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            System.err.println("Error in POST request: " + sendNotificationResponse.getOutput().toString());
        }

        // A new REST API request for notifications to "jad" will return a single notification
        MockHttpServletResponse notificationsResponse2 = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        servlet.service(requestGetNotifications, notificationsResponse2);
        assertEquals(200, notificationsResponse2.getStatus());
        assertEquals("application/json", notificationsResponse2.getContentType());
        List<Notification> notificationsToJad2 = mapper.readValue(notificationsResponse2.getOutput().toByteArray(), new TypeReference<List<Notification>>() {});
        assertEquals(utbetalt.getTitle(), notificationsToJad2.get(0).getTitle());
        assertEquals(utbetalt.getMessage(), notificationsToJad2.get(0).getMessage());
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(eq(ServerProperties.PROVIDER_PACKAGES))).thenReturn("no.priv.bang.ukelonn.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return ukelonn.getJobTypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }

}
