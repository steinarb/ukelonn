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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.vaadin.touchkit.ui.NavigationManager;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import no.priv.bang.ukelonn.impl.data.AmountAndBalance;

import com.vaadin.ui.Button.ClickEvent;

public class UserView extends AbstractView { // NOSONAR
    private static final long serialVersionUID = 1388525490129647161L;
    private UkelonnUIProvider provider;
    // Updatable containers
    private Label greeting;
    private AmountAndBalance amountAndBalance = new AmountAndBalance(); // NOSONAR
    Binder<AmountAndBalance> amountAndBalanceBinder = new Binder<>(AmountAndBalance.class);
    private ListDataProvider<TransactionType> jobTypesContainer = new ListDataProvider<>(new ArrayList<>(10));
    private ListDataProvider<Transaction> recentJobs = new ListDataProvider<>(new ArrayList<Transaction>(10));
    private ListDataProvider<Transaction> recentPayments = new ListDataProvider<>(new ArrayList<Transaction>(10));
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

        greeting.setValue("Ukelønn for " + account.getFirstName());
        recentJobs.getItems().clear();
        recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
        recentJobs.refreshAll();
        recentPayments.getItems().clear();
        recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
        recentPayments.refreshAll();
    }

    private VerticalComponentGroup createBalanceAndNewJobForm() {
        amountAndBalanceBinder.setBean(amountAndBalance);
        CssLayout balanceAndNewJobForm = new CssLayout();
        VerticalComponentGroup balanceAndNewJobGroup = new VerticalComponentGroup();
        balanceAndNewJobGroup.setWidth("100%");

        // Display the greeting
        greeting = new Label("Ukelønn for ????");
        greeting.setStyleName("h1");
        balanceAndNewJobGroup.addComponent(greeting);

        // Display the current balance
        TextField balanceDisplay = new TextField("Til gode:");
        amountAndBalanceBinder.forField(balanceDisplay)
            .withConverter(new StringToDoubleConverter("Ikke et tall"))
            .bind("balance");
        balanceDisplay.addStyleName("inline-label");
        balanceAndNewJobGroup.addComponent(balanceDisplay);

        // Initialize the list of job types
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        List<TransactionType> jobTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        jobTypesContainer.getItems().clear();
        jobTypesContainer.getItems().addAll(jobTypes);
        jobTypesContainer.refreshAll();
        NativeSelect<TransactionType> jobtypeSelector = new NativeSelect<>("Velg jobb", jobTypesContainer);
        jobtypeSelector.setItemCaptionGenerator(TransactionType::getTransactionTypeName);
        jobtypeSelector.setEmptySelectionAllowed(true);
        jobtypeSelector.addSelectionListener(job->changeJobAmountWhenJobTypeIsChanged(jobtypeSelector, amountAndBalanceBinder));
        balanceAndNewJobGroup.addComponent(jobtypeSelector);

        TextField newAmountDisplay = new TextField();
        amountAndBalanceBinder.forField(newAmountDisplay)
            .withConverter(new StringToDoubleConverter("Ikke et tall"))
            .bind("amount");
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJobGroup.addComponent(newAmountDisplay);

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
        jobtypeSelector.setSelectedItem(jobTypes.get(0));
        return balanceAndNewJobGroup;
    }

    void changeJobAmountWhenJobTypeIsChanged(NativeSelect<TransactionType> jobtypeSelector, Binder<AmountAndBalance> binder) {
        Optional<TransactionType> selectedJob = jobtypeSelector.getSelectedItem();
        if (!selectedJob.isPresent()) {
            updateAmount(binder, 0.0);
        } else {
            Double newAmount = selectedJob.get().getTransactionAmount();
            updateAmount(binder, newAmount);
        }
    }

    void updateAmount(Binder<AmountAndBalance> binder, Double amount) {
        AmountAndBalance bean = binder.getBean();
        bean.setAmount(amount);
        binder.readBean(bean);
    }

    void updateBalance(Binder<AmountAndBalance> binder, Double amount) {
        AmountAndBalance bean = binder.getBean();
        bean.setBalance(amount);
        binder.readBean(bean);
    }

    void registerJobInDatabase(NativeSelect<TransactionType> jobtypeSelector) {
        TransactionType jobType = jobtypeSelector.getValue();
        if (jobType != null) {
            registerNewJobInDatabase(provider, getClass(), account, jobType.getId(), jobType.getTransactionAmount());
            jobtypeSelector.setValue(null);
            updateBalance(amountAndBalanceBinder, account.getBalance());
            recentJobs.getItems().clear();
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentJobs.refreshAll();
        }
    }
}
