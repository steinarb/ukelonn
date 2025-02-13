/*
 * Copyright 2018-2025 Steinar Bang
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
package no.priv.bang.ukelonn.backend;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.LocaleBean;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

class UkelonnServiceProviderTest {
    private static final Locale NB_NO = Locale.forLanguageTag("nb-no");

    @BeforeAll
    static void setupForAllTests() throws Exception {
        setupFakeOsgiServices();
    }

    @AfterAll
    static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    void testGetAccounts() {
        var provider = getUkelonnServiceSingleton();
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        provider.setUserAdmin(useradmin);
        var accounts = provider.getAccounts();
        assertThat(accounts).hasSizeGreaterThan(1);
    }

    @Test
    void testGetAccountsWithUserMissing() {
        var provider = getUkelonnServiceSingleton();
        var originalLogService = provider.getLogservice();
        try {
            var logservice = new MockLogService();
            provider.setLogservice(logservice);
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with().userid(1)
                .username("jad")
                .email("jad@gmail.com")
                .firstname("Jane")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString()))
                .thenReturn(user)
                .thenThrow(AuthserviceException.class);
            provider.setUserAdmin(useradmin);
            assertThat(logservice.getLogmessages()).isEmpty(); // Verify no log messages before fetching users
            var accounts = provider.getAccounts();
            assertThat(accounts).hasSizeGreaterThan(1);
            assertThat(logservice.getLogmessages()).isNotEmpty();
            assertThat(logservice.getLogmessages().get(0))
                .startsWith("[WARNING] No authservice user for username \"jod\" when fetching account");
        } finally {
            provider.setLogservice(originalLogService);
        }
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#getAccounts(Class)}
     * method when a resultset that throws SQLException is returned from the
     * {@link UkelonnDatabase#query(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and an empty list to be returned
     *
     * @throws SQLException
     */
    @Test()
    void testGetAccountsWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            var resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            var accounts = ukelonn.getAccounts();
            assertEquals(0, accounts.size(), "Expected a non-null, empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#getAccounts(Class)}
     * method when a null resultset is returned from the
     * {@link UkelonnDatabase#query(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and an empty list to be returned
     * @throws Exception
     */
    @Test()
    void testGetAccountsNullResultSet() throws Exception {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(datasource);
            var accounts = ukelonn.getAccounts();
            assertEquals(0, accounts.size(), "Expected a non-null, empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testGetAccount() {
        var provider = getUkelonnServiceSingleton();
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        provider.setUserAdmin(useradmin);
        var account = provider.getAccount("jad");
        assertEquals("jad", account.username());
        assertEquals("Jane", account.firstName());
        assertEquals("Doe", account.lastName());
        var jobs = provider.getJobs(account.accountId());
        assertEquals(10, jobs.size());
        var payments = provider.getPayments(account.accountId());
        assertEquals(10, payments.size());
    }

    /**
     * Corner case test: test what happens when an account has no transactions
     * (the query result is empty)
     */
    @Test
    void testGetAccountInfoFromDatabaseAccountHasNoTransactions() {
        var provider = getUkelonnServiceSingleton();
        assertThrows(UkelonnException.class, () -> provider.getAccount("on"));
    }

    /**
     * Corner case test: test what happens when trying to get an
     * account for a username that isn't present in the database
     */
    @Test
    void testGetAccountInfoFromDatabaseWhenAccountDoesNotExist() {
        var ukelonn = getUkelonnServiceSingleton();
        assertThrows(UkelonnException.class, () -> ukelonn.getAccount("unknownuser"));
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#updateBalanseFromDatabase(Class, Account)}
     * method when a resultset that throws SQLException is returned from the
     * {@link UkelonnDatabase#query(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and a dummy {@link Account} object to be returned.
     *
     * @throws SQLException
     */
    @Test
    void testGetAccountInfoFromDatabaseWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var statement = mock(PreparedStatement.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            var resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            assertThrows(UkelonnException.class, () -> ukelonn.getAccount("jad"));
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testGetJobs() {
        var ukelonn = getUkelonnServiceSingleton();
        var username = "jad";
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username(username)
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        var account = ukelonn.getAccount(username);
        var jobs = ukelonn.getJobs(account.accountId());
        assertEquals(10, jobs.size());
    }

    @Test
    void testRegisterPerformedJob() throws Exception {
        try {
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jad";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jad@gmail.com")
                .firstname("Jane")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);
            var oldBalance = account.balance();
            var jobtype = ukelonn.getJobTypes().get(0);
            var performedJob = PerformedTransaction.with()
                .account(account)
                .transactionTypeId(jobtype.id())
                .transactionAmount(jobtype.transactionAmount())
                .transactionDate(new Date())
                .build();
            var updatedAccount = ukelonn.registerPerformedJob(performedJob);
            assertThat(updatedAccount.balance()).isGreaterThan(oldBalance);
        } finally {
            restoreTestDatabase();
        }
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#registerNewJobInDatabase(Class, Account, int, double)}
     * method when a resultset that throws SQLException is returned from the
     * {@link UkelonnDatabase#update(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and an empty map to be returned
     *
     * @throws SQLException
     */
    @Test
    void testRegisterNewJobInDatabaseWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        var account = ukelonn.getAccount("jad");
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeUpdate()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            var performedJob = PerformedTransaction.with()
                .account(account)
                .transactionTypeId(1)
                .transactionAmount(45.0)
                .transactionDate(new Date())
                .build();
            assertThrows(UkelonnException.class, () -> ukelonn.registerPerformedJob(performedJob));
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testDeleteAllJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jod";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);
            var jobs = ukelonn.getJobs(account.accountId());
            assertEquals(2, jobs.size());
            var idsOfJobsToDelete = Arrays.asList(jobs.get(0).id(), jobs.get(1).id());

            // Do the delete
            var jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.accountId(), idsOfJobsToDelete);

            // Check that the job list that was two items earlier is now empty
            assertEquals(0, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void testDeleteSomeJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jod";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);
            var jobs = ukelonn.getJobs(account.accountId());
            assertEquals(2, jobs.size());
            var idsOfJobsToDelete = Arrays.asList(jobs.get(0).id());

            // Do the delete
            var jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.accountId(), idsOfJobsToDelete);

            // Check that the job list that was two items earlier is now empty
            assertEquals(1, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void testDeleteJobsWithErrorOnClosingStatement() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database with a prepared statement that will fail on close
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        doThrow(SQLException.class).when(statement).close();
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        assertEquals(0, logservice.getLogmessages().size());
        ukelonn.deleteJobsFromAccount(1, Arrays.asList(1, 2, 3));
        assertEquals(2, logservice.getLogmessages().size(), "Expected the errors to be logged");
    }

    @Test
    void verifyDeletingNoJobsOfUserHasNoEffect() throws Exception {
        try {
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jod";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);

            // Check preconditions
            var jobs = ukelonn.getJobs(account.accountId());
            assertEquals(2, jobs.size());

            // Delete with an empty argument
            List<Integer> idsOfJobsToDelete = Collections.emptyList();
            var jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.accountId(), idsOfJobsToDelete);

            // Verify that nothing has been deleted
            assertEquals(2, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void verifyThatTryingToDeletePaymentsAsJobsWillDoNothing() throws Exception {
        try {
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jod";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);

            // Check the preconditions
            var jobs = ukelonn.getJobs(account.accountId());
            assertEquals(2, jobs.size());
            var payments = ukelonn.getPayments(account.accountId());
            assertEquals(1, payments.size());

            // Try deleting the payment as a job
            var idsOfJobsToDelete = Arrays.asList(payments.get(0).id());
            var jobsAfterAttemptedDelete = ukelonn.deleteJobsFromAccount(account.accountId(), idsOfJobsToDelete);

            // Verify that both the jobs and payments are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            var paymentsAfterAttemptedDelete = ukelonn.getPayments(account.accountId());
            assertEquals(1, paymentsAfterAttemptedDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void verifyThatTryingToDeleteJobsOfDifferentAccountWillDoNothing() throws Exception {
        try {
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jod";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);
            var otherUsername = "jad";
            var otherAccount = ukelonn.getAccount(otherUsername);

            // Check the preconditions
            var jobs = ukelonn.getJobs(account.accountId());
            assertEquals(2, jobs.size());
            var otherAccountJobs = ukelonn.getJobs(otherAccount.accountId());
            assertEquals(10, otherAccountJobs.size());

            // Try deleting the payment as a job
            var idsOfJobsToDelete = Arrays.asList(otherAccountJobs.get(0).id(), otherAccountJobs.get(1).id(), otherAccountJobs.get(2).id(), otherAccountJobs.get(3).id(), otherAccountJobs.get(4).id(), otherAccountJobs.get(5).id(), otherAccountJobs.get(6).id(), otherAccountJobs.get(7).id(), otherAccountJobs.get(8).id(), otherAccountJobs.get(9).id());
            var jobsAfterAttemptedDelete = ukelonn.deleteJobsFromAccount(account.accountId(), idsOfJobsToDelete);

            // Verify that both the account's jobs and and the other account's jobs are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            var otherAccountsJobsAfterAttemptedDelete = ukelonn.getJobs(otherAccount.accountId());
            assertThat(otherAccountsJobsAfterAttemptedDelete).containsAll(otherAccountJobs);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Mock a database that will fail
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        // trying to set the parameter here will throw an UkelonnException
        assertThrows(UkelonnException.class, () -> ukelonn.addParametersToDeleteJobsStatement(1, statement));
    }

    @Test
    void testUpdateJob() throws Exception {
        try {
            var ukelonn = getUkelonnServiceSingleton();
            var username = "jad";
            var useradmin = mock(UserManagementService.class);
            var user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jad@gmail.com")
                .firstname("Jane")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            var account = ukelonn.getAccount(username);
            var job = ukelonn.getJobs(account.accountId()).get(0);
            int jobId = job.id();

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

            var updatedJobs = ukelonn.updateJob(editedJob);

            var editedJobFromDatabase = updatedJobs.stream().filter(t->t.id() == job.id()).toList().get(0);

            assertEquals(editedJob.transactionTypeId(), editedJobFromDatabase.transactionType().id());
            assertThat(editedJobFromDatabase.transactionTime().getTime()).isGreaterThan(originalTransactionTime.getTime());
            assertEquals(editedJob.transactionAmount(), editedJobFromDatabase.transactionAmount(), 0.0);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    void testUpdateJobGetSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());

        ukelonn.setDataSource(datasource);

        var updatedTransaction = UpdatedTransaction.with().build();
        assertThrows(UkelonnException.class, () -> ukelonn.updateJob(updatedTransaction));
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return ukelonn.getJobTypes().stream().filter(t->t.id() != transactionTypeId).filter(t->t.transactionAmount() != amount).toList().get(0);
    }

    @Test
    void testGetPayments() {
        var ukelonn = getUkelonnServiceSingleton();
        var username = "jad";
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username(username)
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        var account = ukelonn.getAccount(username);
        var payments = ukelonn.getPayments(account.accountId());
        assertEquals(10, payments.size());
    }

    @Test
    void testGetPaymenttypes() {
        var ukelonn = getUkelonnServiceSingleton();
        var paymenttypes = ukelonn.getPaymenttypes();
        assertEquals(2, paymenttypes.size());
    }

    @Test
    void testGetPaymenttypesWithDatabasePreparestatementFailure() throws Exception {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        var logservice = new MockLogService();
        try {
            ukelonn.setLogservice(logservice);
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            assertEquals(0, logservice.getLogmessages().size()); // Verify precondition: no logmessages
            var paymenttypes = ukelonn.getPaymenttypes();
            assertEquals(0, paymenttypes.size(), "Expected empty list");
            assertEquals(1, logservice.getLogmessages().size(), "Expect database error to be logged");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testGetPaymenttypesWithDatabaseFailure() throws Exception {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(datasource);
            var paymenttypes = ukelonn.getPaymenttypes();
            assertEquals(0, paymenttypes.size(), "Expected empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testRegisterPayment() {
        var ukelonn = getUkelonnServiceSingleton();
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);

        // Create the request
        var account = ukelonn.getAccount("jad");
        var originalBalance = account.balance();
        var paymenttypes = ukelonn.getPaymenttypes();
        var payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(paymenttypes.get(0).id())
            .transactionAmount(account.balance())
            .transactionDate(new Date())
            .build();

        // Run the method under test
        var result = ukelonn.registerPayment(payment);

        // Check the response
        assertEquals("jad", result.username());
        assertThat(result.balance()).isLessThan(originalBalance);
    }

    @Test
    void testRegisterPaymentWithDatabaseFailure() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create a mock log service
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create the request
        var account = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(2.0).build();
        var payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(1)
            .transactionAmount(2.0)
            .transactionDate(new Date())
            .build();

        // Run the method under test
        var result = ukelonn.registerPayment(payment);

        // Check the response
        assertNull(result);
        assertEquals(1, logservice.getLogmessages().size());
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#getTransactionTypesFromUkelonnDatabase(Class)}
     * method when a null resultset is returned from the {@link UkelonnDatabase#query(PreparedStatement)}
     * method.
     *
     * Expect no exception to be thrown, and a non-null empty map to be returned.
     * @throws Exception
     */
    @Test()
    void testGetJobTypesNullResultSet() throws Exception {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var database = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(database.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(database);
            var jobtypes = ukelonn.getJobTypes();
            assertEquals(0, jobtypes.size(), "Expected a non-null, empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    /**
     * Corner case test: Tests what happens to the {@link CommonDatabaseMethods#getTransactionTypesFromUkelonnDatabase(Class)}
     * methid when a resultset that throws SQLException is returned from the
     * {@link UkelonnDatabase#query(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and a non-null empty map to be returned.
     *
     * @throws SQLException
     */
    @Test()
    void testGetJobTypesWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            var resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            var jobtypes = ukelonn.getJobTypes();
            assertEquals(0, jobtypes.size(), "Expected a non-null, empty map");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testModifyJobtype() {
        var ukelonn = getUkelonnServiceSingleton();

        // Find a jobtyoe
        var jobtypes = ukelonn.getJobTypes();
        var jobtype = jobtypes.get(0);
        var originalAmount = jobtype.transactionAmount();

        // Modify the amount of the jobtype
        jobtype = TransactionType.with(jobtype).transactionAmount(originalAmount + 1).build();

        // Update the job type in the database
        var updatedJobtypes = ukelonn.modifyJobtype(jobtype);

        // Verify that the updated amount is larger than the original amount
        var updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.transactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testModifyJobtypeFailure() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a non-existing jobtype
        var jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> ukelonn.modifyJobtype(jobtype));
    }

    @Test
    void testCreateJobtype() {
        var ukelonn = getUkelonnServiceSingleton();

        // Get the list of jobtypes before adding a new job type
        var originalJobtypes = ukelonn.getJobTypes();

        // Create new jobtype
        var jobtype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Skrubb badegolv")
            .transactionAmount(200.0)
            .transactionIsWork(true)
            .build();

        // Update the job type in the database
        var updatedJobtypes = ukelonn.createJobtype(jobtype);

        // Verify that a new jobtype has been added
        assertThat(updatedJobtypes).hasSizeGreaterThan(originalJobtypes.size());
    }

    @Test
    void testCreateJobtypeFailure() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a new jobtype
        var jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> ukelonn.createJobtype(jobtype));
    }

    @Test
    void testModifyPaymenttype() {
        var ukelonn = getUkelonnServiceSingleton();

        // Find a payment type
        var paymenttypes = ukelonn.getPaymenttypes();
        var paymenttype = paymenttypes.get(0);
        var originalAmount = paymenttype.transactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();

        // Update the payment type in the database
        var updatedPaymenttypes = ukelonn.modifyPaymenttype(paymenttype);

        // Verify that the updated amount is larger than the original amount
        var updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.transactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testModifyPaymenttypeFailure() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var database = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(database);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a non-existing payment type
        var paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> ukelonn.modifyPaymenttype(paymenttype));
    }

    @Test
    void testCreatePaymenttype() {
        var ukelonn = getUkelonnServiceSingleton();

        // Get the list of payment types before adding a new job type
        var originalPaymenttypes = ukelonn.getPaymenttypes();

        // Create new payment type
        var paymenttype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Vipps")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Update the payments type in the database
        var updatedPaymenttypes = ukelonn.createPaymenttype(paymenttype);

        // Verify that a new payment type has been added
        assertThat(updatedPaymenttypes).hasSizeGreaterThan(originalPaymenttypes.size());
    }

    @Test
    void testCreatePaymenttypeFailure() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a new payment type
        var paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try creating the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> ukelonn.createPaymenttype(paymenttype));
    }

    @Test
    void testPasswordsEqualAndNotEmpty() {
        var equalPasswords = PasswordsWithUser.with().password("zekret").password2("zekret").build();
        assertTrue(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(equalPasswords));
        var differentPasswords = PasswordsWithUser.with().password("zekret").password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(differentPasswords));
        var firstPasswordNull = PasswordsWithUser.with().password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordNull));
        var secondPasswordNull = PasswordsWithUser.with().password("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordNull));
        var bothPasswordsNull = PasswordsWithUser.with().build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsNull));
        var firstPasswordEmpty = PasswordsWithUser.with().password("").password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordEmpty));
        var secondPasswordEmpty = PasswordsWithUser.with().password("secret").password2("").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordEmpty));
        var bothPasswordsEmpty = PasswordsWithUser.with().password("").password2("").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsEmpty));
    }

    @Test
    void testHasUserWithNonEmptyUsername() {
        var passwords = PasswordsWithUser.with().build();
        var userWithUsername = User.with().userId(1).username("foo").build();
        passwords = PasswordsWithUser.with(passwords).user(userWithUsername).build();
        assertTrue(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        var userWithEmptyUsername = User.with().userId(1).username("").build();
        passwords = PasswordsWithUser.with(passwords).user(userWithEmptyUsername).build();
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        var userWithNullUsername = User.with().userId(1).username(null).build();
        passwords = PasswordsWithUser.with(passwords).user(userWithNullUsername).build();
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        passwords = PasswordsWithUser.with(passwords).user(null).build();
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
    }

    @Test
    void testNotifications() {
        var ukelonn = new UkelonnServiceProvider();
        var notificationsToJad = ukelonn.notificationsTo("jad");
        assertThat(notificationsToJad).isEmpty();

        // Send notification to "jad"
        var utbetalt = Notification.with().title("Ukelønn").message("150 kroner tbetalt til konto").build();
        ukelonn.notificationTo("jad", utbetalt);

        // Verify that notifcations to a different user is empty
        assertThat(ukelonn.notificationsTo("jod")).isEmpty();

        // Verify that notifications to "jad" contains the sent notification
        assertEquals(utbetalt, ukelonn.notificationsTo("jad").get(0));
    }

    @Test
    void testJoinIds() {
        assertEquals("", UkelonnServiceProvider.joinIds(null).toString());
        assertEquals("", UkelonnServiceProvider.joinIds(Collections.emptyList()).toString());
        assertEquals("1", UkelonnServiceProvider.joinIds(Arrays.asList(1)).toString());
        assertEquals("1, 2", UkelonnServiceProvider.joinIds(Arrays.asList(1, 2)).toString());
        assertEquals("1, 2, 3, 4", UkelonnServiceProvider.joinIds(Arrays.asList(1, 2, 3, 4)).toString());
        var useradmin = mock(UserManagementService.class);
        var user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        var ukelonn = getUkelonnServiceSingleton();
        ukelonn.setUserAdmin(useradmin);
        var account = ukelonn.getAccount("jad");
        var jobs = ukelonn.getJobs(account.accountId()).stream().map(Transaction::id).toList();
        assertEquals("31, 33, 34, 35, 37, 38, 39, 41, 42, 43", UkelonnServiceProvider.joinIds(jobs).toString());
    }

    /**
     * Corner case test: Tests what happens to the {@link UkelonnServiceProvider#addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(UkelonnDatabase, int)}
     * method when a resultset that throws SQLException is returned from the
     * {@link UkelonnDatabase#update(PreparedStatement)} method.
     *
     * Expect no exception to be thrown, and {@link CommonDatabaseMethods#UPDATE_FAILED} to be returned
     *
     * @throws SQLException
     */
    @Test()
    void testaddDummyPaymentToAccountSoThatAccountWillAppearInAccountsViewWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        var ukelonn = getUkelonnServiceSingleton();
        var originalDatasource = ukelonn.getDataSource();
        try {
            var datasource = mock(DataSource.class);
            var connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            var statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeUpdate()).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            var updateStatus = ukelonn.addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView("jad");
            assertEquals(-1, updateStatus);
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testGetResourceAsStringNoResource() {
        var ukelonn = new UkelonnServiceProvider();
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);
        var resource = ukelonn.getResourceAsString("finnesikke");
        assertNull(resource);
    }

    @Test
    void testEarningsSumOverYear() {
        var ukelonn = getUkelonnServiceSingleton();
        var statistics = ukelonn.earningsSumOverYear("jad");
        assertThat(statistics).isNotEmpty();
        var firstYear = statistics.get(0);
        assertEquals(1250.0, firstYear.sum(), 0.0);
        assertEquals(2016, firstYear.year());
    }

    @Test
    void testEarningsSumOverYearWhenSqlExceptionIsThrown() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Try update the payment type in the database, which should cause an exception
        var statistics = ukelonn.earningsSumOverYear("jad");
        assertEquals(0, statistics.size()); // No exception was thrown but result is empty

        // Verify that the error has been logged
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get sum of earnings per year for account");
    }

    @Test
    void testEarningsSumOverMonth() {
        var ukelonn = getUkelonnServiceSingleton();
        var statistics = ukelonn.earningsSumOverMonth("jad");
        assertThat(statistics).isNotEmpty();
        var firstYear = statistics.get(0);
        assertEquals(125.0, firstYear.sum(), 0.0);
        assertEquals(2016, firstYear.year());
        assertEquals(7, firstYear.month());
    }

    @Test
    void testEarningsSumOverMonthWhenSqlExceptionIsThrown() throws Exception {
        var ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Try update the payment type in the database, which should cause an exception
        var statistics = ukelonn.earningsSumOverMonth("jad");
        assertEquals(0, statistics.size()); // No exception was thrown but result is empty

        // Verify that the error has been logged
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get sum of earnings per month for account");
    }

    @Test
    void testGetCreateModifyAndDeleteBonuses() {
        var ukelonn = getUkelonnServiceSingleton();
        var initialBonusCount = ukelonn.getAllBonuses().size();

        // Verify that without any bonuses addBonus() will
        // return the job registration transaction amount unchanged
        var amount = 25.0;
        assertEquals(amount, ukelonn.addBonus(amount), 0.0);

        // Add an enabled bonus with start date before today and end date after today
        // this will show up as an active bonus
        var julestart = Date.from(LocalDateTime.now().minusDays(3).toInstant(ZoneOffset.UTC));
        var juleslutt = Date.from(LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.UTC));
        var julebonus = Bonus.with()
            .bonusId(0)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt betaling for utførte jobber")
            .bonusFactor(2.0)
            .startDate(julestart)
            .endDate(juleslutt)
            .build();
        var enabledBonus = ukelonn.createBonus(julebonus).stream().filter(b -> "Julebonus".equals(b.title())).findFirst().get();
        var bonusCountWithOneAddedBonus = ukelonn.getAllBonuses().size();
        assertThat(bonusCountWithOneAddedBonus).isGreaterThan(initialBonusCount);

        // Verify that the active bonus will double the payment
        // of registered jobs.
        var expectAmount = 2 * amount;
        assertEquals(expectAmount, ukelonn.addBonus(amount), 0.0);

        // Add an extra active bonus to verify that two
        // concurrent bonuses will give the expected result
        var julebonus2 = ukelonn.createBonus(Bonus.with()
            .bonusId(0)
            .enabled(true)
            .title("Julebonuz")
            .description("Dobbelt betaling for utførte jobber")
            .bonusFactor(1.25)
            .startDate(julestart)
            .endDate(juleslutt)
            .build()).stream().filter(b -> "Julebonuz".equals(b.title())).findFirst().get();
        var expectAmount2 = julebonus.bonusFactor() * amount + julebonus2.bonusFactor() * amount - amount;
        assertEquals(expectAmount2, ukelonn.addBonus(amount), 0.0);
        ukelonn.deleteBonus(julebonus2);

        // Add an inactive bonus with start and end date both in the future
        // Since we're outside of the startDate/endDate, this will not show up
        // as an active bonus
        var paaskestart = Date.from(LocalDateTime.now().plusDays(5).toInstant(ZoneOffset.UTC));
        var paaskeslutt = Date.from(LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC));
        var paaskebonus = Bonus.with()
            .enabled(true)
            .title("Påskebonus")
            .description("Dobbelt betaling for utførte jobber")
            .bonusFactor(2.0)
            .startDate(paaskestart)
            .endDate(paaskeslutt)
            .build();
        var inactiveBonus = ukelonn.createBonus(paaskebonus).stream().filter(b -> "Påskebonus".equals(b.title())).findFirst().get();
        assertThat(ukelonn.getAllBonuses()).hasSizeGreaterThan(bonusCountWithOneAddedBonus);

        // Verify that active count is larger than 0 and is less than total count
        var activeBonuses = ukelonn.getActiveBonuses();
        assertThat(activeBonuses).isNotEmpty();
        var activeBonusCount = activeBonuses.size();
        assertThat(ukelonn.getAllBonuses()).hasSizeGreaterThan(activeBonusCount);

        // Verify that active count is greater than initial count
        assertThat(activeBonusCount).isGreaterThan(initialBonusCount);

        // Change the enabled bonus to set the enabled flag to false, and keep the rest of the values
        // (ie. deactivate the currenly active bonus)
        var bonuses = ukelonn.modifyBonus(disableBonus(enabledBonus));
        var disabledBonus = bonuses.stream().filter(b -> b.bonusId() == enabledBonus.bonusId()).findFirst().get();
        assertFalse(disabledBonus.enabled());
        assertEquals(enabledBonus.title(), disabledBonus.title());
        assertEquals(enabledBonus.description(), disabledBonus.description());
        assertEquals(enabledBonus.bonusFactor(), disabledBonus.bonusFactor(), 0.0);
        assertEquals(enabledBonus.startDate(), disabledBonus.startDate());
        assertEquals(enabledBonus.endDate(), disabledBonus.endDate());

        // Verify that the active bonus count is less than before the update
        assertThat(ukelonn.getActiveBonuses()).hasSizeLessThan(activeBonusCount);

        // Delete both bonuses and verify that the count decreases
        var countBeforeDelete = bonuses.size();
        bonuses = ukelonn.deleteBonus(disabledBonus);
        var countAfterFirstDelete = bonuses.size();
        assertThat(countAfterFirstDelete).isLessThan(countBeforeDelete);
        bonuses = ukelonn.deleteBonus(inactiveBonus);
        assertEquals(initialBonusCount, bonuses.size());
    }

    @Test
    void testGetActiveBonusesWithSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        var bonuses = ukelonn.getActiveBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of active bonuses");
    }

    @Test
    void testGetAllBonusesWithSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        var bonuses = ukelonn.getAllBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of all bonuses");
    }

    @Test
    void testAddBonusWithSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        var bonuses = ukelonn.createBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to add Bonus");
    }

    @Test
    void testUpdateBonusWithSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        var bonuses = ukelonn.modifyBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to update Bonus");
    }

    @Test
    void testDeleteBonusWithSQLException() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        var bonuses = ukelonn.deleteBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to delete Bonus");
    }

    @Test
    void testAddRoleIfNotPresentWhenRoleIsPresent() {
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        var userrole = Role.with().id(2).rolename(UKELONNUSER_ROLE).description("ukelonn user").build();
        when(useradmin.getRoles()).thenReturn(Arrays.asList(userrole, adminrole));
        var ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        var role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().rolename());
    }

    @Test
    void testAddRoleIfNotPresentWhenRoleIsNotPresent() {
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        var userrole = Role.with().id(2).rolename(UKELONNUSER_ROLE).description("ukelonn user").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(userrole));
        when(useradmin.addRole(any())).thenReturn(Arrays.asList(userrole, adminrole));
        var ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        var role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().rolename());
    }

    @Test
    void testAddAdminroleToUserAdminWhenRoleIsMissing() {
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        var useradmin = mock(UserManagementService.class);
        var ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(1)).addUserRoles(any());
    }

    @Test
    void testAddAdminroleToUserAdminWhenRoleIsPresent() {
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        var ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(0)).addUserRoles(any());
    }

    @Test
    void testAddAdminroleToUserAdminWhenAdminUserIsNotPresent() {
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenThrow(AuthserviceException.class);
        var ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(0)).getRolesForUser(anyString());
        verify(useradmin, times(0)).addUserRoles(any());
    }

    @Test
    void testDefaultLocale() {
        var ukelonn = new UkelonnServiceProvider();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        assertEquals(NB_NO, ukelonn.defaultLocale());
    }

    @Test
    void testAvailableLocales() {
        var ukelonn = new UkelonnServiceProvider();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        var locales = ukelonn.availableLocales();
        assertThat(locales).isNotEmpty().contains(LocaleBean.with().locale(ukelonn.defaultLocale()).build());
    }

    @Test
    void testDisplayTextsForDefaultLocale() {
        var ukelonn = new UkelonnServiceProvider();
        var useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        var displayTexts = ukelonn.displayTexts(ukelonn.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

    private Bonus disableBonus(Bonus bonus) {
        return Bonus.with().bonusId(bonus.bonusId()).enabled(false).iconurl(bonus.iconurl()).title(bonus.title()).description(bonus.description()).bonusFactor(bonus.bonusFactor()).startDate(bonus.startDate()).endDate(bonus.endDate()).build();
    }
}
