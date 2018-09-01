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
package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.User;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class UkelonnServiceProviderTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetAccounts() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        List<Account> accounts = provider.getAccounts();
        assertThat(accounts.size()).isGreaterThan(1);
    }

    @Test
    public void testGetAccount() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        Account account = provider.getAccount("jad");
        assertEquals("jad", account.getUsername());
    }

    @Test
    public void testGetJobs() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
        List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
        assertEquals(10, jobs.size());
    }

    @Test
    public void testGetPayments() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
        List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
        assertEquals(10, payments.size());
    }

    @Test
    public void testGetPaymenttypes() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        assertEquals(2, paymenttypes.size());
    }

    @Test
    public void testRegisterPayment() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

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
    public void testRegisterPaymentWithDatabaseFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a mock log service
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create the request
        Account account = new Account(1, 1, "jad", "Jane", "Doe", 2.0);
        PerformedTransaction payment = new PerformedTransaction(account, 1, 2.0, new Date());

        // Run the method under test
        Account result = ukelonn.registerPayment(payment);

        // Check the response
        assertNull(result);
        assertEquals(2, logservice.getLogmessages().size());
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
    @Test(expected=UkelonnException.class)
    public void testModifyJobtypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a non-existing jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        ukelonn.modifyJobtype(jobtype);
        fail("Should never get here!");
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
    @Test(expected=UkelonnException.class)
    public void testCreateJobtypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a new jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        ukelonn.createJobtype(jobtype);
        fail("Should never get here!");
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
    @Test(expected=UkelonnException.class)
    public void testModifyPaymenttypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a non-existing payment type
        TransactionType paymenttype = new TransactionType(-2001, "Bar", 0.0, false, true);

        // Try update the payment type in the database, which should cause an exception
        ukelonn.modifyPaymenttype(paymenttype);
        fail("Should never get here!");
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
    @Test(expected=UkelonnException.class)
    public void testCreatePaymenttypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a new payment type
        TransactionType paymenttype = new TransactionType(-2001, "Bar", 0.0, false, true);

        // Try creating the payment type in the database, which should cause an exception
        ukelonn.createPaymenttype(paymenttype);
        fail("Should never get here!");
    }

    @Test
    public void testGetUsers() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        List<User> users = ukelonn.getUsers();

        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testModifyUser() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get first user and modify all properties except id
        List<User> users = ukelonn.getUsers();
        User user = users.get(0);
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        user.setUsername(modifiedUsername);
        user.setEmail(modifiedEmailaddress);
        user.setFirstname(modifiedFirstname);
        user.setLastname(modifiedLastname);

        // Save the modification
        List<User> updatedUsers = ukelonn.modifyUser(user);

        // Verify that the first user has the modified values
        User firstUser = updatedUsers.get(0);
        assertEquals(modifiedUsername, firstUser.getUsername());
        assertEquals(modifiedEmailaddress, firstUser.getEmail());
        assertEquals(modifiedFirstname, firstUser.getFirstname());
        assertEquals(modifiedLastname, firstUser.getLastname());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testModifyUserFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a user bean
        User user = new User();

        // Save the modification
        ukelonn.modifyUser(user);

        // Verify that the update fails
        fail("Should never get here!");
    }

    @Test
    public void testCreateUser() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Save the number of users before adding a user
        int originalUserCount = ukelonn.getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Create a user
        List<User> updatedUsers = ukelonn.createUser(passwords);

        // Verify that the first user has the modified values
        assertThat(updatedUsers.size()).isGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @Test(expected=UkelonnBadRequestException.class)
    public void testCreateUserPasswordsNotIdentical() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "secret");

        // Creating user should fail
        ukelonn.createUser(passwords);

        fail("Should never get here");
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testCreateUserDatabaseException() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a logservice and inject it
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Creating user should fail
        ukelonn.createUser(passwords);

        fail("Should never get here");
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
    public void testChangePassword() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get first user and modify all properties except id
        List<User> users = ukelonn.getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Save the modification
        List<User> updatedUsers = ukelonn.changePassword(passwords);

        // Verify that the size of the users list hasn't changed
        // (passwords can't be downloaded so we can't check the change)
        assertEquals(users.size(), updatedUsers.size());
    }

    @Test(expected = UkelonnBadRequestException.class)
    public void testChangePasswordWithEmptyUsername() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Create a user with an empty username
        User user = new User(0, "", null, null, null);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Save the modification and cause an exception
        ukelonn.changePassword(passwords);

        fail("Should never get here");
    }

    @Test(expected=UkelonnBadRequestException.class)
    public void testChangePasswordWhenPasswordsDontMatch() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get first user to get a user with valid username
        List<User> users = ukelonn.getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid but non-identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "secret");

        // Change the passwords and cause the exception
        ukelonn.changePassword(passwords);

        fail("Should never get here");
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

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testChangePasswordDatabaseException() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a logservice and inject it
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a user object with a valid username
        User user = new User(0, "validusername", null, null, null);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Changing the password should fail
        ukelonn.changePassword(passwords);

        fail("Should never get here");
    }

}
