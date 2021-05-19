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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

public class AdminJobsTest {

    @Test
    public void testDeleteAllJobsOfUser() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Set up the POST argument for the delete
        Account account = getJodAccount();
        List<Transaction> jobs = getJodJobs();
        List<Integer> jobIds = Arrays.asList(jobs.get(0).getId(), jobs.get(1).getId());
        AccountWithJobIds accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(jobIds).build();

        // Do the delete
        List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);
        assertEquals(0, jobsAfterDelete.size());
    }

    @Test
    public void testDeleteSomeJobsOfUser() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteJobsFromAccount(anyInt(), anyList())).thenReturn(getFirstJodJob());
        resource.ukelonn = ukelonn;

        // Set up the POST argument for the delete
        Account account = getJodAccount();
        List<Transaction> jobs = getJodJobs();
        List<Integer> idsOfJobsToDelete = Arrays.asList(jobs.get(0).getId());
        AccountWithJobIds accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();

        // Do the delete
        List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);
        assertEquals(1, jobsAfterDelete.size());
    }

    @Test
    public void verifyDeletingNoJobsOfUserHasNoEffect() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteJobsFromAccount(anyInt(), anyList())).thenReturn(getJodJobs());
        resource.ukelonn = ukelonn;

        Account account = getJodAccount();

        // Delete with an empty argument
        List<Integer> idsOfJobsToDelete = Collections.emptyList();
        AccountWithJobIds accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();
        List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);

        // Verify that nothing has been deleted
        assertEquals(2, jobsAfterDelete.size());
    }

    @Test(expected=InternalServerErrorException.class)
    public void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a mock database that will fail during query setup
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        // Create the jersey resource that is to be tested
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi services
        resource.ukelonn = ukelonn;
        resource.setLogservice(logservice);

        // trying to delete jobs here will throw a Jersey Internal Error exception
        Account account = getJodAccount();
        List<Integer> idsOfJobsToDelete = Arrays.asList(1);
        AccountWithJobIds accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();
        resource.delete(accountWithJobIds);
        fail("Should never get here!");
    }

}
