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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;

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

    @Test
    public void testLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "1ad");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.getRoles().length).isGreaterThan(0);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void testAdminLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("admin", "admin");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet and do the login
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet and do the login
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Unknown account", result.getErrorMessage());
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        // Set up the request
        LoginCredentials credentials = new LoginCredentials("jad", "wrong");
        HttpServletRequest request = buildLoginRequest(credentials);

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet and do the login
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Wrong password", result.getErrorMessage());
    }

    @Test
    public void testLoginWrongJson() throws Exception {
        // Set up the request
        HttpServletRequest request = buildRequestFromStringBody("xxxyzzy");

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet and do the login
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Set up Shiro to be in a logged-in state
        loginUser(request, response, "jad", "1ad");

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Set up shiro
        createSubjectAndBindItToThread(request, response);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobTypes()).thenReturn(getJobtypes());
        servlet.setUkelonnService(ukelonn);

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

        List<TransactionType> jobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccounts()).thenReturn(getDummyAccounts());
        servlet.setUkelonnService(ukelonn);

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

        List<Account> accounts = mapper.readValue(getBinaryContent(response), new TypeReference<List<Account>>() {});
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());
        servlet.setUkelonnService(ukelonn);

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

        double expectedAccountBalance = getJadAccount().getBalance();
        Account result = ServletTestBase.mapper.readValue(getBinaryContent(response), Account.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());
        servlet.setUkelonnService(ukelonn);

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

        double expectedAccountBalance = getJadAccount().getBalance();
        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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

    @SuppressWarnings("unchecked")
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenThrow(UkelonnException.class);
        servlet.setUkelonnService(ukelonn);

        // Inject fake OSGi service UserAdminService
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

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
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        account.setBalance(account.getBalance() + jobTypes.get(0).getTransactionAmount());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);
        servlet.setUkelonnService(ukelonn);

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

        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
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
        Account account = getJodAccount();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        account.setBalance(account.getBalance() + jobTypes.get(0).getTransactionAmount());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);
        servlet.setUkelonnService(ukelonn);

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


        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    @Test
    public void testRegisterJobNoUsername() throws Exception {
        // Create the request
        Account account = new Account();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        HttpServletRequest request = buildRequestFromStringBody(jobAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/job/register"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/job/register");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        Account account = getJadAccount();
        String requestURL = String.format("http://localhost:8181/ukelonn/api/jobs/%d", account.getAccountId());
        String requestURI = String.format("/ukelonn/api/jobs/%d", account.getAccountId());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobs(anyInt())).thenReturn(getJadJobs());
        servlet.setUkelonnService(ukelonn);

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

        List<Transaction> jobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
        assertEquals(10, jobs.size());
    }

    @Test
    public void testDeleteJobs() throws Exception {
        // Set up the request
        Account account = getJodAccount();
        List<Transaction> jobs = getJodJobs();
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

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
        List<Transaction> jobsAfterDelete = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        assertEquals(0, jobsAfterDelete.size());
    }

    @Test
    public void testUpdateJob() throws Exception {
        // Find the job that is to be modified
        Account account = getJodAccount();
        Transaction job = getJodJobs().get(0);
        Integer originalTransactionTypeId = job.getTransactionType().getId();
        double originalTransactionAmount = job.getTransactionAmount();

        // Find a different job type that has a different amount than the
        // job's original type
        TransactionType newJobType = findJobTypeWithDifferentIdAndAmount(originalTransactionTypeId, originalTransactionAmount);

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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.updateJob(any())).thenReturn(Arrays.asList(convertUpdatedTransaction(editedJob)));
        servlet.setUkelonnService(ukelonn);

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
        List<Transaction> updatedJobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        Transaction editedJobFromDatabase = updatedJobs.stream().filter(t->t.getId() == job.getId()).collect(Collectors.toList()).get(0);

        assertEquals(editedJob.getTransactionTypeId(), editedJobFromDatabase.getTransactionType().getId().intValue());
        assertThat(editedJobFromDatabase.getTransactionTime().getTime()).isGreaterThan(job.getTransactionTime().getTime());
        assertEquals(editedJob.getTransactionAmount(), editedJobFromDatabase.getTransactionAmount(), 0.0);
    }

    @Test
    public void testGetPayments() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        Account account = getJadAccount();
        String requestURL = String.format("http://localhost:8181/ukelonn/api/payments/%d", account.getAccountId());
        String requestURI = String.format("/ukelonn/api/payments/%d", account.getAccountId());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPayments(anyInt())).thenReturn(getJadPayments());
        servlet.setUkelonnService(ukelonn);

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

        List<Transaction> payments = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);
        servlet.setUserManagement(useradmin);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPaymenttypes()).thenReturn(getPaymenttypes());
        servlet.setUkelonnService(ukelonn);

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

        List<TransactionType> paymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertEquals(2, paymenttypes.size());
    }

    @Test
    public void testRegisterPayments() throws Exception {
        // Create the request
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> paymentTypes = getPaymenttypes();
        PerformedTransaction payment = new PerformedTransaction(account, paymentTypes.get(0).getId(), account.getBalance(), new Date());
        account.setBalance(0.0);
        String paymentAsJson = ServletTestBase.mapper.writeValueAsString(payment);
        HttpServletRequest request = buildRequestFromStringBody(paymentAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/registerpayment"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/registerpayment");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPayment(any())).thenReturn(account);
        servlet.setUkelonnService(ukelonn);

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

        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @Test
    public void testModifyJobtype() throws Exception {
        // Find a jobtype to modify
        List<TransactionType> jobtypes = getJobtypes();
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyJobtype(any())).thenReturn(Arrays.asList(jobtype));
        servlet.setUkelonnService(ukelonn);

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

        List<TransactionType> updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    public void testCreateJobtype() throws Exception {
        // Save the jobtypes before adding a new jobtype
        List<TransactionType> originalJobtypes = getJobtypes();

        // Create new jobtyoe
        TransactionType jobtype = new TransactionType(-1, "Skrubb badegolv", 200.0, true, false);

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        HttpServletRequest request = buildRequestFromStringBody(jobtypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/jobtype/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/jobtype/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<TransactionType> updatedjobtypes = Stream.concat(originalJobtypes.stream(), Stream.of(jobtype)).collect(Collectors.toList());
        when(ukelonn.createJobtype(any())).thenReturn(updatedjobtypes);
        servlet.setUkelonnService(ukelonn);

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
        List<TransactionType> updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @Test
    public void testModifyPaymenttype() throws Exception {
        // Find a payment type to modify
        List<TransactionType> paymenttypes = getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(1);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype.setTransactionAmount(originalAmount + 1);

        // Create the request
        String paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        HttpServletRequest request = buildRequestFromStringBody(paymenttypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/jobtype/modify"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/paymenttype/modify");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyPaymenttype(any())).thenReturn(Arrays.asList(paymenttype));
        servlet.setUkelonnService(ukelonn);

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

        List<TransactionType> updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    public void testCreatePaymenttype() throws Exception {
        // Save the payment types before adding a new payment type
        List<TransactionType> originalPaymenttypes = getPaymenttypes();

        // Create new payment type
        TransactionType paymenttype = new TransactionType(-2, "Vipps", 0.0, false, true);

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        HttpServletRequest request = buildRequestFromStringBody(jobtypeAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/paymenttype/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/paymenttype/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<TransactionType> updatedpaymenttypes = Stream.concat(originalPaymenttypes.stream(), Stream.of(paymenttype)).collect(Collectors.toList());
        when(ukelonn.createPaymenttype(any())).thenReturn(updatedpaymenttypes);
        servlet.setUkelonnService(ukelonn);

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
        List<TransactionType> updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
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
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create the servlet that is to be tested
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);

        // Inject fake OSGi service UserAdminService
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(getUsersForUserManagement());
        servlet.setUserManagement(useradmin);

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

        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testModifyUser() throws Exception {
        // Get a user and modify all properties except id
        int userToModify = 0;
        List<User> users = getUsersForUserManagement();
        User userOriginal = users.get(userToModify);
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        User user = new User(userOriginal.getUserid(), modifiedUsername, modifiedEmailaddress, modifiedFirstname, modifiedLastname);

        // Create the request
        String userAsJson = ServletTestBase.mapper.writeValueAsString(user);
        HttpServletRequest request = buildRequestFromStringBody(userAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/modify"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/modify");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(user));
        servlet.setUserManagement(useradmin);

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
        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        User firstUser = updatedUsers.get(userToModify);
        assertEquals(modifiedUsername, firstUser.getUsername());
        assertEquals(modifiedEmailaddress, firstUser.getEmail());
        assertEquals(modifiedFirstname, firstUser.getFirstname());
        assertEquals(modifiedLastname, firstUser.getLastname());
    }

    @Test
    public void testCreateUser() throws Exception {
        // Save the number of users before adding a user
        int originalUserCount = getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        HttpServletRequest request = buildRequestFromStringBody(passwordsAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/create"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/create");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
        UserManagementService useradmin = mock(UserManagementService.class);
        List<User> updatedusers = Stream.concat(getUsersForUserManagement().stream(), Stream.of(user)).collect(Collectors.toList());
        when(useradmin.addUser(any())).thenReturn(updatedusers);
        servlet.setUserManagement(useradmin);

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
        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

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
        List<User> users = getUsersForUserManagement();

        // Save the number of users before adding a user
        int originalUserCount = users.size();

        // Get a user with a valid username
        User user = users.get(1);

        // Create a passwords object containing the user and with valid passwords
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        HttpServletRequest request = buildRequestFromStringBody(passwordsAsJson);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/admin/user/password"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/admin/user/password");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Create the servlet
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        UkelonnService ukelonn = mock(UkelonnService.class);
        servlet.setUkelonnService(ukelonn);
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.updatePassword(any())).thenReturn(users);
        servlet.setUserManagement(useradmin);

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

        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(originalUserCount, updatedUsers.size());
    }

    @Test
    public void testStatisticsEarningsSumOverYear() throws Exception {
        // Set up REST API servlet with mocked services
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<SumYear> earningsSumOverYear = Arrays.asList(new SumYear(1250.0, 2016), new SumYear(2345.0, 2017), new SumYear(5467.0, 2018), new SumYear(2450.0, 2019));
        when(ukelonn.earningsSumOverYear(eq("jad"))).thenReturn(earningsSumOverYear);
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        servlet.setUkelonnService(ukelonn);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/statistics/earnings/sumoveryear/jad");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<SumYear> statistics = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYear>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(4, statistics.size());
    }

    @Test
    public void testNotifications() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = new UkelonnServiceProvider();
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogservice(logservice);
        servlet.setUserManagement(useradmin);
        servlet.setUkelonnService(ukelonn);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        // A request for notifications to a user
        HttpServletRequest requestGetNotifications = buildGetRequest();
        when(requestGetNotifications.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/notificationsto/jad"));
        when(requestGetNotifications.getRequestURI()).thenReturn("/ukelonn/api/notificationsto/jad");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse notificationsResponse = new MockHttpServletResponse();

        // Do a REST API call
        servlet.service(requestGetNotifications, notificationsResponse);

        // Check the REST API response (no notifications expected)
        assertEquals(200, notificationsResponse.getStatus());
        assertEquals("application/json", notificationsResponse.getContentType());
        List<User> notificationsToJad = mapper.readValue(getBinaryContent(notificationsResponse), new TypeReference<List<Notification>>() {});
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
            System.err.println("Error in POST request: " + sendNotificationResponse.getOutputStreamContent());
        }

        // A new REST API request for notifications to "jad" will return a single notification
        MockHttpServletResponse notificationsResponse2 = new MockHttpServletResponse();
        servlet.service(requestGetNotifications, notificationsResponse2);
        assertEquals(200, notificationsResponse2.getStatus());
        assertEquals("application/json", notificationsResponse2.getContentType());
        List<Notification> notificationsToJad2 = mapper.readValue(getBinaryContent(notificationsResponse2), new TypeReference<List<Notification>>() {});
        assertEquals(utbetalt.getTitle(), notificationsToJad2.get(0).getTitle());
        assertEquals(utbetalt.getMessage(), notificationsToJad2.get(0).getMessage());
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(eq(ServerProperties.PROVIDER_PACKAGES))).thenReturn("no.priv.bang.ukelonn.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/ukelonn");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(Integer transactionTypeId, double amount) {
        return getJobtypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }

    private MockHttpServletRequest buildGetUrl(String localpath) {
        MockHttpServletRequest request = buildGetRootUrl();
        request.setRequestURL("http://localhost:8181/ukelonn/api" + localpath);
        request.setRequestURI("/ukelonn/api" + localpath);
        return request;
    }

    private MockHttpServletRequest buildGetRootUrl() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setMethod("GET");
        request.setRequestURL("http://localhost:8181/ukelonn/api/");
        request.setRequestURI("/ukelonn/api/");
        request.setContextPath("/ukelonn");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

}
