package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommonDatabaseMethodsTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetAdminUserFromDatabase() {
        AdminUser admin = getAdminUserFromDatabase(getClass(), "on");
        assertEquals("on", admin.getUserName());
        assertEquals(2, admin.getUserId());
        assertEquals(2, admin.getAdministratorId());
        assertEquals("Ola", admin.getFirstname());
        assertEquals("Nordmann", admin.getSurname());

        AdminUser notAdmin = getAdminUserFromDatabase(getClass(), "jad");
        assertEquals("jad", notAdmin.getUserName());
        assertEquals(0, notAdmin.getUserId());
        assertEquals("Ikke innlogget", notAdmin.getFirstname());
        assertNull(notAdmin.getSurname());

        AdminUser notInDabase = getAdminUserFromDatabase(getClass(), "unknownuser");
        assertEquals("unknownuser", notInDabase.getUserName());
        assertEquals(0, notInDabase.getUserId());
        assertEquals("Ikke innlogget", notInDabase.getFirstname());
        assertNull(notInDabase.getSurname());
    }

    @Test
    public void testGetAccountInfoFromDatabase() {
        Account account = getAccountInfoFromDatabase(getClass(), "jad");
        assertEquals("jad", account.getUsername());
        assertEquals(4, account.getUserId());
        assertEquals("Jane", account.getFirstName());
        assertEquals("Doe", account.getLastName());
        List<Transaction> jobs = getJobsFromAccount(account, getClass());
        assertEquals(10, jobs.size());
        List<Transaction> payments = getPaymentsFromAccount(account, getClass());
        assertEquals(10, payments.size());

        Account accountForAdmin = getAccountInfoFromDatabase(getClass(), "on");
        assertEquals("on", accountForAdmin.getUsername());
        assertEquals(0, accountForAdmin.getUserId());
        assertEquals("Ikke innlogget", accountForAdmin.getFirstName());

        Account accountNotInDatabase = getAccountInfoFromDatabase(getClass(), "unknownuser");
        assertEquals("unknownuser", accountNotInDatabase.getUsername());
        assertEquals(0, accountNotInDatabase.getUserId());
        assertEquals("Ikke innlogget", accountNotInDatabase.getFirstName());
    }

    @Test
    public void testUpdateUserInDatabase() {
        try {
            List<User> users = getUsers(getClass());
            User jad = findUserInListByName(users, "jad");
            int jadUserid = jad.getUserId();

            String newUsername = "nn";
            String newEmail = "nn213@aol.com";
            String newFirstname = "Nomen";
            String newLastname = "Nescio";

            // Verify that the new values are different from the old values
            assertNotEquals(newUsername, jad.getUsername());
            assertNotEquals(newEmail, jad.getEmail());
            assertNotEquals(newFirstname, jad.getFirstname());
            assertNotEquals(newLastname, jad.getLastname());

            // Create a brand new User bean to use for the update (password won't be used in the update)
            User jadToUpdate = new User(jadUserid, newUsername, newEmail, null, newFirstname, newLastname);
            int expectedNumberOfUpdatedRecords = 1;
            int numberOfUpdatedRecords = updateUserInDatabase(getClass(), jadToUpdate);
            assertEquals(expectedNumberOfUpdatedRecords, numberOfUpdatedRecords);

            // Read back an updated user and compare with the expected values
            List<User> usersAfterUpdate = getUsers(getClass());
            assertEquals("Expected no new users added", users.size(), usersAfterUpdate.size());
            User jadAfterUpdate = findUserInListById(usersAfterUpdate, jadUserid);
            assertEquals(newUsername, jadAfterUpdate.getUsername());
            assertEquals(newEmail, jadAfterUpdate.getEmail());
            assertEquals(newFirstname, jadAfterUpdate.getFirstname());
            assertEquals(newLastname, jadAfterUpdate.getLastname());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testAddJobTypeToDatabase() {
        // Verify precondition
        List<TransactionType> jobTypesBefore = getJobTypesFromTransactionTypes(getTransactionTypesFromUkelonnDatabase(getClass()).values());
        assertEquals(3, jobTypesBefore.size());

        addJobTypeToDatabase(getClass(), "Klippe gress", 45);

        // Verify that a job has been added
        List<TransactionType> jobTypesAfter = getJobTypesFromTransactionTypes(getTransactionTypesFromUkelonnDatabase(getClass()).values());
        assertEquals(4, jobTypesAfter.size());
    }

    @Test
    public void testAddPaymentTypeToDatabase() {
        // Verify precondition
        List<TransactionType> jobTypesBefore = getPaymentTypesFromTransactionTypes(getTransactionTypesFromUkelonnDatabase(getClass()).values());
        assertEquals(2, jobTypesBefore.size());

        addPaymentTypeToDatabase(getClass(), "Sjekk", null);

        // Verify that a job has been added
        List<TransactionType> jobTypesAfter = getPaymentTypesFromTransactionTypes(getTransactionTypesFromUkelonnDatabase(getClass()).values());
        assertEquals(3, jobTypesAfter.size());
    }

    @Test
    public void testAddUserToDatabase() {
        // Verify precondition
        List<User> usersBefore = getUsers(getClass());
        assertEquals(5, usersBefore.size());

        addUserToDatabase(getClass(), "un", "zecret", "un@gmail.com", "User", "Name");

        // Verify that a user has been added
        List<User> usersAfter = getUsers(getClass());
        assertEquals(6, usersAfter.size());
    }

    @Test
    public void testChangePasswordForUser() {
        UkelonnRealm realm = new UkelonnRealm();
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        String username = "jad";
        String originalPassword = "1ad";

        // Verify old password
        assertTrue(passwordMatcher(realm, username, originalPassword));

        // Change the password
        String newPassword = "nupass";
        changePasswordForUser(username, newPassword, getClass());

        // Verify new password
        assertTrue(passwordMatcher(realm, username, newPassword));
    }

    @Test
    public void testUpdateTransactionTypeInDatabase() {
        // Verify the initial state of the transaction type that is to be modified
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        TransactionType transactionTypeBeforeModification = transactionTypes.get(3);
        assertEquals("Gå med resirk", transactionTypeBeforeModification.getTransactionTypeName());
        assertEquals(Double.valueOf(35), transactionTypeBeforeModification.getTransactionAmount());
        assertTrue(transactionTypeBeforeModification.isTransactionIsWork());
        assertFalse(transactionTypeBeforeModification.isTransactionIsWagePayment());

        // Modify the transaction type
        transactionTypeBeforeModification.setTransactionTypeName("Vaske tøy");
        transactionTypeBeforeModification.setTransactionAmount(75.0);
        updateTransactionTypeInDatabase(getClass(), transactionTypeBeforeModification);

        // Verify the changed state of the transaction type in the database
        transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        TransactionType transactionTypeAfterModification = transactionTypes.get(3);
        assertEquals("Vaske tøy", transactionTypeAfterModification.getTransactionTypeName());
        assertEquals(Double.valueOf(75), transactionTypeAfterModification.getTransactionAmount());
        assertTrue(transactionTypeAfterModification.isTransactionIsWork());
        assertFalse(transactionTypeAfterModification.isTransactionIsWagePayment());
    }

    @Test
    public void testDeleteTransactions() {
        // Verify initial job size for a user
        Account account = getAccountInfoFromDatabase(getClass(), "jod");
        List<Transaction> initialJobsForJod = getJobsFromAccount(account, getClass());
        assertEquals(2, initialJobsForJod.size());

        // Add two jobs that are to be deleted later
        registerNewJobInDatabase(getClass(), account, 1, 45);
        registerNewJobInDatabase(getClass(), account, 2, 45);

        // Verify the number of jobs for the user in the database before deleting any
        List<Transaction> jobs = getJobsFromAccount(account, getClass());
        assertEquals(4, jobs.size());

        // Delete two jobs for the user
        List<Transaction> jobsToDelete = Arrays.asList(jobs.get(0), jobs.get(2));
        deleteTransactions(getClass(), jobsToDelete);

        // Verify that the jobs has been deleted
        List<Transaction> jobsAfterDelete = getJobsFromAccount(account, getClass());
        assertEquals(2, jobsAfterDelete.size());
    }

    @Test
    public void testAddNewPaymentToAccount() {
        // Verify initial number of payments for a user
        Account account = getAccountInfoFromDatabase(getClass(), "jod");
        List<Transaction> initialPaymentsForJod = getPaymentsFromAccount(account, getClass());
        assertEquals(1, initialPaymentsForJod.size());

        // Register a payment
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        addNewPaymentToAccount(getClass(), account, transactionTypes.get(4), account.getBalance());

        // Verify that a payment have been added
        List<Transaction> paymentsForJod = getPaymentsFromAccount(account, getClass());
        assertEquals(2, paymentsForJod.size());
    }

    private boolean passwordMatcher(UkelonnRealm realm, String username, String password) {
        AuthenticationToken token = new UsernamePasswordToken(username, password.toCharArray());
        try {
            realm.getAuthenticationInfo(token);
            return true;
        } catch(AuthenticationException e) {
            return false;
        }
    }

    private User findUserInListByName(List<User> users, String username) {
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    private User findUserInListById(List<User> users, int userId) {
        for (User user : users) {
            if (userId == user.getUserId()) {
                return user;
            }
        }

        return null;
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}
