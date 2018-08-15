/*
 * Copyright 2018 Steinar Bang
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
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.impl.UkelonnServiceProvider;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class AdminPaymenttypeTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }


    @Test
    public void testModifyPaymenttype() {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        // Find a payment type to modify
        List<TransactionType> paymenttypes = getUkelonnServiceSingleton().getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(0);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype.setTransactionAmount(originalAmount + 1);

        // Run the method that is to be tested
        List<TransactionType> updatedPaymenttypes = resource.modify(paymenttype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testModifyJobtypeFailure() {
        // Create the resource that is to be tested
        AdminPaymenttype resource = new AdminPaymenttype();

        // Inject fake OSGi service UkelonnService
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;

        // Inject a fake OSGi log service
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a non-existing payment type
        TransactionType paymenttype = new TransactionType(-2001, "Bar", 0.0, false, true);

        // Try update the payment type in the database, which should cause an
        // "500 Internal Server Error" exception
        resource.modify(paymenttype);
        fail("Should never get here!");
    }

}
