/*
 * Copyright 2018-2021 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

public class AdminPaymenttypeTest {

    @Test
    public void testModifyPaymenttype() {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Find a payment type to modify
        List<TransactionType> paymenttypes = getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(1);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();
        when(ukelonn.modifyPaymenttype(paymenttype)).thenReturn(Arrays.asList(paymenttype));

        // Run the method that is to be tested
        List<TransactionType> updatedPaymenttypes = resource.modify(paymenttype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testModifyPaymenttypeFailure() throws Exception {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;

        // Inject a fake OSGi log service
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;
        ukelonn.setLogservice(logservice);

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create a non-existing payment type
        TransactionType paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the payment type in the database, which should cause an
        // "500 Internal Server Error" exception
        resource.modify(paymenttype);
        fail("Should never get here!");
    }

    @Test
    public void testCreatePaymenttype() {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Get the list of payment types before adding a new job type
        List<TransactionType> originalPaymenttypes = getPaymenttypes();
        List<TransactionType> newPaymenttypes = new ArrayList<>(originalPaymenttypes);

        // Create new payment type
        TransactionType paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();
        newPaymenttypes.add(paymenttype);
        when(ukelonn.createPaymenttype(any())).thenReturn(newPaymenttypes);

        // Add the payment type to the database
        List<TransactionType> updatedPaymenttypes = resource.create(paymenttype);

        // Verify that a new jobtype has been added
        assertThat(updatedPaymenttypes.size()).isGreaterThan(originalPaymenttypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testCreatePaymenttypeFailure() throws Exception {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();


        // Inject fake OSGi service UkelonnService
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;

        // Inject a fake OSGi log service
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);
        resource.logservice = logservice;

        // Create a mock database that throws exceptions and inject it
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create new payment type
        TransactionType paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the jobtype in the database, which should cause an
        // "500 Internal Server Error" exception
        resource.create(paymenttype);
        fail("Should never get here!");
    }

}
