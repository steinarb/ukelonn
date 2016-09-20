package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.faces.event.ActionEvent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

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
    	assertEquals("Ikke innlogget!", ukelonn.getFornavn());
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
        assertEquals(Double.valueOf(0), ukelonn.getBalanse());
        ukelonn.setBalanse(Double.valueOf(-1));
        assertEquals(Double.valueOf(-1), ukelonn.getBalanse());
    }

    @Test
    public void testJobs() {
        UkelonnController ukelonn = new UkelonnController();
        ukelonn.setUsername("jad");
        assertEquals(3, ukelonn.getJobs().size());
    }

    @Test
    public void testAddJob() {
    	try {
            UkelonnController ukelonn = new UkelonnController();
            ukelonn.setUsername("jod");
            assertEquals(Double.valueOf(0), ukelonn.getBalanse());
            assertEquals(2, ukelonn.getJobs().size());
            TransactionType newJobType = ukelonn.getJobTypes().get(0);
            ukelonn.setNewJobType(newJobType);
            ukelonn.setNewJobWages(newJobType.getTransactionAmount());
            ukelonn.registerNewJob(mock(ActionEvent.class));
            assertEquals(Double.valueOf(35), ukelonn.getBalanse());
            assertEquals(3, ukelonn.getJobs().size());
    	} finally {
            dropTestDatabase();
    	}
    }
}
