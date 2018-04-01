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

import org.apache.shiro.SecurityUtils;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;

public class AdminView extends AbstractView { // NOSONAR
    private static final String TRANSACTION_AMOUNT = "transactionAmount";
    private static final String TRANSACTION_TYPE_NAME = "transactionTypeName";
    static final int ID_OF_PAY_TO_BANK = 4;
    private static final long serialVersionUID = -1581589472749242129L;
    private UkelonnUIProvider provider;

    // Data model for handling payments to users
    private ObjectProperty<String> greetingProperty = new ObjectProperty<>("Ukelønn admin UI, bruker: ????");
    BeanItemContainer<Account> accountsContainer;
    ObjectProperty<Double> balance = new ObjectProperty<>(0.0);
    ObjectProperty<Double> amount = new ObjectProperty<>(0.0);
    BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<>(Transaction.class, getDummyTransactions());
    BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<>(Transaction.class, getDummyTransactions());
    Map<Integer, TransactionType> transactionTypes; // NOSONAR
    BeanItemContainer<TransactionType> paymentTypes;
    BeanItemContainer<TransactionType> jobTypes;

    // Data model for the admin tasks
    ObjectProperty<String> newJobTypeName = new ObjectProperty<>("");
    ObjectProperty<Double> newJobTypeAmount = new ObjectProperty<>(0.0);
    ObjectProperty<String> editedJobTypeName = new ObjectProperty<>("");
    ObjectProperty<Double> editedJobTypeAmount = new ObjectProperty<>(0.0);
    ObjectProperty<String> newPaymentTypeName = new ObjectProperty<>("");
    ObjectProperty<Double> newPaymentTypeAmount = new ObjectProperty<>(0.0);
    ObjectProperty<String> editedPaymentTypeName = new ObjectProperty<>("");
    ObjectProperty<Double> editedPaymentTypeAmount = new ObjectProperty<>(0.0);
    ObjectProperty<String> newUserUsername = new ObjectProperty<>("");
    ObjectProperty<String> newUserPassword1 = new ObjectProperty<>("");
    ObjectProperty<String> newUserPassword2 = new ObjectProperty<>("");
    ObjectProperty<String> newUserEmail = new ObjectProperty<>("");
    ObjectProperty<String> newUserFirstname = new ObjectProperty<>("");
    ObjectProperty<String> newUserLastname = new ObjectProperty<>("");
    BeanItemContainer<User> editUserPasswordUsers;
    ObjectProperty<String> editUserPassword1 = new ObjectProperty<>("");
    ObjectProperty<String> editUserPassword2 = new ObjectProperty<>("");
    BeanItemContainer<User> editUserUsers;
    ObjectProperty<String> editUserUsername = new ObjectProperty<>("");
    ObjectProperty<String> editUserEmail = new ObjectProperty<>("");
    ObjectProperty<String> editUserFirstname = new ObjectProperty<>("");
    ObjectProperty<String> editUserLastname = new ObjectProperty<>("");

    public AdminView(UkelonnUIProvider provider, VaadinRequest request) {
        this.provider = provider;
        accountsContainer = new BeanItemContainer<>(Account.class, getAccounts(provider, getClass()));
        transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        editUserPasswordUsers = new BeanItemContainer<>(User.class, getUsers(provider, getClass()));
        editUserUsers = new BeanItemContainer<>(User.class, getUsers(provider, getClass()));
        paymentTypes = new BeanItemContainer<>(TransactionType.class, getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        jobTypes = new BeanItemContainer<>(TransactionType.class, getJobTypesFromTransactionTypes(transactionTypes.values()));
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
        greetingProperty.setValue("Ukelønn admin UI, bruker: " + admin.getFirstname());
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
        CssLayout registerPaymentTabForm = new CssLayout();
        VerticalComponentGroup registerPaymentTabGroup = new VerticalComponentGroup();

        // Display the greeting
        Component greeting = new Label(greetingProperty);
        greeting.setStyleName("h1");
        registerPaymentTabGroup.addComponent(greeting);

        NativeSelect paymenttype = new NativeSelect("Registrer utbetaling", paymentTypes);
        NativeSelect accountSelector = new NativeSelect("Velg hvem det skal betales til", accountsContainer);
        accountSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        accountSelector.setItemCaptionPropertyId("fullName");
        accountSelector.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -781514357123503476L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    updateFormsAfterAccountIsSelected(paymenttype, accountSelector);
                }
            });
        registerPaymentTabGroup.addComponent(accountSelector);

        FormLayout paymentLayout = new FormLayout();
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        paymentLayout.addComponent(balanceDisplay);

        paymenttype.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        paymenttype.setItemCaptionPropertyId(TRANSACTION_TYPE_NAME);
        paymenttype.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -8306551057458139402L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(paymenttype);
                }
            });
        paymentLayout.addComponent(paymenttype);

        TextField amountField = new TextField("Beløp:", amount);
        paymentLayout.addComponent(amountField);

        paymentLayout.addComponent(new Button("Registrer betaling",
                                              new Button.ClickListener() {
                                                  private static final long serialVersionUID = 5260321175219218136L;

                                                  @Override
                                                  public void buttonClick(ClickEvent event) {
                                                      registerPaymentInDatabase(paymenttype, accountSelector);
                                                  }
                                              }));
        registerPaymentTabGroup.addComponent(paymentLayout);
        registerPaymentTabForm.addComponent(registerPaymentTabGroup);
        NavigationView registerPaymentView = new NavigationView("Registrer betaling", registerPaymentTabForm);
        registerPaymentTab.addComponent(registerPaymentView);
        registerPaymentTab.navigateTo(registerPaymentView);
        return registerPaymentTabGroup;
    }

    private void createJobtypeAdminstrationTab(TabBarView tabs) {
        NavigationManager jobtypeAdminTab = new NavigationManager();
        VerticalComponentGroup jobtypeAdminContent = createVerticalComponentGroupWithCssLayoutAndNavigationView(jobtypeAdminTab, new NavigationView(), "Administrere jobbtyper");

        String newJobtypeLabel = "Ny jobbtype";
        String modifyJobtypesLabel = "Endre jobbtyper";

        NavigationView newJobTypeTab = new NavigationView();
        VerticalComponentGroup newJobTypeTabContent = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(jobtypeAdminTab, newJobTypeTab, newJobtypeLabel);
        TextField newJobTypeNameField = new TextField("Navn på ny jobbtype:", newJobTypeName);
        newJobTypeTabContent.addComponent(newJobTypeNameField);
        TextField newJobTypeAmountField = new TextField("Beløp for ny jobbtype:", newJobTypeAmount);
        newJobTypeTabContent.addComponent(newJobTypeAmountField);
        newJobTypeTabContent.addComponent(new Button("Lag jobbtype", new Button.ClickListener() {
                private static final long serialVersionUID = 1338062460936195627L;

                @Override
                public void buttonClick(ClickEvent event) {
                    makeNewJobType();
                }
            }));

        NavigationView jobtypesTab = new NavigationView();
        VerticalComponentGroup jobtypesform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(jobtypeAdminTab, jobtypesTab, modifyJobtypesLabel);
        Table jobtypesTable = new Table();
        jobtypesTable.addContainerProperty(TRANSACTION_TYPE_NAME, String.class, null, "Navn", null, null);
        jobtypesTable.addContainerProperty(TRANSACTION_AMOUNT, Double.class, null, "Beløp", null, null);
        jobtypesTable.setContainerDataSource(jobTypes);
        jobtypesTable.setVisibleColumns(TRANSACTION_TYPE_NAME, TRANSACTION_AMOUNT);
        jobtypesTable.setSelectable(true);
        jobtypesTable.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -8324617275480799162L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    TransactionType transactionType = (TransactionType) jobtypesTable.getValue();
                    if (transactionType != null) {
                        editedJobTypeName.setValue(transactionType.getTransactionTypeName());
                        editedJobTypeAmount.setValue(transactionType.getTransactionAmount());
                    }
                }
            });
        jobtypesTable.setPageLength(jobTypes.size());
        jobtypesform.addComponent(jobtypesTable);
        FormLayout editJobLayout = new FormLayout();
        TextField editJobTypeNameField = new TextField("Endre Navn på jobbtype:", editedJobTypeName);
        editJobLayout.addComponent(editJobTypeNameField);
        TextField editJobTypeAmountField = new TextField("Endre beløp for jobbtype:", editedJobTypeAmount);
        editJobLayout.addComponent(editJobTypeAmountField);
        editJobLayout.addComponent(new Button("Lagre endringer i jobbtype", new Button.ClickListener() {
                private static final long serialVersionUID = 347708021528799659L;

                @Override
                public void buttonClick(ClickEvent event) {
                    saveChangesToJobType(jobtypesTable, editJobTypeNameField);
                }

            }));
        jobtypesform.addComponent(editJobLayout);

        jobtypeAdminContent.addComponent(createNavigationButton(newJobtypeLabel, newJobTypeTab));
        jobtypeAdminContent.addComponent(createNavigationButton(modifyJobtypesLabel, jobtypesTab));
        tabs.addTab(jobtypeAdminTab, "Administrere jobbtyper");
    }

    boolean identicalToExistingValues(TransactionType transactionType, ObjectProperty<String> transactionTypeName, ObjectProperty<Double> transactionTypeAmount) {
        if (transactionType == null || transactionType.getTransactionTypeName() == null || transactionType.getTransactionAmount() == null) {
            return false; // Nothing to compare against, always false
        }

        return
            transactionType.getTransactionTypeName().equals(transactionTypeName.getValue()) &&
            transactionType.getTransactionAmount().equals(transactionTypeAmount.getValue());
    }

    private void createPaymenttypeAdministrationTab(TabBarView tabs) {
        // Payment type administration.
        NavigationManager paymentstypeadminTab = new NavigationManager();
        VerticalComponentGroup paymentstypeadmin = createVerticalComponentGroupWithCssLayoutAndNavigationView(paymentstypeadminTab, new NavigationView(), "Administrere utbetalingstyper");

        String newPaymenttypeLabel = "Lag ny betalingstype";
        String modifyPayementtypesLabel = "Endre utbetalingstyper";

        NavigationView newpaymenttypeTab = new NavigationView();
        VerticalComponentGroup newpaymenttypeForm = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(paymentstypeadminTab, newpaymenttypeTab, newPaymenttypeLabel);
        TextField newPaymentTypeNameField = new TextField("Navn på ny betalingstype:", newPaymentTypeName);
        newpaymenttypeForm.addComponent(newPaymentTypeNameField);
        TextField newPaymentTypeAmountField = new TextField("Beløp for ny betalingstype:", newPaymentTypeAmount);
        newpaymenttypeForm.addComponent(newPaymentTypeAmountField);
        newpaymenttypeForm.addComponent(new Button("Lag betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2160144195348196823L;

                @Override
                public void buttonClick(ClickEvent event) {
                    createPaymentType();
                }
            }));

        NavigationView paymentstypeTab = new NavigationView();
        VerticalComponentGroup paymenttypesform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(paymentstypeadminTab, paymentstypeTab, modifyPayementtypesLabel);
        Table paymentTypesTable = new Table();
        paymentTypesTable.addContainerProperty(TRANSACTION_TYPE_NAME, String.class, null, "Navn", null, null);
        paymentTypesTable.addContainerProperty(TRANSACTION_AMOUNT, Double.class, null, "Beløp", null, null);
        paymentTypesTable.setContainerDataSource(paymentTypes);
        paymentTypesTable.setVisibleColumns(TRANSACTION_TYPE_NAME, TRANSACTION_AMOUNT);
        paymentTypesTable.setSelectable(true);
        paymentTypesTable.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -1432137451555587595L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    updatePaymentForEditWhenPaymentTypeIsSelected(paymentTypesTable);
                }
            });
        paymentTypesTable.setPageLength(paymentTypes.size());
        paymenttypesform.addComponent(paymentTypesTable);
        FormLayout editPaymentsLayout = new FormLayout();
        TextField editPaymentTypeNameField = new TextField("Endre Navn på betalingstype:", editedPaymentTypeName);
        editPaymentsLayout.addComponent(editPaymentTypeNameField);
        TextField editPaymentTypeAmountField = new TextField("Endre beløp for betalingstype:", editedPaymentTypeAmount);
        editPaymentsLayout.addComponent(editPaymentTypeAmountField);
        editPaymentsLayout.addComponent(new Button("Lagre endringer i betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2168895121731055862L;

                @Override
                public void buttonClick(ClickEvent event) {
                    saveChangesToPaymentTypes(paymentTypesTable, editPaymentTypeNameField);
                }
            }));
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
        TextField newUserUsernameField = new TextField("Brukernavn:", newUserUsername);
        newUserForm.addComponent(newUserUsernameField);
        PasswordField newUserPassword1Field = new PasswordField("Passord:", newUserPassword1);
        newUserForm.addComponent(newUserPassword1Field);
        PasswordField newUserPassword2Field = new PasswordField("Gjenta passord:", newUserPassword2);
        newUserPassword2Field.addValidator(new PasswordCompareValidator(newUserPassword1Field));
        newUserForm.addComponent(newUserPassword2Field);
        TextField newUserEmailField = new TextField("Epostadresse:", newUserEmail);
        newUserEmailField.addValidator(new EmailValidator("Ikke en gyldig epostadresse"));
        newUserForm.addComponent(newUserEmailField);
        TextField newUserFirstnameField = new TextField("Fornavn:", newUserFirstname);
        newUserForm.addComponent(newUserFirstnameField);
        Class<? extends AdminView> classForLogMessage = getClass();
        TextField newUserLastnameField = new TextField("Etternavn:", newUserLastname);
        newUserForm.addComponent(newUserLastnameField);
        newUserForm.addComponent(new Button("Lag bruker", new Button.ClickListener() {
                private static final long serialVersionUID = 2493188115512727312L;

                @Override
                public void buttonClick(ClickEvent event) {
                    if (newUserIsAValidUser())
                    {
                        addUserToDatabase(
                            provider,
                            classForLogMessage,
                            newUserUsername.getValue(),
                            newUserPassword2.getValue(),
                            newUserEmail.getValue(),
                            newUserFirstname.getValue(),
                            newUserLastname.getValue());

                        clearAllNewUserFormElements();

                        refreshListWidgetsAffectedByChangesToUsers();
                    }
                }

                private void clearAllNewUserFormElements() {
                    newUserUsername.setValue("");
                    newUserPassword1.setValue("");
                    newUserPassword2.setValue("");
                    newUserEmail.setValue("");
                    newUserFirstname.setValue("");
                    newUserLastname.setValue("");
                }

                private boolean newUserIsAValidUser() {
                    return
                        !"".equals(newUserUsername.getValue()) &&
                        !"".equals(newUserPassword1.getValue()) &&
                        newUserPassword1.getValue().equals(newUserPassword2Field.getValue()) &&
                        newUserPassword2Field.isValid() &&
                        newUserEmailField.isValid() &&
                        !"".equals(newUserFirstname.getValue()) &&
                        !"".equals(newUserLastname.getValue());
                }

                private void refreshListWidgetsAffectedByChangesToUsers() {
                    List<Account> accounts = getAccounts(provider, classForLogMessage);
                    accountsContainer.removeAllItems();
                    accountsContainer.addAll(accounts);
                    List<User> users = getUsers(provider, classForLogMessage);
                    editUserPasswordUsers.removeAllItems();
                    editUserUsers.removeAllItems();
                    editUserPasswordUsers.addAll(users);
                    editUserUsers.addAll(users);
                }
            }));

        NavigationView changeuserpasswordTab = new NavigationView();
        VerticalComponentGroup changeuserpasswordForm = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(useradminTab, changeuserpasswordTab, changePasswordForUserLabel);
        NativeSelect editUserPasswordUsersField = new NativeSelect("Velg bruker", editUserPasswordUsers);
        editUserPasswordUsersField.setItemCaptionPropertyId("fullname");
        editUserPasswordUsersField.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -5949282377337763508L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    User user = (User) editUserPasswordUsersField.getValue();
                    if (user != null) {
                        editUserPassword1.setValue("");
                        editUserPassword2.setValue("");
                    }
                }
            });
        changeuserpasswordForm.addComponent(editUserPasswordUsersField);
        PasswordField editUserPassword1Field = new PasswordField("Passord:", editUserPassword1);
        changeuserpasswordForm.addComponent(editUserPassword1Field);
        PasswordField editUserPassword2Field = new PasswordField("Gjenta passord:", editUserPassword2);
        editUserPassword2Field.addValidator(new PasswordCompareValidator(editUserPassword1Field));
        changeuserpasswordForm.addComponent(editUserPassword2Field);
        changeuserpasswordForm.addComponent(new Button("Endre passord", new Button.ClickListener() {
                private static final long serialVersionUID = 811470485549038444L;

                @Override
                public void buttonClick(ClickEvent event) {
                    User user = (User) editUserPasswordUsersField.getValue();
                    if (user != null &&
                        !"".equals(editUserPassword1Field.getValue()) &&
                        editUserPassword2Field.isValid())
                    {
                        changePasswordForUser(provider, user.getUsername(), editUserPassword2.getValue(), classForLogMessage);
                        editUserPassword1.setValue("");
                        editUserPassword2.setValue("");

                    }
                }
            }));

        NavigationView usersTab = new NavigationView();
        VerticalComponentGroup usersform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(useradminTab, usersTab, modifyUsersLabel);
        NativeSelect editUserUsersField = new NativeSelect("Velg bruker", editUserUsers);
        editUserUsersField.setItemCaptionPropertyId("fullname");
        editUserUsersField.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = 7774428541884411808L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    User user = (User) editUserUsersField.getValue();
                    if (user != null) {
                        editUserUsername.setValue(user.getUsername());
                        editUserEmail.setValue(user.getEmail());
                        editUserFirstname.setValue(user.getFirstname());
                        editUserLastname.setValue(user.getLastname());
                    }
                }
            });
        usersform.addComponent(editUserUsersField);

        TextField editUserUsernameField = new TextField("Brukernavn:", editUserUsername);
        usersform.addComponent(editUserUsernameField);
        TextField editUserEmailField = new TextField("Epostadresse:", editUserEmail);
        editUserEmailField.addValidator(new EmailValidator("Ikke en gyldig epostadresse"));
        usersform.addComponent(editUserEmailField);
        TextField editUserFirstnameField = new TextField("Fornavn:", editUserFirstname);
        usersform.addComponent(editUserFirstnameField);
        TextField editUserLastnameField = new TextField("Etternavn:", editUserLastname);
        usersform.addComponent(editUserLastnameField);
        usersform.addComponent(new Button("Lagre endringer av bruker", new Button.ClickListener() {
                private static final long serialVersionUID = 1658760136279718499L;

                @Override
                public void buttonClick(ClickEvent event) {
                    User user = (User) editUserUsersField.getValue();
                    if (user != null) {
                        user.setUsername(editUserUsername.getValue());
                        user.setEmail(editUserEmail.getValue());
                        user.setFirstname(editUserFirstname.getValue());
                        user.setLastname(editUserLastname.getValue());

                        updateUserInDatabase(provider, classForLogMessage, user);

                        clearFormElements();

                        refreshListWidgetsAffectedByChangesToUsers();
                    }
                }

                private void clearFormElements() {
                    editUserUsername.setValue("");
                    editUserEmail.setValue("");
                    editUserFirstname.setValue("");
                    editUserLastname.setValue("");
                }

                private void refreshListWidgetsAffectedByChangesToUsers() {
                    List<Account> accounts = getAccounts(provider, classForLogMessage);
                    accountsContainer.removeAllItems();
                    accountsContainer.addAll(accounts);
                    List<User> users = getUsers(provider, classForLogMessage);
                    editUserPasswordUsers.removeAllItems();
                    editUserUsers.removeAllItems();
                    editUserPasswordUsers.addAll(users);
                    editUserUsers.addAll(users);
                }
            }));

        useradmin.addComponent(createNavigationButton(newUserLabel, newUserTab));
        useradmin.addComponent(createNavigationButton(changePasswordForUserLabel, changeuserpasswordTab));
        useradmin.addComponent(createNavigationButton(modifyUsersLabel, usersTab));
        tabs.addTab(useradminTab, "Administrere brukere");
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
        jobTypes.removeAllItems();
        jobTypes.addAll(getJobTypesFromTransactionTypes(transactiontypes.values()));
    }

    private void refreshPaymentTypesFromDatabase() {
        Map<Integer, TransactionType> transactiontypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        paymentTypes.removeAllItems();
        paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactiontypes.values()));
    }

    void updateFormsAfterAccountIsSelected(NativeSelect paymenttype, NativeSelect accountSelector) {
        Account account = (Account) accountSelector.getValue();
        jobTypes.removeAllItems();
        paymentTypes.removeAllItems();
        recentJobs.removeAllItems();
        recentPayments.removeAllItems();
        if (account != null) {
            refreshAccount(provider, getClass(), account);
            balance.setValue(account.getBalance());
            Map<Integer, TransactionType> transactiontypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
            jobTypes.addAll(getJobTypesFromTransactionTypes(transactiontypes.values()));
            paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactiontypes.values()));
            paymenttype.select(transactiontypes.get(ID_OF_PAY_TO_BANK));
            amount.setValue(balance.getValue());
            recentJobs.addAll(getJobsFromAccount(provider, account, getClass()));
            recentPayments.addAll(getPaymentsFromAccount(provider, account, getClass()));
        }
    }

    void updateVisiblePaymentPropertiesWhenPaymentTypeIsSelected(NativeSelect paymenttype) {
        TransactionType payment = (TransactionType) paymenttype.getValue();
        if (payment != null) {
            Double paymentAmount = payment.getTransactionAmount();
            if (payment.getId() == ID_OF_PAY_TO_BANK || paymentAmount == null) {
                amount.setValue(balance.getValue());
            } else {
                amount.setValue(paymentAmount);
            }
        }
    }

    void registerPaymentInDatabase(NativeSelect paymenttype, NativeSelect accountSelector) {
        Account account = (Account) accountSelector.getValue();
        TransactionType payment = (TransactionType) paymenttype.getValue();
        if (account != null && payment != null) {
            addNewPaymentToAccount(provider, getClass(), account, payment, amount.getValue());
            recentPayments.removeAllItems();
            recentPayments.addAll(getPaymentsFromAccount(provider, account, getClass()));
            recentJobs.removeAllItems();
            recentJobs.addAll(getJobsFromAccount(provider, account, getClass()));
            refreshAccount(provider, getClass(), account);
            balance.setValue(account.getBalance());
            amount.setValue(0.0);
        }
    }

    void makeNewJobType() {
        String jobname = newJobTypeName.getValue();
        Double jobamount = newJobTypeAmount.getValue();
        if (!"".equals(jobname) && !Double.valueOf(0.0).equals(jobamount)) {
            addJobTypeToDatabase(provider, getClass(), jobname, jobamount);
            newJobTypeName.setValue("");
            newJobTypeAmount.setValue(0.0);
            refreshJobTypesFromDatabase();
        }
    }

    void saveChangesToJobType(Table jobtypesTable, TextField editJobTypeNameField) {
        TransactionType transactionType = (TransactionType) jobtypesTable.getValue();
        if (transactionType != null &&
            !"".equals(editJobTypeNameField.getValue()) &&
            !identicalToExistingValues(transactionType, editedJobTypeName, editedJobTypeAmount))
        {
            transactionType.setTransactionTypeName(editedJobTypeName.getValue());
            transactionType.setTransactionAmount(editedJobTypeAmount.getValue());
            updateTransactionTypeInDatabase(provider, getClass(), transactionType);
            jobtypesTable.setValue(null);
            editedJobTypeName.setValue("");
            editedJobTypeAmount.setValue(0.0);
            refreshJobTypesFromDatabase();
        }
    }

    void createPaymentType() {
        String paymentName = newPaymentTypeName.getValue();
        Double rawPaymentAmount = newPaymentTypeAmount.getValue();
        Double paymentAmount = Double.valueOf(0.0).equals(rawPaymentAmount) ? null : rawPaymentAmount;
        if (!"".equals(paymentName)) {
            addPaymentTypeToDatabase(provider, getClass(), paymentName, paymentAmount);
            newPaymentTypeName.setValue("");
            newPaymentTypeAmount.setValue(0.0);
            refreshPaymentTypesFromDatabase();
        }
    }

    void updatePaymentForEditWhenPaymentTypeIsSelected(Table paymentTypesTable) {
        TransactionType transactionType = (TransactionType) paymentTypesTable.getValue();
        if (transactionType != null) {
            editedPaymentTypeName.setValue(transactionType.getTransactionTypeName());
            editedPaymentTypeAmount.setValue(transactionType.getTransactionAmount());
        }
    }

    void saveChangesToPaymentTypes(Table paymentTypesTable, TextField editPaymentTypeNameField) {
        TransactionType transactionType = (TransactionType) paymentTypesTable.getValue();
        if (transactionType != null &&
            !"".equals(editPaymentTypeNameField.getValue()) &&
            !identicalToExistingValues(transactionType, editedPaymentTypeName, editedPaymentTypeAmount))
        {
            transactionType.setTransactionTypeName(editedPaymentTypeName.getValue());
            transactionType.setTransactionAmount(editedPaymentTypeAmount.getValue());
            updateTransactionTypeInDatabase(provider, getClass(), transactionType);
            paymentTypesTable.setValue(null);
            editedPaymentTypeName.setValue("");
            editedPaymentTypeAmount.setValue(0.0);
            refreshPaymentTypesFromDatabase();
        }
    }

}
