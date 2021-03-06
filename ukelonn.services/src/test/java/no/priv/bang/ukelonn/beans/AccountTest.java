/*
 * Copyright 2018-2021 Steinar Bang
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

public class AccountTest {

    @Test
    public void testProperties() {
        int expectedAccountId = 1;
        String expectedUsername = "jad";
        String expectedFirstname = "Jane";
        String expectedLastname = "Doe";
        double expectedBalance = 1;
        Account account = Account.with()
            .accountid(expectedAccountId)
            .username(expectedUsername)
            .firstName(expectedFirstname)
            .lastName(expectedLastname)
            .balance(expectedBalance)
            .build();

        assertEquals(expectedAccountId, account.getAccountId());
        assertEquals(expectedUsername, account.getUsername());
        assertEquals(expectedFirstname, account.getFirstName());
        assertEquals(expectedLastname, account.getLastName());
        assertEquals(expectedBalance, account.getBalance(), 0.0);
        assertEquals("Jane Doe", account.getFullName());

        account.setBalance(2);
        assertEquals(2.0, account.getBalance(), 0.0);
    }

    @Test
    public void testEquals() {
        Account account = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(1).build();
        Account accountDifferentAccountId = Account.with().accountid(2).username("jad").firstName("Jane").lastName("Doe").balance(1).build();
        assertNotEquals(account, accountDifferentAccountId);
        Account accountDifferentUsername = Account.with().accountid(1).username("jadd").firstName("Jane").lastName("Doe").balance(1).build();
        assertNotEquals(account, accountDifferentUsername);
        Account accountDifferentFirstname = Account.with().accountid(1).username("jad").firstName("Julie").lastName("Doe").balance(1).build();
        assertNotEquals(account, accountDifferentFirstname);
        Account accountDifferentLastname = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Deer").balance(1).build();
        assertNotEquals(account, accountDifferentLastname);
        Account equalAccount = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(1).build();
        assertEquals(account, equalAccount);
        assertEquals(account, account);
        Account accountWithNullStrings = Account.with().accountid(1).balance(1).build();
        assertNotEquals(accountWithNullStrings, account);
        assertNotEquals(account, null);
        assertNotEquals(account, "");
    }

    @Test
    public void testToString() {
        Account account = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(1).build();
        assertEquals("Account [getAccountId()=1, getUsername()=jad, getFirstName()=Jane, getLastName()=Doe]", account.toString());
        Account accountWithNullStrings = Account.with().accountid(1).build();
        assertEquals("Account [getAccountId()=1, getUsername()=null, getFirstName()=null, getLastName()=null]", accountWithNullStrings.toString());
    }

}
