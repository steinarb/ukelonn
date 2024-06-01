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
package no.priv.bang.ukelonn.api;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.AdminStatus;
import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.api.resources.ErrorMessage;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.LocaleBean;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

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
class UkelonnRestApiServletTest extends ServletTestBase {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");
    private final static Locale EN_UK = Locale.forLanguageTag("en-uk");

    UkelonnRestApiServletTest() {
        super("/ukelonn", "/api");
    }

    static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void beforeEachTest() {
        removeWebSubjectFromThread();
    }

    @Test
    void testLoginOk() throws Exception {
        // Set up the request
        var credentials = LoginCredentials.with()
            .username("jad")
            .password(Base64.getEncoder().encodeToString("1ad".getBytes()))
            .build();
        var request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create shiro context but do not log in
        createSubjectAndBindItToThread(request, response);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isNotEmpty();
        assertEquals("", result.errorMessage());
    }

    @Test
    void testAdminLoginOk() throws Exception {
        // Set up the request
        var credentials = LoginCredentials.with()
            .username("admin")
            .password(Base64.getEncoder().encodeToString("admin".getBytes()))
            .build();
        var request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create shiro context but do not log in
        createSubjectAndBindItToThread(request, response);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isNotEmpty();
        assertEquals("", result.errorMessage());
    }

    @Disabled("Gets wrong password exception instead of unknown user exception, don't know why")
    @Test
    void testLoginUnknownUser() throws Exception {
        // Set up the request
        var credentials = LoginCredentials.with()
            .username("unknown")
            .password("unknown")
            .build();
        var request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var ukelonn = mock(UkelonnService.class);
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isEmpty();
        assertEquals("Unknown account", result.errorMessage());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        // Set up the request
        var credentials = LoginCredentials.with()
            .username("jad")
            .password(Base64.getEncoder().encodeToString("wrong".getBytes()))
            .build();
        var request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create shiro context but do not log in
        createSubjectAndBindItToThread(request, response);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isEmpty();
        assertEquals("Wrong password", result.errorMessage());
    }

    @Test
    void testLoginWrongJson() throws Exception {
        // Set up the request
        var request = buildPostUrl("/login");
        request.setBodyContent("xxxyzzy");

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

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
    void testGetLoginStateWhenLoggedIn() throws Exception {
        // Set up the request
        var request = buildGetUrl("/login");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        // Set up Shiro to be in a logged-in state
        var subject = createSubjectAndBindItToThread(request, response);
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isNotEmpty();
        assertEquals("", result.errorMessage());
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
    void testGetLoginStateWhenNotLoggedIn() throws Exception {
        // Set up the request
        var request = buildGetUrl("/login");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        // Set up Shiro to be in a logged-in state
        var subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isEmpty();
        assertEquals("", result.errorMessage());
    }

    @Test
    void testLogoutOk() throws Exception {
        // Set up the request
        var request = buildPostUrl("/logout");

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isEmpty();
        assertEquals("", result.errorMessage());
    }

    /**
     * Verify that logging out a not-logged in shiro, is harmless.
     *
     * @throws Exception
     */
    @Test
    void testLogoutNotLoggedIn() throws Exception {
        // Set up the request
        var request = buildPostUrl("/logout");

        // Create the response that will receive the login result
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.roles()).isEmpty();
        assertEquals("", result.errorMessage());
    }

    @Test
    void testGetJobtypes() throws Exception {
        // Set up the request
        var request = buildGetUrl("/jobtypes");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobTypes()).thenReturn(getJobtypes());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var jobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(jobtypes).isNotEmpty();
    }

    @Test
    void testGetAccounts() throws Exception {
        // Create the request
        var request = buildGetUrl("/accounts");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccounts()).thenReturn(getDummyAccounts());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var accounts = mapper.readValue(getBinaryContent(response), new TypeReference<List<Account>>() {});
        assertEquals(2, accounts.size());
    }

    @Test
    void testGetAccount() throws Exception {
        // Create the request
        var request = buildGetUrl("/account/jad");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var expectedAccountBalance = getJadAccount().balance();
        var result = ServletTestBase.mapper.readValue(getBinaryContent(response), Account.class);
        assertEquals("jad", result.username());
        assertEquals(expectedAccountBalance, result.balance(), 0.0);
    }

    /**
     * Test that verifies that a regular user can't access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testGetAccountOtherUsername() throws Exception {
        // Create the request
        var request = buildGetUrl("/account/jod");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

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
    void testGetAccountWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        var request = buildGetUrl("/account/jad");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var expectedAccountBalance = getJadAccount().balance();
        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.username());
        assertEquals(expectedAccountBalance, result.balance(), 0.0);
    }

    @Test
    void testGetAccountNoUsername() throws Exception {
        // Create the request
        var request = buildGetUrl("/account");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

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
    void testGetAccountUsernameNotPresentInDatabase() throws Exception {
        // Create the request
        var request = buildGetUrl("/account/unknownuse");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenThrow(UkelonnException.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

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
    void testRegisterJob() throws Exception {
        // Create the request
        var account = getJadAccount();
        var originalBalance = account.balance();
        var jobTypes = getJobtypes();
        var job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).id())
            .transactionAmount(jobTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();
        account = Account.with(account).balance(account.balance() + jobTypes.get(0).transactionAmount()).build();
        var jobAsJson = mapper.writeValueAsString(job);
        var request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.username());
        assertThat(result.balance()).isGreaterThan(originalBalance);
    }

    /**
     * Test that verifies that a regular user can't update the job list of
     * other users than the one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testRegisterJobOtherUsername() throws Exception {
        // Create the request
        var account = getJodAccount();
        var jobTypes = getJobtypes();
        var job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).id())
            .transactionAmount(jobTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();
        var jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        var request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

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
    void testRegisterJobtWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        var account = getJadAccount();
        var originalBalance = account.balance();
        var jobTypes = getJobtypes();
        var job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).id())
            .transactionAmount(jobTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();
        account = Account.with(account).balance(account.balance() + jobTypes.get(0).transactionAmount()).build();
        var jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        var request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.username());
        assertThat(result.balance()).isGreaterThan(originalBalance);
    }

    @Test
    void testRegisterJobNoUsername() throws Exception {
        // Create the request
        var account = Account.with().build();
        var jobTypes = getJobtypes();
        var job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).id())
            .transactionAmount(jobTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();
        var jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        var request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    @Test
    void testRegisterJobUnparsablePostData() throws Exception {
        // Create the request
        var request = buildPostUrl("/job/register");
        request.setBodyContent("this is not json");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
    }

    @Test
    void testGetJobs() throws Exception {
        // Set up the request
        var account = getJadAccount();
        var request = buildGetUrl(String.format("/jobs/%d", account.accountId()));

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobs(anyInt())).thenReturn(getJadJobs());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var jobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
        assertEquals(10, jobs.size());
    }

    @Test
    void testDeleteJobs() throws Exception {
        // Set up the request
        var account = getJodAccount();
        var jobs = getJodJobs();
        var jobIds = Arrays.asList(jobs.get(0).id(), jobs.get(1).id());
        var accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(jobIds).build();
        var accountWithJobIdsAsJson = ServletTestBase.mapper.writeValueAsString(accountWithJobIds);
        var request = buildPostUrl("/admin/jobs/delete");
        request.setBodyContent(accountWithJobIdsAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var jobsAfterDelete = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        assertEquals(0, jobsAfterDelete.size());
    }

    @Test
    void testUpdateJob() throws Exception {
        // Find the job that is to be modified
        var account = getJodAccount();
        var job = getJodJobs().get(0);
        var originalTransactionTypeId = job.transactionType().id();
        var originalTransactionAmount = job.transactionAmount();

        // Find a different job type that has a different amount than the
        // job's original type
        var newJobType = findJobTypeWithDifferentIdAndAmount(originalTransactionTypeId, originalTransactionAmount);

        // Create a new job object with a different jobtype and the same id
        var now = new Date();
        var editedJob = UpdatedTransaction.with()
            .id(job.id())
            .accountId(account.accountId())
            .transactionTypeId(newJobType.id())
            .transactionTime(now)
            .transactionAmount(newJobType.transactionAmount())
            .build();

        // Build the HTTP request
        var editedJobAsJson = ServletTestBase.mapper.writeValueAsString(editedJob);
        var request = buildPostUrl("/job/update");
        request.setBodyContent(editedJobAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.updateJob(any())).thenReturn(Arrays.asList(convertUpdatedTransaction(editedJob)));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Call the method under test
        servlet.service(request, response);

        // Check the output (compare the updated job against the edited job values)
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var updatedJobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        var editedJobFromDatabase = updatedJobs.stream().filter(t->t.id() == job.id()).collect(Collectors.toList()).get(0);

        assertEquals(editedJob.transactionTypeId(), editedJobFromDatabase.transactionType().id());
        assertThat(editedJobFromDatabase.transactionTime().getTime()).isGreaterThan(job.transactionTime().getTime());
        assertEquals(editedJob.transactionAmount(), editedJobFromDatabase.transactionAmount(), 0.0);
    }

    @Test
    void testGetPayments() throws Exception {
        // Set up the request
        var account = getJadAccount();
        var request = buildGetUrl(String.format("/payments/%d", account.accountId()));

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPayments(anyInt())).thenReturn(getJadPayments());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var payments = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
        assertEquals(10, payments.size());
    }

    @Test
    void testGetPaymenttypes() throws Exception {
        // Set up the request
        var request = buildGetUrl("/paymenttypes");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPaymenttypes()).thenReturn(getPaymenttypes());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var paymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertEquals(2, paymenttypes.size());
    }

    @Test
    void testRegisterPayments() throws Exception {
        // Create the request
        var account = getJadAccount();
        var originalBalance = account.balance();
        var paymentTypes = getPaymenttypes();
        var payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(paymentTypes.get(0).id())
            .transactionAmount(paymentTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();
        account = Account.with(account).balance(0.0).build();
        var paymentAsJson = ServletTestBase.mapper.writeValueAsString(payment);
        var request = buildPostUrl("/registerpayment");
        request.setBodyContent(paymentAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPayment(any())).thenReturn(account);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.username());
        assertThat(result.balance()).isLessThan(originalBalance);
    }

    @Test
    void testModifyJobtype() throws Exception {
        // Find a jobtype to modify
        var jobtypes = getJobtypes();
        var jobtype = jobtypes.get(0);
        var originalAmount = jobtype.transactionAmount();

        // Modify the amount of the jobtype
        jobtype = TransactionType.with(jobtype).transactionAmount(originalAmount + 1).build();

        // Create the request
        var jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        var request = buildPostUrl("/admin/jobtype/modify");
        request.setBodyContent(jobtypeAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyJobtype(any())).thenReturn(Arrays.asList(jobtype));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        var updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.transactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testCreateJobtype() throws Exception {
        // Save the jobtypes before adding a new jobtype
        var originalJobtypes = getJobtypes();

        // Create new jobtyoe
        var jobtype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Skrubb badegolv")
            .transactionAmount(200.0)
            .transactionIsWork(true)
            .build();

        // Create the request
        var jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        var request = buildPostUrl("/admin/jobtype/create");
        request.setBodyContent(jobtypeAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        var updatedjobtypes = Stream.concat(originalJobtypes.stream(), Stream.of(jobtype)).collect(Collectors.toList());
        when(ukelonn.createJobtype(any())).thenReturn(updatedjobtypes);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        var updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedJobtypes).hasSizeGreaterThan(originalJobtypes.size());
    }

    @Test
    void testModifyPaymenttype() throws Exception {
        // Find a payment type to modify
        var paymenttypes = getPaymenttypes();
        var paymenttype = paymenttypes.get(1);
        var originalAmount = paymenttype.transactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();

        // Create the request
        var paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        var request = buildPostUrl("/admin/paymenttype/modify");
        request.setBodyContent(paymenttypeAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyPaymenttype(any())).thenReturn(Arrays.asList(paymenttype));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.transactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testCreatePaymenttype() throws Exception {
        // Save the payment types before adding a new payment type
        var originalPaymenttypes = getPaymenttypes();

        // Create new payment type
        var paymenttype = TransactionType.with()
            .id(-2)
            .transactionTypeName("Vipps")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Create the request
        var paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        var request = buildPostUrl("/admin/paymenttype/create");
        request.setBodyContent(paymenttypeAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        var updatedpaymenttypes = Stream.concat(originalPaymenttypes.stream(), Stream.of(paymenttype)).collect(Collectors.toList());
        when(ukelonn.createPaymenttype(any())).thenReturn(updatedpaymenttypes);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        var updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedPaymenttypes).hasSizeGreaterThan(originalPaymenttypes.size());
    }

    @Test
    void testGetUsers() throws Exception {
        // Set up the request
        var request = buildGetUrl("/users");

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        var ukelonn = mock(UkelonnService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(getUsersForUserManagement());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testModifyUser() throws Exception {
        // Get a user and modify all properties except id
        var userToModify = 0;
        var users = getUsersForUserManagement();
        var userOriginal = users.get(userToModify);
        var modifiedUsername = "gandalf";
        var modifiedEmailaddress = "wizard@hotmail.com";
        var modifiedFirstname = "Gandalf";
        var modifiedLastname = "Grey";
        var user = User.with(userOriginal)
            .username(modifiedUsername)
            .email(modifiedEmailaddress)
            .firstname(modifiedFirstname)
            .lastname(modifiedLastname)
            .build();

        // Create the request
        var userAsJson = ServletTestBase.mapper.writeValueAsString(user);
        var request = buildPostUrl("/admin/user/modify");
        request.setBodyContent(userAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var ukelonn = mock(UkelonnService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(user));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        var updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        var firstUser = updatedUsers.get(userToModify);
        assertEquals(modifiedUsername, firstUser.username());
        assertEquals(modifiedEmailaddress, firstUser.email());
        assertEquals(modifiedFirstname, firstUser.firstname());
        assertEquals(modifiedLastname, firstUser.lastname());
    }

    @Test
    void testCreateUser() throws Exception {
        // Save the number of users before adding a user
        var originalUserCount = getUsers().size();

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create the request
        var passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        var request = buildPostUrl("/admin/user/create");
        request.setBodyContent(passwordsAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var ukelonn = mock(UkelonnService.class);
        var useradmin = mock(UserManagementService.class);
        var updatedusers = Stream.concat(getUsersForUserManagement().stream(), Stream.of(user)).collect(Collectors.toList());
        when(useradmin.addUser(any())).thenReturn(updatedusers);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        var updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

        // Verify that the last user has the expected values
        assertThat(updatedUsers).hasSizeGreaterThan(originalUserCount);
        var lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.username());
        assertEquals(newEmailaddress, lastUser.email());
        assertEquals(newFirstname, lastUser.firstname());
        assertEquals(newLastname, lastUser.lastname());
    }

    @Test
    void testChangePassword() throws Exception {
        var users = getUsersForUserManagement();

        // Save the number of users before adding a user
        var originalUserCount = users.size();

        // Get a user with a valid username
        var user = users.get(1);

        // Create a passwords object containing the user and with valid passwords
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create the request
        var passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        var request = buildPostUrl("/admin/user/password");
        request.setBodyContent(passwordsAsJson);

        // Create a response object that will receive and hold the servlet output
        var response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        var logservice = new MockLogService();
        var ukelonn = mock(UkelonnService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.updatePassword(any())).thenReturn(users);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(originalUserCount, updatedUsers.size());
    }

    @Test
    void testStatisticsEarningsSumOverYear() throws Exception {
        // Set up REST API servlet with mocked services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = mock(UkelonnService.class);
        var earningsSumOverYear = Arrays.asList(
            SumYear.with().sum(1250.0).year(2016).build(),
            SumYear.with().sum(2345.0).year(2017).build(),
            SumYear.with().sum(5467.0).year(2018).build(),
            SumYear.with().sum(2450.0).year(2019).build());
        when(ukelonn.earningsSumOverYear("jad")).thenReturn(earningsSumOverYear);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/statistics/earnings/sumoveryear/jad");
        var response = new MockHttpServletResponse();

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        var statistics = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYear>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(4, statistics.size());
    }

    @Test
    void testNotifications() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = new UkelonnServiceProvider();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // A request for notifications to a user
        var requestGetNotifications = buildGetUrl("/notificationsto/jad");

        // Create a response object that will receive and hold the servlet output
        var notificationsResponse = new MockHttpServletResponse();

        // Log in admin user
        loginUser("admin", "admin");

        // Do a REST API call
        servlet.service(requestGetNotifications, notificationsResponse);

        // Check the REST API response (no notifications expected)
        assertEquals(200, notificationsResponse.getStatus());
        assertEquals("application/json", notificationsResponse.getContentType());
        var notificationsToJad = mapper.readValue(getBinaryContent(notificationsResponse), new TypeReference<List<User>>() {});
        assertThat(notificationsToJad).isEmpty();

        // Send a notification to user "jad" over the REST API
        var utbetalt = Notification.with().title("Ukelønn").message("150 kroner utbetalt til konto").build();
        var utbetaltAsJson = mapper.writeValueAsString(utbetalt);
        var sendNotificationRequest = buildPostUrl("/notificationto/jad");
        sendNotificationRequest.setBodyContent(utbetaltAsJson);
        var sendNotificationResponse = new MockHttpServletResponse();
        servlet.service(sendNotificationRequest, sendNotificationResponse);

        if (sendNotificationResponse.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            System.err.println("Error in POST request: " + sendNotificationResponse.getOutputStreamContent());
        }

        // A new REST API request for notifications to "jad" will return a single notification
        var notificationsResponse2 = new MockHttpServletResponse();
        servlet.service(requestGetNotifications, notificationsResponse2);
        assertEquals(200, notificationsResponse2.getStatus());
        assertEquals("application/json", notificationsResponse2.getContentType());
        var notificationsToJad2 = mapper.readValue(getBinaryContent(notificationsResponse2), new TypeReference<List<Notification>>() {});
        assertEquals(utbetalt.title(), notificationsToJad2.get(0).title());
        assertEquals(utbetalt.message(), notificationsToJad2.get(0).message());
    }

    @Test
    void testGetActiveBonuses() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getActiveBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/activebonuses");
        var response = new MockHttpServletResponse();

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var activeBonuses = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    void testGetAllBonuses() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAllBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/allbonuses");
        var response = new MockHttpServletResponse();

        // Log in non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var allBonuses = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(allBonuses).isNotEmpty();
    }

    @Test
    void testPostCreateBonus() throws Exception {
        // Set up REST API servlet with mocked services
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.createBonus(bonus)).thenReturn(Collections.singletonList(bonus));

        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildPostUrl("/admin/createbonus");
        var postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var bonusesWithAddedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithAddedBonus).contains(bonus);
    }

    @Test
    void testPostUpdateBonus() throws Exception {
        // Set up REST API servlet with mocked services
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyBonus(bonus)).thenReturn(Collections.singletonList(bonus));

        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildPostUrl("/admin/modifybonus");
        var postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var bonusesWithUpdatedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithUpdatedBonus).contains(bonus);
    }

    @Test
    void testPostDeleteBonus() throws Exception {
        // Set up REST API servlet with mocked services
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteBonus(bonus)).thenReturn(Collections.singletonList(Bonus.with().build()));

        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildPostUrl("/admin/deletebonus");
        var postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var bonusesWithDeletedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithDeletedBonus)
            .isNotEmpty()
            .doesNotContain(bonus);
    }

    @Test
    void testPostAdminStatus() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create the request and response
        var request = buildPostUrl("/admin/user/adminstatus");
        var postBody = mapper.writeValueAsString(user);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var updatedStatus = mapper.readValue(getBinaryContent(response), AdminStatus.class);
        assertEquals(user, updatedStatus.user());
        assertTrue(updatedStatus.administrator());
    }

    @Test
    void testPostChangeAdminStatus() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        var status = AdminStatus.with().user(user).administrator(true).build();

        // Create the request and response
        var request = buildPostUrl("/admin/user/changeadminstatus");
        var postBody = mapper.writeValueAsString(status);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Log in admin user
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var updatedStatus = mapper.readValue(getBinaryContent(response), AdminStatus.class);
        assertEquals(user, updatedStatus.user());
        assertTrue(updatedStatus.administrator());
    }

    @Test
    void testPostChangeAdminStatusNonAdminUser() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        var status = AdminStatus.with().user(user).administrator(true).build();

        // Create the request and response
        var request = buildPostUrl("/admin/user/changeadminstatus");
        var postBody = mapper.writeValueAsString(status);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Login non-admin user
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus(), "Expect 403 Forbidden");
    }

    @Test
    void testPostChangeAdminStatusNotLoggedIn() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        var status = AdminStatus.with().user(user).administrator(true).build();

        // Create the request and response
        var request = buildPostUrl("/admin/user/changeadminstatus");
        var postBody = mapper.writeValueAsString(status);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        // Create security context
        createSubjectAndBindItToThread(request, response);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(401, response.getStatus(), "Expect 401 Unauthorized");
    }

    @Test
    void testDefaultLocale() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn(NB_NO);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/defaultlocale");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var defaultLocale = mapper.readValue(getBinaryContent(response), Locale.class);
        assertEquals(NB_NO, defaultLocale);
    }

    @Test
    void testAvailableLocales() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.availableLocales()).thenReturn(Collections.singletonList(Locale.forLanguageTag("nb-NO")).stream().map(l -> LocaleBean.with().locale(l).build()).collect(Collectors.toList()));
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/availablelocales");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var availableLocales = mapper.readValue(getBinaryContent(response), new TypeReference<List<LocaleBean>>() {});
        assertThat(availableLocales).isNotEmpty().contains(LocaleBean.with().locale(Locale.forLanguageTag("nb-NO")).build());
    }

    @Test
    void testDisplayTexts() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var texts = new HashMap<String, String>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(NB_NO)).thenReturn(texts);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=nb_NO");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var displayTexts = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, String>>() {});
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayTextsWithUnknownLocale() throws Exception {
        // Set up REST API servlet with mocked services
        var ukelonn = mock(UkelonnService.class);
        var texts = new HashMap<String, String>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(EN_UK)).thenThrow(MissingResourceException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=en_UK");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var errorMessage = mapper.readValue(getBinaryContent(response), ErrorMessage.class);
        assertEquals(500, errorMessage.status());
        assertThat(errorMessage.message()).startsWith("Unknown locale");
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(Integer transactionTypeId, double amount) {
        return getJobtypes().stream().filter(t->t.id() != transactionTypeId).filter(t->t.transactionAmount() != amount).collect(Collectors.toList()).get(0);
    }


    private UkelonnRestApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(UkelonnService ukelonn, LogService logservice, UserManagementService useradmin) throws Exception {
        var servlet = new UkelonnRestApiServlet();
        servlet.setLogService(logservice);
        servlet.setUkelonnService(ukelonn);
        servlet.setUserManagement(useradmin);
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.ukelonn.api.resources");
        var servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/ukelonn");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

}
