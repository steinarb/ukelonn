/*
 * Copyright 2018-2021 Steinar Bang
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
import no.priv.bang.ukelonn.beans.LocaleBean;
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

class UkelonnServiceProviderTest {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");

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
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        provider.setUserAdmin(useradmin);
        List<Account> accounts = provider.getAccounts();
        assertThat(accounts.size()).isGreaterThan(1);
    }

    @Test
    void testGetAccountsWithUserMissing() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        var originalLogService = provider.getLogservice();
        try {
            var logservice = new MockLogService();
            provider.setLogservice(logservice);
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with().userid(1)
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
            List<Account> accounts = provider.getAccounts();
            assertThat(accounts.size()).isGreaterThan(1);
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
            assertEquals(0, accounts.size(), "Expected a non-null, empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testGetAccount() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
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
    void testGetAccountInfoFromDatabaseAccountHasNoTransactions() {
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
    void testGetAccountInfoFromDatabaseWhenAccountDoesNotExist() {
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
    @Test
    void testGetAccountInfoFromDatabaseWhenSQLExceptionIsThrown() throws SQLException {
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
    void testAddAccount() {
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
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create a user in the database, and retrieve it (to get the user id)
        List<no.priv.bang.osgiservice.users.User> updatedUsers = usermanagement.addUser(passwords);
        no.priv.bang.osgiservice.users.User createdUser = updatedUsers.stream().filter(u -> newUsername.equals(u.getUsername())).findFirst().get();

        // Add a new account to the database
        User userWithUserId = User.with()
            .userId(createdUser.getUserid())
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        Account newAccount = ukelonn.addAccount(userWithUserId);
        assertThat(newAccount.getAccountId()).isPositive();
        assertEquals(0.0, newAccount.getBalance(), 0);
    }

    @Test
    void testAddAccountWhenSqlExceptionIsThrown() throws Exception {
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
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create a user in the database, expected to fail
        assertThrows(AuthserviceException.class, () -> {
                usermanagement.addUser(passwords);
            });
    }

    @Test
    void testGetJobs() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username(username)
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount(username);
        List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
        assertEquals(10, jobs.size());
    }

    @Test
    void testRegisterPerformedJob() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jad";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jad@gmail.com")
                .firstname("Jane")
                .lastname("Doe")
                .build();
            when(useradmin.getUser(anyString())).thenReturn(user);
            ukelonn.setUserAdmin(useradmin);
            Account account = ukelonn.getAccount(username);
            double oldBalance = account.getBalance();
            TransactionType jobtype = ukelonn.getJobTypes().get(0);
            PerformedTransaction performedJob = PerformedTransaction.with()
                .account(account)
                .transactionTypeId(jobtype.getId())
                .transactionAmount(jobtype.getTransactionAmount())
                .transactionDate(new Date())
                .build();
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
    @Test
    void testRegisterNewJobInDatabaseWhenSQLExceptionIsThrown() throws SQLException {
        // Swap the real derby database with a mock
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
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
            PerformedTransaction performedJob = PerformedTransaction.with()
                .account(account)
                .transactionTypeId(1)
                .transactionAmount(45.0)
                .transactionDate(new Date())
                .build();
            assertThrows(UkelonnException.class, () -> {
                    ukelonn.registerPerformedJob(performedJob);
                });
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testDeleteAllJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
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
    void testDeleteSomeJobsOfUser() throws Exception {
        try {
            // Create the delete arguments
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
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
    void testDeleteJobsWithErrorOnClosingStatement() throws Exception {
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
        assertEquals(2, logservice.getLogmessages().size(), "Expected the errors to be logged");
    }

    @Test
    void verifyDeletingNoJobsOfUserHasNoEffect() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
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
    void verifyThatTryingToDeletePaymentsAsJobsWillDoNothing() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
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
    void verifyThatTryingToDeleteJobsOfDifferentAccountWillDoNothing() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jod";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jod@gmail.com")
                .firstname("John")
                .lastname("Doe")
                .build();
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
    void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
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
    void testUpdateJob() throws Exception {
        try {
            UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
            String username = "jad";
            UserManagementService useradmin = mock(UserManagementService.class);
            no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
                .userid(1)
                .username(username)
                .email("jad@gmail.com")
                .firstname("Jane")
                .lastname("Doe")
                .build();
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
            UpdatedTransaction editedJob = UpdatedTransaction.with()
                .id(jobId)
                .accountId(account.getAccountId())
                .transactionTypeId(newJobType.getId())
                .transactionTime(now)
                .transactionAmount(newJobType.getTransactionAmount())
                .build();

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
    void testUpdateJobGetSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());

        ukelonn.setDataSource(datasource);

        UpdatedTransaction updatedTransaction = UpdatedTransaction.with().build();
        assertThrows(UkelonnException.class, () -> {
                ukelonn.updateJob(updatedTransaction);
            });
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return ukelonn.getJobTypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }

    @Test
    void testGetPayments() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username(username)
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);
        Account account = ukelonn.getAccount(username);
        List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
        assertEquals(10, payments.size());
    }

    @Test
    void testGetPaymenttypes() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        assertEquals(2, paymenttypes.size());
    }

    @Test
    void testGetPaymenttypesWithDatabasePreparestatementFailure() throws Exception {
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
            assertEquals(0, paymenttypes.size(), "Expected empty list");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testRegisterPayment() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        UserManagementService useradmin = mock(UserManagementService.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUser(anyString())).thenReturn(user);
        ukelonn.setUserAdmin(useradmin);

        // Create the request
        Account account = ukelonn.getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        PerformedTransaction payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(paymenttypes.get(0).getId())
            .transactionAmount(account.getBalance())
            .transactionDate(new Date())
            .build();

        // Run the method under test
        Account result = ukelonn.registerPayment(payment);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @Test
    void testRegisterPaymentWithDatabaseFailure() throws Exception {
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
        Account account = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(2.0).build();
        PerformedTransaction payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(1)
            .transactionAmount(2.0)
            .transactionDate(new Date())
            .build();

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
    void testGetJobTypesNullResultSet() throws Exception {
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
            assertEquals(0, jobtypes.size(), "Expected a non-null, empty map");
        } finally {
            // Restore the real derby database
            ukelonn.setDataSource(originalDatasource);
        }
    }

    @Test
    void testModifyJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Find a jobtyoe
        List<TransactionType> jobtypes = ukelonn.getJobTypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype = TransactionType.with(jobtype).transactionAmount(originalAmount + 1).build();

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.modifyJobtype(jobtype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testModifyJobtypeFailure() throws Exception {
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
        TransactionType jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.modifyJobtype(jobtype);
            });
    }

    @Test
    void testCreateJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get the list of jobtypes before adding a new job type
        List<TransactionType> originalJobtypes = ukelonn.getJobTypes();

        // Create new jobtype
        TransactionType jobtype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Skrubb badegolv")
            .transactionAmount(200.0)
            .transactionIsWork(true)
            .build();

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.createJobtype(jobtype);

        // Verify that a new jobtype has been added
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @Test
    void testCreateJobtypeFailure() throws Exception {
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
        TransactionType jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.createJobtype(jobtype);
            });
    }

    @Test
    void testModifyPaymenttype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Find a payment type
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(0);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();

        // Update the payment type in the database
        List<TransactionType> updatedPaymenttypes = ukelonn.modifyPaymenttype(paymenttype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testModifyPaymenttypeFailure() throws Exception {
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
        TransactionType paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.modifyPaymenttype(paymenttype);
            });
    }

    @Test
    void testCreatePaymenttype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get the list of payment types before adding a new job type
        List<TransactionType> originalPaymenttypes = ukelonn.getPaymenttypes();

        // Create new payment type
        TransactionType paymenttype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Vipps")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Update the payments type in the database
        List<TransactionType> updatedPaymenttypes = ukelonn.createPaymenttype(paymenttype);

        // Verify that a new payment type has been added
        assertThat(updatedPaymenttypes.size()).isGreaterThan(originalPaymenttypes.size());
    }

    @Test
    void testCreatePaymenttypeFailure() throws Exception {
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
        TransactionType paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try creating the payment type in the database, which should cause an exception
        assertThrows(UkelonnException.class, () -> {
                ukelonn.createPaymenttype(paymenttype);
            });
    }

    @Test
    void testPasswordsEqualAndNotEmpty() {
        PasswordsWithUser equalPasswords = PasswordsWithUser.with().password("zekret").password2("zekret").build();
        assertTrue(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(equalPasswords));
        PasswordsWithUser differentPasswords = PasswordsWithUser.with().password("zekret").password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(differentPasswords));
        PasswordsWithUser firstPasswordNull = PasswordsWithUser.with().password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordNull));
        PasswordsWithUser secondPasswordNull = PasswordsWithUser.with().password("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordNull));
        PasswordsWithUser bothPasswordsNull = PasswordsWithUser.with().build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsNull));
        PasswordsWithUser firstPasswordEmpty = PasswordsWithUser.with().password("").password2("secret").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordEmpty));
        PasswordsWithUser secondPasswordEmpty = PasswordsWithUser.with().password("secret").password2("").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(secondPasswordEmpty));
        PasswordsWithUser bothPasswordsEmpty = PasswordsWithUser.with().password("").password2("").build();
        assertFalse(UkelonnServiceProvider.passwordsEqualsAndNotEmpty(bothPasswordsEmpty));
    }

    @Test
    void testHasUserWithNonEmptyUsername() {
        PasswordsWithUser passwords = PasswordsWithUser.with().build();
        User userWithUsername = User.with().userId(1).username("foo").build();
        passwords.setUser(userWithUsername);
        assertTrue(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithEmptyUsername = User.with().userId(1).username("").build();
        passwords.setUser(userWithEmptyUsername);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithNullUsername = User.with().userId(1).username(null).build();
        passwords.setUser(userWithNullUsername);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
        passwords.setUser(null);
        assertFalse(UkelonnServiceProvider.hasUserWithNonEmptyUsername(passwords));
    }

    @Test
    void testNotifications() {
        UkelonnService ukelonn = new UkelonnServiceProvider();
        List<Notification> notificationsToJad = ukelonn.notificationsTo("jad");
        assertThat(notificationsToJad).isEmpty();

        // Send notification to "jad"
        Notification utbetalt = Notification.with().title("Ukelønn").message("150 kroner tbetalt til konto").build();
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
        UserManagementServiceProvider useradmin = mock(UserManagementServiceProvider.class);
        no.priv.bang.osgiservice.users.User user = no.priv.bang.osgiservice.users.User.with()
            .userid(1)
            .username("jad")
            .email("jad@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
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
    @Test()
    void testaddDummyPaymentToAccountSoThatAccountWillAppearInAccountsViewWhenSQLExceptionIsThrown() throws SQLException {
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
    void testGetResourceAsStringNoResource() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);
        String resource = ukelonn.getResourceAsString("finnesikke");
        assertNull(resource);
    }

    @Test
    void testEarningsSumOverYear() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        List<SumYear> statistics = ukelonn.earningsSumOverYear("jad");
        assertThat(statistics.size()).isPositive();
        SumYear firstYear = statistics.get(0);
        assertEquals(1250.0, firstYear.getSum(), 0.0);
        assertEquals(2016, firstYear.getYear());
    }

    @Test
    void testEarningsSumOverYearWhenSqlExceptionIsThrown() throws Exception {
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
    void testEarningsSumOverMonth() {
        UkelonnServiceProvider ukelonn = getUkelonnServiceSingleton();
        List<SumYearMonth> statistics = ukelonn.earningsSumOverMonth("jad");
        assertThat(statistics.size()).isPositive();
        SumYearMonth firstYear = statistics.get(0);
        assertEquals(125.0, firstYear.getSum(), 0.0);
        assertEquals(2016, firstYear.getYear());
        assertEquals(7, firstYear.getMonth());
    }

    @Test
    void testEarningsSumOverMonthWhenSqlExceptionIsThrown() throws Exception {
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
    void testGetCreateModifyAndDeleteBonuses() {
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
        Bonus julebonus = Bonus.with()
            .bonusId(0)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt betaling for utførte jobber")
            .bonusFactor(2.0)
            .startDate(julestart)
            .endDate(juleslutt)
            .build();
        Bonus enabledBonus = ukelonn.createBonus(julebonus).stream().filter(b -> "Julebonus".equals(b.getTitle())).findFirst().get();
        int bonusCountWithOneAddedBonus = ukelonn.getAllBonuses().size();
        assertThat(bonusCountWithOneAddedBonus).isGreaterThan(initialBonusCount);

        // Verify that the active bonus will double the payment
        // of registered jobs.
        double expectAmount = 2 * amount;
        assertEquals(expectAmount, ukelonn.addBonus(amount), 0.0);

        // Add an extra active bonus to verify that two
        // concurrent bonuses will give the expected result
        Bonus julebonus2 = ukelonn.createBonus(Bonus.with()
                                               .bonusId(0)
                                               .enabled(true)
                                               .title("Julebonuz")
                                               .description("Dobbelt betaling for utførte jobber")
                                               .bonusFactor(1.25)
                                               .startDate(julestart)
                                               .endDate(juleslutt)
                                               .build()).stream().filter(b -> "Julebonuz".equals(b.getTitle())).findFirst().get();
        double expectAmount2 = julebonus.getBonusFactor() * amount + julebonus2.getBonusFactor() * amount - amount;
        assertEquals(expectAmount2, ukelonn.addBonus(amount), 0.0);
        ukelonn.deleteBonus(julebonus2);

        // Add an inactive bonus with start and end date both in the future
        // Since we're outside of the startDate/endDate, this will not show up
        // as an active bonus
        Date paaskestart = Date.from(LocalDateTime.now().plusDays(5).toInstant(ZoneOffset.UTC));
        Date paaskeslutt = Date.from(LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC));
        Bonus paaskebonus = Bonus.with()
            .enabled(true)
            .title("Påskebonus")
            .description("Dobbelt betaling for utførte jobber")
            .bonusFactor(2.0)
            .startDate(paaskestart)
            .endDate(paaskeslutt)
            .build();
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

    @Test
    void testGetActiveBonusesWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.getActiveBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of active bonuses");
    }

    @Test
    void testGetAllBonusesWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.getAllBonuses();
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to get list of all bonuses");
    }

    @Test
    void testAddBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.createBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to add Bonus");
    }

    @Test
    void testUpdateBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.modifyBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to update Bonus");
    }

    @Test
    void testDeleteBonusWithSQLException() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setLogservice(logservice);
        ukelonn.setDataSource(datasource);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Verify that what we get with an SQL failure
        // is an empty result and a warning in the log
        List<Bonus> bonuses = ukelonn.deleteBonus(Bonus.with().build());
        assertThat(bonuses).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[WARNING] Failed to delete Bonus");
    }

    @Test
    void testAddRoleIfNotPresentWhenRoleIsPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        Role userrole = Role.with().id(2).rolename(UKELONNUSER_ROLE).description("ukelonn user").build();
        when(useradmin.getRoles()).thenReturn(Arrays.asList(userrole, adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        Optional<Role> role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().getRolename());
    }

    @Test
    void testAddRoleIfNotPresentWhenRoleIsNotPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        Role userrole = Role.with().id(2).rolename(UKELONNUSER_ROLE).description("ukelonn user").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(userrole));
        when(useradmin.addRole(any())).thenReturn(Arrays.asList(userrole, adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        Optional<Role> role = ukelonn.addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        assertThat(role).isNotEmpty();
        assertEquals(UKELONNADMIN_ROLE, role.get().getRolename());
    }

    @Test
    void testAddAdminroleToUserAdminWhenRoleIsMissing() {
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(1)).addUserRoles(any());
    }

    @Test
    void testAddAdminroleToUserAdminWhenRoleIsPresent() {
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        ukelonn.setUserAdmin(useradmin);

        ukelonn.addAdminroleToUserAdmin(Optional.of(adminrole));
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).getRolesForUser(anyString());
        verify(useradmin, times(0)).addUserRoles(any());
    }

    @Test
    void testAddAdminroleToUserAdminWhenAdminUserIsNotPresent() {
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn administrator").build();
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
    void testDefaultLocale() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        assertEquals(NB_NO, ukelonn.defaultLocale());
    }

    @Test
    void testAvailableLocales() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        List<LocaleBean> locales = ukelonn.availableLocales();
        assertThat(locales).isNotEmpty().contains(LocaleBean.with().locale(ukelonn.defaultLocale()).build());
    }

    @Test
    void testDisplayTextsForDefaultLocale() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ukelonn.setUserAdmin(useradmin);
        ukelonn.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        Map<String, String> displayTexts = ukelonn.displayTexts(ukelonn.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

    private Bonus disableBonus(Bonus bonus) {
        return Bonus.with().bonusId(bonus.getBonusId()).enabled(false).iconurl(bonus.getIconurl()).title(bonus.getTitle()).description(bonus.getDescription()).bonusFactor(bonus.getBonusFactor()).startDate(bonus.getStartDate()).endDate(bonus.getEndDate()).build();
    }
}
