/*
 * Copyright 2016-2018 Steinar Bang
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.shiro.SecurityUtils;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.impl.data.AmountAndBalance;
import no.priv.bang.ukelonn.impl.data.Passwords;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ComboBox;

public class AdminFallbackView extends AbstractView { // NOSONAR
    static final int ID_OF_PAY_TO_BANK = 4;
    private static final long serialVersionUID = -1581589472749242129L;
    private UkelonnUIProvider provider;

    // Datamodel for the UI (updates to these will be transferred to the GUI listeners).
    private Label greeting;
    private AmountAndBalance amountAndBalance = new AmountAndBalance(); // NOSONAR
    Binder<AmountAndBalance> amountAndBalanceBinder = new Binder<>(AmountAndBalance.class);
    ListDataProvider<Transaction> recentJobs = new ListDataProvider<>(new ArrayList<>(10));
    ListDataProvider<Transaction> recentPayments = new ListDataProvider<>(new ArrayList<>(10));
    ListDataProvider<TransactionType> paymentTypes = new ListDataProvider<>(new ArrayList<>(10));
    ListDataProvider<TransactionType> jobTypes = new ListDataProvider<>(new ArrayList<>(10));
    ListDataProvider<Account> accountsContainer = new ListDataProvider<>(new ArrayList<>(10));
    private TransactionType newJobType = new TransactionType(0, "", 0.0, true, false); // NOSONAR
    Binder<TransactionType> newJobTypeBinder = new Binder<>(TransactionType.class);
    private TransactionType editedJobType = new TransactionType(0, "", 0.0, true, false); // NOSONAR
    Binder<TransactionType> editedJobTypeBinder = new Binder<>(TransactionType.class);
    private TransactionType newPaymentType = new TransactionType(0, "", 0.0, false, true); // NOSONAR
    Binder<TransactionType> newPaymentTypeBinder = new Binder<>(TransactionType.class);
    private TransactionType editedPaymentType = new TransactionType(0, "", 0.0, false, true); // NOSONAR
    Binder<TransactionType> editedPaymentTypeBinder = new Binder<>(TransactionType.class);
    private User newUser = new User(0, "", "", "", ""); // NOSONAR
    Binder<User> newUserBinder = new Binder<>(User.class);
    private Passwords newUserPasswords = new Passwords("", ""); // NOSONAR
    Binder<Passwords> newUserPasswordsBinder = new Binder<>(Passwords.class);
    private User editedUser = new User(0, "", "", "", ""); // NOSONAR
    Binder<User> editedUserBinder = new Binder<>(User.class);
    private Passwords editedUserPasswords = new Passwords("", ""); // NOSONAR
    Binder<Passwords> editedUserPasswordsBinder = new Binder<>(Passwords.class);
    ListDataProvider<User> editUserPasswordUsers = new ListDataProvider<>(new ArrayList<>());
    ListDataProvider<User> editUserUsers = new ListDataProvider<>(new ArrayList<>());

    public AdminFallbackView(UkelonnUIProvider provider, VaadinRequest request) {
        amountAndBalanceBinder.setBean(amountAndBalance);
        newJobTypeBinder.setBean(newJobType);
        editedJobTypeBinder.setBean(editedJobType);
        newPaymentTypeBinder.setBean(newPaymentType);
        editedPaymentTypeBinder.setBean(editedPaymentType);
        newUserBinder.setBean(newUser);
        newUserPasswordsBinder.setBean(newUserPasswords);
        editedUserBinder.setBean(editedUser);
        editedUserPasswordsBinder.setBean(editedUserPasswords);
        this.provider = provider;
        VerticalLayout content = new VerticalLayout();
        content.addStyleName("ukelonn-responsive-layout");
        Responsive.makeResponsive(content);
        // Display the greeting
        greeting = new Label("Ukelønn admin UI, bruker: ????");
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        // Updatable containers
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        paymentTypes.getItems().clear();
        paymentTypes.getItems().addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        jobTypes.getItems().clear();
        jobTypes.getItems().addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
        editUserPasswordUsers.getItems().clear();
        editUserPasswordUsers.getItems().addAll(getUsers(provider, getClass()));
        editUserUsers.getItems().clear();
        editUserUsers.getItems().addAll(getUsers(provider, getClass()));
        ComboBox<TransactionType> paymenttype = new ComboBox<>("Registrer utbetaling");
        paymenttype.setDataProvider(paymentTypes);

        Accordion accordion = new Accordion();
        createPaymentRegistrationTab(paymenttype, accordion);
        createJobtypeAdminTab(accordion);
        createPaymenttypesAdminTab(accordion);
        createUserAdminTab(accordion);

        content.addComponent(accordion);

        HorizontalLayout links = createLinksToBrowserVersionAndLogout(request, "mobile", "Mobilversjon");
        content.addComponent(links);

        addComponent(content);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        AdminUser admin = getAdminUserFromDatabase(provider, getClass(), currentUser);
        greeting.setValue("Ukelønn admin UI, bruker: " + admin.getFirstname());
    }

    private void createPaymentRegistrationTab(ComboBox<TransactionType> paymenttype, Accordion accordion) {
        Accordion registerPaymentTab = new Accordion();
        VerticalLayout userinfo = new VerticalLayout();
        List<Account> accounts = getAccounts(provider, getClass());
        accountsContainer.getItems().clear();
        accountsContainer.getItems().addAll(accounts);
        ComboBox<Account> accountSelector = new ComboBox<>("Velg hvem det skal betales til");
        accountSelector.setDataProvider(accountsContainer);
        accountSelector.setItemCaptionGenerator(Account::getFullName);
        accountSelector.addSelectionListener(account->updateFormsAfterAccountIsSelected(paymenttype, accountSelector));
        userinfo.addComponent(accountSelector);

        FormLayout paymentLayout = new FormLayout();
        TextField balanceDisplay = new TextField("Til gode:");
        amountAndBalanceBinder.forField(balanceDisplay)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind("balance");
        balanceDisplay.addStyleName("inline-label");
        paymentLayout.addComponent(balanceDisplay);

        paymenttype.setItemCaptionGenerator(TransactionType::getTransactionTypeName);
        paymenttype.addSelectionListener(pt->updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype));
        paymentLayout.addComponent(paymenttype);

        TextField amountField = new TextField("Beløp:");
        amountAndBalanceBinder.forField(amountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind("amount");
        paymentLayout.addComponent(amountField);

        paymentLayout.addComponent(
            new Button("Registrer betaling",
                       new Button.ClickListener() {
                           private static final long serialVersionUID = 5260321175219218136L;

                           @Override
                           public void buttonClick(ClickEvent event) {
                               registerPaymentInDatabase(paymenttype, accountSelector);
                           }
                       }));
        userinfo.addComponent(paymentLayout);

        registerPaymentTab.addTab(userinfo, "Brukerinfo");
        VerticalLayout jobsTab = new VerticalLayout();
        Grid<Transaction> lastJobsTable = createTransactionTable("Jobbtype", recentJobs, true);
        jobsTab.addComponent(lastJobsTable);
        registerPaymentTab.addTab(jobsTab, "Siste jobber");
        VerticalLayout paymentsTab = new VerticalLayout();
        Grid<Transaction> lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments, false);
        paymentsTab.addComponent(lastPaymentsTable);
        registerPaymentTab.addTab(paymentsTab, "Siste utbetalinger");
        accordion.addTab(wrapInPanel(registerPaymentTab), "Registrere utbetaling");
    }

    @SuppressWarnings("unchecked")
    private void createJobtypeAdminTab(Accordion accordion) {
        VerticalLayout jobtypeAdminTab = new VerticalLayout();
        Accordion jobtypes = new Accordion();
        FormLayout newJobTypeTab = new FormLayout();
        TextField newJobTypeNameField = new TextField("Navn på ny jobbtype:");
        newJobTypeBinder.forField(newJobTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        newJobTypeTab.addComponent(newJobTypeNameField);
        TextField newJobTypeAmountField = new TextField("Beløp for ny jobbtype:");
        newJobTypeBinder.forField(newJobTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        newJobTypeTab.addComponent(newJobTypeAmountField);
        newJobTypeTab.addComponent(new Button("Lag jobbtype", new Button.ClickListener() {
                private static final long serialVersionUID = 1338062460936195627L;

                @Override
                public void buttonClick(ClickEvent event) {
                    makeNewJobType();
                }
            }));
        jobtypes.addTab(newJobTypeTab, "Lag ny jobbtype");
        VerticalLayout jobtypesform = new VerticalLayout();
        Grid<TransactionType> jobtypesTable = new Grid<>(TransactionType.class);
        jobtypesTable.setDataProvider(jobTypes);
        jobtypesTable.setDataProvider(jobTypes);
        jobtypesTable.setSelectionMode(SelectionMode.SINGLE);
        jobtypesTable.removeColumn("id");
        jobtypesTable.removeColumn("transactionIsWork");
        jobtypesTable.removeColumn("transactionIsWagePayment");
        Column<TransactionType, ?> nameColumn = jobtypesTable.getColumn(TRANSACTION_TYPE_NAME_PROPERTY).setCaption("Navn");
        Column<TransactionType, ?> amountColumn = jobtypesTable.getColumn(TRANSACTION_AMOUNT_PROPERTY).setCaption("Beløp");
        jobtypesTable.setColumnOrder(nameColumn, amountColumn);
        jobtypesTable.addSelectionListener(jt->updateEditJobTypeFormsWhenJobTypeIsSelected(jobtypesTable));
        jobtypesform.addComponent(jobtypesTable);
        FormLayout editJobLayout = new FormLayout();
        TextField editJobTypeNameField = new TextField("Endre Navn på jobbtype:");
        editedJobTypeBinder.forField(editJobTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        editJobLayout.addComponent(editJobTypeNameField);
        TextField editJobTypeAmountField = new TextField("Endre beløp for jobbtype:");
        editedJobTypeBinder.forField(editJobTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        editJobLayout.addComponent(editJobTypeAmountField);
        Button saveChanges = new Button("Lagre endringer i jobbtype");
        saveChanges.addClickListener(event->saveChangesToJobType(jobtypesTable, editJobTypeNameField));
        editJobLayout.addComponent(saveChanges);
        jobtypesform.addComponent(editJobLayout);
        jobtypes.addTab(jobtypesform, "Endre jobbtyper");
        jobtypeAdminTab.addComponent(jobtypes);
        accordion.addTab(wrapInPanel(jobtypeAdminTab), "Administrere jobbtyper");
    }

    void updateEditJobTypeFormsWhenJobTypeIsSelected(Grid<TransactionType> jobtypesTable) {
        Set<TransactionType> selection = jobtypesTable.getSelectedItems();
        TransactionType transactionType = selection.isEmpty() ? null : selection.iterator().next();
        if (transactionType != null) {
            editedJobTypeBinder.readBean(transactionType);
            try {
                editedJobTypeBinder.writeBean(editedJobTypeBinder.getBean());
            } catch (ValidationException e) {
                throw new UkelonnException("Failed to select job type for change", e);
            }
        }
    }

    private boolean identicalToExistingValues(TransactionType transactionType, TransactionType otherTransactionType) {
        if (transactionType == null || transactionType.getTransactionTypeName() == null || transactionType.getTransactionAmount() == null) {
            return false; // Nothing to compare against, always false
        }

        return
            transactionType.getTransactionTypeName().equals(otherTransactionType.getTransactionTypeName()) &&
            transactionType.getTransactionAmount().equals(otherTransactionType.getTransactionAmount());
    }

    @SuppressWarnings("unchecked")
    private void createPaymenttypesAdminTab(Accordion accordion) {
        VerticalLayout paymentstypeadminTab = new VerticalLayout();
        Accordion paymentstypeadmin = new Accordion();
        FormLayout newpaymenttypeTab = new FormLayout();
        TextField newPaymentTypeNameField = new TextField("Navn på ny betalingstype:");
        newPaymentTypeBinder.forField(newPaymentTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        newpaymenttypeTab.addComponent(newPaymentTypeNameField);
        TextField newPaymentTypeAmountField = new TextField("Beløp for ny betalingstype:");
        newPaymentTypeBinder.forField(newPaymentTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        newpaymenttypeTab.addComponent(newPaymentTypeAmountField);
        newpaymenttypeTab.addComponent(new Button("Lag betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2160144195348196823L;

                @Override
                public void buttonClick(ClickEvent event) {
                    createPaymentType();
                }
            }));
        paymentstypeadmin.addTab(newpaymenttypeTab, "Lag ny utbetalingstype");
        VerticalLayout paymenttypesform = new VerticalLayout();
        Grid<TransactionType> paymentTypesTable = new Grid<>(TransactionType.class);
        paymentTypesTable.setDataProvider(paymentTypes);
        paymentTypesTable.setDataProvider(paymentTypes);
        paymentTypesTable.removeColumn("id");
        paymentTypesTable.removeColumn("transactionIsWork");
        paymentTypesTable.removeColumn("transactionIsWagePayment");
        Column<TransactionType, ?> nameColumn = paymentTypesTable.getColumn(TRANSACTION_TYPE_NAME_PROPERTY).setCaption("Navn");
        Column<TransactionType, ?> amountColumn = paymentTypesTable.getColumn(TRANSACTION_AMOUNT_PROPERTY).setCaption("Beløp");
        paymentTypesTable.setColumnOrder(nameColumn, amountColumn);
        paymentTypesTable.addSelectionListener(pt->updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypesTable));
        paymenttypesform.addComponent(paymentTypesTable);
        FormLayout editPaymentsLayout = new FormLayout();
        TextField editPaymentTypeNameField = new TextField("Endre Navn på betalingstype:");
        editedPaymentTypeBinder.forField(editPaymentTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        editPaymentsLayout.addComponent(editPaymentTypeNameField);
        TextField editPaymentTypeAmountField = new TextField("Endre beløp for betalingstype:");
        editedPaymentTypeBinder.forField(editPaymentTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        editPaymentsLayout.addComponent(editPaymentTypeAmountField);
        editPaymentsLayout.addComponent(new Button("Lagre endringer i betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2168895121731055862L;

                @Override
                public void buttonClick(ClickEvent event) {
                    saveChangesToPaymentTypes(paymentTypesTable, editPaymentTypeNameField);
                }
            }));
        paymenttypesform.addComponent(editPaymentsLayout);
        paymentstypeadmin.addTab(paymenttypesform, "Endre utbetalingstyper");
        paymentstypeadminTab.addComponent(paymentstypeadmin);
        accordion.addTab(wrapInPanel(paymentstypeadminTab), "Administrere utbetalingstyper");
    }

    private void createUserAdminTab(Accordion accordion) {
        Class<?> classForLogMessage = getClass();
        VerticalLayout useradminTab = new VerticalLayout();
        Accordion useradmin = new Accordion();
        FormLayout newUserTab = new FormLayout();
        TextField newUserUsernameField = new TextField("Brukernavn:");
        newUserBinder.forField(newUserUsernameField).bind("username");
        newUserTab.addComponent(newUserUsernameField);
        PasswordField newUserPassword1Field = new PasswordField("Passord:");
        newUserPasswordsBinder.forField(newUserPassword1Field).bind("password1");
        newUserTab.addComponent(newUserPassword1Field);
        PasswordField newUserPassword2Field = new PasswordField("Gjenta passord:");
        newUserPasswordsBinder
            .forField(newUserPassword2Field)
            .withValidator(new PasswordCompareValidator("Passord ikke identisk", newUserPassword1Field))
            .bind("password2");
        newUserTab.addComponent(newUserPassword2Field);
        TextField newUserEmailField = new TextField("Epostadresse:");
        newUserBinder.forField(newUserEmailField)
            .withValidator(new EmailValidator("Ikke en gyldig epostadresse"))
            .bind("email");
        newUserTab.addComponent(newUserEmailField);
        TextField newUserFirstnameField = new TextField("Fornavn:");
        newUserBinder.forField(newUserFirstnameField).bind("firstname");
        newUserTab.addComponent(newUserFirstnameField);
        TextField newUserLastnameField = new TextField("Etternavn:");
        newUserBinder.forField(newUserFirstnameField).bind("lastname");
        newUserTab.addComponent(newUserLastnameField);
        Button createUser = new Button("Lag bruker");
        createUser.addClickListener(event->createNewUser(getClass()));
        newUserTab.addComponent(createUser);
        useradmin.addTab(newUserTab, "Legg til ny bruker");
        FormLayout changeuserpasswordTab = new FormLayout();
        ComboBox<User> editUserPasswordUsersField = new ComboBox<>("Velg bruker");
        editUserPasswordUsersField.setDataProvider(editUserPasswordUsers);
        editUserPasswordUsersField.setItemCaptionGenerator(User::getFullname);
        editUserPasswordUsersField.addSelectionListener(u->updatePasswordFieldsWhenUserIsSelected(editUserPasswordUsersField));
        changeuserpasswordTab.addComponent(editUserPasswordUsersField);
        PasswordField editUserPassword1Field = new PasswordField("Passord:");
        editedUserPasswordsBinder.forField(editUserPassword1Field).bind("password1");
        changeuserpasswordTab.addComponent(editUserPassword1Field);
        PasswordField editUserPassword2Field = new PasswordField("Gjenta passord:");
        editedUserPasswordsBinder
            .forField(editUserPassword2Field)
            .withValidator(new PasswordCompareValidator("Passord ikke identisk", editUserPassword1Field))
            .bind("password2");
        changeuserpasswordTab.addComponent(editUserPassword2Field);
        Button changePassword = new Button("Endre passord");
        changePassword.addClickListener(event->{
                User user = editUserPasswordUsersField.getValue();
                if (user != null &&
                    !"".equals(editUserPassword1Field.getValue())
                    && editedUserPasswordsBinder.isValid())
                {
                    Passwords bean = editedUserPasswordsBinder.getBean();
                    changePasswordForUser(provider, user.getUsername(), bean.getPassword2(), classForLogMessage);
                    bean.setPassword1("");
                    bean.setPassword2("");
                    editedUserPasswordsBinder.readBean(bean);
                }
            });
        changeuserpasswordTab.addComponent(changePassword);
        useradmin.addTab(changeuserpasswordTab, "Bytt passord på bruker");
        FormLayout usersTab = new FormLayout();
        ComboBox<User> editUserUsersField = new ComboBox<>("Velg bruker");
        editUserUsersField.setDataProvider(editUserUsers);
        editUserUsersField.setItemCaptionGenerator(User::getFullname);
        editUserUsersField.addSelectionListener(u->{
                Optional<User> user = editUserUsersField.getSelectedItem();
                if (user.isPresent()) {
                    editedUserBinder.readBean(user.get());
                }
            });
        usersTab.addComponent(editUserUsersField);

        TextField editUserUsernameField = new TextField("Brukernavn:");

        usersTab.addComponent(editUserUsernameField);
        TextField editUserEmailField = new TextField("Epostadresse:");
        editedUserBinder.forField(editUserEmailField)
            .withValidator(new EmailValidator("Ikke en gyldig epostadresse"))
            .bind("email");
        usersTab.addComponent(editUserEmailField);
        TextField editUserFirstnameField = new TextField("Fornavn:");
        editedUserBinder.forField(editUserFirstnameField).bind("firstname");
        usersTab.addComponent(editUserFirstnameField);
        TextField editUserLastnameField = new TextField("Etternavn:");
        editedUserBinder.forField(editUserFirstnameField).bind("lastname");
        usersTab.addComponent(editUserLastnameField);
        usersTab.addComponent(new Button("Lagre endringer av bruker", new Button.ClickListener() {
                private static final long serialVersionUID = 1658760136279718499L;

                @Override
                public void buttonClick(ClickEvent event) {
                    Optional<User> user = editUserUsersField.getSelectedItem();
                    if (user.isPresent()) {
                        User editeduser = editedUserBinder.getBean();
                        try {
                            editedUserBinder.writeBean(editeduser);
                        } catch (ValidationException e) {
                            throw new UkelonnException("Failed to set form fields for user to be edited", e);
                        }

                        user.get().setUsername(editeduser.getUsername());
                        user.get().setEmail(editeduser.getEmail());
                        user.get().setFirstname(editeduser.getFirstname());
                        user.get().setLastname(editeduser.getLastname());

                        updateUserInDatabase(provider, classForLogMessage, user.get());

                        clearFormElements();

                        refreshListWidgetsAffectedByChangesToUsers();
                    }
                }

                private void clearFormElements() {
                    User empty = new User(0, "", "", "", "");
                    editedUserBinder.readBean(empty);
                }

                private void refreshListWidgetsAffectedByChangesToUsers() {
                    List<Account> accounts = getAccounts(provider, classForLogMessage);
                    accountsContainer.getItems().clear();
                    accountsContainer.getItems().addAll(accounts);
                    accountsContainer.refreshAll();
                    List<User> users = getUsers(provider, classForLogMessage);
                    editUserPasswordUsers.getItems().clear();
                    editUserUsers.getItems().clear();
                    editUserPasswordUsers.getItems().addAll(users);
                    editUserUsers.getItems().addAll(users);
                    editUserPasswordUsers.refreshAll();
                    editUserUsers.refreshAll();
                }
            }));
        useradmin.addTab(usersTab, "Endre brukere");
        useradminTab.addComponent(useradmin);
        accordion.addTab(wrapInPanel(useradminTab), "Administrere brukere");
    }

    void updatePasswordFieldsWhenUserIsSelected(ComboBox<User> editUserPasswordUsersField) {
        Optional<User> user = editUserPasswordUsersField.getSelectedItem();
        if (user.isPresent()) {
            Passwords bean = editedUserPasswordsBinder.getBean();
            bean.setPassword1("");
            bean.setPassword2("");
            editedUserPasswordsBinder.readBean(bean);
        }
    }

    void createNewUser(Class<?> classForLogMessage) {
        if (newUserIsAValidUser())
        {
            addUserToDatabase(
                provider,
                classForLogMessage,
                newUserBinder.getBean().getUsername(),
                newUserPasswordsBinder.getBean().getPassword2(),
                newUserBinder.getBean().getEmail(),
                newUserBinder.getBean().getFirstname(),
                newUserBinder.getBean().getLastname());

            clearAllNewUserFormElements();

            refreshListWidgetsAffectedByChangesToUsers();
        }
    }

    private void clearAllNewUserFormElements() {
        User emptyUser = new User(0, "", "", "", "");
        Passwords blankPasswords = new Passwords("", "");
        newUserBinder.readBean(emptyUser);
        newUserPasswordsBinder.readBean(blankPasswords);
        try {
            newUserBinder.writeBean(newUserBinder.getBean());
            newUserPasswordsBinder.writeBean(newUserPasswordsBinder.getBean());
        } catch (ValidationException e) {
            throw new UkelonnException("Failed to blank the \"new user\" form fields", e);
        }
    }

    private boolean newUserIsAValidUser() {
        return
            !"".equals(newUserBinder.getBean().getUsername()) &&
            !"".equals(newUserPasswordsBinder.getBean().getPassword1()) &&
            newUserPasswordsBinder.getBean().getPassword1().equals(newUserPasswordsBinder.getBean().getPassword2()) &&
            newUserPasswordsBinder.isValid() &&
            newUserBinder.isValid() &&
            !"".equals(newUserBinder.getBean().getFirstname()) &&
            !"".equals(newUserBinder.getBean().getLastname());
    }

    private void refreshListWidgetsAffectedByChangesToUsers() {
        List<Account> accounts = getAccounts(provider, getClass());
        accountsContainer.getItems().clear();
        accountsContainer.getItems().addAll(accounts);
        List<User> users = getUsers(provider, getClass());
        editUserPasswordUsers.getItems().clear();
        editUserUsers.getItems().clear();
        editUserPasswordUsers.getItems().addAll(users);
        editUserUsers.getItems().addAll(users);
        editUserPasswordUsers.refreshAll();
        editUserUsers.refreshAll();
    }

    private void refreshJobTypesFromDatabase() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        jobTypes.getItems().clear();
        jobTypes.getItems().addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
        jobTypes.refreshAll();
    }

    private void refreshPaymentTypesFromDatabase() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        paymentTypes.getItems().clear();
        paymentTypes.getItems().addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        paymentTypes.refreshAll();
    }

    void updateFormsAfterAccountIsSelected(ComboBox<TransactionType> paymenttype, ComboBox<Account> accountSelector) {
        Account account = accountSelector.getValue();
        jobTypes.getItems().clear();
        paymentTypes.getItems().clear();
        recentJobs.getItems().clear();
        recentPayments.getItems().clear();
        if (account != null) {
            refreshAccount(provider, getClass(), account);
            amountAndBalanceBinder.getBean().setBalance(account.getBalance());
            amountAndBalanceBinder.readBean(amountAndBalanceBinder.getBean());
            Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
            jobTypes.getItems().addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
            paymentTypes.getItems().addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
            paymenttype.setSelectedItem(transactionTypes.get(ID_OF_PAY_TO_BANK));
            amountAndBalanceBinder.getBean().setBalance(account.getBalance());
            amountAndBalanceBinder.readBean(amountAndBalanceBinder.getBean());
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
        }

        jobTypes.refreshAll();
        paymentTypes.refreshAll();
        recentJobs.refreshAll();
        recentPayments.refreshAll();
    }

    void updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(ComboBox<TransactionType> paymenttype) {
        TransactionType payment = paymenttype.getValue();
        if (payment != null) {
            Double paymentAmount = payment.getTransactionAmount();
            if (payment.getId() == ID_OF_PAY_TO_BANK || paymentAmount == null) {
                AmountAndBalance bean = amountAndBalanceBinder.getBean();
                bean.setAmount(bean.getBalance());
                amountAndBalanceBinder.readBean(bean);
            } else {
                AmountAndBalance bean = amountAndBalanceBinder.getBean();
                bean.setAmount(paymentAmount);
                amountAndBalanceBinder.readBean(bean);
            }
        }
    }

    void registerPaymentInDatabase(ComboBox<TransactionType> paymenttype, ComboBox<Account> accountSelector) {
        Account account = accountSelector.getValue();
        TransactionType payment = paymenttype.getValue();
        if (account != null && payment != null) {
            addNewPaymentToAccount(provider, getClass(), account, payment, amountAndBalanceBinder.getBean().getAmount());
            recentPayments.getItems().clear();
            recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
            recentPayments.refreshAll();
            recentJobs.getItems().clear();
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentJobs.refreshAll();
            refreshAccount(provider, getClass(), account);
            AmountAndBalance bean = amountAndBalanceBinder.getBean();
            bean.setBalance(account.getBalance());
            bean.setAmount(0.0);
            amountAndBalanceBinder.readBean(bean);
        }
    }

    void makeNewJobType() {
        TransactionType newJob = newJobTypeBinder.getBean();
        if (!"".equals(newJob.getTransactionTypeName()) && !Double.valueOf(0.0).equals(newJob.getTransactionAmount())) {
            addJobTypeToDatabase(provider, getClass(), newJob.getTransactionTypeName(), newJob.getTransactionAmount());
            newJob.setTransactionTypeName("");
            newJob.setTransactionAmount(0.0);
            newJobTypeBinder.readBean(newJob);
            refreshJobTypesFromDatabase();
        }
    }

    void saveChangesToJobType(Grid<TransactionType> jobtypesTable, TextField editJobTypeNameField) {
        Set<TransactionType> selectedJobtypes = jobtypesTable.getSelectedItems();
        TransactionType transactionType = selectedJobtypes.isEmpty() ? null : selectedJobtypes.iterator().next();
        if (transactionType != null &&
            !"".equals(editJobTypeNameField.getValue()) &&
            !identicalToExistingValues(transactionType, editedJobTypeBinder.getBean()))
        {
            TransactionType editedJobtype = editedJobTypeBinder.getBean();
            transactionType.setTransactionTypeName(editedJobtype.getTransactionTypeName());
            transactionType.setTransactionAmount(editedJobtype.getTransactionAmount());
            updateTransactionTypeInDatabase(provider, getClass(), transactionType);
            jobtypesTable.deselectAll();
            editedJobtype.setTransactionTypeName("");
            editedJobtype.setTransactionAmount(0.0);
            editedJobTypeBinder.readBean(editedJobtype);
            refreshJobTypesFromDatabase();
        }
    }

    void createPaymentType() {
        TransactionType newPaymenttype = newPaymentTypeBinder.getBean();
        if (!"".equals(newPaymenttype.getTransactionTypeName())) {
            addPaymentTypeToDatabase(provider, getClass(), newPaymenttype.getTransactionTypeName(), newPaymenttype.getTransactionAmount());
            newPaymenttype.setTransactionTypeName("");
            newPaymenttype.setTransactionAmount(0.0);
            newPaymentTypeBinder.readBean(newPaymenttype);
            refreshPaymentTypesFromDatabase();
        }
    }

    void updatePaymentForEditWhenPaymentTypeIsSelected(Grid<TransactionType> paymentTypesTable) {
        Set<TransactionType> selectedPaymenttypes = paymentTypesTable.getSelectedItems();
        TransactionType transactionType = selectedPaymenttypes.isEmpty() ? null : selectedPaymenttypes.iterator().next();
        if (transactionType != null) {
            editedPaymentTypeBinder.readBean(transactionType);
            try {
                editedPaymentTypeBinder.writeBean(editedPaymentTypeBinder.getBean());
            } catch (ValidationException e) {
                throw new UkelonnException("Failed to update payment type model bean", e);
            }
        }
    }

    void saveChangesToPaymentTypes(Grid<TransactionType> paymentTypesTable, TextField editPaymentTypeNameField) {
        Set<TransactionType> selectedPaymenttypes = paymentTypesTable.getSelectedItems();
        TransactionType transactionType = selectedPaymenttypes.isEmpty() ? null : selectedPaymenttypes.iterator().next();
        if (transactionType != null &&
            !"".equals(editPaymentTypeNameField.getValue()) &&
            !identicalToExistingValues(transactionType, editedPaymentTypeBinder.getBean()))
        {
            transactionType.setTransactionTypeName(editedPaymentTypeBinder.getBean().getTransactionTypeName());
            transactionType.setTransactionAmount(editedPaymentTypeBinder.getBean().getTransactionAmount());
            updateTransactionTypeInDatabase(provider, getClass(), transactionType);
            paymentTypesTable.deselectAll();
            editedPaymentTypeBinder.getBean().setTransactionTypeName("");
            editedPaymentTypeBinder.getBean().setTransactionAmount(0.0);
            editedPaymentTypeBinder.readBean(editedPaymentTypeBinder.getBean());
            refreshPaymentTypesFromDatabase();
        }
    }

}
