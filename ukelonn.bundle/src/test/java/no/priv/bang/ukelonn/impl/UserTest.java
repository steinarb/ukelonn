package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

    @Test
    public void testEquals() {
        User user = new User(1, "jad", "jane21@gmail.com", "1ad", "Jane", "Doe");
        User userDifferentUserId = new User(2, "jad", "jane21@gmail.com", "1ad", "Jane", "Doe");
        assertNotEquals(user, userDifferentUserId);
        User userDifferentUsername = new User(1, "jadd", "jane21@gmail.com", "1ad", "Jane", "Doe");
        assertNotEquals(user, userDifferentUsername);
        User userDifferentEmail = new User(1, "jad", "jane22@gmail.com", "1ad", "Jane", "Doe");
        assertNotEquals(user, userDifferentEmail);
        User userDifferentPassword = new User(1, "jad", "jane21@gmail.com", "2ad", "Jane", "Doe");
        assertNotEquals(user, userDifferentPassword);
        User userDifferentFirstname = new User(1, "jad", "jane21@gmail.com", "1ad", "Julie", "Doe");
        assertNotEquals(user, userDifferentFirstname);
        User userDifferentLastname = new User(1, "jad", "jane21@gmail.com", "1ad", "Jane", "Deer");
        assertNotEquals(user, userDifferentLastname);
        User equalUser = new User(1, "jad", "jane21@gmail.com", "1ad", "Jane", "Doe");
        assertEquals(user, equalUser);
        assertEquals(user, user);
        User userWithNullStrings = new User(1, null, null, null, null, null);
        assertNotEquals(userWithNullStrings, user);
        assertNotEquals(user, null);
        assertNotEquals(user, "");
    }

}
