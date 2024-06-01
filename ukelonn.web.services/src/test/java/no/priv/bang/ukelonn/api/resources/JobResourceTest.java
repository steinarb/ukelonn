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
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;

class JobResourceTest extends ServletTestBase {

    JobResourceTest() {
        super("/ukelonn", "/api");
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

        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Create the object to be tested
        var resource = new JobResource();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        var accountWithUpdatedBalance = Account.with(account).balance(account.balance() + job.transactionAmount()).build();
        when(ukelonn.registerPerformedJob(any())).thenReturn(accountWithUpdatedBalance);
        resource.ukelonn = ukelonn;

        // Run the method under test
        var result = resource.doRegisterJob(job);

        // Check the response
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

        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        var resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        assertThrows(ForbiddenException.class, () -> resource.doRegisterJob(job));
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

        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        var resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        var accountWithUpdatedBalance = Account.with(account).balance(account.balance() + job.transactionAmount()).build();
        when(ukelonn.registerPerformedJob(any())).thenReturn(accountWithUpdatedBalance);
        resource.ukelonn = ukelonn;

        // Run the method under test
        var result = resource.doRegisterJob(job);

        // Check the response
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

        // Create the request and response for the Shiro login
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        var resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        assertThrows(ForbiddenException.class, () -> resource.doRegisterJob(job));
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
    void testRegisterJobInternalServerError() throws Exception {
        // Create the request
        var account = Account.with().build();
        var jobTypes = getJobtypes();
        var job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).id())
            .transactionAmount(jobTypes.get(0).transactionAmount())
            .transactionDate(new Date())
            .build();

        // Create the object to be tested
        var resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Run the method under test
        assertThrows(InternalServerErrorException.class, () -> resource.doRegisterJob(job));
    }

    @Test
    void testUpdateJob() {
        var ukelonn = mock(UkelonnService.class);
        var resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        resource.ukelonn = ukelonn;

        var account = getJadAccount();
        var job = getJadJobs().get(0);
        var jobId = job.id();

        // Save initial values of the job for comparison later
        var originalTransactionTypeId = job.transactionType().id();
        var originalTransactionTime = job.transactionTime();
        var originalTransactionAmount = job.transactionAmount();

        // Find a different job type that has a different amount
        var newJobType = findJobTypeWithDifferentIdAndAmount(ukelonn, originalTransactionTypeId, originalTransactionAmount);

        // Create a new job object with a different jobtype and the same id
        var now = new Date();
        var editedJob = UpdatedTransaction.with()
            .id(jobId)
            .accountId(account.accountId())
            .transactionTypeId(newJobType.id())
            .transactionTime(now)
            .transactionAmount(newJobType.transactionAmount())
            .build();
        when(ukelonn.updateJob(any())).thenReturn(Arrays.asList(convertUpdatedTransaction(editedJob)));

        var updatedJobs = resource.doUpdateJob(editedJob);

        var editedJobFromDatabase = updatedJobs.stream().filter(t->t.id() == job.id()).toList().get(0);

        assertEquals(editedJob.transactionTypeId(), editedJobFromDatabase.transactionType().id());
        assertThat(editedJobFromDatabase.transactionTime().getTime()).isGreaterThan(originalTransactionTime.getTime());
        assertEquals(editedJob.transactionAmount(), editedJobFromDatabase.transactionAmount(), 0.0);
    }

    @Test
    void testUpdateJobGetSQLException() throws Exception {
        // Create an ukelonn service with a mock database that throws exception
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        ukelonn.setDataSource(datasource);

        // Create a resource and inject OSGi services
        var resource = new JobResource();
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        var emptyTransaction = UpdatedTransaction.with().build();
        assertThrows(InternalServerErrorException.class, () -> resource.doUpdateJob(emptyTransaction));
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return getJobtypes().stream().filter(t->!(t.id() == transactionTypeId)).filter(t->t.transactionAmount() != amount).toList().get(0);
    }

}
