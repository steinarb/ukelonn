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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Grid;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.impl.data.AmountAndBalance;
import no.priv.bang.ukelonn.impl.data.Passwords;


public class AdminViewTest {

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
        AdminView view = new AdminView(provider, request);

        // Verify preconditions
        assertEquals("0", view.balance.getValue());

        // Try selecing a null account
        NativeSelect<TransactionType> paymenttype = mock(NativeSelect.class);
        NativeSelect<Account> accountSelector = mock(NativeSelect.class);
        Binder<AmountAndBalance> binder = new Binder<>(AmountAndBalance.class);
        AmountAndBalance bean = new AmountAndBalance();
        binder.setBean(bean);
        view.updateFormsAfterAccountIsSelected(binder, paymenttype, accountSelector);

        // Verify value hasn't been changed
        assertEquals("0", view.balance.getValue());

        // Try selecting with a real account
        Account account = getAccountInfoFromDatabase(provider, getClass(), "jad");
        when(accountSelector.getValue()).thenReturn(account);
        view.updateFormsAfterAccountIsSelected(binder, paymenttype, accountSelector);

        // Verify value has been changed
        assertNotEquals("0.0", view.balance.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateVisiblePaymentPropertiesWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);
        view.balance.setValue("100.0");

        // Verify preconditions
        assertEquals("0", view.amount.getValue());

        // Try selecting with a null payment type
        NativeSelect<TransactionType> paymenttype = mock(NativeSelect.class);
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify value hasn't been changed
        assertEquals("0", view.amount.getValue());

        // Set up the mock for repeated calls
        double paidAmount = 215;
        TransactionType transactionType = mock(TransactionType.class);
        when(transactionType.getId()).thenReturn(AdminView.ID_OF_PAY_TO_BANK, -1, -1);
        when(transactionType.getTransactionAmount()).thenReturn(null, null, paidAmount);
        when(paymenttype.getValue()).thenReturn(transactionType);

        // Try selecting with pay to bank (will set an amount equal to the balance)
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the balance
        assertEquals(view.balance.getValue(), view.amount.getValue());

        // Zero out the amount
        view.amount.setValue("0.0");

        // Try selecing a payment with a null value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is again equal to the balance
        assertEquals(view.balance.getValue(), view.amount.getValue());

        // Try selecting a different payment type with a set value
        view.updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);

        // Verify amount is now equal to the paid amount
        assertEquals(Double.toString(paidAmount), view.amount.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPaymentInDatabase() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Set up the mock for repeated calls
        NativeSelect<TransactionType> paymenttype = mock(NativeSelect.class);
        NativeSelect<Account> accountSelector = mock(NativeSelect.class);
        TransactionType payment = mock(TransactionType.class);
        Account account = mock(Account.class);
        when(paymenttype.getValue()).thenReturn(null, null, payment);
        when(accountSelector.getValue()).thenReturn(null, account, account);

        // Set initial condition
        Binder<AmountAndBalance> binder = view.amountAndBalanceBinder;
        double initialAmount = 210;
        binder.getBean().setAmount(initialAmount);
        binder.readBean(binder.getBean());
        String initialAmountFormFieldValue = view.amount.getValue(); // Value formatted as String

        // Test with null account and null paymenttype
        view.registerPaymentInDatabase(binder, paymenttype, accountSelector);

        // Verify amount is unchanged by the registerPaymentInDatabase() call
        assertEquals(initialAmountFormFieldValue, view.amount.getValue());

        // Test with non-null account and null paymenttype
        view.registerPaymentInDatabase(binder, paymenttype, accountSelector);

        // Verify amount is still unchanged by the registerPaymentInDatabase() call
        assertEquals(initialAmountFormFieldValue, view.amount.getValue());

        // Test with non-null account and non-null paymenttype
        view.registerPaymentInDatabase(binder, paymenttype, accountSelector);

        // Verify that amount has been changed by the registerPaymentInDatabase() call
        assertEquals("0", view.amount.getValue());
    }

    @Test
    public void testMakeNewJobType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Setup
        TransactionType newJobType = new TransactionType(0, "", 0.0, true, false);
        Binder<TransactionType> binder = new Binder<>(TransactionType.class);
        binder.setBean(newJobType);

        // Call the code that is to be tested
        view.makeNewJobType(binder);

        // Verify that conditions are unchanged
        newJobType.equals(binder.getBean());

        // Set the new job type name
        newJobType.setTransactionTypeName("Vaske gulv og tak");
        binder.setBean(newJobType);

        // Call the code that is to be tested
        view.makeNewJobType(binder);

        // Verify that conditions are unchanged
        newJobType.equals(binder.getBean());

        // Set the new job type amount
        newJobType.setTransactionAmount(Double.valueOf(100));
        binder.setBean(newJobType);

        // Call the code that is to be tested
        view.makeNewJobType(binder);

        // Verify that name and amount has been nulled out
        TransactionType bean = binder.getBean();
        assertEquals("", bean.getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), bean.getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveChangesToJobType() throws Exception {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Create mocks
        Grid<TransactionType> jobtypesTable = mock(Grid.class);
        TransactionType jobType1 = new TransactionType(0, "Vaske golv", 100.0, true, false);
        TransactionType jobType2 = new TransactionType(0, "", 0.0, true, false);
        Set<TransactionType> jobType1Selected = new HashSet<>(Arrays.asList(jobType1));
        Set<TransactionType> jobType2Selected = new HashSet<>(Arrays.asList(jobType2));
        when(jobtypesTable.getSelectedItems()).thenReturn(Collections.emptySet(), jobType1Selected, jobType2Selected);

        Binder<TransactionType> binder = new Binder<>(TransactionType.class);
        TransactionType bean = new TransactionType(0, "", 0.0, true, false);
        binder.setBean(bean);
        TextField editJobTypeNameField = new TextField("Endre Navn på jobbtype:");
        binder.forField(editJobTypeNameField).bind("transactionTypeName");

        TextField editJobTypeAmountField = new TextField("Endre beløp for jobbtype:");
        binder.forField(editJobTypeAmountField)
            .withConverter(new StringToDoubleConverter("Ikke et tall"))
            .bind("transactionAmount");

        // Set current values for the job type
        TransactionType unchangedJobtype = new TransactionType(0, "Vaske golv", 100.0, true, false);
        binder.readBean(unchangedJobtype);
        binder.writeBean(binder.getBean());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, binder);

        // Verify values are unchanged
        assertEquals("Vaske golv", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, binder);

        // Verify values are still unchanged
        assertEquals("Vaske golv", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToJobType(jobtypesTable, binder);

        // Verify values are changed
        assertEquals("", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), binder.getBean().getTransactionAmount());
    }

    @Test
    public void testCreatePaymentType() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Set the new payment value
        TransactionType newPaymentType = new TransactionType(0, "", 10.0, false, true);
        Binder<TransactionType> binder = new Binder<>(TransactionType.class);
        binder.setBean(newPaymentType);

        // Call the code that is to be tested
        view.createPaymentType(binder);

        // Verify the payment value hasn't been nulled
        assertEquals(newPaymentType, binder.getBean());

        // Set the new payment name
        newPaymentType.setTransactionTypeName("Sjekk");
        binder.setBean(newPaymentType);

        // Call the code that is to be tested
        view.createPaymentType(binder);

        // Verify the payment value has been nulled
        assertEquals(Double.valueOf(0.0), binder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdatePaymentForEditWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Mock argument to the method
        String expectedPaymentTypeName = "Postgiro";
        Double expectedPaymentTypeAmount = Double.valueOf(50);
        Grid<TransactionType> paymentTypeTable = mock(Grid.class);
        TransactionType paymentType = new TransactionType(0, expectedPaymentTypeName, expectedPaymentTypeAmount, false, true);
        Set<TransactionType> selectedPaymentType = new HashSet<>(Arrays.asList(paymentType));
        when(paymentTypeTable.getSelectedItems()).thenReturn(Collections.emptySet(), selectedPaymentType);

        // Test settings before the test
        TransactionType editedPaymentType = new TransactionType(0, "", 0.0, false, true);
        Binder<TransactionType> binder = new Binder<>(TransactionType.class);
        binder.setBean(editedPaymentType);
        TextField nameField = new TextField();
        binder.forField(nameField).bind("transactionTypeName");
        TextField amountField = new TextField();
        binder.forField(amountField).withConverter(new StringToDoubleConverter("Ikke et tall")).bind("transactionAmount");
        assertEquals("", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable, binder);

        // Verify that values are unchanged after the test
        assertEquals("", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypeTable, binder);

        // Verify that values are set after the test
        assertEquals(expectedPaymentTypeName, binder.getBean().getTransactionTypeName());
        assertEquals(expectedPaymentTypeAmount, binder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveChangesToPaymentTypes() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        AdminView view = new AdminView(provider, request);

        // Create mocks
        Grid<TransactionType> jobtypesTable = mock(Grid.class);
        TransactionType paymentType1 = new TransactionType(0, "Kontanter", 100.0, false, true);
        TransactionType paymentType2 = new TransactionType(0, "", 50.0, false, true);
        Set<TransactionType> selectedPaymentType1 = new HashSet<>(Arrays.asList(paymentType1));
        Set<TransactionType> selectedPaymentType2 = new HashSet<>(Arrays.asList(paymentType2));
        when(jobtypesTable.getSelectedItems()).thenReturn(Collections.emptySet(), selectedPaymentType1, selectedPaymentType2);

        // Set current values for the payment type
        Binder<TransactionType> binder = new Binder<>(TransactionType.class);
        TransactionType bean = new TransactionType(0, "Kontanter", 100.0, false, true);
        binder.setBean(bean);

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, binder);

        // Verify values are unchanged
        assertEquals("Kontanter", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, binder);

        // Verify values are still unchanged
        assertEquals("Kontanter", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(100.0), binder.getBean().getTransactionAmount());

        // Call the code that is to be tested
        view.saveChangesToPaymentTypes(jobtypesTable, binder);

        // Verify values are changed
        assertEquals("", binder.getBean().getTransactionTypeName());
        assertEquals(Double.valueOf(0.0), binder.getBean().getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSelectAUserToBeEdited() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);
        AdminView view = new AdminView(provider, request);

        Binder<User> editUserBinder = new Binder<>(User.class);
        User user = new User(0, "", "", "", "");
        editUserBinder.setBean(user);
        TextField username = new TextField(); // Need a single field to round-trip with binding
        editUserBinder.forField(username).bind("username");
        NativeSelect<User> editUserUsersField = mock(NativeSelect.class);
        User selectedUser = new User(1, "selecteduser", "selus@gmail.com", "Selected", "User");
        when(editUserUsersField.getSelectedItem()).thenReturn(Optional.empty(), Optional.of(selectedUser));

        // Verify preconditions
        assertEquals("", user.getUsername());

        // Run the code with no selection in the NativeSelect
        view.selectAUserToBeEdited(editUserBinder, editUserUsersField);
        editUserBinder.writeBean(editUserBinder.getBean()); // Write from fields to bean

        // Verify the binding's bean is unchanged
        assertEquals("", user.getUsername());

        // Run the code with a User selected in the NativeSelect
        view.selectAUserToBeEdited(editUserBinder, editUserUsersField);
        editUserBinder.writeBean(editUserBinder.getBean()); // Write from fields to bean

        // Verify the binding's bean has a new value
        assertEquals("selecteduser", editUserBinder.getBean().getUsername());
    }

    @Test
    public void testSaveUserModifications() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);
        AdminView view = new AdminView(provider, request);

        Binder<User> editUserBinder = new Binder<>(User.class);
        User user = new User(0, "editedusername", "", "", "");
        editUserBinder.setBean(user);
        TextField username = new TextField(); // Need a single field to round-trip with binding
        editUserBinder.forField(username).bind("username");
        editUserBinder.readBean(user);

        // Verify preconditions, ie. that the bound form field has a value
        assertEquals("editedusername", username.getValue());

        // Run the code with no selection in the NativeSelect
        view.saveUserModifications(editUserBinder);

        // Verify the the bound form field has been cleared
        assertEquals("", username.getValue());

        // Corner case test: verify that using a null for the bean doesn't break the method
        editUserBinder.setBean(null);
        username.setValue("notchanged");
        view.saveUserModifications(editUserBinder);
        assertEquals("notchanged", username.getValue());
    }

    @Test(expected=UkelonnException.class)
    public void testClearAllNewUserFormElements() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);
        AdminView view = new AdminView(provider, request);

        // First test that the method actually clears the forms
        Binder<User> binder = new Binder<>(User.class);
        User user = new User(0, "", "", "", "");
        binder.setBean(user);
        TextField username = new TextField();
        binder.forField(username).bind("username");

        // Set edited value in form field
        username.setValue("edited");
        binder.writeBean(user);
        assertEquals("edited", user.getUsername());

        // Call the tested method
        view.clearAllNewUserFormElements(binder);

        // Verify that the form has been cleared
        assertEquals("", username.getValue());

        // Then verify that a failing validation throws an UkelonnException
        // (In real life the ValidationException can never be thrown here
        // the joys of checked exceptions...)
        @SuppressWarnings("unchecked")
            Binder<User> binderFailingValidation = mock(Binder.class);
        doThrow(ValidationException.class).when(binderFailingValidation).writeBean(any());
        view.clearAllNewUserFormElements(binderFailingValidation);
    }

    @Test
    public void testNewUserIsAValidUser() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);
        AdminView view = new AdminView(provider, request);

        Binder<User> userbinder = new Binder<>(User.class);
        User user = new User(0, "", "", "", "");
        userbinder.setBean(user);
        Binder<Passwords> passwordbinder = new Binder<>(Passwords.class);
        Passwords passwords = new Passwords("", "");
        passwordbinder.setBean(passwords);

        assertFalse(view.newUserIsAValidUser(userbinder, passwordbinder));

        user.setUsername("username");
        assertFalse(view.newUserIsAValidUser(userbinder, passwordbinder));
        passwords.setPassword1("secret");
        assertFalse(view.newUserIsAValidUser(userbinder, passwordbinder));
        passwords.setPassword2("secret");
        assertFalse(view.newUserIsAValidUser(userbinder, passwordbinder));
        user.setFirstname("firstname");
        assertFalse(view.newUserIsAValidUser(userbinder, passwordbinder));
        user.setLastname("lastname");
        assertTrue(view.newUserIsAValidUser(userbinder, passwordbinder));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetEditJobTypeFormsFromSelectedJobTypeInTable() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);
        AdminView view = new AdminView(provider, request);

        Binder<TransactionType> editJobBinder = new Binder<>(TransactionType.class);
        TransactionType job = new TransactionType(0, "", 0.0, true, false);
        editJobBinder.setBean(job);
        TextField name = new TextField(); // Need a single field to round-trip with binding
        editJobBinder.forField(name).bind("transactionTypeName");
        Grid<TransactionType> jobtypesTable = mock(Grid.class);
        TransactionType vask = new TransactionType(0, "Vaske gulv", 10.0, true, false);
        when(jobtypesTable.getSelectedItems()).thenReturn(Collections.emptySet(), new HashSet<>(Arrays.asList(vask)));

        // Verify preconditions
        assertEquals("", job.getTransactionTypeName());

        // Run the code with no selection in the NativeSelect
        view.setEditJobTypeFormsFromSelectedJobTypeInTable(jobtypesTable, editJobBinder);
        editJobBinder.writeBean(editJobBinder.getBean()); // Write from fields to bean

        // Verify the binding's bean is unchanged
        assertEquals("", job.getTransactionTypeName());

        // Run the code with a User selected in the NativeSelect
        view.setEditJobTypeFormsFromSelectedJobTypeInTable(jobtypesTable, editJobBinder);
        editJobBinder.writeBean(editJobBinder.getBean()); // Write from fields to bean

        // Verify the binding's bean has a new value
        assertEquals("Vaske gulv", editJobBinder.getBean().getTransactionTypeName());
    }
}
