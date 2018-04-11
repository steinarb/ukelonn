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

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import no.priv.bang.ukelonn.impl.data.AmountAndBalance;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;

public class UserFallbackView extends AbstractView { // NOSONAR
    private static final long serialVersionUID = 1388525490129647161L;
    private UkelonnUIProvider provider;
    Account account; // NOSONAR

    // Datamodel for the UI (updates to these will be transferred to the GUI listeners).
    private Label greeting;
    private AmountAndBalance amountAndBalance = new AmountAndBalance(); // NOSONAR
    Binder<AmountAndBalance> amountAndBalanceBinder = new Binder<>(AmountAndBalance.class);
    ListDataProvider<TransactionType> paymentTypesContainer = new ListDataProvider<>(new ArrayList<>());
    ListDataProvider<Transaction> recentJobs = new ListDataProvider<>(new ArrayList<>());
    ListDataProvider<Transaction> recentPayments = new ListDataProvider<>(new ArrayList<>());

    public UserFallbackView(UkelonnUIProvider provider, VaadinRequest request) {
        amountAndBalanceBinder.setBean(amountAndBalance);
        this.provider = provider;
        // Display the greeting
        VerticalLayout content = new VerticalLayout();
        greeting = new Label("Ukelønn for ????");
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        FormLayout balanceLayout = new FormLayout();
        // Display the current balance
        TextField balanceDisplay = new TextField("Til gode:");
        amountAndBalanceBinder.forField(balanceDisplay)
            .withConverter(new StringToDoubleConverter("Ikke et tall"))
            .bind("balance");
        balanceDisplay.addStyleName("inline-label");
        balanceLayout.addComponent(balanceDisplay);
        content.addComponent(balanceLayout);

        Accordion accordion = new Accordion();

        FormLayout balanceAndNewJobTab = new FormLayout();
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(provider, getClass());
        List<TransactionType> paymentTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        paymentTypesContainer.getItems().clear();
        paymentTypesContainer.getItems().addAll(paymentTypes);
        ComboBox<TransactionType> jobtypeSelector = new ComboBox<>("Velg jobb");
        jobtypeSelector.setDataProvider(paymentTypesContainer);
        jobtypeSelector.setItemCaptionGenerator(TransactionType::getTransactionTypeName);
        jobtypeSelector.setEmptySelectionAllowed(true);
        jobtypeSelector.addSelectionListener(jt->changeJobAmountWhenJobTypeIsChanged(jobtypeSelector));
        balanceAndNewJobTab.addComponent(jobtypeSelector);
        TextField newAmountDisplay = new TextField();
        amountAndBalanceBinder.forField(balanceDisplay)
            .withConverter(new StringToDoubleConverter("Ikke et tall"))
            .bind("balance");
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJobTab.addComponent(newAmountDisplay);

        // Updatable containers
        recentJobs.getItems().clear();
        recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
        recentJobs.refreshAll();
        Grid<Transaction> lastJobsTable = createTransactionTable("Jobbtype", recentJobs, true);
        recentPayments.getItems().clear();
        recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
        recentPayments.refreshAll();

        // Have a clickable button
        balanceAndNewJobTab.addComponent(new Button("Registrer jobb",
                                                    new Button.ClickListener() {
                                                        private static final long serialVersionUID = 2723190031041985566L;

                                                        @Override
                                                        public void buttonClick(ClickEvent e) {
                                                            registerJobInDatabase(jobtypeSelector);
                                                        }
                                                    }));
        accordion.addTab(balanceAndNewJobTab, "Registrere jobb");

        VerticalLayout lastJobsTab = new VerticalLayout();
        lastJobsTab.addComponent(lastJobsTable);
        accordion.addTab(lastJobsTab, "Siste jobber");

        VerticalLayout lastPaymentsTab = new VerticalLayout();
        Grid<Transaction> lastPaymentsTable = createTransactionTable("Type utbetaling", recentPayments, false);
        lastPaymentsTab.addComponent(lastPaymentsTable);
        accordion.addTab(lastPaymentsTab, "Siste utbetalinger");

        content.addComponent(accordion);

        HorizontalLayout links = createLinksToBrowserVersionAndLogout(request, "mobile", "Mobilversjon");
        content.addComponent(links);

        addComponent(content);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
        account = getAccountInfoFromDatabase(provider, getClass(), currentUser);

        greeting.setValue("Ukelønn for " + account.getFirstName());
        AmountAndBalance bean = amountAndBalanceBinder.getBean();
        bean.setBalance(account.getBalance());
        amountAndBalanceBinder.readBean(bean);
        recentJobs.getItems().clear();
        recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
        recentJobs.refreshAll();
        recentPayments.getItems().clear();
        recentPayments.getItems().addAll(getPaymentsFromAccount(provider, account, getClass()));
        recentPayments.refreshAll();
    }

    void changeJobAmountWhenJobTypeIsChanged(ComboBox<TransactionType> jobtypeSelector) {
        Optional<TransactionType> selectedJobtype = jobtypeSelector.getSelectedItem();
        if (selectedJobtype.isPresent()) {
            amountAndBalanceBinder.getBean().setAmount(selectedJobtype.get().getTransactionAmount());
            amountAndBalanceBinder.readBean(amountAndBalanceBinder.getBean());
        } else {
            amountAndBalanceBinder.getBean().setAmount(0.0);
            amountAndBalanceBinder.readBean(amountAndBalanceBinder.getBean());
        }
    }

    void registerJobInDatabase(ComboBox<TransactionType> jobtypeSelector) {
        Optional<TransactionType> jobType = jobtypeSelector.getSelectedItem();
        if (jobType.isPresent()) {
            registerNewJobInDatabase(provider, getClass(), account, jobType.get().getId(), jobType.get().getTransactionAmount());
            AmountAndBalance bean = amountAndBalanceBinder.getBean();
            bean.setBalance(account.getBalance());
            amountAndBalanceBinder.readBean(bean);
            jobtypeSelector.setValue(null);
            recentJobs.getItems().clear();
            recentJobs.getItems().addAll(getJobsFromAccount(provider, account, getClass()));
            recentJobs.refreshAll();
        }
    }
}
