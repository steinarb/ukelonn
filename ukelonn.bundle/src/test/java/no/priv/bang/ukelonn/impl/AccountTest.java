package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTest {

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

}
