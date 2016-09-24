package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.faces.event.ActionEvent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UkelonnAdminControllerTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	releaseFakeOsgiServices();
    }

    @Test
    public void testNoAdministratorUsername() {
        UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

        assertNull(ukelonnAdmin.getAdministratorUsername());
        assertEquals(0, ukelonnAdmin.getAdministratorUserId());
        assertEquals(0, ukelonnAdmin.getAdministratorId());
        assertEquals("Ikke innlogget", ukelonnAdmin.getAdministratorFornavn());
        assertEquals("", ukelonnAdmin.getAdministratorEtternavn());
    }

    @Test
    public void testUserNotAdministrator() {
        UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

        // Set non-administrator user
        ukelonnAdmin.setAdministratorUsername("jad");

        assertEquals("jad", ukelonnAdmin.getAdministratorUsername());
        assertEquals(0, ukelonnAdmin.getAdministratorUserId());
        assertEquals(0, ukelonnAdmin.getAdministratorId());
        assertEquals("Ikke innlogget", ukelonnAdmin.getAdministratorFornavn());
        assertEquals("", ukelonnAdmin.getAdministratorEtternavn());
    }

    @Test
    public void testUserIsAdministrator() {
    	try {
            UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

            // Set administrator user
            ukelonnAdmin.setAdministratorUsername("on");

            assertEquals("on", ukelonnAdmin.getAdministratorUsername());
            assertEquals(1, ukelonnAdmin.getAdministratorUserId());
            assertEquals(1, ukelonnAdmin.getAdministratorId());
            assertEquals("Ola", ukelonnAdmin.getAdministratorFornavn());
            assertEquals("Nordmann", ukelonnAdmin.getAdministratorEtternavn());

            // Check the initial empty values for the account that is to be administrated
            assertNull(ukelonnAdmin.getAccount());
            assertEquals(0.0, ukelonnAdmin.getBalanse(), .1);
            assertEquals(0, ukelonnAdmin.getJobs().size());
            assertEquals(0, ukelonnAdmin.getPayments().size());
            assertEquals(2, ukelonnAdmin.getPaymentTypes().size());
            assertNull(ukelonnAdmin.getNewPaymentType());
            assertEquals(0.0, ukelonnAdmin.getNewPayment(), 0.1);

            // Test selecting an account and check that
            // the values become non-empty
            Account selectedAccount = ukelonnAdmin.getAccounts().get(0);
            ukelonnAdmin.setAccount(selectedAccount);
            assertEquals(673.0, ukelonnAdmin.getBalanse(), .1);
            assertEquals(33, ukelonnAdmin.getJobs().size());
            assertEquals(11, ukelonnAdmin.getPayments().size());
            assertEquals(2, ukelonnAdmin.getPaymentTypes().size());
            TransactionType payToBank = ukelonnAdmin.findPayToBank(ukelonnAdmin.getPaymentTypes());
            assertEquals(payToBank, ukelonnAdmin.getNewPaymentType());
            assertEquals(673.0, ukelonnAdmin.getNewPayment(), 0.1);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testRegisterNewPayment() {
        try {
            UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

            // Set administrator user
            ukelonnAdmin.setAdministratorUsername("on");

            // Test selecting an account and verify expected initial state
            Account selectedAccount = ukelonnAdmin.getAccounts().get(0);
            ukelonnAdmin.setAccount(selectedAccount);
            double initialBalance = 673.0;
            assertEquals(initialBalance, ukelonnAdmin.getBalanse(), .1);
            assertEquals(33, ukelonnAdmin.getJobs().size());
            int initialNumberOfPayments = 11;
            assertEquals(initialNumberOfPayments, ukelonnAdmin.getPayments().size());
            assertEquals(2, ukelonnAdmin.getPaymentTypes().size());

            // Check that the default payment type is pay to bank
            TransactionType payToBank = ukelonnAdmin.findPayToBank(ukelonnAdmin.getPaymentTypes());
            assertEquals(payToBank, ukelonnAdmin.getNewPaymentType());
            assertEquals(673.0, ukelonnAdmin.getNewPayment(), 0.1);

            // Simulate the button click
            ActionEvent event = mock(ActionEvent.class);
            ukelonnAdmin.registerNewPayment(event);

            // Verify that a payment has been made and that balance has been set to 0.0
            assertEquals(initialNumberOfPayments + 1, ukelonnAdmin.getPayments().size());
            assertEquals(0.0, ukelonnAdmin.getBalanse(), .1);

            // Register a payment with a payment type but without an amount
            TransactionType paymentType = ukelonnAdmin.getPaymentTypes().get(0);
            ukelonnAdmin.setNewPaymentType(paymentType);
            ukelonnAdmin.registerNewPayment(event);

            // Verify that no new payment has been made (payment and balance unchanged)
            assertEquals(initialNumberOfPayments + 1, ukelonnAdmin.getPayments().size());
            assertEquals(0.0, ukelonnAdmin.getBalanse(), .1);

            // Set an payment type and an amount
            ukelonnAdmin.setNewPaymentType(paymentType);
            double newAmount = 35.0;
            ukelonnAdmin.setNewPayment(newAmount);
            ukelonnAdmin.registerNewPayment(event);

            // Verify updated values
            assertEquals(initialNumberOfPayments + 2, ukelonnAdmin.getPayments().size());
            assertEquals(0.0 - newAmount, ukelonnAdmin.getBalanse(), .1);

            // Verify that the new payment type and amount have been nulled
            assertNull(ukelonnAdmin.getNewPaymentType());
            assertEquals(0.0, ukelonnAdmin.getNewPayment(), 0.1);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testRegisterNewNegativePayment() {
        try {
            UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

            // Set administrator user
            ukelonnAdmin.setAdministratorUsername("on");

            // Test selecting an account and save initial state
            Account selectedAccount = ukelonnAdmin.getAccounts().get(0);
            ukelonnAdmin.setAccount(selectedAccount);
            int initialNumberOfPayments = ukelonnAdmin.getPayments().size();
            double initialBalance = ukelonnAdmin.getBalanse();

            // Register a payment with a negative amount value
            ActionEvent event = mock(ActionEvent.class);
            TransactionType paymentType = ukelonnAdmin.getPaymentTypes().get(0);
            ukelonnAdmin.setNewPaymentType(paymentType);
            double newAmount = -35.0;
            ukelonnAdmin.setNewPayment(newAmount);
            ukelonnAdmin.registerNewPayment(event);

            // Verify that setting a negative amount has the same effect as a positive amount
            assertEquals(initialNumberOfPayments + 1, ukelonnAdmin.getPayments().size());
            assertEquals(initialBalance + newAmount, ukelonnAdmin.getBalanse(), .1);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testRegisterNewPaymentWithNoAccountSet() {
        try {
            UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

            // Set administrator user
            ukelonnAdmin.setAdministratorUsername("on");

            // Note expected initial state
            int initialNumberOfPayments = ukelonnAdmin.getPayments().size();
            double initialBalance = ukelonnAdmin.getBalanse();

            // Register a payment
            ActionEvent event = mock(ActionEvent.class);
            TransactionType paymentType = ukelonnAdmin.getPaymentTypes().get(0);
            ukelonnAdmin.setNewPaymentType(paymentType);
            double newAmount = 35.0;
            ukelonnAdmin.setNewPayment(newAmount);
            ukelonnAdmin.registerNewPayment(event);

            // Verify that setting had no effect
            assertEquals(initialNumberOfPayments, ukelonnAdmin.getPayments().size());
            assertEquals(initialBalance, ukelonnAdmin.getBalanse(), .1);
        } finally {
            restoreTestDatabase();
        }
    }

}
