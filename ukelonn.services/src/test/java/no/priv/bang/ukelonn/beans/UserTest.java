/*
 * Copyright 2016-2021 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testNoArgConstructor() {
        User bean = User.with().build();
        assertEquals(-1, bean.getUserId());
        assertEquals("", bean.getUsername());
        assertEquals("", bean.getEmail());
        assertEquals("", bean.getFirstname());
        assertEquals("", bean.getLastname());
        assertEquals(" ", bean.getFullname());
    }

    @Test
    void testProperties() {
        int expectedUserId = 1;
        String expectedUsername = "jad";
        String expectedEmail = "jane21@gmail.com";
        String expectedFirstname = "Jane";
        String expectedLastname = "Doe";
        User user = User.with()
            .userId(expectedUserId)
            .username(expectedUsername)
            .email(expectedEmail)
            .firstname(expectedFirstname)
            .lastname(expectedLastname)
            .build();
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
        user = User.with(user).username(newUsername).build();
        assertEquals(newUsername, user.getUsername());
        user = User.with(user).email(newEmail).build();
        assertEquals(newEmail, user.getEmail());
        user = User.with(user).firstname(newFirstname).build();
        assertEquals(newFirstname, user.getFirstname());
        assertEquals("Juliet Doe", user.getFullname());
        user = User.with(user).lastname(newLastname).build();
        assertEquals(newLastname, user.getLastname());
        assertEquals("Juliet Deere", user.getFullname());
    }

    @Test
    void testToString() {
        User user = User.with().userId(1).username("jad").email("jane21@gmail.com").firstname("Jane").lastname("Doe").build();
        assertEquals("User [userId=1, username=jad, email=jane21@gmail.com, firstname=Jane, lastname=Doe]", user.toString());
        User userWithNullStrings = User.with().userId(1).username(null).email(null).firstname(null).lastname(null).build();
        assertEquals("User [userId=1, username=null, email=null, firstname=null, lastname=null]", userWithNullStrings.toString());
    }

}
