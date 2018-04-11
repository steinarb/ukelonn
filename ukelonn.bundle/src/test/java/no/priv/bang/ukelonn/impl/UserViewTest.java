/*
 * Copyright 2017 Steinar Bang
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

import javax.servlet.ServletException;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.Binder;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.NativeSelect;

import no.priv.bang.ukelonn.impl.data.AmountAndBalance;


public class UserViewTest {

    private static VaadinSession session;

    @BeforeClass
    public static void beforeAllTests() throws ServletException, ServiceException {
        setupFakeOsgiServices();
        session = createSession();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testChangeJobAmountWhenJobTypeIsChanged() throws ServiceException, ServletException {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        UserView view = new UserView(provider, request);

        // Mock the vaadin component
        NativeSelect<TransactionType> jobtypeSelector = mock(NativeSelect.class);
        Double newAmountValue = 51.0;
        TransactionType jobtype = new TransactionType(0, "", newAmountValue, true, false);
        when(jobtypeSelector.getSelectedItem()).thenReturn(Optional.empty(), Optional.of(jobtype));

        // Data storage and binder
        Binder<AmountAndBalance> binder = new Binder<>(AmountAndBalance.class);
        AmountAndBalance bean = new AmountAndBalance();
        binder.setBean(bean);

        // Give the amount an inital value
        double initialJobAmount = 10.0;
        view.updateAmount(binder, initialJobAmount);

        // Run the code under test
        view.changeJobAmountWhenJobTypeIsChanged(jobtypeSelector, binder);

        // Verify value has changed
        assertEquals(Double.valueOf(0.0), binder.getBean().getAmount(), 0);

        // Run the code under test
        view.changeJobAmountWhenJobTypeIsChanged(jobtypeSelector, binder);

        // Verify value has changed
        assertEquals(Double.valueOf(newAmountValue), binder.getBean().getAmount(), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterJobInDatabase() {
        UkelonnUIProvider provider = getUkelonnServlet().getUkelonnUIProvider();
        VaadinSession.setCurrent(session);
        VaadinRequest request = createMockVaadinRequest("http://localhost:8181/ukelonn/");
        UserView view = new UserView(provider, request);

        // Mock setup
        NativeSelect<TransactionType> jobtypeSelector = mock(NativeSelect.class);
        TransactionType jobType = mock(TransactionType.class);
        when(jobtypeSelector.getValue()).thenReturn(null, jobType);
        double accountBalance = 20;
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(accountBalance);
        view.account = account;

        // Set initial balance
        view.amountAndBalanceBinder.getBean().setBalance(10.0);

        // Call the method under test
        view.registerJobInDatabase(jobtypeSelector);

        // Verify balance hasn't been changed
        assertEquals(Double.valueOf(10.0), view.amountAndBalanceBinder.getBean().getBalance(), 0);

        // Call the method under test
        view.registerJobInDatabase(jobtypeSelector);

        // Verify balance has been changed to the account balance
        assertEquals(Double.valueOf(accountBalance), view.amountAndBalanceBinder.getBean().getBalance(), 0);
    }

}
