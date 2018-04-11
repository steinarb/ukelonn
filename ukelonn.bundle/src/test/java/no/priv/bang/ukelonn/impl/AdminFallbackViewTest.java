/*
 * Copyright 2017 Steinar Bang
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

import javax.servlet.ServletException;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import no.priv.bang.ukelonn.impl.data.AmountAndBalance;


public class AdminFallbackViewTest {

    private static VaadinSession session;

    @BeforeClass
    public static void beforeAllTests() throws ServletException, ServiceException {
        setupFakeOsgiServices();
        session = createSession();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateFormsAfterAccountIsSelected() throws ServiceException, ServletException {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Verify preconditions
        assertEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getBalance(), 0);

        // Try selecing a null account
        ComboBox<TransactionType> paymenttype = mock(ComboBox.class);
        ComboBox<Account> accountSelector = mock(ComboBox.class);
        view.updateFormsAfterAccountIsSelected(paymenttype, accountSelector);

        // Verify value hasn't been changed
        assertEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getBalance(), 0);

        // Try selecting with a real account
        Account account = getAccountInfoFromDatabase(provider, getClass(), "jad");
        when(accountSelector.getValue()).thenReturn(account);
        view.updateFormsAfterAccountIsSelected(paymenttype, accountSelector);

        // Verify value has been changed
        assertNotEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getBalance(), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateVisiblePaymentPropertiesWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);
        AmountAndBalance bean = view.amountAndBalanceBinder.getBean();
        bean.setBalance(100.0);
        view.amountAndBalanceBinder.setBean(bean);

        // Verify preconditions
        assertEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Try selecting with a null payment type
        ComboBox<TransactionType> paymenttype = mock(ComboBox.class);
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify value hasn't been changed
        assertEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Set up the mock for repeated calls
        double paidAmount = 215;
        TransactionType transactionType = mock(TransactionType.class);
        when(transactionType.getId()).thenReturn(AdminFallbackView.ID_OF_PAY_TO_BANK, -1, -1);
        when(transactionType.getTransactionAmount()).thenReturn(null, null, paidAmount);
        when(paymenttype.getValue()).thenReturn(transactionType);

        // Try selecting with pay to bank (will set an amount equal to the balance)
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the balance
        assertEquals(view.amountAndBalanceBinder.getBean().getBalance(), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Zero out the amount
        view.amountAndBalanceBinder.getBean().setAmount(0.0);

        // Try selecing a payment with a null value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is again equal to the balance
        assertEquals(view.amountAndBalanceBinder.getBean().getBalance(), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Try selecting a different payment type with a set value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the paid amount
        assertEquals(Double.valueOf(paidAmount), view.amountAndBalanceBinder.getBean().getAmount(), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPaymentInDatabase() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Set up the mock for repeated calls
        ComboBox<TransactionType> paymenttype = mock(ComboBox.class);
        ComboBox<Account> accountSelector = mock(ComboBox.class);
        TransactionType payment = mock(TransactionType.class);
        Account account = mock(Account.class);
        when(paymenttype.getValue()).thenReturn(null, null, payment);
        when(accountSelector.getValue()).thenReturn(null, account, account);

        // Set initial condition
        double initialAmount = 210;
        view.amountAndBalanceBinder.getBean().setAmount(initialAmount);

        // Test with null account and null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify amount is unchanged by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(initialAmount), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Test with non-null account and null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify amount is still unchanged by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(initialAmount), view.amountAndBalanceBinder.getBean().getAmount(), 0);

        // Test with non-null account and non-null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify that amount has been changed by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(0.0), view.amountAndBalanceBinder.getBean().getAmount(), 0);
    }

    @Test
    public void testMakeNewJobType() throws Exception {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Verify initial conditions
        assertEquals("", view.newJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.newJobTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that conditions are unchanged
        assertEquals("", view.newJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.newJobTypeBinder.getBean().getTransactionAmount());

        // Set the new job type name
        TransactionType newjobtype = new TransactionType(0, "Vaske gulv og tak", 0.0, true, false);
        view.newJobTypeBinder.readBean(newjobtype);
        view.newJobTypeBinder.writeBean(view.newJobTypeBinder.getBean());

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that conditions are unchanged
        assertEquals("Vaske gulv og tak", view.newJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.newJobTypeBinder.getBean().getTransactionAmount());

        // Set the new job type amount
        newjobtype.setTransactionAmount(100.0);
        view.newJobTypeBinder.readBean(newjobtype);
        view.newJobTypeBinder.writeBean(view.newJobTypeBinder.getBean());

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that name and amount has been nulled out
        assertEquals("", view.newJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.newJobTypeBinder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveChangesToJobType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Create mocks
        Grid<TransactionType> jobtypesTable = mock(Grid.class);
        TransactionType jobType1 = new TransactionType(0, "Vaske golv", 100.0, true, false);
        TransactionType jobType2 = new TransactionType(0, "", 50.0, true, false);
        when(jobtypesTable.getSelectedItems()).thenReturn(Collections.emptySet(), new HashSet<>(Arrays.asList(jobType1)), new HashSet<>(Arrays.asList(jobType2)));

        TextField editJobTypeNameField = mock(TextField.class);
        when(editJobTypeNameField.getValue()).thenReturn("", "Vaske golv");

        // Set current values for the job type
        view.editedJobTypeBinder.getBean().setTransactionTypeName("Vaske golv");
        view.editedJobTypeBinder.getBean().setTransactionAmount(100.0);

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are unchanged
        assertEquals("Vaske golv", view.editedJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), view.editedJobTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are still unchanged
        assertEquals("Vaske golv", view.editedJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), view.editedJobTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are changed
        assertEquals("", view.editedJobTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.editedJobTypeBinder.getBean().getTransactionAmount());
    }

    @Test
    public void testCreatePaymentType() throws Exception {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Set the new payment value
        TransactionType paymentType = new TransactionType(0, "", 0.0, false, true);
        view.newPaymentTypeBinder.writeBean(paymentType);
        paymentType.setTransactionAmount(Double.valueOf(10));
        view.newPaymentTypeBinder.readBean(paymentType);
        view.newPaymentTypeBinder.writeBean(view.newPaymentTypeBinder.getBean());

        // Call the code that is to be tested
        view.createPaymentType();

        // Verify the payment value hasn't been nulled
        assertEquals(Double.valueOf(10), view.newPaymentTypeBinder.getBean().getTransactionAmount());

        // Set the new payment name
        paymentType.setTransactionTypeName("Sjekk");
        view.newPaymentTypeBinder.readBean(paymentType);
        view.newPaymentTypeBinder.writeBean(view.newPaymentTypeBinder.getBean());

        // Call the code that is to be tested
        view.createPaymentType();

        // Verify the payment value has been nulled
        assertEquals(Double.valueOf(0.0), view.newPaymentTypeBinder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdatePaymentForEditWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Mock argument to the method
        String expectedPaymentTypeName = "Postgiro";
        Double expectedPaymentTypeAmount = Double.valueOf(50);
        Grid<TransactionType> paymentTypeTable = mock(Grid.class);
        TransactionType paymentType = new TransactionType(0, expectedPaymentTypeName, expectedPaymentTypeAmount, false, true);
        when(paymentTypeTable.getSelectedItems()).thenReturn(Collections.emptySet(), new HashSet<>(Arrays.asList(paymentType)));

        // Test settings before the test
        assertEquals("", view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.editedPaymentTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable);

        // Verify that values are unchanged after the test
        assertEquals("", view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.editedPaymentTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable);

        // Verify that values are set after the test
        assertEquals(expectedPaymentTypeName, view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(expectedPaymentTypeAmount, view.editedPaymentTypeBinder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveChangesToPaymentTypes() throws Exception {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Create mocks
        Grid<TransactionType> jobtypesTable = mock(Grid.class);
        TextField editJobTypeNameField = mock(TextField.class);
        TransactionType jobType1 = new TransactionType(0, "Kontanter", 100.0, false, true);
        TransactionType jobType2 = new TransactionType(0, "", 50.0, false, true);
        when(jobtypesTable.getSelectedItems()).thenReturn(Collections.emptySet(), new HashSet<>(Arrays.asList(jobType1)), new HashSet<>(Arrays.asList(jobType2)));
        when(editJobTypeNameField.getValue()).thenReturn("", "Kontanter");

        // Set current values for the job type
        TransactionType paymenttype = new TransactionType(0, "Kontanter", 100.0, false, true);
        view.editedPaymentTypeBinder.readBean(paymenttype);
        view.editedPaymentTypeBinder.writeBean(view.editedPaymentTypeBinder.getBean());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are unchanged
        assertEquals("Kontanter", view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), view.editedPaymentTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are still unchanged
        assertEquals("Kontanter", view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), view.editedPaymentTypeBinder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are changed
        assertEquals("", view.editedPaymentTypeBinder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), view.editedPaymentTypeBinder.getBean().getTransactionAmount());
    }

}
