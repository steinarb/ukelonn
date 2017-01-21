package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button.ClickEvent;

@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class UkelonnUI extends AbstractUI {
    private static final long serialVersionUID = 1388525490129647161L;

    @Override
    protected void init(VaadinRequest request) {
    	if (isAdministrator()) {
            URI adminPage = addPathToURI(getPage().getLocation(), "../admin/");
            getPage().setLocation(adminPage);
    	}

    	Principal currentUser = request.getUserPrincipal();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getName());

    	// Display the greeting
    	VerticalLayout content = new VerticalLayout();
        Component greeting = new Label("Hei " + account.getFirstName());
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        FormLayout balanceLayout = new FormLayout();
        // Display the current balance
        ObjectProperty<Double> balance = new ObjectProperty<Double>(account.getBalance());
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        balanceLayout.addComponent(balanceDisplay);
        content.addComponent(balanceLayout);

        Accordion accordion = new Accordion();

        FormLayout balanceAndNewJobTab = new FormLayout();
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        List<TransactionType> paymentTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        BeanItemContainer<TransactionType> paymentTypesContainer = new BeanItemContainer<TransactionType>(TransactionType.class, paymentTypes);
        ComboBox jobtypeSelector = new ComboBox("Velg jobb", paymentTypesContainer);
        jobtypeSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        jobtypeSelector.setItemCaptionPropertyId("transactionTypeName");
        jobtypeSelector.setNullSelectionAllowed(true);
        balanceAndNewJobTab.addComponent(jobtypeSelector);
        ObjectProperty<Double> newJobAmount = new ObjectProperty<Double>(0.0);
        TextField newAmountDisplay = new TextField(newJobAmount);
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJobTab.addComponent(newAmountDisplay);
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
        balanceAndNewJobTab.addComponent(new Button("Registrer jobb",
                                                    new Button.ClickListener() {
                                                        private static final long serialVersionUID = 2723190031041985566L;

                                                        @Override
                                                        public void buttonClick(ClickEvent e) {
                                                            TransactionType jobType = (TransactionType) jobtypeSelector.getValue();
                                                            if (jobType != null) {
                                                                registerNewJobInDatabase(classForLogMessage, account, jobType.getId(), jobType.getTransactionAmount());
                                                                balance.setValue(account.getBalance());
                                                                jobtypeSelector.setValue(null);
                                                                recentJobs.removeAllItems();
                                                                recentJobs.addAll(getJobsFromAccount(account, classForLogMessage));
                                                            }
                                                        }
                                                    }));
        accordion.addTab(balanceAndNewJobTab, "Registrere jobb");

        VerticalLayout lastJobsTab = new VerticalLayout();
        lastJobsTab.addComponent(lastJobsTable);
        accordion.addTab(lastJobsTab, "Siste jobber");

        VerticalLayout lastPaymentsTab = new VerticalLayout();
        Table lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments);
        lastPaymentsTab.addComponent(lastPaymentsTable);
        accordion.addTab(lastPaymentsTab, "Siste utbetalinger");

        content.addComponent(accordion);
        setContent(content);
    }
}
