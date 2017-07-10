package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;

import java.util.List;

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

}
