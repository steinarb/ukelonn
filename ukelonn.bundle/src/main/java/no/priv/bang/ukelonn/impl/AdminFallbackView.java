/*
 * Copyright 2016-2017 Steinar Bang
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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;

public class AdminFallbackView extends AbstractView {
    private static final long serialVersionUID = -1581589472749242129L;
    final int idOfPayToBank = 4;

    // Datamodel for the UI (updates to these will be transferred to the GUI listeners).
    private ObjectProperty<String> greetingProperty = new ObjectProperty<String>("Ukelønn admin UI, bruker: ????");
    ObjectProperty<Double> balance = new ObjectProperty<Double>(0.0);
    BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<Transaction>(Transaction.class);
    BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<Transaction>(Transaction.class);
    BeanItemContainer<TransactionType> paymentTypes = new BeanItemContainer<TransactionType>(TransactionType.class);
    BeanItemContainer<TransactionType> jobTypes = new BeanItemContainer<TransactionType>(TransactionType.class);
    ObjectProperty<Double> amount = new ObjectProperty<Double>(0.0);
    BeanItemContainer<Account> accountsContainer = new BeanItemContainer<Account>(Account.class);
    ObjectProperty<String> newJobTypeName = new ObjectProperty<String>("");
    ObjectProperty<Double> newJobTypeAmount = new ObjectProperty<Double>(0.0);
    ObjectProperty<String> editedJobTypeName = new ObjectProperty<String>("");
    ObjectProperty<Double> editedJobTypeAmount = new ObjectProperty<Double>(0.0);
    ObjectProperty<String> newPaymentTypeName = new ObjectProperty<String>("");
    ObjectProperty<Double> newPaymentTypeAmount = new ObjectProperty<Double>(0.0);
    ObjectProperty<String> editedPaymentTypeName = new ObjectProperty<String>("");
    ObjectProperty<Double> editedPaymentTypeAmount = new ObjectProperty<Double>(0.0);
    ObjectProperty<String> newUserUsername = new ObjectProperty<String>("");
    ObjectProperty<String> newUserPassword1 = new ObjectProperty<String>("");
    ObjectProperty<String> newUserPassword2 = new ObjectProperty<String>("");
    ObjectProperty<String> newUserEmail = new ObjectProperty<String>("");
    ObjectProperty<String> newUserFirstname = new ObjectProperty<String>("");
    ObjectProperty<String> newUserLastname = new ObjectProperty<String>("");
    BeanItemContainer<User> editUserPasswordUsers = new BeanItemContainer<User>(User.class);
    ObjectProperty<String> editUserPassword1 = new ObjectProperty<String>("");
    ObjectProperty<String> editUserPassword2 = new ObjectProperty<String>("");
    BeanItemContainer<User> editUserUsers = new BeanItemContainer<User>(User.class);
    ObjectProperty<String> editUserUsername = new ObjectProperty<String>("");
    ObjectProperty<String> editUserEmail = new ObjectProperty<String>("");
    ObjectProperty<String> editUserFirstname = new ObjectProperty<String>("");
    ObjectProperty<String> editUserLastname = new ObjectProperty<String>("");

    public AdminFallbackView(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.addStyleName("ukelonn-responsive-layout");
        Responsive.makeResponsive(content);
        // Display the greeting
        Component greeting = new Label(greetingProperty);
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        // Updatable containers
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        jobTypes.addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
        editUserPasswordUsers.addAll(getUsers(getClass()));
        editUserUsers.addAll(getUsers(getClass()));
        ComboBox paymenttype = new ComboBox("Registrer utbetaling", paymentTypes);

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
        AdminUser admin = getAdminUserFromDatabase(getClass(), currentUser);
        greetingProperty.setValue("Ukelønn admin UI, bruker: " + admin.getFirstname());
    }

    private void createPaymentRegistrationTab(ComboBox paymenttype, Accordion accordion) {
        Accordion registerPaymentTab = new Accordion();
        VerticalLayout userinfo = new VerticalLayout();
        List<Account> accounts = getAccounts(getClass());
        accountsContainer.addAll(accounts);
        Class<?> classForLogMessage = getClass();
        ComboBox accountSelector = new ComboBox("Velg hvem det skal betales til", accountsContainer);
        accountSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        accountSelector.setItemCaptionPropertyId("fullName");
        accountSelector.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -781514357123503476L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    Account account = (Account) accountSelector.getValue();
                    jobTypes.removeAllItems();
                    paymentTypes.removeAllItems();
                    recentJobs.removeAllItems();
                    recentPayments.removeAllItems();
                    if (account != null) {
                        refreshAccount(classForLogMessage, account);
                        balance.setValue(account.getBalance());
                        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(classForLogMessage);
                        jobTypes.addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
                        paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
                        paymenttype.select(transactionTypes.get(idOfPayToBank));
                        amount.setValue(balance.getValue());
                        recentJobs.addAll(getJobsFromAccount(account, classForLogMessage));
                        recentPayments.addAll(getPaymentsFromAccount(account, classForLogMessage));
                    }
                }
            });
        userinfo.addComponent(accountSelector);

        FormLayout paymentLayout = new FormLayout();
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        paymentLayout.addComponent(balanceDisplay);

        paymenttype.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        paymenttype.setItemCaptionPropertyId("transactionTypeName");
        paymenttype.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -8306551057458139402L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    TransactionType payment = (TransactionType) paymenttype.getValue();
                    if (payment != null) {
                        Double paymentAmount = payment.getTransactionAmount();
                        if (payment.getId() == idOfPayToBank || paymentAmount != null) {
                            amount.setValue(balance.getValue());
                        } else {
                            amount.setValue(paymentAmount);
                        }
                    }
                }
            });
        paymentLayout.addComponent(paymenttype);

        TextField amountField = new TextField("Beløp:", amount);
        paymentLayout.addComponent(amountField);

        paymentLayout.addComponent(
            new Button("Registrer betaling",
                       new Button.ClickListener() {
                           private static final long serialVersionUID = 5260321175219218136L;

                           @Override
                           public void buttonClick(ClickEvent event) {
                               Account account = (Account) accountSelector.getValue();
                               TransactionType payment = (TransactionType) paymenttype.getValue();
                               if (account != null && payment != null) {
                                   addNewPaymentToAccount(classForLogMessage, account, payment, amount.getValue());
                                   recentPayments.removeAllItems();
                                   recentPayments.addAll(getPaymentsFromAccount(account, classForLogMessage));
                                   refreshAccount(classForLogMessage, account);
                                   balance.setValue(account.getBalance());
                                   amount.setValue(0.0);
                               }
                           }
                       }));
        userinfo.addComponent(paymentLayout);

        registerPaymentTab.addTab(userinfo, "Brukerinfo");
        VerticalLayout jobsTab = new VerticalLayout();
        Table lastJobsTable = createTransactionTable("Jobbtype", recentJobs, true);
        jobsTab.addComponent(lastJobsTable);
        registerPaymentTab.addTab(jobsTab, "Siste jobber");
        VerticalLayout paymentsTab = new VerticalLayout();
        Table lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments, false);
        paymentsTab.addComponent(lastPaymentsTable);
        registerPaymentTab.addTab(paymentsTab, "Siste utbetalinger");
        accordion.addTab(wrapInPanel(registerPaymentTab), "Registrere utbetaling");
    }

    private void createJobtypeAdminTab(Accordion accordion) {
        VerticalLayout jobtypeAdminTab = new VerticalLayout();
        Accordion jobtypes = new Accordion();
        FormLayout newJobTypeTab = new FormLayout();
        TextField newJobTypeNameField = new TextField("Navn på ny jobbtype:", newJobTypeName);
        newJobTypeTab.addComponent(newJobTypeNameField);
        Class<?> classForLogMessage = getClass();
        TextField newJobTypeAmountField = new TextField("Beløp for ny jobbtype:", newJobTypeAmount);
        newJobTypeTab.addComponent(newJobTypeAmountField);
        newJobTypeTab.addComponent(new Button("Lag jobbtype", new Button.ClickListener() {
                private static final long serialVersionUID = 1338062460936195627L;

                @Override
                public void buttonClick(ClickEvent event) {
                    String jobname = newJobTypeName.getValue();
                    Double jobamount = newJobTypeAmount.getValue();
                    if (!"".equals(jobname) && !Double.valueOf(0.0).equals(jobamount)) {
                        addJobTypeToDatabase(classForLogMessage, jobname, jobamount);
                        newJobTypeName.setValue("");
                        newJobTypeAmount.setValue(0.0);
                        refreshJobTypesFromDatabase();
                    }
                }
            }));
        jobtypes.addTab(newJobTypeTab, "Lag ny jobbtype");
        VerticalLayout jobtypesform = new VerticalLayout();
        Table jobtypesTable = new Table();
        jobtypesTable.addContainerProperty("transactionTypeName", String.class, null, "Navn", null, null);
        jobtypesTable.addContainerProperty("transactionAmount", Double.class, null, "Beløp", null, null);
        jobtypesTable.setContainerDataSource(jobTypes);
        jobtypesTable.setVisibleColumns("transactionTypeName", "transactionAmount");
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
                    TransactionType transactionType = (TransactionType) jobtypesTable.getValue();
                    if (transactionType != null) {
                        if (!"".equals(editJobTypeNameField.getValue()) &&
                            !identicalToExistingValues(transactionType, editedJobTypeName, editedJobTypeAmount))
                        {
                            transactionType.setTransactionTypeName(editedJobTypeName.getValue());
                            transactionType.setTransactionAmount(editedJobTypeAmount.getValue());
                            updateTransactionTypeInDatabase(classForLogMessage, transactionType);
                            jobtypesTable.setValue(null);
                            editedJobTypeName.setValue("");
                            editedJobTypeAmount.setValue(0.0);
                            refreshJobTypesFromDatabase();
                        }
                    }
                }

                private boolean identicalToExistingValues(TransactionType transactionType, ObjectProperty<String> transactionTypeName, ObjectProperty<Double> transactionTypeAmount) {
                    if (transactionType == null || transactionType.getTransactionTypeName() == null || transactionType.getTransactionAmount() == null) {
                        return false; // Nothing to compare against, always false
                    }

                    boolean isIdentical =
                        transactionType.getTransactionTypeName().equals(transactionTypeName.getValue()) &&
                        transactionType.getTransactionAmount().equals(transactionTypeAmount.getValue());
                    return isIdentical;
                }
            }));
        jobtypesform.addComponent(editJobLayout);
        jobtypes.addTab(jobtypesform, "Endre jobbtyper");
        jobtypeAdminTab.addComponent(jobtypes);
        accordion.addTab(wrapInPanel(jobtypeAdminTab), "Administrere jobbtyper");
    }

    private void createPaymenttypesAdminTab(Accordion accordion) {
        VerticalLayout paymentstypeadminTab = new VerticalLayout();
        Accordion paymentstypeadmin = new Accordion();
        FormLayout newpaymenttypeTab = new FormLayout();
        TextField newPaymentTypeNameField = new TextField("Navn på ny betalingstype:", newPaymentTypeName);
        newpaymenttypeTab.addComponent(newPaymentTypeNameField);
        Class<?> classForLogMessage = getClass();
        TextField newPaymentTypeAmountField = new TextField("Beløp for ny betalingstype:", newPaymentTypeAmount);
        newpaymenttypeTab.addComponent(newPaymentTypeAmountField);
        newpaymenttypeTab.addComponent(new Button("Lag betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2160144195348196823L;

                @Override
                public void buttonClick(ClickEvent event) {
                    String paymentName = newPaymentTypeName.getValue();
                    Double paymentAmount = newPaymentTypeAmount.getValue();
                    if (!"".equals(paymentName)) {
                        addPaymentTypeToDatabase(classForLogMessage, paymentName, paymentAmount);
                        newPaymentTypeName.setValue("");
                        newPaymentTypeAmount.setValue(0.0);
                        refreshPaymentTypesFromDatabase();
                    }
                }
            }));
        paymentstypeadmin.addTab(newpaymenttypeTab, "Lag ny utbetalingstype");
        VerticalLayout paymenttypesform = new VerticalLayout();
        Table paymentTypesTable = new Table();
        paymentTypesTable.addContainerProperty("transactionTypeName", String.class, null, "Navn", null, null);
        paymentTypesTable.addContainerProperty("transactionAmount", Double.class, null, "Beløp", null, null);
        paymentTypesTable.setContainerDataSource(paymentTypes);
        paymentTypesTable.setVisibleColumns("transactionTypeName", "transactionAmount");
        paymentTypesTable.setSelectable(true);
        paymentTypesTable.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = -1432137451555587595L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    TransactionType transactionType = (TransactionType) paymentTypesTable.getValue();
                    if (transactionType != null) {
                        editedPaymentTypeName.setValue(transactionType.getTransactionTypeName());
                        editedPaymentTypeAmount.setValue(transactionType.getTransactionAmount());
                    }
                }
            });
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
                    TransactionType transactionType = (TransactionType) paymentTypesTable.getValue();
                    if (transactionType != null) {
                        if (!"".equals(editPaymentTypeNameField.getValue()) &&
                            !identicalToExistingValues(transactionType, editedPaymentTypeName, editedPaymentTypeAmount))
                        {
                            transactionType.setTransactionTypeName(editedPaymentTypeName.getValue());
                            transactionType.setTransactionAmount(editedPaymentTypeAmount.getValue());
                            updateTransactionTypeInDatabase(classForLogMessage, transactionType);
                            paymentTypesTable.setValue(null);
                            editedPaymentTypeName.setValue("");
                            editedPaymentTypeAmount.setValue(0.0);
                            refreshPaymentTypesFromDatabase();
                        }
                    }
                }

                private boolean identicalToExistingValues(TransactionType transactionType, ObjectProperty<String> transactionTypeName, ObjectProperty<Double> transactionTypeAmount) {
                    if (transactionType == null || transactionType.getTransactionTypeName() == null || transactionType.getTransactionAmount() == null) {
                        return false; // Nothing to compare against, always false
                    }

                    boolean isIdentical =
                        transactionType.getTransactionTypeName().equals(transactionTypeName.getValue()) &&
                        transactionType.getTransactionAmount().equals(transactionTypeAmount.getValue());
                    return isIdentical;
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
        TextField newUserUsernameField = new TextField("Brukernavn:", newUserUsername);
        newUserTab.addComponent(newUserUsernameField);
        PasswordField newUserPassword1Field = new PasswordField("Passord:", newUserPassword1);
        newUserTab.addComponent(newUserPassword1Field);
        PasswordField newUserPassword2Field = new PasswordField("Gjenta passord:", newUserPassword2);
        newUserPassword2Field.addValidator(new PasswordCompareValidator(newUserPassword1Field));
        newUserTab.addComponent(newUserPassword2Field);
        TextField newUserEmailField = new TextField("Epostadresse:", newUserEmail);
        newUserEmailField.addValidator(new EmailValidator("Ikke en gyldig epostadresse"));
        newUserTab.addComponent(newUserEmailField);
        TextField newUserFirstnameField = new TextField("Fornavn:", newUserFirstname);
        newUserTab.addComponent(newUserFirstnameField);
        TextField newUserLastnameField = new TextField("Etternavn:", newUserLastname);
        newUserTab.addComponent(newUserLastnameField);
        newUserTab.addComponent(new Button("Lag bruker", new Button.ClickListener() {
                private static final long serialVersionUID = 2493188115512727312L;

                @Override
                public void buttonClick(ClickEvent event) {
                    if (newUserIsAValidUser())
                    {
                        addUserToDatabase(
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
                    List<Account> accounts = getAccounts(classForLogMessage);
                    accountsContainer.removeAllItems();
                    accountsContainer.addAll(accounts);
                    List<User> users = getUsers(classForLogMessage);
                    editUserPasswordUsers.removeAllItems();
                    editUserUsers.removeAllItems();
                    editUserPasswordUsers.addAll(users);
                    editUserUsers.addAll(users);
                }
            }));
        useradmin.addTab(newUserTab, "Legg til ny bruker");
        FormLayout changeuserpasswordTab = new FormLayout();
        ComboBox editUserPasswordUsersField = new ComboBox("Velg bruker", editUserPasswordUsers);
        editUserPasswordUsersField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
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
        changeuserpasswordTab.addComponent(editUserPasswordUsersField);
        PasswordField editUserPassword1Field = new PasswordField("Passord:", editUserPassword1);
        changeuserpasswordTab.addComponent(editUserPassword1Field);
        PasswordField editUserPassword2Field = new PasswordField("Gjenta passord:", editUserPassword2);
        editUserPassword2Field.addValidator(new PasswordCompareValidator(editUserPassword1Field));
        changeuserpasswordTab.addComponent(editUserPassword2Field);
        changeuserpasswordTab.addComponent(new Button("Endre passord", new Button.ClickListener() {
                private static final long serialVersionUID = 811470485549038444L;

                @Override
                public void buttonClick(ClickEvent event) {
                    User user = (User) editUserPasswordUsersField.getValue();
                    if (user != null) {
                        if (!"".equals(editUserPassword1Field.getValue()) &&
                            editUserPassword2Field.isValid())
                        {
                            changePasswordForUser(user.getUsername(), editUserPassword2.getValue(), classForLogMessage);
                            editUserPassword1.setValue("");
                            editUserPassword2.setValue("");
                        }
                    }
                }
            }));
        useradmin.addTab(changeuserpasswordTab, "Bytt passord på bruker");
        FormLayout usersTab = new FormLayout();
        ComboBox editUserUsersField = new ComboBox("Velg bruker", editUserUsers);
        editUserUsersField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
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
        usersTab.addComponent(editUserUsersField);

        TextField editUserUsernameField = new TextField("Brukernavn:", editUserUsername);
        usersTab.addComponent(editUserUsernameField);
        TextField editUserEmailField = new TextField("Epostadresse:", editUserEmail);
        editUserEmailField.addValidator(new EmailValidator("Ikke en gyldig epostadresse"));
        usersTab.addComponent(editUserEmailField);
        TextField editUserFirstnameField = new TextField("Fornavn:", editUserFirstname);
        usersTab.addComponent(editUserFirstnameField);
        TextField editUserLastnameField = new TextField("Etternavn:", editUserLastname);
        usersTab.addComponent(editUserLastnameField);
        usersTab.addComponent(new Button("Lagre endringer av bruker", new Button.ClickListener() {
                private static final long serialVersionUID = 1658760136279718499L;

                @Override
                public void buttonClick(ClickEvent event) {
                    User user = (User) editUserUsersField.getValue();
                    if (user != null) {
                        user.setUsername(editUserUsername.getValue());
                        user.setEmail(editUserEmail.getValue());
                        user.setFirstname(editUserFirstname.getValue());
                        user.setLastname(editUserLastname.getValue());

                        updateUserInDatabase(classForLogMessage, user);

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
                    List<Account> accounts = getAccounts(classForLogMessage);
                    accountsContainer.removeAllItems();
                    accountsContainer.addAll(accounts);
                    List<User> users = getUsers(classForLogMessage);
                    editUserPasswordUsers.removeAllItems();
                    editUserUsers.removeAllItems();
                    editUserPasswordUsers.addAll(users);
                    editUserUsers.addAll(users);
                }
            }));
        useradmin.addTab(usersTab, "Endre brukere");
        useradminTab.addComponent(useradmin);
        accordion.addTab(wrapInPanel(useradminTab), "Administrere brukere");
    }

    private void refreshJobTypesFromDatabase() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        jobTypes.removeAllItems();
        jobTypes.addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
    }

    private void refreshPaymentTypesFromDatabase() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        paymentTypes.removeAllItems();
        paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
    }

}
