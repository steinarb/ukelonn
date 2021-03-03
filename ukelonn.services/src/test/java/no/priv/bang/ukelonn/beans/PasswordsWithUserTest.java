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
        User user = User.with()
            .userId(expectedUserId)
            .username(expectedUsername)
            .email(expectedEmail)
            .firstname(expectedFirstname)
            .lastname(expectedLastname)
            .build();
        String password = "zecret";
        String password2 = "zecret2";
        PasswordsWithUser passwords = PasswordsWithUser.with().user(user).password(password).password2(password2).build();
        assertEquals("jad", passwords.getUser().getUsername());
        assertEquals(password, passwords.getPassword());
        assertEquals(password2, passwords.getPassword2());
        User user2 = User.with().build();
        passwords.setUser(user2);
        assertEquals(user2, passwords.getUser());
    }

}
