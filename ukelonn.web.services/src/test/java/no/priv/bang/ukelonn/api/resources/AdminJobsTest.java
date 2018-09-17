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

import static no.priv.bang.ukelonn.backend.CommonDatabaseMethods.getAccountInfoFromDatabase;
import static no.priv.bang.ukelonn.testutils.TestUtils.getUkelonnServiceSingleton;
import static no.priv.bang.ukelonn.testutils.TestUtils.releaseFakeOsgiServices;
import static no.priv.bang.ukelonn.testutils.TestUtils.restoreTestDatabase;
import static no.priv.bang.ukelonn.testutils.TestUtils.setupFakeOsgiServices;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

public class AdminJobsTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testDeleteAllJobsOfUser() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        resource.ukelonn = ukelonn;

        try {
            // Set up the POST argument for the delete
            String username = "jod";
            Account account = getAccount(username);
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Integer> jobIds = Arrays.asList(jobs.get(0).getId(), jobs.get(1).getId());
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, jobIds);

            // Do the delete
            List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);
            assertEquals(0, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void testDeleteSomeJobsOfUser() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        resource.ukelonn = ukelonn;

        try {
            // Set up the POST argument for the delete
            String username = "jod";
            Account account = getAccount(username);
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Integer> idsOfJobsToDelete = Arrays.asList(jobs.get(0).getId());
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, idsOfJobsToDelete);

            // Do the delete
            List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);
            assertEquals(1, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyDeletingNoJobsOfUserHasNoEffect() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        resource.ukelonn = ukelonn;

        try {
            String username = "jod";
            Account account = getAccount(username);

            // Check preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());

            // Delete with an empty argument
            List<Integer> idsOfJobsToDelete = Collections.emptyList();
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, idsOfJobsToDelete);
            List<Transaction> jobsAfterDelete = resource.delete(accountWithJobIds);

            // Verify that nothing has been deleted
            assertEquals(2, jobsAfterDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyThatTryingToDeletePaymentsAsJobsWillDoNothing() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        resource.ukelonn = ukelonn;

        try {
            String username = "jod";
            Account account = getAccount(username);

            // Check the preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
            assertEquals(1, payments.size());

            // Try deleting the payment as a job
            List<Integer> idsOfJobsToDelete = Arrays.asList(payments.get(0).getId());
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, idsOfJobsToDelete);
            List<Transaction> jobsAfterAttemptedDelete = resource.delete(accountWithJobIds);

            // Verify that both the jobs and payments are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            List<Transaction> paymentsAfterAttemptedDelete = ukelonn.getPayments(account.getAccountId());
            assertEquals(1, paymentsAfterAttemptedDelete.size());
        } finally {
            restoreTestDatabase();
        }
    }

    @Test
    public void verifyThatTryingToDeleteJobsOfDifferentAccountWillDoNothing() {
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        resource.ukelonn = ukelonn;

        try {
            String username = "jod";
            Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
            String otherUsername = "jad";
            Account otherAccount = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), otherUsername);

            // Check the preconditions
            List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
            assertEquals(2, jobs.size());
            List<Transaction> otherAccountJobs = ukelonn.getJobs(otherAccount.getAccountId());
            assertEquals(10, otherAccountJobs.size());

            // Try deleting the payment as a job
            List<Integer> idsOfJobsToDelete = Arrays.asList(otherAccountJobs.get(0).getId(), otherAccountJobs.get(1).getId(), otherAccountJobs.get(2).getId(), otherAccountJobs.get(3).getId(), otherAccountJobs.get(4).getId(), otherAccountJobs.get(5).getId(), otherAccountJobs.get(6).getId(), otherAccountJobs.get(7).getId(), otherAccountJobs.get(8).getId(), otherAccountJobs.get(9).getId());
            AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, idsOfJobsToDelete);
            List<Transaction> jobsAfterAttemptedDelete = resource.delete(accountWithJobIds);

            // Verify that both the account's jobs and and the other account's jobs are unaffected
            assertEquals(2, jobsAfterAttemptedDelete.size());
            List<Transaction> otherAccountsJobsAfterAttemptedDelete = ukelonn.getJobs(otherAccount.getAccountId());
            assertThat(otherAccountsJobsAfterAttemptedDelete).containsAll(otherAccountJobs);
        } finally {
            restoreTestDatabase();
        }
    }

    @Test(expected=InternalServerErrorException.class)
    public void verifyExceptionIsThrownWhenFailingToSetDeleteJobParameter() throws Exception {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Create a mock database that will fail during query setup
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        when(database.prepareStatement(anyString())).thenReturn(statement);
        ukelonn.setUkelonnDatabase(database);

        // Create the jersey resource that is to be tested
        AdminJobs resource = new AdminJobs();

        // Inject fake OSGi services
        resource.ukelonn = ukelonn;
        resource.logservice = logservice;

        // trying to delete jobs here will throw a Jersey Internal Error exception
        String username = "jod";
        Account account = getAccount(username);
        List<Integer> idsOfJobsToDelete = Arrays.asList(1);
        AccountWithJobIds accountWithJobIds = new AccountWithJobIds(account, idsOfJobsToDelete);
        resource.delete(accountWithJobIds);
        fail("Should never get here!");
    }

    Account getAccount(String username) {
        return getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
    }

}
