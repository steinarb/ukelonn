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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

public class UserView extends AbstractView { // NOSONAR
    private static final long serialVersionUID = 1388525490129647161L;
    private UkelonnUIProvider provider;
    // Updatable containers
    private ObjectProperty<String> greetingProperty = new ObjectProperty<>("Ukelønn for ????");
    ObjectProperty<Double> balance = new ObjectProperty<>(0.0);
    private BeanItemContainer<TransactionType> jobTypesContainer; // NOSONAR
    private BeanItemContainer<Transaction> recentJobs = new BeanItemContainer<>(Transaction.class);
    private BeanItemContainer<Transaction> recentPayments = new BeanItemContainer<>(Transaction.class);
    Account account; // NOSONAR

    public UserView(UkelonnUIProvider provider, VaadinRequest request) {
        this.provider = provider;
        setSizeFull();

        NavigationManager navigationManager = new NavigationManager();
        VerticalComponentGroup balanceAndNewJobGroup = createBalanceAndNewJobForm();
        NavigationView balanceAndNewJobView = new NavigationView("Registrere jobb", balanceAndNewJobGroup);
        navigationManager.addComponent(balanceAndNewJobView);
        navigationManager.navigateTo(balanceAndNewJobView);

        String lastJobsLabel = "Siste jobber";
        String lastPaymentsLabel = "Siste utbetalinger";

        // Create Subviews with tables
        NavigationView lastJobsView = createNavigationViewWithTable(navigationManager, "Jobber", recentJobs, lastJobsLabel, true);
        NavigationView lastPaymentsView = createNavigationViewWithTable(navigationManager, "Utbetalinger", recentPayments, lastPaymentsLabel, false);

        // Add buttons to the top view, linking to the subviews
        balanceAndNewJobGroup.addComponent(createNavigationButton(lastJobsLabel, lastJobsView));
        balanceAndNewJobGroup.addComponent(createNavigationButton(lastPaymentsLabel, lastPaymentsView));
        addComponent(navigationManager);

        HorizontalLayout links = createLinksToBrowserVersionAndLogout(request, "browser", "Nettleserversjon");
        addComponent(links);

        // Set the stretch to minimize the size used by the link
        setExpandRatio(navigationManager, 100);
        setExpandRatio(links, 1);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        account = getAccountInfoFromDatabase(provider, getClass(), currentUser);

        greetingProperty.setValue("Ukelønn for " + account.getFirstName());
        balance.setValue(account.getBalance());
        recentJobs.removeAllItems();
        recentJobs.addAll(getJobsFromAccount(provider, account, getClass()));
        recentPayments.removeAllItems();
        recentPayments.addAll(getPaymentsFromAccount(provider, account, getClass()));
    }

    private VerticalComponentGroup createBalanceAndNewJobForm() {
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
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        List<TransactionType> jobTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        jobTypesContainer = new BeanItemContainer<>(TransactionType.class, jobTypes);
        NativeSelect jobtypeSelector = new NativeSelect("Velg jobb", jobTypesContainer);
        jobtypeSelector.setValue("Item " + 2);
        jobtypeSelector.setItemCaptionPropertyId("transactionTypeName");
        jobtypeSelector.setNullSelectionAllowed(true);
        balanceAndNewJobGroup.addComponent(jobtypeSelector);
        ObjectProperty<Double> newJobAmount = new ObjectProperty<>(0.0);
        TextField newAmountDisplay = new TextField(newJobAmount);
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJobGroup.addComponent(newAmountDisplay);
        jobtypeSelector.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = 3145027593224884343L;
                @Override
                public void valueChange(ValueChangeEvent event) {
                    changeJobAmountWhenJobTypeIsChanged(jobtypeSelector, newJobAmount);
                }
            });

        // Have a clickable button
        balanceAndNewJobGroup.addComponent(new Button("Registrer jobb",
                                                      new Button.ClickListener() {
                                                          private static final long serialVersionUID = 2723190031041985566L;

                                                          @Override
                                                          public void buttonClick(ClickEvent e) {
                                                              registerJobInDatabase(jobtypeSelector);
                                                          }
                                                      }));
        balanceAndNewJobForm.addComponent(balanceAndNewJobGroup);
        return balanceAndNewJobGroup;
    }

    void changeJobAmountWhenJobTypeIsChanged(NativeSelect jobtypeSelector, ObjectProperty<Double> newJobAmount) {
        if (jobtypeSelector.getValue() == null) {
            newJobAmount.setValue(0.0);
        } else {
            newJobAmount.setValue(((TransactionType) jobtypeSelector.getValue()).getTransactionAmount());
        }
    }

    void registerJobInDatabase(NativeSelect jobtypeSelector) {
        TransactionType jobType = (TransactionType) jobtypeSelector.getValue();
        if (jobType != null) {
            registerNewJobInDatabase(provider, getClass(), account, jobType.getId(), jobType.getTransactionAmount());
            jobtypeSelector.setValue(null);
            balance.setValue(account.getBalance());
            recentJobs.removeAllItems();
            recentJobs.addAll(getJobsFromAccount(provider, account, getClass()));
        }
    }
}
