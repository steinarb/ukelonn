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
package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.getAccountInfoFromDatabase;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;

public class UkelonnServiceProviderTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetAccounts() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        List<Account> accounts = provider.getAccounts();
        assertEquals(2, accounts.size());
    }

    @Test
    public void testGetAccount() {
        UkelonnServiceProvider provider = getUkelonnServiceSingleton();
        Account account = provider.getAccount("jad");
        assertEquals("jad", account.getUsername());
    }

    @Test
    public void testGetJobs() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
        List<Transaction> jobs = ukelonn.getJobs(account.getAccountId());
        assertEquals(10, jobs.size());
    }

    @Test
    public void testGetPayments() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        String username = "jad";
        Account account = getAccountInfoFromDatabase(getClass(), getUkelonnServiceSingleton(), username);
        List<Transaction> payments = ukelonn.getPayments(account.getAccountId());
        assertEquals(10, payments.size());
    }

    @Test
    public void testGetPaymenttypes() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        assertEquals(2, paymenttypes.size());
    }

    @Test
    public void testRegisterPayment() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Create the request
        Account account = ukelonn.getAccount("jad");
        double originalBalance = account.getBalance();
        List<TransactionType> paymenttypes = ukelonn.getPaymenttypes();
        PerformedTransaction payment = new PerformedTransaction(account, paymenttypes.get(0).getId(), account.getBalance());

        // Run the method under test
        Account result = ukelonn.registerPayment(payment);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @Test
    public void testModifyJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Find a jobtyoe
        List<TransactionType> jobtypes = ukelonn.getJobTypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype.setTransactionAmount(originalAmount + 1);

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.modifyJobtype(jobtype);

        // Verify that the updated amount is larger than the original amount
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testModifyJobtypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a non-existing jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        ukelonn.modifyJobtype(jobtype);
        fail("Should never get here!");
    }

    @Test
    public void testCreateJobtype() {
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Get the list of jobtypes before adding a new job type
        List<TransactionType> originalJobtypes = ukelonn.getJobTypes();

        // Create new jobtyoe
        TransactionType jobtype = new TransactionType(-1, "Skrubb badegolv", 200.0, true, false);

        // Update the job type in the database
        List<TransactionType> updatedJobtypes = ukelonn.createJobtype(jobtype);

        // Verify that a new jobtype has been added
        assertThat(updatedJobtypes.size()).isGreaterThan(originalJobtypes.size());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testCreateJobtypeFailure() {
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a new jobtype
        TransactionType jobtype = new TransactionType(-2000, "Foo", 3.14, true, false);

        // Try update the jobtype in the database, which should cause an exception
        ukelonn.createJobtype(jobtype);
        fail("Should never get here!");
    }

}
