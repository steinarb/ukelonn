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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class AdminJobtypeTest {

    @Test
    public void testModifyJobtype() {
        // Create the resource that is to be tested
        AdminJobtype resource = new AdminJobtype();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Find a jobtype to modify
        List<TransactionType> jobtypes = getJobtypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype = TransactionType.with(jobtype).transactionAmount(originalAmount + 1).build();
        when(ukelonn.modifyJobtype(any())).thenReturn(Arrays.asList(jobtype));

        // Run the method that is to be tested
        List<TransactionType> updatedJobtypes = resource.modify(jobtype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testModifyJobtypeFailure() throws Exception {
        // Create the resource that is to be tested
        AdminJobtype resource = new AdminJobtype();

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

        // Create a non-existing jobtype
        TransactionType jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an
        // "500 Internal Server Error" exception
        resource.modify(jobtype);
        fail("Should never get here!");
    }

    @Test
    public void testCreateJobtype() {
        // Create the resource that is to be tested
        AdminJobtype resource = new AdminJobtype();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Get the list of jobtypes before adding a new job type
        List<TransactionType> originalJobtypes = getJobtypes();
        List<TransactionType> newjobtypes = new ArrayList<>(originalJobtypes);

        // Create new jobtyoe
        TransactionType jobtype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Skrubb badegolv")
            .transactionAmount(200.0)
            .transactionIsWork(true)
            .build();
        newjobtypes.add(jobtype);
        when(ukelonn.createJobtype(jobtype)).thenReturn(newjobtypes);

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = resource.create(jobtype);

        // Verify that a new jobtype has been added
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testCreateJobtypeFailure() throws Exception {
        // Create the resource that is to be tested
        AdminJobtype resource = new AdminJobtype();


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

        // Create a new jobtype
        TransactionType jobtype = TransactionType.with()
            .id(-2000)
            .transactionTypeName("Foo")
            .transactionAmount(3.14)
            .transactionIsWork(true)
            .build();

        // Try update the jobtype in the database, which should cause an
        // "500 Internal Server Error" exception
        resource.create(jobtype);
        fail("Should never get here!");
    }

}
