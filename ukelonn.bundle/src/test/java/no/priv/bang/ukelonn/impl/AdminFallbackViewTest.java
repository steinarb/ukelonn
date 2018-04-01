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

import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;


public class AdminFallbackViewTest {

    private static VaadinSession session;

    @BeforeClass
    public static void beforeAllTests() throws ServletException, ServiceException {
        setupFakeOsgiServices();
        session = createSession();
    }

    @Test
    public void testUpdateFormsAfterAccountIsSelected() throws ServiceException, ServletException {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Verify preconditions
        assertEquals(Double.valueOf(0.0), view.balance.getValue());

        // Try selecing a null account
        ComboBox paymenttype = mock(ComboBox.class);
        ComboBox accountSelector = mock(ComboBox.class);
        view.updateFormsAfterAccountIsSelected(paymenttype, accountSelector);

        // Verify value hasn't been changed
        assertEquals(Double.valueOf(0.0), view.balance.getValue());

        // Try selecting with a real account
        Account account = getAccountInfoFromDatabase(provider, getClass(), "jad");
        when(accountSelector.getValue()).thenReturn(account);
        view.updateFormsAfterAccountIsSelected(paymenttype, accountSelector);

        // Verify value has been changed
        assertNotEquals(Double.valueOf(0.0), view.balance.getValue());
    }

    @Test
    public void testUpdateVisiblePaymentPropertiesWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);
        view.balance.setValue(100.0);

        // Verify preconditions
        assertEquals(Double.valueOf(0.0), view.amount.getValue());

        // Try selecting with a null payment type
        ComboBox paymenttype = mock(ComboBox.class);
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify value hasn't been changed
        assertEquals(Double.valueOf(0.0), view.amount.getValue());

        // Set up the mock for repeated calls
        double paidAmount = 215;
        TransactionType transactionType = mock(TransactionType.class);
        when(transactionType.getId()).thenReturn(AdminFallbackView.ID_OF_PAY_TO_BANK, -1, -1);
        when(transactionType.getTransactionAmount()).thenReturn(null, null, paidAmount);
        when(paymenttype.getValue()).thenReturn(transactionType);

        // Try selecting with pay to bank (will set an amount equal to the balance)
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the balance
        assertEquals(view.balance.getValue(), view.amount.getValue());

        // Zero out the amount
        view.amount.setValue(0.0);

        // Try selecing a payment with a null value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is again equal to the balance
        assertEquals(view.balance.getValue(), view.amount.getValue());

        // Try selecting a different payment type with a set value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the paid amount
        assertEquals(Double.valueOf(paidAmount), view.amount.getValue());
    }

    @Test
    public void testRegisterPaymentInDatabase() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Set up the mock for repeated calls
        ComboBox paymenttype = mock(ComboBox.class);
        ComboBox accountSelector = mock(ComboBox.class);
        TransactionType payment = mock(TransactionType.class);
        Account account = mock(Account.class);
        when(paymenttype.getValue()).thenReturn(null, null, payment);
        when(accountSelector.getValue()).thenReturn(null, account, account);

        // Set initial condition
        double initialAmount = 210;
        view.amount.setValue(initialAmount);

        // Test with null account and null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify amount is unchanged by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(initialAmount), view.amount.getValue());

        // Test with non-null account and null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify amount is still unchanged by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(initialAmount), view.amount.getValue());

        // Test with non-null account and non-null paymenttype
        view.registerPaymentInDatabase(paymenttype, accountSelector);

        // Verify that amount has been changed by the registerPaymentInDatabase() call
        assertEquals(Double.valueOf(0.0), view.amount.getValue());
    }

    @Test
    public void testMakeNewJobType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Verify initial conditions
        assertEquals("", view.newJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.newJobTypeAmount.getValue());

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that conditions are unchanged
        assertEquals("", view.newJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.newJobTypeAmount.getValue());

        // Set the new job type name
        view.newJobTypeName.setValue("Vaske gulv og tak");

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that conditions are unchanged
        assertEquals("Vaske gulv og tak", view.newJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.newJobTypeAmount.getValue());

        // Set the new job type amount
        view.newJobTypeAmount.setValue(Double.valueOf(100));

        // Call the code that is to be tested
        view.makeNewJobType();

        // Verify that name and amount has been nulled out
        assertEquals("", view.newJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.newJobTypeAmount.getValue());
    }

    @Test
    public void testSaveChangesToJobType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Create mocks
        Table jobtypesTable = mock(Table.class);
        TextField editJobTypeNameField = mock(TextField.class);
        TransactionType jobType = mock(TransactionType.class);
        when(jobType.getTransactionTypeName()).thenReturn("Vaske golv", "");
        when(jobType.getTransactionAmount()).thenReturn(Double.valueOf(100.0), Double.valueOf(50.0));
        when(jobtypesTable.getValue()).thenReturn(null, jobType);
        when(editJobTypeNameField.getValue()).thenReturn("", "Vaske golv");

        // Set current values for the job type
        view.editedJobTypeName.setValue("Vaske golv");
        view.editedJobTypeAmount.setValue(Double.valueOf(100.0));

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are unchanged
        assertEquals("Vaske golv", view.editedJobTypeName.getValue());
        assertEquals(Double.valueOf(100.0), view.editedJobTypeAmount.getValue());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are still unchanged
        assertEquals("Vaske golv", view.editedJobTypeName.getValue());
        assertEquals(Double.valueOf(100.0), view.editedJobTypeAmount.getValue());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, editJobTypeNameField);

        // Verify values are changed
        assertEquals("", view.editedJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.editedJobTypeAmount.getValue());
    }

    @Test
    public void testCreatePaymentType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Set the new payment value
        view.newPaymentTypeAmount.setValue(Double.valueOf(10));

        // Call the code that is to be tested
        view.createPaymentType();

        // Verify the payment value hasn't been nulled
        assertEquals(Double.valueOf(10), view.newPaymentTypeAmount.getValue());

        // Set the new payment name
        view.newPaymentTypeName.setValue("Sjekk");

        // Call the code that is to be tested
        view.createPaymentType();

        // Verify the payment value has been nulled
        assertEquals(Double.valueOf(0.0), view.newPaymentTypeAmount.getValue());
    }

    @Test
    public void testUpdatePaymentForEditWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Mock argument to the method
        String expectedPaymentTypeName = "Postgiro";
        Double expectedPaymentTypeAmount = Double.valueOf(50);
        Table paymentTypeTable = mock(Table.class);
        TransactionType paymentType = mock(TransactionType.class);
        when(paymentType.getTransactionTypeName()).thenReturn(expectedPaymentTypeName);
        when(paymentType.getTransactionAmount()).thenReturn(expectedPaymentTypeAmount);
        when(paymentTypeTable.getValue()).thenReturn(null, paymentType);

        // Test settings before the test
        assertEquals("", view.editedPaymentTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.editedPaymentTypeAmount.getValue());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable);

        // Verify that values are unchanged after the test
        assertEquals("", view.editedPaymentTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.editedPaymentTypeAmount.getValue());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable);

        // Verify that values are set after the test
        assertEquals(expectedPaymentTypeName, view.editedPaymentTypeName.getValue());
        assertEquals(expectedPaymentTypeAmount, view.editedPaymentTypeAmount.getValue());
    }

    @Test
    public void testSaveChangesToPaymentTypes() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminFallbackView view = new AdminFallbackView(provider, request);

        // Create mocks
        Table jobtypesTable = mock(Table.class);
        TextField editJobTypeNameField = mock(TextField.class);
        TransactionType jobType = mock(TransactionType.class);
        when(jobType.getTransactionTypeName()).thenReturn("Kontanter", "");
        when(jobType.getTransactionAmount()).thenReturn(Double.valueOf(100.0), Double.valueOf(50.0));
        when(jobtypesTable.getValue()).thenReturn(null, jobType);
        when(editJobTypeNameField.getValue()).thenReturn("", "Kontanter");

        // Set current values for the job type
        view.editedPaymentTypeName.setValue("Kontanter");
        view.editedPaymentTypeAmount.setValue(Double.valueOf(100.0));

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are unchanged
        assertEquals("Kontanter", view.editedPaymentTypeName.getValue());
        assertEquals(Double.valueOf(100.0), view.editedPaymentTypeAmount.getValue());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are still unchanged
        assertEquals("Kontanter", view.editedPaymentTypeName.getValue());
        assertEquals(Double.valueOf(100.0), view.editedPaymentTypeAmount.getValue());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, editJobTypeNameField);

        // Verify values are changed
        assertEquals("", view.editedJobTypeName.getValue());
        assertEquals(Double.valueOf(0.0), view.editedJobTypeAmount.getValue());
    }

}
