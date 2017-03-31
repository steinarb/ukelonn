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
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

public class UserView extends AbstractView {
    private static final long serialVersionUID = 1388525490129647161L;
    // Updatable containers
    private ObjectProperty<String> greetingProperty = new ObjectProperty<String>("Ukelønn for ????");;
    private ObjectProperty<Double> balance = new ObjectProperty<Double>(0.0);
    private BeanItemContainer<TransactionType> jobTypesContainer;
    private BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<Transaction>(Transaction.class);
    private BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<Transaction>(Transaction.class);
    private Account account;

    public UserView(VaadinRequest request) {
    	setSizeFull();
    	TabBarView tabs = new TabBarView();

        NavigationManager balanceAndNewJobTab = new NavigationManager();
        NavigationView balanceAndNewJobView = new NavigationView();
        CssLayout balanceAndNewJobForm = createBalanceAndNewJobForm();
        balanceAndNewJobView.setContent(balanceAndNewJobForm);
        balanceAndNewJobTab.navigateTo(balanceAndNewJobView);
        tabs.addTab(balanceAndNewJobTab, "Registrere jobb");

        NavigationManager lastJobsTab = new NavigationManager();
        CssLayout lastJobsForm = new CssLayout();
        VerticalComponentGroup lastJobsGroup = new VerticalComponentGroup();
        lastJobsGroup.setWidth("100%");
        Table lastJobsTable = createTransactionTable("Jobbtype", recentJobs);
        lastJobsTable.setImmediate(true);
        lastJobsGroup.addComponent(lastJobsTable);
        lastJobsForm.addComponent(lastJobsGroup);
        lastJobsTab.navigateTo(lastJobsForm);
        tabs.addTab(lastJobsTab, "Siste jobber");

        NavigationManager lastPaymentsTab = new NavigationManager();
        CssLayout lastPaymentsForm = new CssLayout();
        VerticalComponentGroup lastPaymentsGroup = new VerticalComponentGroup();
        lastPaymentsGroup.setWidth("100%");
        Table lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments);
        lastPaymentsGroup.addComponent(lastPaymentsTable);
        lastPaymentsForm.addComponent(lastPaymentsGroup);
        lastPaymentsTab.navigateTo(lastPaymentsForm);
        tabs.addTab(lastPaymentsTab, "Siste utbetalinger");

        addComponent(tabs);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        account = getAccountInfoFromDatabase(getClass(), currentUser);

        greetingProperty.setValue("Ukelønn for " + account.getFirstName());
        balance.setValue(account.getBalance());
        recentJobs.removeAllItems();
        recentJobs.addAll(getJobsFromAccount(account, getClass()));
        recentPayments.removeAllItems();
        recentPayments.addAll(getPaymentsFromAccount(account, getClass()));
    }

    private CssLayout createBalanceAndNewJobForm() {
        CssLayout balanceAndNewJobForm = new CssLayout();
        VerticalComponentGroup balanceAndNewJobGroup = new VerticalComponentGroup();
        balanceAndNewJobGroup.setWidth("100%");

        // Display the greeting
        Component greeting = new Label(greetingProperty);
        greeting.setStyleName("h1");
        balanceAndNewJobGroup.addComponent(greeting);

        // Display the current balance
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        balanceAndNewJobGroup.addComponent(balanceDisplay);

        // Initialize the list of job types
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        List<TransactionType> jobTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        jobTypesContainer = new BeanItemContainer<TransactionType>(TransactionType.class, jobTypes);
        NativeSelect jobtypeSelector = new NativeSelect("Velg jobb", jobTypesContainer);
        jobtypeSelector.setValue("Item " + 2);
        jobtypeSelector.setItemCaptionPropertyId("transactionTypeName");
        jobtypeSelector.setNullSelectionAllowed(true);
        balanceAndNewJobGroup.addComponent(jobtypeSelector);
        ObjectProperty<Double> newJobAmount = new ObjectProperty<Double>(0.0);
        TextField newAmountDisplay = new TextField(newJobAmount);
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJobGroup.addComponent(newAmountDisplay);
        jobtypeSelector.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = 3145027593224884343L;
                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (jobtypeSelector.getValue() == null) {
                        newJobAmount.setValue(0.0);
                    } else {
                        newJobAmount.setValue(((TransactionType) jobtypeSelector.getValue()).getTransactionAmount());
                    }
                }
            });

        // Have a clickable button
        Class<? extends UserView> classForLogMessage = getClass();
        balanceAndNewJobGroup.addComponent(new Button("Registrer jobb",
                                                      new Button.ClickListener() {
                                                          private static final long serialVersionUID = 2723190031041985566L;

                                                          @Override
                                                          public void buttonClick(ClickEvent e) {
                                                              TransactionType jobType = (TransactionType) jobtypeSelector.getValue();
                                                              if (jobType != null) {
                                                                  registerNewJobInDatabase(classForLogMessage, account, jobType.getId(), jobType.getTransactionAmount());
                                                                  jobtypeSelector.setValue(null);
                                                                  balance.setValue(account.getBalance());
                                                                  recentJobs.removeAllItems();
                                                                  recentJobs.addAll(getJobsFromAccount(account, classForLogMessage));
                                                              }
                                                          }
                                                      }));
        balanceAndNewJobForm.addComponent(balanceAndNewJobGroup);
        return balanceAndNewJobForm;
    }
}
