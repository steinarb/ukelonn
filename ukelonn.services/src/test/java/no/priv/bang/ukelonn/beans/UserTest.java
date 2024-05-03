/*
 * Copyright 2016-2024 Steinar Bang
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
        var bean = User.with().build();
        assertEquals(-1, bean.userId());
        assertEquals("", bean.username());
        assertEquals("", bean.email());
        assertEquals("", bean.firstname());
        assertEquals("", bean.firstname());
        assertEquals(" ", bean.fullname());
    }

    @Test
    void testProperties() {
        var expectedUserId = 1;
        var expectedUsername = "jad";
        var expectedEmail = "jane21@gmail.com";
        var expectedFirstname = "Jane";
        var expectedLastname = "Doe";
        var user = User.with()
            .userId(expectedUserId)
            .username(expectedUsername)
            .email(expectedEmail)
            .firstname(expectedFirstname)
            .lastname(expectedLastname)
            .build();
        assertEquals(expectedUserId, user.userId());
        assertEquals(expectedUsername, user.username());
        assertEquals(expectedEmail, user.email());
        assertEquals(expectedFirstname, user.firstname());
        assertEquals(expectedLastname, user.lastname());
        assertEquals("Jane Doe", user.fullname());

        var newUsername = "jadd";
        var newEmail = "jane2111@gmail.com";
        var newFirstname = "Juliet";
        var newLastname = "Deere";
        user = User.with(user).username(newUsername).build();
        assertEquals(newUsername, user.username());
        user = User.with(user).email(newEmail).build();
        assertEquals(newEmail, user.email());
        user = User.with(user).firstname(newFirstname).build();
        assertEquals(newFirstname, user.firstname());
        assertEquals("Juliet Doe", user.fullname());
        user = User.with(user).lastname(newLastname).build();
        assertEquals(newLastname, user.lastname());
        assertEquals("Juliet Deere", user.fullname());
    }

    @Test
    void testToString() {
        var user = User.with().userId(1).username("jad").email("jane21@gmail.com").firstname("Jane").lastname("Doe").build();
        assertEquals("User[userId=1, username=jad, email=jane21@gmail.com, firstname=Jane, lastname=Doe]", user.toString());
        var userWithNullStrings = User.with().userId(1).username(null).email(null).firstname(null).lastname(null).build();
        assertEquals("User[userId=1, username=null, email=null, firstname=null, lastname=null]", userWithNullStrings.toString());
    }

}
