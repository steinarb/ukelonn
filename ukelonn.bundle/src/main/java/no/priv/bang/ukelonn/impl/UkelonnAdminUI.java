package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;

@Theme("chameleon")
public class UkelonnAdminUI extends AbstractUI {
    private static final long serialVersionUID = -1581589472749242129L;

    @SuppressWarnings("serial")
    @Override
    protected void init(VaadinRequest request) {
    	if (!isAdministrator()) {
            URI userPage = addPathToURI(getPage().getLocation(), "../user/");
            getPage().setLocation(userPage);
    	}

    	VerticalLayout content = new VerticalLayout();
    	content.addStyleName("ukelonn-responsive-layout");
    	Responsive.makeResponsive(content);
    	Principal currentUser = request.getUserPrincipal();
    	AdminUser admin = getAdminUserFromDatabase(getClass(), (String) currentUser.getName());
        // Display the greeting
        Component greeting = new Label("Hei " + admin.getFirstname());
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        // Updatable containers
        ObjectProperty<Double> balance = new ObjectProperty<Double>(0.0);
        BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<Transaction>(Transaction.class);
        BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<Transaction>(Transaction.class);
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        BeanItemContainer<TransactionType> paymentTypes = new BeanItemContainer<TransactionType>(TransactionType.class, getPaymentTypesFromTransactionTypes(transactionTypes.values()));
        BeanItemContainer<TransactionType> jobTypes = new BeanItemContainer<TransactionType>(TransactionType.class, getJobTypesFromTransactionTypes(transactionTypes.values()));
        ComboBox paymenttype = new ComboBox("Registrer utbetaling", paymentTypes);
        ObjectProperty<Double> amount = new ObjectProperty<Double>(0.0);
        Class<? extends UkelonnAdminUI> classForLogMessage = getClass();

        Accordion accordion = new Accordion();

        VerticalLayout registerPaymentTab = new VerticalLayout();
        List<Account> accounts = getAccounts(getClass());
        BeanItemContainer<Account> accountsContainer = new BeanItemContainer<Account>(Account.class, accounts);
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
                        final int idOfPayToBank = 4;
                        paymenttype.select(transactionTypes.get(idOfPayToBank));
                        amount.setValue(balance.getValue());
                        recentJobs.addAll(getJobsFromAccount(account, classForLogMessage));
                        recentPayments.addAll(getPaymentsFromAccount(account, classForLogMessage));
                    }
                }
            });
        registerPaymentTab.addComponent(accountSelector);

        FormLayout paymentLayout = new FormLayout();
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        paymentLayout.addComponent(balanceDisplay);

        paymenttype.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        paymenttype.setItemCaptionPropertyId("transactionTypeName");
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
        registerPaymentTab.addComponent(paymentLayout);

        Accordion userinfo = new Accordion();
        VerticalLayout jobsTab = new VerticalLayout();
        Table lastJobsTable = createTransactionTable("Jobbtype", recentJobs);
        jobsTab.addComponent(lastJobsTable);
        userinfo.addTab(jobsTab, "Siste jobber");
        VerticalLayout paymentsTab = new VerticalLayout();
        Table lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments);
        paymentsTab.addComponent(lastPaymentsTable);
        userinfo.addTab(paymentsTab, "Siste utbetalinger");
        registerPaymentTab.addComponent(userinfo);
        accordion.addTab(registerPaymentTab, "Registrere utbetaling");

        VerticalLayout jobtypeAdminTab = new VerticalLayout();
        Accordion jobtypes = new Accordion();
        FormLayout newJobTypeTab = new FormLayout();
        ObjectProperty<String> newJobTypeName = new ObjectProperty<String>("");
        ObjectProperty<Double> newJobTypeAmount = new ObjectProperty<Double>(0.0);
        ObjectProperty<String> editedJobTypeName = new ObjectProperty<String>("");
        ObjectProperty<Double> editedJobTypeAmount = new ObjectProperty<Double>(0.0);
        TextField newJobTypeNameField = new TextField("Navn på ny jobbtype:", newJobTypeName);
        newJobTypeTab.addComponent(newJobTypeNameField);
        TextField newJobTypeAmountField = new TextField("Navn på ny jobbtype:", newJobTypeAmount);
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
        TextField editJobTypeAmountField = new TextField("Endre på ny jobbtype:", editedJobTypeAmount);
        editJobLayout.addComponent(editJobTypeAmountField);
        editJobLayout.addComponent(new Button("Lagre endringer i jobbtype", new Button.ClickListener() {
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
        jobtypes.addTab(jobtypesform, "Endre jobbtyper");
        jobtypeAdminTab.addComponent(jobtypes);
        accordion.addTab(jobtypeAdminTab, "Administrere jobbtyper");

        VerticalLayout paymentstypeadminTab = new VerticalLayout();
        Accordion paymentstypeadmin = new Accordion();
        VerticalLayout newpaymenttypeTab = new VerticalLayout();
        paymentstypeadmin.addTab(newpaymenttypeTab, "Lag ny utbetalingstype");
        VerticalLayout paymenttypesform = new VerticalLayout();
        paymentstypeadmin.addTab(paymenttypesform, "Endre utbetalingstyper");
        paymentstypeadminTab.addComponent(paymentstypeadmin);
        accordion.addTab(paymentstypeadminTab, "Endre utbetalingstyper");

        VerticalLayout useradminTab = new VerticalLayout();
        Accordion useradmin = new Accordion();
        VerticalLayout newuserTab = new VerticalLayout();
        useradmin.addTab(newuserTab, "Legg til ny bruker");
        VerticalLayout changeuserpasswordTab = new VerticalLayout();
        useradmin.addTab(changeuserpasswordTab, "Bytt passord på bruker");
        VerticalLayout usersTab = new VerticalLayout();
        useradmin.addTab(usersTab, "Endre brukere");
        useradminTab.addComponent(useradmin);
        accordion.addTab(useradminTab, "Administrere brukere");

        content.addComponent(accordion);

        setContent(content);
    }

}
