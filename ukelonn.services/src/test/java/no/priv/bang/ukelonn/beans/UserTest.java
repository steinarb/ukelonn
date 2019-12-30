/*
 * Copyright 2016-2019 Steinar Bang
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
package no.priv.bang.ukelonn.beans;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

    @Test
    public void testNoArgConstructor() {
        User bean = new User();
        assertEquals(-1, bean.getUserId());
        assertEquals("", bean.getUsername());
        assertEquals("", bean.getEmail());
        assertEquals("", bean.getFirstname());
        assertEquals("", bean.getLastname());
        assertEquals(" ", bean.getFullname());
    }

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
    public void testToString() {
        User user = new User(1, "jad", "jane21@gmail.com", "Jane", "Doe");
        assertEquals("User [userId=1, username=jad, email=jane21@gmail.com, firstname=Jane, lastname=Doe]", user.toString());
        User userWithNullStrings = new User(1, null, null, null, null);
        assertEquals("User [userId=1, username=null, email=null, firstname=null, lastname=null]", userWithNullStrings.toString());
    }

}
