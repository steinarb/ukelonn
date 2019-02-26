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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TransactionTest {

    @Test
    public void testNoArgConstructor() {
        Transaction bean = new Transaction();
        assertEquals(-1, bean.getId());
        assertNull(bean.getTransactionType());
        assertNull(bean.getTransactionTime());
        assertEquals(0.0, bean.getTransactionAmount(), 0.0);
        assertFalse(bean.isPaidOut());
    }

    @Test
    public void testConstructorWithArgs() {
        int id = 5;
        TransactionType transactionType = new TransactionType();
        Date transactionTime = new Date();
        double transactionAmount = 100.0;
        boolean paidOut = true;
        Transaction bean = new Transaction(id, transactionType, transactionTime, transactionAmount, paidOut);
        assertEquals(id, bean.getId());
        assertEquals(transactionType, bean.getTransactionType());
        assertEquals(transactionTime, bean.getTransactionTime());
        assertEquals(transactionAmount, bean.getTransactionAmount(), 0.0);
        assertTrue(bean.isPaidOut());
    }

    @Test
    public void testCompare() {
        int id = 5;
        TransactionType transactionType = new TransactionType();
        Date transactionTime = new Date();
        double transactionAmount = 100.0;
        boolean paidOut = true;
        Transaction bean = new Transaction(id, transactionType, transactionTime, transactionAmount, paidOut);
        Transaction beanCopy = new Transaction(id, transactionType, transactionTime, transactionAmount, paidOut);

        Set<Transaction> set = new HashSet<>();
        set.add(bean);
        assertTrue(set.contains(bean));

        Transaction emptyBean = new Transaction();
        set.add(emptyBean);
        assertTrue(set.contains(emptyBean));

        assertNotEquals(bean, null);
        assertEquals(bean, bean);
        assertNotEquals(bean, "bean");
        assertEquals(bean, beanCopy);
        assertNotEquals(bean, emptyBean);
        assertNotEquals(emptyBean, bean);
        assertEquals(emptyBean, emptyBean);
        Transaction beanDifferentAmount = new Transaction(id, transactionType, transactionTime, 102.0, paidOut);
        assertNotEquals(bean, beanDifferentAmount);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Transaction beanDifferentTime = new Transaction(id, transactionType, calendar.getTime(), transactionAmount, paidOut);
        assertNotEquals(bean, beanDifferentTime);
        Transaction beanNullTime1 = new Transaction(id, transactionType, null, transactionAmount, paidOut);
        assertNotEquals(bean, beanNullTime1);
        Transaction beanNullTime2 = new Transaction(id, transactionType, null, transactionAmount, paidOut);
        assertEquals(beanNullTime1, beanNullTime2);
        assertNotEquals(beanNullTime1, bean);
        Transaction beanWithNullType1 = new Transaction(id, null, transactionTime, transactionAmount, paidOut);
        assertNotEquals(beanWithNullType1, bean);
        Transaction beanWithNullType2 = new Transaction(id, null, transactionTime, transactionAmount, paidOut);
        assertEquals(beanWithNullType1, beanWithNullType2);
        TransactionType differentType = new TransactionType(1, "type", null, true, false);
        Transaction beanWithDifferentType = new Transaction(id, differentType, transactionTime, transactionAmount, paidOut);
        assertNotEquals(bean, beanWithDifferentType);

        assertThat(bean.toString()).startsWith("Transaction [id=");
    }

}
