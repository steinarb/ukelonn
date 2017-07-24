/*
 * Copyright 2016-2017 Steinar Bang
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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static no.priv.bang.ukelonn.impl.UkelonnAdminControllerTest.*;

public class UkelonnControllerTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    /**
     * Verify what happens when no username can be retrieved
     * from Shiro.
     */
    @Test
    public void testNoUsername() {
        UkelonnController ukelonn = new UkelonnController();
        assertNull(ukelonn.getUsername());
        assertEquals(0, ukelonn.getUserId());
        assertEquals("Ikke innlogget", ukelonn.getFornavn());
        assertEquals("", ukelonn.getEtternavn());
        assertEquals(Double.valueOf(0.0), ukelonn.getBalanse());
        assertEquals(0, ukelonn.getAccountId());
        assertEquals(0, ukelonn.getJobTypes().size());
        assertEquals(0, ukelonn.getJobs().size());
    }

    @Test
    public void testFornavn() {
        UkelonnController ukelonn = new UkelonnController();
        ukelonn.setUsername("jod");
        assertEquals("John", ukelonn.getFornavn());
        ukelonn.setFornavn("Kari");
        assertEquals("Kari", ukelonn.getFornavn());
    }

    @Test
    public void testBalanse() {
        UkelonnController ukelonn = new UkelonnController();
        ukelonn.setUsername("jad");
        assertEquals(673.0, ukelonn.getBalanse(), 0.1);
        ukelonn.setBalanse(Double.valueOf(-1));
        assertEquals(Double.valueOf(-1), ukelonn.getBalanse());
    }

    @Test
    public void testJobs() {
        UkelonnController ukelonn = new UkelonnController();
        ukelonn.setUsername("jad");
        assertEquals(10, ukelonn.getJobs().size());
    }

    @Test
    public void testAddJob() {
        try {
            UkelonnController ukelonn = new UkelonnController();

            // Simulate logging in as a non-admin user
            ukelonn.setUsername("jod");

            // Check that the initial values are as expected
            assertEquals(0.0, ukelonn.getBalanse(), 0.1);
            assertEquals(2, ukelonn.getJobs().size());

            // Register a new job
            TransactionType newJobType = ukelonn.getJobTypes().get(0);
            ukelonn.setNewJobType(newJobType);
            ukelonn.setNewJobWages(newJobType.getTransactionAmount());
            ukelonn.registerNewJob(mock(ActionEvent.class));

            // Verify that balance and the job list is modified
            assertEquals(Double.valueOf(35), ukelonn.getBalanse());
            assertEquals(3, ukelonn.getJobs().size());

            // Verify that the job registration form is blanked when
            // the job has been registered
            assertNull(ukelonn.getNewJobType());
            assertEquals(0.0, ukelonn.getNewJobWages(), 0.1);
        } finally {
            dropTestDatabase();
        }
    }

    @Test
    public void testDeleteJobsForUser() {
        try {
            UkelonnController ukelonn = new UkelonnController();

            ukelonn.setUsername("jad");

            // Simulate two checkboxes checked in the jobs data table
            double balanseBeforeDelete = ukelonn.getBalanse();
            List<Transaction> jadJobs = ukelonn.getJobs();
            List<Transaction> jobsWithCheckboxChecked = new ArrayList<Transaction>();
            jobsWithCheckboxChecked.add(jadJobs.get(0));
            jobsWithCheckboxChecked.add(jadJobs.get(3));
            ukelonn.setJobsSelectedForDelete(copyOf(jobsWithCheckboxChecked));

            // Delete the two selected jobs
            ActionEvent event = mock(ActionEvent.class);
            ukelonn.deleteSelectedJobs(event);

            // Expect the same number of jobs after the delete since the total number of jobs is more than 10
            double balanseAfterDelete = ukelonn.getBalanse();
            List<Transaction> jadJobsAfterDelete = ukelonn.getJobs();

            assertNotEquals(balanseBeforeDelete, balanseAfterDelete);
            assertEquals(jadJobs.size(), jadJobsAfterDelete.size());
            // Expected the deleted jobs not to be present in the current jobs list
            assertThat(jadJobsAfterDelete, not(hasItems(jobsWithCheckboxChecked.get(0), jobsWithCheckboxChecked.get(1))));

            // Verify that the delete selection list has been emptied
            assertEquals(0, ukelonn.getJobsSelectedForDelete().size());

            // Corner case: try deleting non-existing transactions (the two transactions already deleted)
            ukelonn.setJobsSelectedForDelete(copyOf(jobsWithCheckboxChecked));
            ukelonn.deleteSelectedJobs(event);

            // Expected the jobs list to be unchanged
            List<Transaction> jadJobsAfterAttemptedDelete = ukelonn.getJobs();
            assertEquals(jadJobsAfterDelete, jadJobsAfterAttemptedDelete);

            // Corner case: setting the selection list to null
            ukelonn.setJobsSelectedForDelete(null);
            ukelonn.deleteSelectedJobs(event);

            // Expected the jobs list to be unchanged
            jadJobsAfterAttemptedDelete = ukelonn.getJobs();
            assertEquals(jadJobsAfterDelete, jadJobsAfterAttemptedDelete);

            // Corner case: setting the selection list to an empty list
            ukelonn.setJobsSelectedForDelete(new ArrayList<Transaction>());
            ukelonn.deleteSelectedJobs(event);

            // Expected the jobs list to be unchanged
            jadJobsAfterAttemptedDelete = ukelonn.getJobs();
            assertEquals(jadJobsAfterDelete, jadJobsAfterAttemptedDelete);

            // Corner case: setting the selection list to an immutable list
            ukelonn.setJobsSelectedForDelete(Collections.unmodifiableList(jobsWithCheckboxChecked));
            ukelonn.deleteSelectedJobs(event);

            // Expected the jobs list to be unchanged (the contents of the immutable list is already deleted
            jadJobsAfterAttemptedDelete = ukelonn.getJobs();
            assertEquals(jadJobsAfterDelete, jadJobsAfterAttemptedDelete);

            // Corner case: selection list combined of non-existing and existing jobs
            Transaction existingJob = jadJobsAfterDelete.get(jadJobsAfterDelete.size() - 1);
            List<Transaction> nonExistingJobsWithExistingJob = copyOf(jobsWithCheckboxChecked);
            nonExistingJobsWithExistingJob.add(existingJob);
            ukelonn.setJobsSelectedForDelete(nonExistingJobsWithExistingJob);
            ukelonn.deleteSelectedJobs(event);

            // Expected the deleted job to be gone from the job list
            jadJobsAfterDelete = ukelonn.getJobs();
            assertThat(jadJobsAfterDelete, not(hasItem(existingJob)));
        } finally {
            restoreTestDatabase();
        }
    }
}
