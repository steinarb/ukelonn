package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UkelonnAdminControllerTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	releaseFakeOsgiServices();
    }

    @Test
    public void testNoAdministratorUsername() {
        UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

        assertNull(ukelonnAdmin.getAdministratorUsername());
        assertEquals(0, ukelonnAdmin.getAdministratorUserId());
        assertEquals(0, ukelonnAdmin.getAdministratorId());
        assertEquals("Ikke innlogget", ukelonnAdmin.getAdministratorFornavn());
        assertEquals("", ukelonnAdmin.getAdministratorEtternavn());
    }

    @Test
    public void testUserNotAdministrator() {
        UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

        // Set non-administrator user
        ukelonnAdmin.setAdministratorUsername("jad");

        assertEquals("jad", ukelonnAdmin.getAdministratorUsername());
        assertEquals(0, ukelonnAdmin.getAdministratorUserId());
        assertEquals(0, ukelonnAdmin.getAdministratorId());
        assertEquals("Ikke innlogget", ukelonnAdmin.getAdministratorFornavn());
        assertEquals("", ukelonnAdmin.getAdministratorEtternavn());
    }

    @Test
    public void testUserIsAdministrator() {
        UkelonnAdminController ukelonnAdmin = new UkelonnAdminController();

        // Set non-administrator user
        ukelonnAdmin.setAdministratorUsername("on");

        assertEquals("on", ukelonnAdmin.getAdministratorUsername());
        assertEquals(1, ukelonnAdmin.getAdministratorUserId());
        assertEquals(1, ukelonnAdmin.getAdministratorId());
        assertEquals("Ola", ukelonnAdmin.getAdministratorFornavn());
        assertEquals("Nordmann", ukelonnAdmin.getAdministratorEtternavn());
    }

}
