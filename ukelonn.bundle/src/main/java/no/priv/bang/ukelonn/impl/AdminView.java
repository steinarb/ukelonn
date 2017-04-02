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
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;

public class AdminView extends AbstractView {
    private static final long serialVersionUID = -1581589472749242129L;
    final int idOfPayToBank = 4;

    // Data model for handling payments to users
    private ObjectProperty<String> greetingProperty = new ObjectProperty<String>("Ukelønn admin UI, bruker: ????");
    BeanItemContainer<Account> accountsContainer = new BeanItemContainer<Account>(Account.class, getAccounts(getClass()));
    ObjectProperty<Double> balance = new ObjectProperty<Double>(0.0);
    ObjectProperty<Double> amount = new ObjectProperty<Double>(0.0);
    BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<Transaction>(Transaction.class, getDummyTransactions());
    BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<Transaction>(Transaction.class, getDummyTransactions());
    Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
    BeanItemContainer<TransactionType> paymentTypes = new BeanItemContainer<TransactionType>(TransactionType.class, getPaymentTypesFromTransactionTypes(transactionTypes.values()));
    BeanItemContainer<TransactionType> jobTypes = new BeanItemContainer<TransactionType>(TransactionType.class, getJobTypesFromTransactionTypes(transactionTypes.values()));

    // Data model for the admin tasks
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
    BeanItemContainer<User> editUserPasswordUsers = new BeanItemContainer<User>(User.class, getUsers(getClass()));
    ObjectProperty<String> editUserPassword1 = new ObjectProperty<String>("");
    ObjectProperty<String> editUserPassword2 = new ObjectProperty<String>("");
    BeanItemContainer<User> editUserUsers = new BeanItemContainer<User>(User.class, getUsers(getClass()));
    ObjectProperty<String> editUserUsername = new ObjectProperty<String>("");
    ObjectProperty<String> editUserEmail = new ObjectProperty<String>("");
    ObjectProperty<String> editUserFirstname = new ObjectProperty<String>("");
    ObjectProperty<String> editUserLastname = new ObjectProperty<String>("");

    public AdminView(VaadinRequest request) {
    	setSizeFull();
        TabBarView tabs = new TabBarView();

        NavigationManager registerPaymentTab = new NavigationManager();
        tabs.addTab(registerPaymentTab, "Registrere utbetaling");
        VerticalComponentGroup registerPaymentTabGroup = createRegisterPaymentForm(registerPaymentTab);

        NavigationView lastJobsForUserView = createNavigationViewWithTable(registerPaymentTab, "Jobber", recentJobs, "Siste jobber");
        NavigationView lastPaymentsForUserView = createNavigationViewWithTable(registerPaymentTab, "Utbetalinger", recentPayments, "Siste utbetalinger");

        registerPaymentTabGroup.addComponent(createNavigationButton("Siste jobber for bruker", lastJobsForUserView));
        registerPaymentTabGroup.addComponent(createNavigationButton("Siste utbetalinger til bruker", lastPaymentsForUserView));


        // Job type administration
        Class<? extends AdminView> classForLogMessage = getClass();
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
                    String jobname = newJobTypeName.getValue();
                    Double jobamount = newJobTypeAmount.getValue();
                    if (!"".equals(jobname) && !Double.valueOf(0.0).equals(jobamount)) {
                        addJobTypeToDatabase(classForLogMessage, jobname, jobamount);
                        newJobTypeName.setValue("");
                        newJobTypeAmount.setValue(0.0);
                    }
                }
            }));

        NavigationView jobtypesTab = new NavigationView();
        VerticalComponentGroup jobtypesform = createVerticalComponentGroupWithCssLayoutAndNavigationSubView(jobtypeAdminTab, jobtypesTab, modifyJobtypesLabel);
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
                            Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(classForLogMessage);
                            jobTypes.removeAllItems();
                            jobTypes.addAll(getJobTypesFromTransactionTypes(transactionTypes.values()));
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

        jobtypeAdminContent.addComponent(createNavigationButton(newJobtypeLabel, newJobTypeTab));
        jobtypeAdminContent.addComponent(createNavigationButton(modifyJobtypesLabel, jobtypesTab));
        tabs.addTab(jobtypeAdminTab, "Administrere jobbtyper");

        VerticalLayout paymentstypeadminTab = new VerticalLayout();
        Accordion paymentstypeadmin = new Accordion();
        FormLayout newpaymenttypeTab = new FormLayout();
        TextField newPaymentTypeNameField = new TextField("Navn på ny betalingstype:", newPaymentTypeName);
        newpaymenttypeTab.addComponent(newPaymentTypeNameField);
        TextField newPaymentTypeAmountField = new TextField("Beløp for ny betalingstype:", newPaymentTypeAmount);
        newpaymenttypeTab.addComponent(newPaymentTypeAmountField);
        newpaymenttypeTab.addComponent(new Button("Lag betalingstype", new Button.ClickListener() {
                private static final long serialVersionUID = -2160144195348196823L;

                @Override
                public void buttonClick(ClickEvent event) {
                    String paymentName = newPaymentTypeName.getValue();
                    Double rawPaymentAmount = newPaymentTypeAmount.getValue();
                    Double paymentAmount = Double.valueOf(0.0).equals(rawPaymentAmount) ? null : rawPaymentAmount;
                    if (!"".equals(paymentName)) {
                        addPaymentTypeToDatabase(classForLogMessage, paymentName, paymentAmount);
                        newPaymentTypeName.setValue("");
                        newPaymentTypeAmount.setValue(0.0);
                        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(classForLogMessage);
                        paymentTypes.removeAllItems();
                        paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
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
                            Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(classForLogMessage);
                            paymentTypes.removeAllItems();
                            paymentTypes.addAll(getPaymentTypesFromTransactionTypes(transactionTypes.values()));
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
        tabs.addTab(paymentstypeadminTab, "Administrere utbetalingstyper");

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
                    	addUserToDatabase(classForLogMessage,
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
        tabs.addTab(useradminTab, "Administrere brukere");

        addComponent(tabs);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        AdminUser admin = getAdminUserFromDatabase(getClass(), currentUser);
        greetingProperty.setValue("Ukelønn admin UI, bruker: " + admin.getFirstname());
    }

    private VerticalComponentGroup createRegisterPaymentForm(NavigationManager registerPaymentTab) {
        CssLayout registerPaymentTabForm = new CssLayout();
        VerticalComponentGroup registerPaymentTabGroup = new VerticalComponentGroup();

        // Display the greeting
        Component greeting = new Label(greetingProperty);
        greeting.setStyleName("h1");
        registerPaymentTabGroup.addComponent(greeting);

        NativeSelect paymenttype = new NativeSelect("Registrer utbetaling", paymentTypes);
        Class<? extends AdminView> classForLogMessage = getClass();
        NativeSelect accountSelector = new NativeSelect("Velg hvem det skal betales til", accountsContainer);
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
        registerPaymentTabGroup.addComponent(accountSelector);

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

        paymentLayout.addComponent(new Button("Registrer betaling",
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
        registerPaymentTabGroup.addComponent(paymentLayout);
        registerPaymentTabForm.addComponent(registerPaymentTabGroup);
        NavigationView registerPaymentView = new NavigationView("Registrer betaling", registerPaymentTabForm);
        registerPaymentTab.addComponent(registerPaymentView);
        registerPaymentTab.navigateTo(registerPaymentView);
        return registerPaymentTabGroup;
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

}
