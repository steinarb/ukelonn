/*
 * Copyright 2018-2024 Steinar Bang
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

class AccountTest {

    @Test
    void testProperties() {
        var expectedAccountId = 1;
        var expectedUsername = "jad";
        var expectedFirstname = "Jane";
        var expectedLastname = "Doe";
        var expectedBalance = 1;
        var account = Account.with()
            .accountid(expectedAccountId)
            .username(expectedUsername)
            .firstName(expectedFirstname)
            .lastName(expectedLastname)
            .balance(expectedBalance)
            .build();

        assertEquals(expectedAccountId, account.accountId());
        assertEquals(expectedUsername, account.username());
        assertEquals(expectedFirstname, account.firstName());
        assertEquals(expectedLastname, account.lastName());
        assertEquals(expectedBalance, account.balance(), 0.0);
        assertEquals("Jane Doe", account.getFullName());
    }

    @Test
    void testFullName() {
        var account = Account.with()
            .username("jad")
            .firstName("Jane")
            .lastName("Doe")
            .build();

        assertEquals("Jane Doe", account.getFullName());
    }

    @Test
    void testFullNameWhenNoLastName() {
        var account = Account.with()
            .username("jad")
            .firstName("Jane")
            .build();

        assertEquals("Jane", account.getFullName());
    }

    @Test
    void testFullNameWhenNoFirstName() {
        var account = Account.with()
            .username("jad")
            .build();

        assertEquals("jad", account.getFullName());
    }

}
