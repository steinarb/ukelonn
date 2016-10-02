package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.faces.event.ActionEvent;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.ukelonn.bundle.test.db.UkelonnDatabaseProvider;

public class UkelonnControllerTest {

    @BeforeClass
    public static void setupFakeOsgiServices() {
        UkelonnServiceProvider ukelonnServiceSingleton = new UkelonnServiceProvider();
        UkelonnDatabaseProvider ukelonnDatabaseProvider = new UkelonnDatabaseProvider();
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        ukelonnDatabaseProvider.setDataSourceFactory(derbyDataSourceFactory);
        ukelonnServiceSingleton.setUkelonnDatabase(ukelonnDatabaseProvider.get());
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
        assertEquals(33, ukelonn.getJobs().size());
    }

    @Test
    public void testAddJob() {
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
    }
}
