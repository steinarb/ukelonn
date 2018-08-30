/*
 * Copyright 2018 Steinar Bang
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
        int expectedUserId = 1;
        String expectedUsername = "jad";
        String expectedFirstname = "Jane";
        String expectedLastname = "Doe";
        double expectedBalance = 1;
        Account account = new Account(expectedAccountId, expectedUserId, expectedUsername, expectedFirstname, expectedLastname, expectedBalance);

        assertEquals(expectedAccountId, account.getAccountId());
        assertEquals(expectedUserId, account.getUserId());
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
        Account account = new Account(1, 1, "jad", "Jane", "Doe", 1);
        Account accountDifferentAccountId = new Account(2, 1, "jad", "Jane", "Doe", 1);
        assertNotEquals(account, accountDifferentAccountId);
        Account accountDifferentUserId = new Account(1, 2, "jad", "Jane", "Doe", 1);
        assertNotEquals(account, accountDifferentUserId);
        Account accountDifferentUsername = new Account(1, 1, "jadd", "Jane", "Doe", 1);
        assertNotEquals(account, accountDifferentUsername);
        Account accountDifferentFirstname = new Account(1, 1, "jad", "Julie", "Doe", 1);
        assertNotEquals(account, accountDifferentFirstname);
        Account accountDifferentLastname = new Account(1, 1, "jad", "Jane", "Deer", 1);
        assertNotEquals(account, accountDifferentLastname);
        Account equalAccount = new Account(1, 1, "jad", "Jane", "Doe", 1);
        assertEquals(account, equalAccount);
        assertEquals(account, account);
        Account accountWithNullStrings = new Account(1, 1, null, null, null, 1);
        assertNotEquals(accountWithNullStrings, account);
        assertNotEquals(account, null);
        assertNotEquals(account, "");
    }

    @Test
    public void testHashCode() {
        Account account = new Account(1, 1, "jad", "Jane", "Doe", 1);
        assertEquals(-66719528, account.hashCode());
        Account accountWithNullStrings = new Account(1, 1, null, null, null, 1);
        assertEquals(29552703, accountWithNullStrings.hashCode());
    }

    @Test
    public void testToString() {
        Account account = new Account(1, 1, "jad", "Jane", "Doe", 1);
        assertEquals("Account [getAccountId()=1, getUserId()=1, getUsername()=jad, getFirstName()=Jane, getLastName()=Doe]", account.toString());
        Account accountWithNullStrings = new Account(1, 1, null, null, null, 1);
        assertEquals("Account [getAccountId()=1, getUserId()=1, getUsername()=null, getFirstName()=null, getLastName()=null]", accountWithNullStrings.toString());
    }

}
