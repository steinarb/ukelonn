package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class UkelonnUI extends AbstractUI {
    private static final long serialVersionUID = 1388525490129647161L;

    @Override
    protected void init(VaadinRequest request) {
    	if (!isLoggedIn()) {
            URI loginPage = addPathToURI(getPage().getLocation(), "../login/");
            getPage().setLocation(loginPage);
    	}

    	if (isAdministrator()) {
            URI adminPage = addPathToURI(getPage().getLocation(), "../admin/");
            getPage().setLocation(adminPage);
    	}

    	Principal currentUser = request.getUserPrincipal();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getName());

        TabBarView tabs = new TabBarView();

        NavigationManager balanceAndNewJobTab = new NavigationManager();
        NavigationView balanceAndNewJobView = new NavigationView();
        CssLayout balanceAndNewJobForm = new CssLayout();
        VerticalComponentGroup balanceAndNewJobGroup = new VerticalComponentGroup();
        balanceAndNewJobGroup.setWidth("100%");

        // Display the greeting
        Component greeting = new Label("Ukelønn for " + account.getFirstName());
        greeting.setStyleName("h1");
        balanceAndNewJobGroup.addComponent(greeting);

        // Display the current balance
        ObjectProperty<Double> balance = new ObjectProperty<Double>(account.getBalance());
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        balanceAndNewJobGroup.addComponent(balanceDisplay);

        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        List<TransactionType> paymentTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        BeanItemContainer<TransactionType> paymentTypesContainer = new BeanItemContainer<TransactionType>(TransactionType.class, paymentTypes);
        //ComboBox jobtypeSelector = new ComboBox("Velg jobb", paymentTypesContainer);
        NativeSelect jobtypeSelector = new NativeSelect("Velg jobb", paymentTypesContainer);
        jobtypeSelector.setValue("Item " + 2);        //jobtypeSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
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

        // Updatable containers
        BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<Transaction>(Transaction.class, getJobsFromAccount(account, getClass()));
        Table lastJobsTable = createTransactionTable("Jobbtype", recentJobs);
        lastJobsTable.setImmediate(true);
        BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<Transaction>(Transaction.class, getPaymentsFromAccount(account, getClass()));
        Class<? extends UkelonnUI> classForLogMessage = getClass();

        // Have a clickable button
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
        balanceAndNewJobView.setContent(balanceAndNewJobForm);
        balanceAndNewJobTab.navigateTo(balanceAndNewJobView);
        tabs.addTab(balanceAndNewJobTab, "Registrere jobb");

        NavigationManager lastJobsTab = new NavigationManager();
        CssLayout lastJobsForm = new CssLayout();
        VerticalComponentGroup lastJobsGroup = new VerticalComponentGroup();
        lastJobsGroup.setWidth("100%");
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

        setContent(tabs);
    }
}
