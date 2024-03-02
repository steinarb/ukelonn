/*
 * Copyright 2018-2024 Steinar Bang
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

class AdminPaymenttypeTest {

    @Test
    void testModifyPaymenttype() {
        // Create the resource that is to be tested
        var resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Find a payment type to modify
        var paymenttypes = getPaymenttypes();
        var paymenttype = paymenttypes.get(1);
        var originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();
        when(ukelonn.modifyPaymenttype(paymenttype)).thenReturn(Arrays.asList(paymenttype));

        // Run the method that is to be tested
        var updatedPaymenttypes = resource.modify(paymenttype);

        // Verify that the updated amount is larger than the original amount
        var updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testModifyPaymenttypeFailure() throws Exception {
        // Create the resource that is to be tested
        var resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        var ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;

        // Inject a fake OSGi log service
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        ukelonn.setLogservice(logservice);

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create a non-existing payment type
        var paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the payment type in the database, which should cause an
        // "500 Internal Server Error" exception
        assertThrows(InternalServerErrorException.class, () -> resource.modify(paymenttype));
    }

    @Test
    void testCreatePaymenttype() {
        // Create the resource that is to be tested
        var resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Get the list of payment types before adding a new job type
        var originalPaymenttypes = getPaymenttypes();
        var newPaymenttypes = new ArrayList<>(originalPaymenttypes);

        // Create new payment type
        var paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();
        newPaymenttypes.add(paymenttype);
        when(ukelonn.createPaymenttype(any())).thenReturn(newPaymenttypes);

        // Add the payment type to the database
        var updatedPaymenttypes = resource.create(paymenttype);

        // Verify that a new jobtype has been added
        assertThat(updatedPaymenttypes).hasSizeGreaterThan(originalPaymenttypes.size());
    }

    @Test
    void testCreatePaymenttypeFailure() throws Exception {
        // Create the resource that is to be tested
        var resource = new AdminPaymenttype();


        // Inject fake OSGi service UkelonnService
        var ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;

        // Inject a fake OSGi log service
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);
        resource.setLogservice(logservice);

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setDataSource(datasource);

        // Create new payment type
        var paymenttype = TransactionType.with()
            .id(-2001)
            .transactionTypeName("Bar")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Try update the jobtype in the database, which should cause an
        // "500 Internal Server Error" exception
        assertThrows(InternalServerErrorException.class, () -> resource.create(paymenttype));
    }

}
