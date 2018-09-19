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
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.util.ThreadContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.TransactionType;

public class RegisterJobTest extends ServletTestBase {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testRegisterJob() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create the object to be tested
        RegisterJob resource = new RegisterJob();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        Account result = resource.doRegisterJob(job);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    /**
     * Test that verifies that a regular user can't update the job list of
     * other users than the one they are logged in as.
     *
     * @throws Exception
     */
    @Test(expected=ForbiddenException.class)
    public void testRegisterJobOtherUsername() throws Exception {
        // Create the request
        Account account = getUkelonnServiceSingleton().getAccount("jod");
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        RegisterJob resource = new RegisterJob();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        resource.doRegisterJob(job);
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

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        RegisterJob resource = new RegisterJob();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        Account result = resource.doRegisterJob(job);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    @Test(expected=ForbiddenException.class)
    public void testRegisterJobNoUsername() throws Exception {
        // Create the request
        Account account = new Account();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        RegisterJob resource = new RegisterJob();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Run the method under test
        resource.doRegisterJob(job);
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
    @Test(expected=InternalServerErrorException.class)
    public void testRegisterJobInternalServerError() throws Exception {
        // Create the request
        Account account = new Account();
        List<TransactionType> jobTypes = getUkelonnServiceSingleton().getJobTypes();
        PerformedTransaction job = new PerformedTransaction(account, jobTypes.get(0).getId(), jobTypes.get(0).getTransactionAmount(), new Date());

        // Create the object to be tested
        RegisterJob resource = new RegisterJob();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Run the method under test
        resource.doRegisterJob(job);
    }

}
