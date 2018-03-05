package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

    @Test
    public void testProperties() {
        int expectedUserId = 1;
        String expectedUsername = "jad";
        String expectedEmail = "jane21@gmail.com";
        String expectedFirstname = "Jane";
        String expectedLastname = "Doe";
        User user = new User(expectedUserId, expectedUsername, expectedEmail, expectedFirstname, expectedLastname);
        assertEquals(expectedUserId, user.getUserId());
        assertEquals(expectedUsername, user.getUsername());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedFirstname, user.getFirstname());
        assertEquals(expectedLastname, user.getLastname());
        assertEquals("Jane Doe", user.getFullname());

        String newUsername = "jadd";
        String newEmail = "jane2111@gmail.com";
        String newFirstname = "Juliet";
        String newLastname = "Deere";
        user.setUsername(newUsername);
        assertEquals(newUsername, user.getUsername());
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());
        user.setFirstname(newFirstname);
        assertEquals(newFirstname, user.getFirstname());
        assertEquals("Juliet Doe", user.getFullname());
        user.setLastname(newLastname);
        assertEquals(newLastname, user.getLastname());
        assertEquals("Juliet Deere", user.getFullname());
    }

    @Test
    public void testEquals() {
        User user = new User(1, "jad", "jane21@gmail.com", "Jane", "Doe");
        User userDifferentUserId = new User(2, "jad", "jane21@gmail.com", "Jane", "Doe");
        assertNotEquals(user, userDifferentUserId);
        User userDifferentUsername = new User(1, "jadd", "jane21@gmail.com", "Jane", "Doe");
        assertNotEquals(user, userDifferentUsername);
        User userDifferentEmail = new User(1, "jad", "jane22@gmail.com", "Jane", "Doe");
        assertNotEquals(user, userDifferentEmail);
        User userDifferentFirstname = new User(1, "jad", "jane21@gmail.com", "Julie", "Doe");
        assertNotEquals(user, userDifferentFirstname);
        User userDifferentLastname = new User(1, "jad", "jane21@gmail.com", "Jane", "Deer");
        assertNotEquals(user, userDifferentLastname);
        User equalUser = new User(1, "jad", "jane21@gmail.com", "Jane", "Doe");
        assertEquals(user, equalUser);
        assertEquals(user, user);
        User userWithNullStrings = new User(1, null, null, null, null);
        assertNotEquals(userWithNullStrings, user);
        assertNotEquals(user, null);
        assertNotEquals(user, "");
    }

    @Test
    public void testHashCode() {
        User user = new User(1, "jad", "jane21@gmail.com", "Jane", "Doe");
        assertEquals(521560053, user.hashCode());
        User userWithNullStrings = new User(1, null, null, null, null);
        assertEquals(28629182, userWithNullStrings.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User(1, "jad", "jane21@gmail.com", "Jane", "Doe");
        assertEquals("User [userId=1, username=jad, email=jane21@gmail.com, firstname=Jane, lastname=Doe]", user.toString());
        User userWithNullStrings = new User(1, null, null, null, null);
        assertEquals("User [userId=1, username=null, email=null, firstname=null, lastname=null]", userWithNullStrings.toString());
    }

}
