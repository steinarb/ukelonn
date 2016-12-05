package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;

    @Override
    protected void init(VaadinRequest request) {
    	Principal currentUser = request.getUserPrincipal();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getName());
        // Create the content root layout for the UI
        VerticalLayout content = new VerticalLayout();
        setContent(content);

        // Display the greeting
        Component greeting = new Label("Hei " + account.getFirstName());
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        FormLayout balanceAndNewJob = new FormLayout();
        // Display the current balance
        ObjectProperty<Double> balance = new ObjectProperty<Double>(account.getBalance());
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        balanceAndNewJob.addComponent(balanceDisplay);

        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        List<TransactionType> paymentTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        BeanItemContainer<TransactionType> paymentTypesContainer = new BeanItemContainer<TransactionType>(TransactionType.class, paymentTypes);
        ComboBox jobtypeSelector = new ComboBox("Velg jobb", paymentTypesContainer);
        jobtypeSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        jobtypeSelector.setItemCaptionPropertyId("transactionTypeName");
        jobtypeSelector.setNullSelectionAllowed(true);
        balanceAndNewJob.addComponent(jobtypeSelector);
        ObjectProperty<Double> newJobAmount = new ObjectProperty<Double>(0.0);
        TextField newAmountDisplay = new TextField(newJobAmount);
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJob.addComponent(newAmountDisplay);
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
        balanceAndNewJob.addComponent(new Button("Registrer jobb",
                                                 new Button.ClickListener() {
                                                     private static final long serialVersionUID = 2723190031041985566L;

                                                     @Override
                                                     public void buttonClick(ClickEvent e) {
                                                         TransactionType jobType = (TransactionType) jobtypeSelector.getValue();
                                                         if (jobType != null) {
                                                             registerNewJobInDatabase(getClass(), account, jobType.getId(), jobType.getTransactionAmount());
                                                             balance.setValue(account.getBalance());
                                                             jobtypeSelector.setValue(null);
                                                         }
                                                     }
                                                 }));
        content.addComponent(balanceAndNewJob);
    }

}
