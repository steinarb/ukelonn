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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.vaadin.touchkit.ui.NavigationManager;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.TabBarView;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.impl.data.AccountHistory;
import no.priv.bang.ukelonn.impl.data.Passwords;
import no.priv.bang.ukelonn.impl.data.AmountAndBalance;


public class AdminView extends AbstractView { // NOSONAR
    static final int ID_OF_PAY_TO_BANK = 4;
    private static final long serialVersionUID = -1581589472749242129L;
    private UkelonnUIProvider provider;

    // Data model for handling payments to users
    private Label greeting = new Label("Ukelønn admin UI, bruker: ????");
    Binder<AmountAndBalance> amountAndBalanceBinder = new Binder<>(AmountAndBalance.class);
    AccountHistory accountHistory = new AccountHistory(); // NOSONAR
    Binder<AccountHistory> accountHistoryBinder = new Binder<>(AccountHistory.class); // NOSONAR
    ListDataProvider<Account> accountsContainer;
    TextField balance = new TextField("Til gode:", "0.0");
    TextField amount = new TextField("Beløp:", "0.0");
    ListDataProvider<Transaction> recentJobs = new ListDataProvider<>(getDummyTransactions());
    ListDataProvider<Transaction> recentPayments = new ListDataProvider<>(getDummyTransactions());

    // Data model for the admin tasks
    Map<Integer, TransactionType> transactionTypes; // NOSONAR
    ListDataProvider<TransactionType> paymentTypes;
    ListDataProvider<TransactionType> jobTypes;
    ListDataProvider<User> editUserPasswordUsers;
    ListDataProvider<User> editUserUsers;

    public AdminView(UkelonnUIProvider provider, VaadinRequest request) {
        this.provider = provider;
        accountsContainer = new ListDataProvider<>(getAccounts(provider, getClass()));
        transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        paymentTypes = new ListDataProvider<>(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        jobTypes = new ListDataProvider<>(getJobTypesFromTransactionTypes(transactionTypes.values()));
        editUserPasswordUsers = new ListDataProvider<>(getUsers(provider, getClass()));
        editUserUsers = new ListDataProvider<>(getUsers(provider, getClass()));
        accountHistoryBinder.setBean(accountHistory);
        setSizeFull();
        TabBarView tabs = new TabBarView();

        createPaymentRegistrationTab(tabs);
        createJobtypeAdminstrationTab(tabs);
        createPaymenttypeAdministrationTab(tabs);
        createUserAdministrationTab(tabs);

        addComponent(tabs);

        HorizontalLayout links = createLinksToBrowserVersionAndLogout(request, "browser", "Nettleserversjon");
        addComponent(links);

        // Set the stretch to minimize the size used by the link
        setExpandRatio(tabs, 100);
        setExpandRatio(links, 1);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        AdminUser admin = getAdminUserFromDatabase(provider, getClass(), currentUser);
        greeting.setValue("Ukelønn admin UI, bruker: " + admin.getFirstname());
    }

    private void createPaymentRegistrationTab(TabBarView tabs) {
        NavigationManager registerPaymentTab = new NavigationManager();
        tabs.addTab(registerPaymentTab, "Registrere utbetaling");
        VerticalComponentGroup registerPaymentTabGroup = createRegisterPaymentForm(registerPaymentTab);

        NavigationView lastJobsForUserView = createNavigationViewWithTable(registerPaymentTab, "Jobber", recentJobs, "Siste jobber", true);
        NavigationView lastPaymentsForUserView = createNavigationViewWithTable(registerPaymentTab, "Utbetalinger", recentPayments, "Siste utbetalinger", false);

        registerPaymentTabGroup.addComponent(createNavigationButton("Siste jobber for bruker", lastJobsForUserView));
        registerPaymentTabGroup.addComponent(createNavigationButton("Siste utbetalinger til bruker", lastPaymentsForUserView));
    }

    private VerticalComponentGroup createRegisterPaymentForm(NavigationManager registerPaymentTab) {
        AmountAndBalance payment = new AmountAndBalance();
        amountAndBalanceBinder.setBean(payment);
        CssLayout registerPaymentTabForm = new CssLayout();
        VerticalComponentGroup registerPaymentTabGroup = new VerticalComponentGroup();

        // Display the greeting
        registerPaymentTabGroup.addComponent(greeting);

        NativeSelect<Account> accountSelector = new NativeSelect<>("Velg hvem det skal betales til");
        NativeSelect<TransactionType> paymenttype = new NativeSelect<>("Registrer utbetaling");

        accountSelector.setItems(getAccounts(provider, getClass()));
        accountSelector.setItemCaptionGenerator(Account::getFullName);
        registerPaymentTabGroup.addComponent(accountSelector);
        accountSelector.addSelectionListener(event -> updateFormsAfterAccountIsSelected(amountAndBalanceBinder, paymenttype, accountSelector));

        FormLayout paymentLayout = new FormLayout();
        balance.addStyleName("inline-label");
        paymentLayout.addComponent(balance);
        amountAndBalanceBinder.forField(balance)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind("balance");

        paymenttype.setDataProvider(paymentTypes);
        paymenttype.setItemCaptionGenerator(TransactionType::getTransactionTypeName);
        paymenttype.addSelectionListener(event -> updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype));

        paymentLayout.addComponent(paymenttype);

        paymentLayout.addComponent(amount);
        amountAndBalanceBinder.forField(amount)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(AmountAndBalance::getAmount, AmountAndBalance::setAmount);

        Button doRegisterPayment = new Button("Registrer betaling");
        doRegisterPayment.addClickListener(event -> registerPaymentInDatabase(amountAndBalanceBinder, paymenttype, accountSelector));
        paymentLayout.addComponent(doRegisterPayment);
        registerPaymentTabGroup.addComponent(paymentLayout);
        registerPaymentTabForm.addComponent(registerPaymentTabGroup);
        NavigationView registerPaymentView = new NavigationView("Registrer betaling", registerPaymentTabForm);
        registerPaymentTab.addComponent(registerPaymentView);
        registerPaymentTab.navigateTo(registerPaymentView);
        return registerPaymentTabGroup;
    }

    @SuppressWarnings("unchecked")
    private void createJobtypeAdminstrationTab(TabBarView tabs) {
        TransactionType newJobType = new TransactionType(0, "", 0.0, true, false);
        Binder<TransactionType> newJobBinder = new Binder<>(TransactionType.class);
        newJobBinder.setBean(newJobType);
        NavigationManager jobtypeAdminTab = new NavigationManager();
        VerticalComponentGroup jobtypeAdminContent = createVerticalComponentGroupWithCssLayoutAndNavigationView(jobtypeAdminTab, new NavigationView(), "Administrere jobbtyper");

        String newJobtypeLabel = "Ny jobbtype";
        String modifyJobtypesLabel = "Endre jobbtyper";

        NavigationView newJobTypeTab = new NavigationView();
        VerticalComponentGroup newJobTypeTabContent = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(jobtypeAdminTab, newJobTypeTab, newJobtypeLabel);

        TextField newJobTypeNameField = new TextField("Navn på ny jobbtype:");
        newJobBinder.forField(newJobTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        newJobTypeTabContent.addComponent(newJobTypeNameField);

        TextField newJobTypeAmountField = new TextField("Beløp for ny jobbtype:");
        newJobBinder.forField(newJobTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        newJobTypeTabContent.addComponent(newJobTypeAmountField);

        Button makeJobType = new Button("Lag jobbtype");
        makeJobType.addClickListener(event -> makeNewJobType(newJobBinder));
        newJobTypeTabContent.addComponent(makeJobType);

        NavigationView jobtypesTab = new NavigationView();
        TransactionType editedJobtype = new TransactionType(0, "", 0.0, true, false);
        Binder<TransactionType> editJobBinder = new Binder<>(TransactionType.class);
        editJobBinder.setBean(editedJobtype);
        VerticalComponentGroup jobtypesform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(jobtypeAdminTab, jobtypesTab, modifyJobtypesLabel);
        Grid<TransactionType> jobtypesTable = new Grid<>(TransactionType.class);
        jobtypesTable.setDataProvider(jobTypes);
        jobtypesTable.setSelectionMode(SelectionMode.SINGLE);
        jobtypesTable.removeColumn("id");
        jobtypesTable.removeColumn("transactionIsWork");
        jobtypesTable.removeColumn("transactionIsWagePayment");
        Column<TransactionType, ?> nameColumn = jobtypesTable.getColumn(TRANSACTION_TYPE_NAME_PROPERTY).setCaption("Navn");
        Column<TransactionType, ?> amountColumn = jobtypesTable.getColumn(TRANSACTION_AMOUNT_PROPERTY).setCaption("Beløp");
        jobtypesTable.setColumnOrder(nameColumn, amountColumn);
        jobtypesTable.addSelectionListener(event -> setEditJobTypeFormsFromSelectedJobTypeInTable(jobtypesTable, editJobBinder));
        jobtypesform.addComponent(jobtypesTable);
        FormLayout editJobLayout = new FormLayout();

        TextField editJobTypeNameField = new TextField("Endre Navn på jobbtype:");
        editJobBinder.forField(editJobTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        editJobLayout.addComponent(editJobTypeNameField);

        TextField editJobTypeAmountField = new TextField("Endre beløp for jobbtype:");
        editJobBinder.forField(editJobTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        editJobLayout.addComponent(editJobTypeAmountField);

        Button saveEditedJobType = new Button("Lagre endringer i jobbtype");
        saveEditedJobType.addClickListener(event->saveChangesToJobType(jobtypesTable, editJobBinder));
        jobtypesform.addComponent(editJobLayout);
        editJobLayout.addComponent(saveEditedJobType);

        jobtypeAdminContent.addComponent(createNavigationButton(newJobtypeLabel, newJobTypeTab));
        jobtypeAdminContent.addComponent(createNavigationButton(modifyJobtypesLabel, jobtypesTab));
        tabs.addTab(jobtypeAdminTab, "Administrere jobbtyper");
    }

    @SuppressWarnings("unchecked")
    private void createPaymenttypeAdministrationTab(TabBarView tabs) {
        // Payment type administration.
        NavigationManager paymentstypeadminTab = new NavigationManager();
        VerticalComponentGroup paymentstypeadmin = createVerticalComponentGroupWithCssLayoutAndNavigationView(paymentstypeadminTab, new NavigationView(), "Administrere utbetalingstyper");

        String newPaymenttypeLabel = "Lag ny betalingstype";
        String modifyPayementtypesLabel = "Endre utbetalingstyper";

        NavigationView newpaymenttypeTab = new NavigationView();
        TransactionType newpaymenttype = new TransactionType(0, "", 0.0, false, true);
        Binder<TransactionType> newPaymentBinder = new Binder<>(TransactionType.class);
        newPaymentBinder.setBean(newpaymenttype);
        VerticalComponentGroup newpaymenttypeForm = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(paymentstypeadminTab, newpaymenttypeTab, newPaymenttypeLabel);

        TextField newPaymentTypeNameField = new TextField("Navn på ny betalingstype:");
        newPaymentBinder.forField(newPaymentTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        newpaymenttypeForm.addComponent(newPaymentTypeNameField);

        TextField newPaymentTypeAmountField = new TextField("Beløp for ny betalingstype:");
        newPaymentBinder.forField(newPaymentTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        newpaymenttypeForm.addComponent(newPaymentTypeAmountField);

        Button createPaymentType = new Button("Lag betalingstype");
        createPaymentType.addClickListener(event->createPaymentType(newPaymentBinder));
        newpaymenttypeForm.addComponent(createPaymentType);

        NavigationView paymentstypeTab = new NavigationView();
        TransactionType editedPaymentType = new TransactionType(0, "", 0.0, false, true);
        Binder<TransactionType> editPaymentBinder = new Binder<>(TransactionType.class);
        editPaymentBinder.setBean(editedPaymentType);
        VerticalComponentGroup paymenttypesform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(paymentstypeadminTab, paymentstypeTab, modifyPayementtypesLabel);
        Grid<TransactionType> paymentTypesTable = new Grid<>(TransactionType.class);
        paymentTypesTable.setDataProvider(paymentTypes);
        paymentTypesTable.removeColumn("id");
        paymentTypesTable.removeColumn("transactionIsWork");
        paymentTypesTable.removeColumn("transactionIsWagePayment");
        Column<TransactionType, ?> nameColumn = paymentTypesTable.getColumn(TRANSACTION_TYPE_NAME_PROPERTY).setCaption("Navn");
        Column<TransactionType, ?> amountColumn = paymentTypesTable.getColumn(TRANSACTION_AMOUNT_PROPERTY).setCaption("Beløp");
        paymentTypesTable.setColumnOrder(nameColumn, amountColumn);
        paymentTypesTable.addSelectionListener(event->updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypesTable, editPaymentBinder));
        paymenttypesform.addComponent(paymentTypesTable);
        FormLayout editPaymentsLayout = new FormLayout();

        TextField editPaymentTypeNameField = new TextField("Endre Navn på betalingstype:");
        newPaymentBinder.forField(editPaymentTypeNameField).bind(TRANSACTION_TYPE_NAME_PROPERTY);
        editPaymentsLayout.addComponent(editPaymentTypeNameField);

        TextField editPaymentTypeAmountField = new TextField("Endre beløp for betalingstype:");
        newPaymentBinder.forField(editPaymentTypeAmountField)
            .withConverter(new StringToDoubleConverter(IKKE_ET_TALL))
            .bind(TRANSACTION_AMOUNT_PROPERTY);
        editPaymentsLayout.addComponent(editPaymentTypeAmountField);

        Button saveEditedPaymentType = new Button("Lagre endringer i betalingstype");
        saveEditedPaymentType.addClickListener(event->saveChangesToPaymentTypes(paymentTypesTable, editPaymentBinder));
        editPaymentsLayout.addComponent(saveEditedPaymentType);
        paymenttypesform.addComponent(editPaymentsLayout);

        paymentstypeadmin.addComponent(createNavigationButton(newPaymenttypeLabel, newpaymenttypeTab));
        paymentstypeadmin.addComponent(createNavigationButton(modifyPayementtypesLabel, paymentstypeTab));
        tabs.addTab(paymentstypeadminTab, "Administrere utbetalingstyper");
    }

    private void createUserAdministrationTab(TabBarView tabs) {
        NavigationManager useradminTab = new NavigationManager();
        VerticalComponentGroup useradmin = createVerticalComponentGroupWithCssLayoutAndNavigationView(useradminTab, new NavigationView(), "Administrere brukere");

        String newUserLabel = "Legg til ny bruker";
        String changePasswordForUserLabel = "Bytt passord på bruker"; // NOSONAR
        String modifyUsersLabel = "Endre brukere";

        NavigationView newUserTab = new NavigationView();
        VerticalComponentGroup newUserForm = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(useradminTab, newUserTab, newUserLabel);
        Binder<User> newUserBinder = new Binder<>(User.class);
        User newUser = new User(0, "", "", "", "");
        newUserBinder.setBean(newUser);

        TextField newUserUsernameField = new TextField("Brukernavn:");
        newUserBinder.forField(newUserUsernameField).bind("username");
        newUserForm.addComponent(newUserUsernameField);

        Passwords newUserPasswords = new Passwords("", "");
        Binder<Passwords> newUserPasswordBinder = new Binder<>(Passwords.class);
        newUserPasswordBinder.setBean(newUserPasswords);
        PasswordField newUserPassword1Field = new PasswordField("Passord:");
        newUserPasswordBinder.forField(newUserPassword1Field).bind("password1");
        newUserForm.addComponent(newUserPassword1Field);
        PasswordField newUserPassword2Field = new PasswordField("Gjenta passord:");
        newUserPasswordBinder
            .forField(newUserPassword2Field)
            .withValidator(new PasswordCompareValidator("Passord ikke identisk", newUserPassword1Field))
            .bind("password2");
        newUserForm.addComponent(newUserPassword2Field);

        TextField newUserEmailField = new TextField("Epostadresse:");
        newUserBinder
            .forField(newUserEmailField)
            .withValidator(new EmailValidator("Ikke en gyldig epostadresse"))
            .bind("email");
        newUserForm.addComponent(newUserEmailField);

        TextField newUserFirstnameField = new TextField("Fornavn:");
        newUserBinder.forField(newUserFirstnameField).bind("firstname");
        newUserForm.addComponent(newUserFirstnameField);

        TextField newUserLastnameField = new TextField("Etternavn:");
        newUserBinder.forField(newUserLastnameField).bind("lastname");
        newUserForm.addComponent(newUserLastnameField);

        Class<? extends AdminView> classForLogMessage = getClass();
        Button createUser = new Button("Lag bruker");
        createUser.addClickListener(event -> {
                if (newUserIsAValidUser(newUserBinder, newUserPasswordBinder))
                {
                    addUserToDatabase(
                        provider,
                        classForLogMessage,
                        newUserBinder.getBean().getUsername(),
                        newUserPassword2Field.getValue(),
                        newUserBinder.getBean().getEmail(),
                        newUserBinder.getBean().getFirstname(),
                        newUserBinder.getBean().getLastname());

                    clearAllNewUserFormElements(newUserBinder);

                    refreshListWidgetsAffectedByChangesToUsers(classForLogMessage);
                }
            });
        newUserForm.addComponent(createUser);

        NavigationView changeuserpasswordTab = new NavigationView();
        VerticalComponentGroup changeuserpasswordForm = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(useradminTab, changeuserpasswordTab, changePasswordForUserLabel);
        Passwords changeUserPasswords = new Passwords("", "");
        Binder<Passwords> changeUserPasswordBinder = new Binder<>(Passwords.class);
        changeUserPasswordBinder.setBean(changeUserPasswords);
        NativeSelect<User> editUserPasswordUsersField = new NativeSelect<>("Velg bruker");
        editUserPasswordUsersField.setDataProvider(editUserPasswordUsers);
        editUserPasswordUsersField.setItemCaptionGenerator(User::getFullname);
        changeuserpasswordForm.addComponent(editUserPasswordUsersField);

        PasswordField editUserPassword1Field = new PasswordField("Passord:");
        changeUserPasswordBinder.forField(editUserPassword1Field).bind("password1");
        changeuserpasswordForm.addComponent(editUserPassword1Field);

        PasswordField editUserPassword2Field = new PasswordField("Gjenta passord:");
        changeUserPasswordBinder
            .forField(editUserPassword2Field)
            .withValidator(new PasswordCompareValidator("Passord ikke identisk", editUserPassword1Field))
            .bind("password2");
        changeuserpasswordForm.addComponent(editUserPassword2Field);

        Button changePassword = new Button("Endre passord");
        changePassword.addClickListener(event -> {
                Optional<User> user = editUserPasswordUsersField.getSelectedItem();
                if (user.isPresent() &&
                    !"".equals(editUserPassword1Field.getValue()) &&
                    changeUserPasswordBinder.isValid())
                {
                    changePasswordForUser(provider, user.get().getUsername(), changeUserPasswordBinder.getBean().getPassword2(), classForLogMessage);
                    Passwords emptyPasswords = new Passwords("", "");
                    changeUserPasswordBinder.readBean(emptyPasswords);
                    try {
                        changeUserPasswordBinder.writeBean(changeUserPasswordBinder.getBean());
                    } catch (ValidationException e) {
                        throw new UkelonnException("Failed to blank the password fields after updating a password", e);
                    }
                }
            });
        changeuserpasswordForm.addComponent(changePassword);

        NavigationView usersTab = new NavigationView();
        VerticalComponentGroup usersform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(useradminTab, usersTab, modifyUsersLabel);
        User editUser = new User(0, "", "", "", "");
        Binder<User> editUserBinder = new Binder<>(User.class);
        editUserBinder.setBean(editUser);

        NativeSelect<User> editUserUsersField = new NativeSelect<>("Velg bruker");
        editUserUsersField.setDataProvider(editUserUsers);
        editUserUsersField.setItemCaptionGenerator(User::getFullname);
        editUserUsersField.addValueChangeListener(event->selectAUserToBeEdited(editUserBinder, editUserUsersField));
        usersform.addComponent(editUserUsersField);

        TextField editUserUsernameField = new TextField("Brukernavn:");
        editUserBinder.forField(editUserUsernameField).bind("username");
        usersform.addComponent(editUserUsernameField);

        TextField editUserEmailField = new TextField("Epostadresse:");
        editUserBinder
            .forField(editUserEmailField)
            .withValidator(new EmailValidator("Ikke en gyldig epostadresse"))
            .bind("email");
        usersform.addComponent(editUserEmailField);

        TextField editUserFirstnameField = new TextField("Fornavn:");
        editUserBinder.forField(editUserFirstnameField).bind("firstname");
        usersform.addComponent(editUserFirstnameField);

        TextField editUserLastnameField = new TextField("Etternavn:");
        editUserBinder.forField(editUserLastnameField).bind("lastname");
        usersform.addComponent(editUserLastnameField);

        Button saveUserModifications = new Button("Lagre endringer av bruker");
        saveUserModifications.addClickListener(event->saveUserModifications(editUserBinder));
        usersform.addComponent(saveUserModifications);

        useradmin.addComponent(createNavigationButton(newUserLabel, newUserTab));
        useradmin.addComponent(createNavigationButton(changePasswordForUserLabel, changeuserpasswordTab));
        useradmin.addComponent(createNavigationButton(modifyUsersLabel, usersTab));
        tabs.addTab(useradminTab, "Administrere brukere");
    }

    void selectAUserToBeEdited(Binder<User> editUserBinder, NativeSelect<User> editUserUsersField) {
        Optional<User> selectedUser = editUserUsersField.getSelectedItem();
        if (selectedUser.isPresent()) {
            editUserBinder.readBean(selectedUser.get());
        }
    }

    void setEditJobTypeFormsFromSelectedJobTypeInTable(Grid<TransactionType> jobtypesTable, Binder<TransactionType> editJobBinder) {
        Set<TransactionType> selectedJobs = jobtypesTable.getSelectedItems();
        TransactionType selectedJob = selectedJobs.isEmpty() ? null : selectedJobs.iterator().next();
        if (selectedJob != null) {
            editJobBinder.readBean(selectedJob);
        }
    }

    void saveUserModifications(Binder<User> editUserBinder) {
        User user = editUserBinder.getBean();
        if(user != null) {
            updateUserInDatabase(provider, getClass(), user);
            user.setUsername("");
            user.setEmail("");
            user.setFirstname("");
            user.setLastname("");
            editUserBinder.readBean(user);
            refreshListWidgetsAffectedByChangesToUsers();
        }
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
    }

    void clearAllNewUserFormElements(Binder<User> binder) {
        User emptyUser = new User(0, "", "", "", "");
        binder.readBean(emptyUser);
        try {
            binder.writeBean(binder.getBean());
        } catch (ValidationException e) {
            throw new UkelonnException("Failed to blank the user fields", e);
        }
    }

    boolean newUserIsAValidUser(Binder<User> binder, Binder<Passwords> passwordsBinder) {
        return
            !"".equals(binder.getBean().getUsername()) &&
            !"".equals(passwordsBinder.getBean().getPassword1()) &&
            passwordsBinder.getBean().getPassword1().equals(passwordsBinder.getBean().getPassword2()) &&
            binder.isValid() &&
            passwordsBinder.isValid() &&
            !"".equals(binder.getBean().getFirstname()) &&
            !"".equals(binder.getBean().getLastname());
    }

    private void refreshListWidgetsAffectedByChangesToUsers(Class<?> classForLogMessage) {
        List<Account> accounts = getAccounts(provider, classForLogMessage);
        accountsContainer.getItems().clear();
        accountsContainer.getItems().addAll(accounts);
        List<User> users = getUsers(provider, classForLogMessage);
        editUserPasswordUsers.getItems().clear();
        editUserUsers.getItems().clear();
        editUserPasswordUsers.getItems().addAll(users);
        editUserUsers.getItems().addAll(users);
    }

    private VerticalComponentGroup createVerticalComponentGroupWithCssLayoutAndNavigationView(NavigationManager navigationManager, NavigationView view, String caption) {
        CssLayout layout = new CssLayout();
        VerticalComponentGroup vContainer = new VerticalComponentGroup();
        layout.addComponent(vContainer);
        view.setCaption(caption);
        view.setContent(layout);
        navigationManager.addComponent(view);
        navigationManager.navigateTo(view);
        return vContainer;
    }

    private VerticalComponentGroup createVerticalComponentGroupWithCssLayoutAndNavigationSubView(NavigationManager navigationManager, NavigationView navigationView, String caption) {
        VerticalComponentGroup vContainer = createVerticalComponentGroupWithCssLayoutAndNavigationView(navigationManager, navigationView, caption);
        navigationManager.navigateBack();
        return vContainer;
    }

    private void refreshJobTypesFromDatabase() {
        Map<Integer, TransactionType> transactiontypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        jobTypes.getItems().clear();
        jobTypes.getItems().addAll(getJobTypesFromTransactionTypes(transactiontypes.values()));
        jobTypes.refreshAll();
    }

    private void refreshPaymentTypesFromDatabase() {
        Map<Integer, TransactionType> transactiontypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        paymentTypes.getItems().clear();
        paymentTypes.getItems().addAll(getPaymentTypesFromTransactionTypes(transactiontypes.values()));
        paymentTypes.refreshAll();
    }

    void updateFormsAfterAccountIsSelected(Binder<AmountAndBalance> binder, NativeSelect<TransactionType> paymenttype, NativeSelect<Account> accountSelector) {
        Account account = accountSelector.getValue();

        if (account != null) {
            refreshAccount(provider, getClass(), account);
            binder.getBean().setBalance(account.getBalance());
            Map<Integer, TransactionType> transactiontypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
            paymenttype.setSelectedItem(transactiontypes.get(ID_OF_PAY_TO_BANK));
            amount.setValue(balance.getValue());
            binder.getBean().setAmount(binder.getBean().getBalance());
            binder.readBean(binder.getBean());
            AccountHistory history = new AccountHistory();
            recentJobs.getItems().clear();
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentJobs.refreshAll();
            recentPayments.getItems().clear();
            recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
            recentPayments.refreshAll();
            accountHistoryBinder.readBean(history);
        }
    }

    void updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(NativeSelect<TransactionType> paymenttype) {
        TransactionType payment = paymenttype.getValue();
        if (payment != null) {
            Double paymentAmount = payment.getTransactionAmount();
            if (payment.getId() == ID_OF_PAY_TO_BANK || paymentAmount == null) {
                amount.setValue(balance.getValue());
            } else {
                amount.setValue(Double.toString(paymentAmount));
            }
        }
    }

    void registerPaymentInDatabase(Binder<AmountAndBalance> binder, NativeSelect<TransactionType> paymenttype, NativeSelect<Account> accountSelector) {
        Account account = accountSelector.getValue();
        TransactionType payment = paymenttype.getValue();
        if (account != null && payment != null) {
            addNewPaymentToAccount(provider, getClass(), account, payment, Double.valueOf(amount.getValue()));
            recentPayments.getItems().clear();
            recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
            recentPayments.refreshAll();
            recentJobs.getItems().clear();
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentJobs.refreshAll();
            refreshAccount(provider, getClass(), account);
            binder.getBean().setBalance(account.getBalance());
            binder.getBean().setAmount(0.0);
            binder.readBean(binder.getBean());
        }
    }

    void makeNewJobType(Binder<TransactionType> binder) {
        TransactionType newJobType = binder.getBean();
        String jobname = newJobType.getTransactionTypeName();
        Double jobamount = newJobType.getTransactionAmount();
        if (!"".equals(jobname) && !Double.valueOf(0.0).equals(jobamount)) {
            addJobTypeToDatabase(provider, getClass(), jobname, jobamount);
            newJobType.setTransactionTypeName("");
            newJobType.setTransactionAmount(0.0);
            binder.setBean(newJobType);
            refreshJobTypesFromDatabase();
        }
    }

    void saveChangesToJobType(Grid<TransactionType> jobtypesTable, Binder<TransactionType> binder) {
        Set<TransactionType> selection = jobtypesTable.getSelectedItems();
        TransactionType transactionType = selection.isEmpty() ? null : selection.iterator().next();
        if (transactionType != null &&
            !"".equals(binder.getBean().getTransactionTypeName()) &&
            !identicalToExistingValues(transactionType, binder.getBean().getTransactionTypeName(), binder.getBean().getTransactionAmount()))
        {
            transactionType.setTransactionTypeName(binder.getBean().getTransactionTypeName());
            transactionType.setTransactionAmount(binder.getBean().getTransactionAmount());
            updateTransactionTypeInDatabase(provider, getClass(), transactionType);
            jobtypesTable.select(null);
            TransactionType emptyJobType = new TransactionType(0, "", 0.0, true, false);
            binder.readBean(emptyJobType);
            try {
                binder.writeBean(binder.getBean());
            } catch (ValidationException e) {
                throw new UkelonnException("Failed to blank the fields of the edited job type", e);
            }
            refreshJobTypesFromDatabase();
        }
    }

    private boolean identicalToExistingValues(TransactionType transactionType, String transactionTypeName, Double transactionAmount) {
        return transactionType.getTransactionTypeName().equals(transactionTypeName) && transactionType.getTransactionAmount().equals(transactionAmount);
    }

    void createPaymentType(Binder<TransactionType> binder) {
        TransactionType newPaymentType = binder.getBean();
        String paymentName = newPaymentType.getTransactionTypeName();
        Double rawPaymentAmount = newPaymentType.getTransactionAmount();
        Double paymentAmount = Double.valueOf(0.0).equals(rawPaymentAmount) ? null : rawPaymentAmount;
        if (!"".equals(paymentName)) {
            addPaymentTypeToDatabase(provider, getClass(), paymentName, paymentAmount);
            newPaymentType.setTransactionTypeName("");
            newPaymentType.setTransactionAmount(0.0);
            binder.setBean(newPaymentType);
            refreshPaymentTypesFromDatabase();
        }
    }

    void updatePaymentForEditWhenPaymentTypeIsSelected(Grid<TransactionType> paymentTypesTable, Binder<TransactionType> binder) {
        Set<TransactionType> selection = paymentTypesTable.getSelectedItems();
        TransactionType transactionType = selection.isEmpty() ? null : selection.iterator().next();
        if (transactionType != null) {
            binder.readBean(transactionType);
            try {
                binder.writeBean(binder.getBean());
            } catch (ValidationException e) {
                throw new UkelonnException("Failed to update payment type model bean", e);
            }
        }
    }

    void saveChangesToPaymentTypes(Grid<TransactionType> paymentTypesTable, Binder<TransactionType> binder) {
        Set<TransactionType> selection = paymentTypesTable.getSelectedItems();
        TransactionType transactionType = selection.isEmpty() ? null : selection.iterator().next();
        TransactionType paymentType = binder.getBean();
        if (transactionType != null &&
            !"".equals(paymentType.getTransactionTypeName()) &&
            !transactionType.equals(paymentType))
        {
            updateTransactionTypeInDatabase(provider, getClass(), paymentType);
            paymentTypesTable.select(null);
            paymentType.setTransactionTypeName("");
            paymentType.setTransactionAmount(0.0);
            binder.setBean(paymentType);
            refreshPaymentTypesFromDatabase();
        }
    }

}
