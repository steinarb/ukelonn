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
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.provider.ListDataProvider;
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
    public void testMakeNewJobType() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Try creating a job with the default form values,
        // verify that database update is not called
        Binder<TransactionType> binder = view.newJobBinder;
        view.makeNewJobType(binder);
        ArgumentCaptor<PreparedStatement> updateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(updateCaptor.capture());

        // Try creating a job with a name but an empty amount value
        // verify that database update is not called
        TransactionType jobType = binder.getBean();
        jobType.setTransactionTypeName("Vaske golv");
        binder.readBean(jobType);
        view.makeNewJobType(binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Try creating a job with an empty name but a non-empty amount value
        // verify that database update is not called
        jobType.setTransactionTypeName("");
        jobType.setTransactionAmount(50.0);
        binder.readBean(jobType);
        view.makeNewJobType(binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Try creating a job with a name but an non-empty name and amount values
        // verify that database update is called to create the job
        jobType.setTransactionTypeName("Vaske golv");
        binder.readBean(jobType);
        view.makeNewJobType(binder);
        verify(database, times(1)).update(updateCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testSetEditJobTypeFormsFromSelectedJobTypeInTableWithException() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Corner case test: provoke an exception in the setEditJobTypeFormsFromSelectedJobTypeInTable() method
        view.setEditJobTypeFormsFromSelectedJobTypeInTable(null, null);
    }

    @Test
    public void testSaveChangesToJobType() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Create a jobtype and put it in the table
        TransactionType jobType = new TransactionType(0, "Vaske golv", 100.0, true, false);
        view.jobTypes.getItems().add(jobType);
        view.jobTypes.refreshAll();

        // Try saving the job type, database update should not be called
        // since no job type is selected in the table
        Grid<TransactionType> jobtypesTable = view.jobtypesTable;
        Binder<TransactionType> binder = view.editJobBinder;
        view.saveChangesToJobType(jobtypesTable, binder);
        ArgumentCaptor<PreparedStatement> updateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(updateCaptor.capture());

        // Select a job and try saving the job type, database update should not
        // be called, since the field values are identical to the selected job
        jobtypesTable.select(jobType);
        view.saveChangesToJobType(jobtypesTable, binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Set a non-numeric value in the form field, database update should
        // not be called, since the databinding is not valid
        view.editJobTypeAmountField.setValue("xyzzy");
        view.saveChangesToJobType(jobtypesTable, binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Set the job name to the empty string and try saving the job,
        // database update should not be called since the name is empty
        TransactionType editedJobType = binder.getBean();
        editedJobType.setTransactionTypeName("");
        binder.readBean(editedJobType);
        view.saveChangesToJobType(jobtypesTable, binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Give the job name a non-empty value, but set the amount to 0
        // database update should not be called since the amount is 0
        editedJobType.setTransactionTypeName("Vaske golv");
        editedJobType.setTransactionAmount(0.0);
        binder.readBean(editedJobType);
        view.saveChangesToJobType(jobtypesTable, binder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Change the amount and try saving the job, this time update
        // should be called
        editedJobType.setTransactionAmount(editedJobType.getTransactionAmount() + 1);
        binder.readBean(editedJobType);
        view.saveChangesToJobType(jobtypesTable, binder);
        verify(database, times(1)).update(updateCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testSaveChangesToJobTypeWithExceptionThrown() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Corner case test: check the exception handling when saving edited jobs
        view.saveChangesToJobType(null, null);
    }

    @Test
    public void testCreatePaymentType() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        Binder<TransactionType> binder = view.newPaymentBinder;
        // Test creating payment with empty forms, database update is not called
        view.createPaymentType(binder);
        ArgumentCaptor<PreparedStatement> updateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(updateCaptor.capture());

        // Set the new payment name
        TransactionType newPaymentType = binder.getBean();
        newPaymentType.setTransactionTypeName("Sjekk");
        binder.readBean(newPaymentType);

        // Test creating payment, database update should be called
        view.createPaymentType(binder);
        verify(database, times(1)).update(updateCaptor.capture());

        // Set a payment value
        newPaymentType.setTransactionAmount(10.0);
        binder.readBean(newPaymentType);

        // Test creating payment, database update should not be called
        // since payment name is empty (field blanked after database save).
        // (oe. update still called just a single time)
        view.createPaymentType(binder);
        verify(database, times(1)).update(updateCaptor.capture());

        // Give the payment type a name
        newPaymentType.setTransactionTypeName("Sjekk");
        binder.readBean(newPaymentType);

        // Test creating payment, this time database update should be called
        // since payment name is empty (field blanked after database save).
        // (ie. update will be called 2 times in total)
        view.createPaymentType(binder);
        verify(database, times(2)).update(updateCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testUpdatePaymentForEditWhenPaymentTypeIsSelected() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create the object to be tested
        AdminView view = new AdminView(provider, request);

        // Verify that form values are unchanged when calling
        // method with no selection
        Binder<TransactionType> editPaymentBinder = view.editPaymentBinder;
        Grid<TransactionType> paymentTypesTable = view.paymentTypesTable;
        TransactionType editedPaymentType = editPaymentBinder.getBean();
        assertEquals("", editedPaymentType.getTransactionTypeName());
        assertEquals(0.0, editedPaymentType.getTransactionAmount(), 0);
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypesTable, editPaymentBinder);
        assertEquals("", editedPaymentType.getTransactionTypeName());
        assertEquals(0.0, editedPaymentType.getTransactionAmount(), 0);

        // Select an item and verify that the contents are in the forms
        ListDataProvider<TransactionType> paymentTypes = view.paymentTypes;
        TransactionType paymentType = paymentTypes.getItems().iterator().next();
        paymentTypesTable.select(paymentType);
        view.updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypesTable, editPaymentBinder);
        assertEquals("Inn på konto", editedPaymentType.getTransactionTypeName());

        // Provoke an UkelonnException to be thrown
        view.updatePaymentForEditWhenPaymentTypeIsSelected(null, null);
    }

    @Test
    public void testSaveChangesToPaymentType() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create object under test
        AdminView view = new AdminView(provider, request);

        // Add a payment type to the table
        TransactionType paymentType = new TransactionType(4, "Inn på konto", null, false, true);
        view.paymentTypes.getItems().add(paymentType);
        view.paymentTypes.refreshAll();

        // Call the method with no selection, no database update should be called
        Grid<TransactionType> paymentTypesTable = view.paymentTypesTable;
        Binder<TransactionType> editPaymentBinder = view.editPaymentBinder;
        view.saveChangesToPaymentType(paymentTypesTable, editPaymentBinder);
        ArgumentCaptor<PreparedStatement> updateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(updateCaptor.capture());

        // Select an item and try saving, no database update will be called
        // because the values are identical to the original
        paymentTypesTable.select(paymentType);
        view.saveChangesToPaymentType(paymentTypesTable, editPaymentBinder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Change the text to empty and try saving, no update will be called
        // because the text is empty
        editPaymentBinder.getBean().setTransactionTypeName("");
        editPaymentBinder.readBean(editPaymentBinder.getBean());
        view.saveChangesToPaymentType(paymentTypesTable, editPaymentBinder);
        verify(database, times(0)).update(updateCaptor.capture());

        // Change the text to something other than the original
        // and try saving. This time database update will be called
        editPaymentBinder.getBean().setTransactionTypeName("Kontanter");
        editPaymentBinder.readBean(editPaymentBinder.getBean());
        view.saveChangesToPaymentType(paymentTypesTable, editPaymentBinder);
        verify(database, times(1)).update(updateCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testSaveChangesToPaymentTypeWithException() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");

        // Create object under test
        AdminView view = new AdminView(provider, request);

        // Force an exception to check the exception handling
        view.saveChangesToPaymentType(null, null);
    }

    @Test
    public void testIdenticalToExistingValues() {
        TransactionType pay1 = new TransactionType(4, "Innbetalt på konto", null, false, true);
        TransactionType pay2 = new TransactionType(0, "", 0.0, false, true);
        assertFalse(AdminView.identicalToExistingValues(pay1, pay2));
        pay2.setTransactionTypeName("Innbetalt på konto");
        assertTrue(AdminView.identicalToExistingValues(pay1, pay2));
        pay1.setTransactionAmount(0.0);
        assertTrue(AdminView.identicalToExistingValues(pay1, pay2));
        pay2.setTransactionAmount(10.0);
        assertFalse(AdminView.identicalToExistingValues(pay1, pay2));
    }

    @Test
    public void testCreateUserInDatabase() throws Exception {
        // Set up the mocks (mainly needed to be able to create an AdminView)
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Extract form data bindings from the object under test
        Binder<User> newUserBinder = view.newUserBinder;
        Binder<Passwords> newUserPasswordBinder = view.newUserPasswordBinder;

        // Test with empty form fields (no user should be attempted created, ie. database update never called)
        view.createUserInDatabase(newUserBinder, newUserPasswordBinder, getClass());
        ArgumentCaptor<PreparedStatement> neverCalledUpdateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Set the username but keep the email field empty
        // so the database update still won't be called.
        User user = newUserBinder.getBean();
        user.setUsername("usr");
        view.createUserInDatabase(newUserBinder, newUserPasswordBinder, getClass());
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Set the rest of the user fields, but since password fields
        // still are blank, the update still won't be called
        user.setEmail("usr@gmail.com");
        user.setFirstname("User");
        user.setLastname("Name");
        newUserBinder.readBean(user);
        view.createUserInDatabase(newUserBinder, newUserPasswordBinder, getClass());
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Set the password form field, and this time the database update
        // is called exactly once.
        Passwords passwords = newUserPasswordBinder.getBean();
        passwords.setPassword1("secret");
        passwords.setPassword2("secret"); // The two fields values must match to be valid
        newUserPasswordBinder.readBean(passwords);
        view.createUserInDatabase(newUserBinder, newUserPasswordBinder, getClass());
        ArgumentCaptor<PreparedStatement> calledOnceUpdateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(1)).update(calledOnceUpdateCaptor.capture());
    }

    @Test
    public void testChangeUserPasswordInDatabase() throws Exception {
        // Set up the mocks (mainly needed to be able to create an AdminView)
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Insert some dummy data
        User user = new User(123, "usr", "usr@gmail.com", "User", "Name");
        view.editUserPasswordUsers.getItems().addAll(Arrays.asList(user));
        view.editUserPasswordUsers.refreshAll();

        Binder<Passwords> changeUserPasswordBinder = view.changeUserPasswordBinder;
        NativeSelect<User> editUserPasswordUsersField = view.editUserPasswordUsersField;

        // Test with empty form fields (no password update should be attempted, ie. database update never called)
        view.changeUserPasswordInDatabase(changeUserPasswordBinder, editUserPasswordUsersField, getClass());
        ArgumentCaptor<PreparedStatement> neverCalledUpdateCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Test with a selected user and empty form fields
        // (no password update should be attempted, ie. database update never called)
        editUserPasswordUsersField.setSelectedItem(user);
        view.changeUserPasswordInDatabase(changeUserPasswordBinder, editUserPasswordUsersField, getClass());
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Test with a selected user and empty form fields
        // (no password update should be attempted, ie. database update never called)
        editUserPasswordUsersField.setSelectedItem(user);
        view.changeUserPasswordInDatabase(changeUserPasswordBinder, editUserPasswordUsersField, getClass());
        verify(database, times(0)).update(neverCalledUpdateCaptor.capture());

        // Test with a selected user and two identical password fields
        // This time, database update should be called.
        Passwords passwords = changeUserPasswordBinder.getBean();
        passwords.setPassword1("secret");
        passwords.setPassword2("secret");
        changeUserPasswordBinder.readBean(passwords);
        view.changeUserPasswordInDatabase(changeUserPasswordBinder, editUserPasswordUsersField, getClass());
        ArgumentCaptor<PreparedStatement> updateCalledOnceCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(1)).update(updateCalledOnceCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testBlankPasswordFieldsWithException() throws Exception {
        // Set up the mocks (mainly needed to be able to create an AdminView)
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        Binder<Passwords> changeUserPasswordBinder = view.changeUserPasswordBinder;
        changeUserPasswordBinder.setBean(null);
        view.blankPasswordFields(changeUserPasswordBinder);
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
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Insert some dummy data
        User user = new User(123, "usr", "usr@gmail.com", "User", "Name");
        view.editUserUsers.getItems().addAll(Arrays.asList(user));
        view.editUserUsers.refreshAll();

        // Run the code with no selection in the NativeSelect, update should
        // not be called in the database
        Binder<User> editUserBinder = view.editUserBinder;
        NativeSelect<User> editUserUserField = view.editUserUsersField;
        view.saveUserModifications(editUserBinder, editUserUserField );
        ArgumentCaptor<PreparedStatement> updateArgumentCaptor = ArgumentCaptor.forClass(PreparedStatement.class);
        verify(database, times(0)).update(updateArgumentCaptor.capture());

        // Select an element. Update should still not be called
        // since the fields are identical to the current values
        editUserUserField.setSelectedItem(user);
        view.saveUserModifications(editUserBinder, editUserUserField );
        verify(database, times(0)).update(updateArgumentCaptor.capture());

        // Set the email field to something that does not validate
        // update should still not be called
        User userInForms = editUserBinder.getBean();
        userInForms.setEmail("");
        editUserBinder.readBean(userInForms);
        view.saveUserModifications(editUserBinder, editUserUserField );
        verify(database, times(0)).update(updateArgumentCaptor.capture());

        // Set the email field to something that validates as an email address
        // this time update should be called
        userInForms.setEmail("notauser@gmail.com");
        editUserBinder.readBean(userInForms);
        view.saveUserModifications(editUserBinder, editUserUserField );
        verify(database, times(1)).update(updateArgumentCaptor.capture());
    }

    @Test(expected=UkelonnException.class)
    public void testSaveUserModificationsWithExceptionThrown() throws Exception {
        UkelonnUIProvider provider = new UkelonnUIProvider();
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        ResultSet emptyResult = mock(ResultSet.class);
        when(database.query(any())).thenReturn(emptyResult);
        provider.setUkelonnDatabase(database);
        VaadinRequest request = mock(VaadinRequest.class);

        // Create the object under test
        AdminView view = new AdminView(provider, request);

        // Insert some dummy data
        User user = new User(123, "usr", "usr@gmail.com", "User", "Name");
        view.editUserUsers.getItems().addAll(Arrays.asList(user));
        view.editUserUsers.refreshAll();


        Binder<User> editUserBinder = null;
        NativeSelect<User> editUserUserField = view.editUserUsersField;

        editUserUserField.setSelectedItem(user);
        view.saveUserModifications(editUserBinder, editUserUserField );
    }

    @Test
    public void testEditedUserDifferentFromSelectedUser() {
        User selectedUser = new User(123, "usr", "usr@gmail.com", "User", "Name");
        User user = new User(0, "", "", "", "");
        assertFalse(AdminView.editedUserDifferentFromSelectedUser(selectedUser, user));
        user.setUsername("usr");
        assertFalse(AdminView.editedUserDifferentFromSelectedUser(selectedUser, user));
        user.setEmail("usr@gmail.com");
        assertFalse(AdminView.editedUserDifferentFromSelectedUser(selectedUser, user));
        user.setFirstname("User");
        assertFalse(AdminView.editedUserDifferentFromSelectedUser(selectedUser, user));
        user.setLastname("Name");
        assertTrue(AdminView.editedUserDifferentFromSelectedUser(selectedUser, user));
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
