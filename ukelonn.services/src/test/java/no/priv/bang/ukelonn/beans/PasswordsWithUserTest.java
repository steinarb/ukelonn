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

class PasswordsWithUserTest {

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
        var password = "zecret";
        var password2 = "zecret2";
        var passwords = PasswordsWithUser.with().user(user).password(password).password2(password2).build();
        assertEquals("jad", passwords.user().username());
        assertEquals(password, passwords.password());
        assertEquals(password2, passwords.password2());
        var user2 = User.with().build();
        passwords = PasswordsWithUser.with(passwords).user(user2).build();
        assertEquals(user2, passwords.user());
    }

}
