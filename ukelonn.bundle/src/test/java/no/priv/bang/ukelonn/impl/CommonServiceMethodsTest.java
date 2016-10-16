package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;

public class CommonServiceMethodsTest {

    @Before
    public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    @AfterClass
    static public void completeCleanup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    @Test(expected=RuntimeException.class)
    public void testConnectionCheckNoConnection() {
        UkelonnService service = CommonServiceMethods.connectionCheck(getClass());
        assertNull(service);
    }

    @Test
    public void testConnectionCheck() {
        setupFakeOsgiServices();
        UkelonnService service = CommonServiceMethods.connectionCheck(getClass());
        assertNotNull(service);
    }

}
