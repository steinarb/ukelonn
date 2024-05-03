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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

class AdminJobsTest {

    @Test
    void testDeleteAllJobsOfUser() {
        var resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Set up the POST argument for the delete
        var account = getJodAccount();
        var jobs = getJodJobs();
        var jobIds = Arrays.asList(jobs.get(0).id(), jobs.get(1).id());
        var accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(jobIds).build();

        // Do the delete
        var jobsAfterDelete = resource.delete(accountWithJobIds);
        assertEquals(0, jobsAfterDelete.size());
    }

    @Test
    void testDeleteSomeJobsOfUser() {
        var resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteJobsFromAccount(anyInt(), anyList())).thenReturn(getFirstJodJob());
        resource.ukelonn = ukelonn;

        // Set up the POST argument for the delete
        var account = getJodAccount();
        var jobs = getJodJobs();
        var idsOfJobsToDelete = Arrays.asList(jobs.get(0).id());
        var accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();

        // Do the delete
        var jobsAfterDelete = resource.delete(accountWithJobIds);
        assertEquals(1, jobsAfterDelete.size());
    }

    @Test
    void verifyDeletingNoJobsOfUserHasNoEffect() {
        var resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteJobsFromAccount(anyInt(), anyList())).thenReturn(getJodJobs());
        resource.ukelonn = ukelonn;

        var account = getJodAccount();

        // Delete with an empty argument
        List<Integer> idsOfJobsToDelete = Collections.emptyList();
        var accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();
        var jobsAfterDelete = resource.delete(accountWithJobIds);

        // Verify that nothing has been deleted
        assertEquals(2, jobsAfterDelete.size());
    }

    @Test
    void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
        var ukelonn = new UkelonnServiceProvider();
        var logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a mock database that will fail during query setup
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setDataSource(datasource);

        // Create the jersey resource that is to be tested
        var resource = new AdminJobs();

        // Inject fake OSGi services
        resource.ukelonn = ukelonn;
        resource.setLogservice(logservice);

        // trying to delete jobs here will throw a Jersey Internal Error exception
        var account = getJodAccount();
        var idsOfJobsToDelete = Arrays.asList(1);
        var accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(idsOfJobsToDelete).build();
        assertThrows(InternalServerErrorException.class, () -> resource.delete(accountWithJobIds));
    }

}
