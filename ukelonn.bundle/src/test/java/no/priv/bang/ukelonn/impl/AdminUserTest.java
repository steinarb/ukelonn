package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AdminUserTest {

    @Test
    public void testEquals() {
        AdminUser user = new AdminUser("jad", 1, 1, "Jane", "Doe");
        AdminUser userDifferentAdministratorId = new AdminUser("jad", 1, 2, "Jane", "Doe");
        assertNotEquals(user, userDifferentAdministratorId);
        AdminUser userDifferentUserId = new AdminUser("jad", 2, 1, "Jane", "Doe");
        assertNotEquals(user, userDifferentUserId);
        AdminUser userDifferentUsername = new AdminUser("jadd", 1, 1, "Jane", "Doe");
        assertNotEquals(user, userDifferentUsername);
        AdminUser userDifferentFirstname = new AdminUser("jad", 1, 1, "Julie", "Doe");
        assertNotEquals(user, userDifferentFirstname);
        AdminUser userDifferentLastname = new AdminUser("jad", 1, 1, "Jane", "Deer");
        assertNotEquals(user, userDifferentLastname);
        AdminUser equalUser = new AdminUser("jad", 1, 1, "Jane", "Doe");
        assertEquals(user, equalUser);
        assertEquals(user, user);
        AdminUser userWithNullStrings = new AdminUser(null, 1, 1, null, null);
        assertNotEquals(userWithNullStrings, user);
        assertNotEquals(user, null);
        assertNotEquals(user, "");
    }

}
