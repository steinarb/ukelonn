/*
 * Copyright 2018-2020 Steinar Bang
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
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.users.UserManagementServiceProvider;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.SumYearMonth;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

public class UkelonnServiceProviderTest {

    @BeforeClass
    public static void setupForAllTests() throws Exception {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetAccounts() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, "jad", "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        provider.setUserAdmin(useradmin);
        List<Account> accounts = provider.getAccounts();
        assertThat(accounts.size()).isGreaterThan(1);
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
    @SuppressWarnings("unchecked")
    @Test()
    public void testGetAccountsWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ResultSet resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            List<Account> accounts = ukelonn.getAccounts();
            assertEquals("Expected a non-null, empty list", 0, accounts.size());
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
    public void testGetAccountsNullResultSet() throws Exception {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(datasource);
            List<Account> accounts = ukelonn.getAccounts();
            assertEquals("Expected a non-null, empty list", 0, accounts.size());
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testGetAccount() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, "jad", "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        provider.setUserAdmin(useradmin);
        Account account = provider.getAccount("jad");
        assertEquals("jad", account.getUsername());
        assertEquals("Jane", account.getFirstName());
        assertEquals("Doe", account.getLastName());
        List<Transaction> jobs = provider.getJobs(account.getAccountId());
        assertEquals(10, jobs.size());
        List<Transaction> payments = provider.getPayments(account.getAccountId());
        assertEquals(10, payments.size());
    }

    /**
     * Corner case test: test what happens when an account has no transactions
     * (the query result is empty)
     */
    @Test
    public void testGetAccountInfoFromDatabaseAccountHasNoTransactions() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        assertThrows(UkelonnException.class, () -> {
                provider.getAccount("on");
            });
    }

    /**
     * Corner case test: test what happens when trying to get an
     * account for a username that isn't present in the database
     */
    @Test
    public void testGetAccountInfoFromDatabaseWhenAccountDoesNotExist() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        assertThrows(UkelonnException.class, () -> {
                ukelonn.getAccount("unknownuser");
            });
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
    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccountInfoFromDatabaseWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            PreparedStatement statement = mock(PreparedStatement.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ResultSet resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            assertThrows(UkelonnException.class, () -> {
                    ukelonn.getAccount("jad");
                });
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testAddAccount() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        UserManagementServiceProvider usermanagement = new UserManagementServiceProvider();
        usermanagement.setLogservice(ukelonn.getLogservice());
        usermanagement.setDataSource(ukelonn.getDataSource());
        ukelonn.setUserAdmin(usermanagement);

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Create a user in the database, and retrieve it (to get the user id)
        List<no.priv.bang.osgiservice.users.User> updatedUsers = usermanagement.addUser(passwords);
        no.priv.bang.osgiservice.users.User createdUser = updatedUsers.stream().filter(u -> newUsername.equals(u.getUsername())).findFirst().get();

        // Add a new account to the database
        User userWithUserId = new User(createdUser.getUserid(), newUsername, newEmailaddress, newFirstname, newLastname);
        Account newAccount = ukelonn.addAccount(userWithUserId);
        assertThat(newAccount.getAccountId()).isPositive();
        assertEquals(0.0, newAccount.getBalance(), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddAccountWhenSqlExceptionIsThrown() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        UserManagementServiceProvider usermanagement = new UserManagementServiceProvider();
        usermanagement.setDataSource(getUkelonnServiceSingleton().getDataSource());
        usermanagement.setLogservice(getUkelonnServiceSingleton().getLogservice());
        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        ukelonn.setLogservice(getUkelonnServiceSingleton().getLogservice());

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Create a user in the database, expected to fail
        assertThrows(AuthserviceException.class, () -> {
                usermanagement.addUser(passwords);
            });
    }

    @Test
    public void testGetJobs() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount(username);
        List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
        assertEquals(10, jobs.size());
    }

    @Test
    public void testRegisterPerformedJob() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jad";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jad@gmail.com", "Jane", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            double oldBalance = account.getBalance();
            TransactionType jobtype = ukelonn.getJobTypes().get(0);
            PerformedTransaction performedJob = new PerformedTransaction(account, jobtype.getId(), jobtype.getTransactionAmount(), new Date());
            Account updatedAccount = ukelonn.registerPerformedJob(performedJob);
            assertThat(updatedAccount.getBalance()).isGreaterThan(oldBalance);
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
    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterNewJobInDatabaseWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, "jad", "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount("jad");
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeUpdate()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            PerformedTransaction performedJob = new PerformedTransaction(account, 1, 45.0, new Date());
            assertThrows(UkelonnException.class, () -> {
                    ukelonn.registerPerformedJob(performedJob);
                });
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testDeleteAllJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jod@gmail.com", "John", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Integer> idsOfJobsToDelete = Arrays.asList(jobs.get(0).getId(), jobs.get(1).getId());

            // Do the delete
            List<Transaction> jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.getAccountId(), idsOfJobsToDelete);

            // Check that the job list that was two items earlier is now empty
            assertEquals(0, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testDeleteSomeJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jod@gmail.com", "John", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Integer> idsOfJobsToDelete = Arrays.asList(jobs.get(0).getId());

            // Do the delete
            List<Transaction> jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.getAccountId(), idsOfJobsToDelete);

            // Check that the job list that was two items earlier is now empty
            assertEquals(1, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testDeleteJobsWithErrorOnClosingStatement() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database with a prepared statement that will fail on close
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        doThrow(SQLException.class).when(statement).close();
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        assertEquals(0, logservice.getLogmessages().size());
        ukelonn.deleteJobsFromAccount(1, Arrays.asList(1, 2, 3));
        assertEquals("Expected the errors to be logged", 2, logservice.getLogmessages().size());
    }

    @Test
    public void verifyDeletingNoJobsOfUserHasNoEffect() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jod@gmail.com", "John", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);

            // Check preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());

            // Delete with an empty argument
            List<Integer> idsOfJobsToDelete = Collections.emptyList();
            List<Transaction> jobsAfterDelete = ukelonn.deleteJobsFromAccount(account.getAccountId(), idsOfJobsToDelete);

            // Verify that nothing has been deleted
            assertEquals(2, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyThatTryingToDeletePaymentsAsJobsWillDoNothing() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jod@gmail.com", "John", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);

            // Check the preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
            assertEquals(1, payments.size());

            // Try deleting the payment as a job
            List<Integer> idsOfJobsToDelete = Arrays.asList(payments.get(0).getId());
            List<Transaction> jobsAfterAttemptedDelete = ukelonn.deleteJobsFromAccount(account.getAccountId(), idsOfJobsToDelete);

            // Verify that both the jobs and payments are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            List<Transaction> paymentsAfterAttemptedDelete = ukelonn.getPayments(account.getAccountId());
            assertEquals(1, paymentsAfterAttemptedDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyThatTryingToDeleteJobsOfDifferentAccountWillDoNothing() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jod@gmail.com", "John", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            String otherUsername = "jad";
            Account otherAccount = ukelonn.getAccount(otherUsername);

            // Check the preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Transaction> otherAccountJobs = ukelonn.getJobs(otherAccount.getAccountId());
            assertEquals(10, otherAccountJobs.size());

            // Try deleting the payment as a job
            List<Integer> idsOfJobsToDelete = Arrays.asList(otherAccountJobs.get(0).getId(), otherAccountJobs.get(1).getId(), otherAccountJobs.get(2).getId(), otherAccountJobs.get(3).getId(), otherAccountJobs.get(4).getId(), otherAccountJobs.get(5).getId(), otherAccountJobs.get(6).getId(), otherAccountJobs.get(7).getId(), otherAccountJobs.get(8).getId(), otherAccountJobs.get(9).getId());
            List<Transaction> jobsAfterAttemptedDelete = ukelonn.deleteJobsFromAccount(account.getAccountId(), idsOfJobsToDelete);

            // Verify that both the account's jobs and and the other account's jobs are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            List<Transaction> otherAccountsJobsAfterAttemptedDelete = ukelonn.getJobs(otherAccount.getAccountId());
            assertThat(otherAccountsJobsAfterAttemptedDelete).containsAll(otherAccountJobs);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Mock a database that will fail
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        // trying to set the parameter here will throw an UkelonnException
        assertThrows(UkelonnException.class, () -> {
                ukelonn.addParametersToDeleteJobsStatement(1, statement);
            });
    }

    @Test
    public void testUpdateJob() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jad";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jad@gmail.com", "Jane", "Doe");
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            Transaction job = ukelonn.getJobs(account.getAccountId()).get(0);
            int jobId = job.getId();

            // Save initial values of the job for comparison later
            Integer originalTransactionTypeId = job.getTransactionType().getId();
            Date originalTransactionTime = job.getTransactionTime();
            double originalTransactionAmount = job.getTransactionAmount();

            // Find a different job type that has a different amount
            TransactionType newJobType = findJobTypeWithDifferentIdAndAmount(ukelonn, originalTransactionTypeId, originalTransactionAmount);

            // Create a new job object with a different jobtype and the same id
            Date now = new Date();
            UpdatedTransaction editedJob = new UpdatedTransaction(jobId, account.getAccountId(), newJobType.getId(), now, newJobType.getTransactionAmount());

            List<Transaction> updatedJobs = ukelonn.updateJob(editedJob);

            Transaction editedJobFromDatabase = updatedJobs.stream().filter(t->t.getId() == job.getId()).collect(Collectors.toList()).get(0);

            assertEquals(editedJob.getTransactionTypeId(), editedJobFromDatabase.getTransactionType().getId().intValue());
            assertThat(editedJobFromDatabase.getTransactionTime().getTime()).isGreaterThan(originalTransactionTime.getTime());
            assertEquals(editedJob.getTransactionAmount(), editedJobFromDatabase.getTransactionAmount(), 0.0);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testUpdateJobGetSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());

        ukelonn.setDataSource(datasource);

        UpdatedTransaction updatedTransaction = new UpdatedTransaction();
        assertThrows(UkelonnException.class, () -> {
                ukelonn.updateJob(updatedTransaction);
            });
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return ukelonn.getJobTypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }

    @Test
    public void testGetPayments() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, username, "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount(username);
        List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
        assertEquals(10, payments.size());
    }

    @Test
    public void testGetPaymenttypes() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        assertEquals(2, paymenttypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetPaymenttypesWithDatabasePreparestatementFailure() throws Exception {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        MockLogService logservice = new MockLogService();
        try {
            ukelonn.setLogservice(logservice);
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            assertEquals(0, logservice.getLogmessages().size()); // Verify precondition: no logmessages
            List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
            assertEquals("Expected empty list", 0, paymenttypes.size());
            assertEquals("Expect database error to be logged", 1, logservice.getLogmessages().size());
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testGetPaymenttypesWithDatabaseFailure() throws Exception {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(datasource);
            List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
            assertEquals("Expected empty list", 0, paymenttypes.size());
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testRegisterPayment() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, "jad", "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);

        // Create the request
        Account account = ukelonn.getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        PerformedTransaction payment = new PerformedTransaction(account, paymenttypes.get(0).getId(), account.getBalance(), new Date());

        // Run the method under test
        Account result = ukelonn.registerPayment(payment);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPaymentWithDatabaseFailure() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create a mock log service
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create the request
        Account account = new Account(1, "jad", "Jane", "Doe", 2.0);
        PerformedTransaction payment = new PerformedTransaction(account, 1, 2.0, new Date());

        // Run the method under test
        Account result = ukelonn.registerPayment(payment);

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
    public void testGetJobTypesNullResultSet() throws Exception {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource database = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(database.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ukelonn.setDataSource(database);
            List<TransactionType> jobtypes = ukelonn.getJobTypes();
            assertEquals("Expected a non-null, empty list", 0, jobtypes.size());
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
    @SuppressWarnings("unchecked")
    @Test()
    public void testGetJobTypesWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            ResultSet resultset = mock(ResultSet.class);
            when(resultset.next()).thenThrow(SQLException.class);
            when(statement.executeQuery()).thenReturn(resultset);
            ukelonn.setDataSource(datasource);
            List<TransactionType> jobtypes = ukelonn.getJobTypes();
            assertEquals("Expected a non-null, empty map", 0, jobtypes.size());
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testModifyJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Find a jobtyoe
        List<TransactionType> jobtypes = ukelonn.getJobTypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype.setTransactionAmount(originalAmount + 1);

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.modifyJobtype(jobtype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyJobtypeFailure() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a non-existing jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.modifyJobtype(jobtype);
            });
    }

    @Test
    public void testCreateJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get the list of jobtypes before adding a new job type
        List<TransactionType> originalJobtypes = ukelonn.getJobTypes();

        // Create new jobtyoe
        TransactionType jobtype = new TransactionType(-1, "Skrubb badegolv", 200.0, true, false);

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.createJobtype(jobtype);

        // Verify that a new jobtype has been added
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateJobtypeFailure() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a new jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.createJobtype(jobtype);
            });
    }

    @Test
    public void testModifyPaymenttype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Find a payment type
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(0);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype.setTransactionAmount(originalAmount + 1);

        // Update the payment type in the database
        List<TransactionType> updatedPaymenttypes = ukelonn.modifyPaymenttype(paymenttype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifyPaymenttypeFailure() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource database = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(database);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a non-existing payment type
        TransactionType paymenttype = new TransactionType(-2001, "Bar", 0.0, false, true);

        // Try update the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.modifyPaymenttype(paymenttype);
            });
    }

    @Test
    public void testCreatePaymenttype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get the list of payment types before adding a new job type
        List<TransactionType> originalPaymenttypes = ukelonn.getPaymenttypes();

        // Create new payment type
        TransactionType paymenttype = new TransactionType(-1, "Vipps", 0.0, false, true);

        // Update the payments type in the database
        List<TransactionType> updatedPaymenttypes = ukelonn.createPaymenttype(paymenttype);

        // Verify that a new payment type has been added
        assertThat(updatedPaymenttypes.size()).isGreaterThan(originalPaymenttypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePaymenttypeFailure() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a new payment type
        TransactionType paymenttype = new TransactionType(-2001, "Bar", 0.0, false, true);

        // Try creating the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.createPaymenttype(paymenttype);
            });
    }

    @Test
    public void testPasswordsEqualAndNotEmpty() {
        PasswordsWithUser equalPasswords = new PasswordsWithUser(null, "zekret", "zekret");
        assertTrue(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(equalPasswords));
        PasswordsWithUser differentPasswords = new PasswordsWithUser(null, "zekret", "secret");
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(differentPasswords));
        PasswordsWithUser firstPasswordNull = new PasswordsWithUser(null, null, "secret");
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordNull));
        PasswordsWithUser secondPasswordNull = new PasswordsWithUser(null, "secret", null);
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordNull));
        PasswordsWithUser bothPasswordsNull = new PasswordsWithUser(null, null, null);
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsNull));
        PasswordsWithUser firstPasswordEmpty = new PasswordsWithUser(null, "", "secret");
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordEmpty));
        PasswordsWithUser secondPasswordEmpty = new PasswordsWithUser(null, "secret", "");
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordEmpty));
        PasswordsWithUser bothPasswordsEmpty = new PasswordsWithUser(null, "", "");
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsEmpty));
    }

    @Test
    public void testHasUserWithNonEmptyUsername() {
        PasswordsWithUser passwords = new PasswordsWithUser();
        User userWithUsername = new User(1, "foo", null, null, null);
        passwords.setUser(userWithUsername);
        assertTrue(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithEmptyUsername = new User(1, "", null, null, null);
        passwords.setUser(userWithEmptyUsername);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithNullUsername = new User(1, null, null, null, null);
        passwords.setUser(userWithNullUsername);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        passwords.setUser(null);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
    }

    @Test
    public void testNotifications() {
        UkelonnService ukelonn = new UkelonnServiceProvider();
        List<Notification> notificationsToJad = ukelonn.notificationsTo("jad");
        assertThat(notificationsToJad).isEmpty();

        // Send notification to "jad"
        Notification utbetalt = new Notification("Ukelønn", "150 kroner betalt til konto");
        ukelonn.notificationTo("jad", utbetalt);

        // Verify that notifcations to a different user is empty
        assertThat(ukelonn.notificationsTo("jod")).isEmpty();

        // Verify that notifications to "jad" contains the sent notification
        assertEquals(utbetalt, ukelonn.notificationsTo("jad").get(0));
    }

    @Test
    public void testJoinIds() {
        assertEquals("", UkelonnServiceProvider.joinIds(null).toString());
        assertEquals("", UkelonnServiceProvider.joinIds(Collections.emptyList()).toString());
        assertEquals("1", UkelonnServiceProvider.joinIds(Arrays.asList(1)).toString());
        assertEquals("1, 2", UkelonnServiceProvider.joinIds(Arrays.asList(1, 2)).toString());
        assertEquals("1, 2, 3, 4", UkelonnServiceProvider.joinIds(Arrays.asList(1, 2, 3, 4)).toString());
        UserManagementServiceProvider useradmin = mock(UserManagementServiceProvider.class);
        no.priv.bang.osgiservice.users.User user = new no.priv.bang.osgiservice.users.User(1, "jad", "jad@gmail.com", "Jane", "Doe");
        when(useradmin.getUser(anyString())).thenReturn(user);
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount("jad");
        List<Integer> jobs = ukelonn.getJobs(account.getAccountId()).stream().map(Transaction::getId).collect(Collectors.toList());
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
    @SuppressWarnings("unchecked")
    @Test()
    public void testaddDummyPaymentToAccountSoThatAccountWillAppearInAccountsViewWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        DataSource originalDatasource = ukelonn.getDataSource();
        try {
            DataSource datasource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            when(datasource.getConnection()).thenReturn(connection);
            PreparedStatement statement = mock(PreparedStatement.class);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeUpdate()).thenThrow(SQLException.class);
            ukelonn.setDataSource(datasource);
            int updateStatus = ukelonn.addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView("jad");
            assertEquals(-1, updateStatus);
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    public void testGetResourceAsStringNoResource() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);
        String resource = ukelonn.getResourceAsString("finnesikke");
        assertNull(resource);
    }

    @Test
    public void testEarningsSumOverYear() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        List<SumYear> statistics = ukelonn.earningsSumOverYear("jad");
        assertThat(statistics.size()).isPositive();
        SumYear firstYear = statistics.get(0);
        assertEquals(1250.0, firstYear.getSum(), 0.0);
        assertEquals(2016, firstYear.getYear());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEarningsSumOverYearWhenSqlExceptionIsThrown() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Try update the payment type in the database, which should cause an exception
        List<SumYear> statistics = ukelonn.earningsSumOverYear("jad");
        assertEquals(0, statistics.size()); // No exception was thrown but result is empty

        // Verify that the error has been logged
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get sum of earnings per year for account");
    }

    @Test
    public void testEarningsSumOverMonth() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        List<SumYearMonth> statistics = ukelonn.earningsSumOverMonth("jad");
        assertThat(statistics.size()).isPositive();
        SumYearMonth firstYear = statistics.get(0);
        assertEquals(125.0, firstYear.getSum(), 0.0);
        assertEquals(2016, firstYear.getYear());
        assertEquals(7, firstYear.getMonth());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEarningsSumOverMonthWhenSqlExceptionIsThrown() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Try update the payment type in the database, which should cause an exception
        List<SumYearMonth> statistics = ukelonn.earningsSumOverMonth("jad");
        assertEquals(0, statistics.size()); // No exception was thrown but result is empty

        // Verify that the error has been logged
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get sum of earnings per month for account");
    }

    @Test
    public void testGetCreateModifyAndDeleteBonuses() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        int initialBonusCount = ukelonn.getAllBonuses().size();

        // Verify that without any bonuses addBonus() will
        // return the job registration transaction amount unchanged
        double amount = 25.0;
        assertEquals(amount, ukelonn.addBonus(amount), 0.0);

        // Add an enabled bonus with start date before today and end date after today
        // this will show up as an active bonus
        Date julestart = Date.from(LocalDateTime.now().minusDays(3).toInstant(ZoneOffset.UTC));
        Date juleslutt = Date.from(LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.UTC));
        Bonus julebonus = new Bonus(0, true, null, "Julebonus", "Dobbelt betaling for utførte jobber", 2.0, julestart, juleslutt);
        Bonus enabledBonus = ukelonn.createBonus(julebonus).stream().filter(b -> "Julebonus".equals(b.getTitle())).findFirst().get();
        int bonusCountWithOneAddedBonus = ukelonn.getAllBonuses().size();
        assertThat(bonusCountWithOneAddedBonus).isGreaterThan(initialBonusCount);

        // Verify that the active bonus will double the payment
        // of registered jobs.
        double expectAmount = 2 * amount;
        assertEquals(expectAmount, ukelonn.addBonus(amount), 0.0);

        // Add an extra active bonus to verify that two
        // concurrent bonuses will give the expected result
        Bonus julebonus2 = ukelonn.createBonus(new Bonus(0, true, null, "Julebonuz", "Dobbelt betaling for utførte jobber", 1.25, julestart, juleslutt)).stream().filter(b -> "Julebonuz".equals(b.getTitle())).findFirst().get();
        double expectAmount2 = julebonus.getBonusFactor() * amount + julebonus2.getBonusFactor() * amount - amount;
        assertEquals(expectAmount2, ukelonn.addBonus(amount), 0.0);
        ukelonn.deleteBonus(julebonus2);

        // Add an inactive bonus with start and end date both in the future
        // Since we're outside of the startDate/endDate, this will not show up
        // as an active bonus
        Date paaskestart = Date.from(LocalDateTime.now().plusDays(5).toInstant(ZoneOffset.UTC));
        Date paaskeslutt = Date.from(LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC));
        Bonus paaskebonus = new Bonus(0, true, null, "Påskebonus", "Dobbelt betaling for utførte jobber", 2.0, paaskestart, paaskeslutt);
        Bonus inactiveBonus = ukelonn.createBonus(paaskebonus).stream().filter(b -> "Påskebonus".equals(b.getTitle())).findFirst().get();
        assertThat(ukelonn.getAllBonuses().size()).isGreaterThan(bonusCountWithOneAddedBonus);

        // Verify that active count is larger than 0 and is less than total count
        List<Bonus> activeBonuses = ukelonn.getActiveBonuses();
        assertThat(activeBonuses).isNotEmpty();
        int activeBonusCount = activeBonuses.size();
        assertThat(ukelonn.getAllBonuses().size()).isGreaterThan(activeBonusCount);

        // Verify that active count is greater than initial count
        assertThat(activeBonusCount).isGreaterThan(initialBonusCount);

        // Change the enabled bonus to set the enabled flag to false, and keep the rest of the values
        // (ie. deactivate the currenly active bonus)
        List<Bonus> bonuses = ukelonn.modifyBonus(disableBonus(enabledBonus));
        Bonus disabledBonus = bonuses.stream().filter(b -> b.getBonusId() == enabledBonus.getBonusId()).findFirst().get();
        assertFalse(disabledBonus.isEnabled());
        assertEquals(enabledBonus.getTitle(), disabledBonus.getTitle());
        assertEquals(enabledBonus.getDescription(), disabledBonus.getDescription());
        assertEquals(enabledBonus.getBonusFactor(), disabledBonus.getBonusFactor(), 0.0);
        assertEquals(enabledBonus.getStartDate(), disabledBonus.getStartDate());
        assertEquals(enabledBonus.getEndDate(), disabledBonus.getEndDate());

        // Verify that the active bonus count is less than before the update
        assertThat(ukelonn.getActiveBonuses().size()).isLessThan(activeBonusCount);

        // Delete both bonuses and verify that the count decreases
        int countBeforeDelete = bonuses.size();
        bonuses = ukelonn.deleteBonus(disabledBonus);
        int countAfterFirstDelete = bonuses.size();
        assertThat(countAfterFirstDelete).isLessThan(countBeforeDelete);
        bonuses = ukelonn.deleteBonus(inactiveBonus);
        assertEquals(initialBonusCount, bonuses.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetActiveBonusesWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate();

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.getActiveBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of active bonuses");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAllBonusesWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate();

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.getAllBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of all bonuses");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate();

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.createBonus(new Bonus());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to add Bonus");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate();

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.modifyBonus(new Bonus());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to update Bonus");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate();

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.deleteBonus(new Bonus());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to delete Bonus");
    }

    @Test
    public void testAddRoleIfNotPresentWhenRoleIsPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = new Role(1, UKELONNADMIN_ROLE, "ukelonn administrator");
        Role userrole = new Role(2, UKELONNUSER_ROLE, "ukelonn user");
        when(useradmin.getRoles()).thenReturn(Arrays.asList(userrole, adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        Optional<Role> role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().getRolename());
    }

    @Test
    public void testAddRoleIfNotPresentWhenRoleIsNotPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = new Role(1, UKELONNADMIN_ROLE, "ukelonn administrator");
        Role userrole = new Role(2, UKELONNUSER_ROLE, "ukelonn user");
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(userrole));
        when(useradmin.addRole(any())).thenReturn(Arrays.asList(userrole, adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        Optional<Role> role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().getRolename());
    }

    @Test
    public void testAddAdminroleToUserAdminWhenRoleIsMissing() {
        Role adminrole = new Role(1, UKELONNADMIN_ROLE, "ukelonn administrator");
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(1)).addUserRoles(any());
    }

    @Test
    public void testAddAdminroleToUserAdminWhenRoleIsPresent() {
        Role adminrole = new Role(1, UKELONNADMIN_ROLE, "ukelonn administrator");
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(0)).addUserRoles(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddAdminroleToUserAdminWhenAdminUserIsNotPresent() {
        Role adminrole = new Role(1, UKELONNADMIN_ROLE, "ukelonn administrator");
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenThrow(AuthserviceException.class);
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(0)).getRolesForUser(anyString());
        verify(useradmin, times(0)).addUserRoles(any());
    }

    @Test
    public void testDefaultLocale() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        assertEquals("nb_NO", ukelonn.defaultLocale());
    }

    @Test
    public void testAvailableLocales() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        List<String> locales = ukelonn.availableLocales();
        assertThat(locales).isNotEmpty().contains(ukelonn.defaultLocale());
    }

    @Test
    public void testDisplayTextsForDefaultLocale() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        Map<String, String> displayTexts = ukelonn.displayTexts(ukelonn.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

    private Bonus disableBonus(Bonus bonus) {
        return new Bonus(bonus.getBonusId(), false, bonus.getIconurl(), bonus.getTitle(), bonus.getDescription(), bonus.getBonusFactor(), bonus.getStartDate(), bonus.getEndDate());
    }
}
