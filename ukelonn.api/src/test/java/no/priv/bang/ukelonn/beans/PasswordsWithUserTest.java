/*
 * Copyright 2016-2017 Steinar Bang
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

public class PasswordsWithUserTest {

    @Test
    public void testProperties() {
        int expectedUserId = 1;
        String expectedUsername = "jad";
        String expectedEmail = "jane21@gmail.com";
        String expectedFirstname = "Jane";
        String expectedLastname = "Doe";
        User user = new User(expectedUserId, expectedUsername, expectedEmail, expectedFirstname, expectedLastname);
        String password = "zecret";
        String password2 = "zecret2";
        PasswordsWithUser passwords = new PasswordsWithUser(user, password, password2);
        assertEquals("jad", passwords.getUser().getUsername());
        assertEquals(password, passwords.getPassword());
        assertEquals(password2, passwords.getPassword2());
        User user2 = new User();
        passwords.setUser(user2);
        assertEquals(user2, passwords.getUser());
    }

    @Test
    public void testNoArgsConstructor() {
        PasswordsWithUser passwords = new PasswordsWithUser();
        assertNull(passwords.getUser());
        assertEquals("", passwords.getPassword());
        assertEquals("", passwords.getPassword2());
    }

}
