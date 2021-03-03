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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TransactionTypeTest {

    @Test
    public void testNoArgumentConstructor() {
        TransactionType bean = TransactionType.with().build();
        assertEquals(Integer.valueOf(0), bean.getId());
        assertNull(bean.getTransactionTypeName());
        assertNull(bean.getTransactionAmount());
        assertFalse(bean.isTransactionIsWork());
        assertFalse(bean.isTransactionIsWagePayment());
    }

    @Test
    public void testConstructorWithArguments() {
        TransactionType bean = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertEquals(Integer.valueOf(1), bean.getId());
        assertEquals("Vaske", bean.getTransactionTypeName());
        assertEquals(Double.valueOf(45), bean.getTransactionAmount());
        assertTrue(bean.isTransactionIsWork());
        assertFalse(bean.isTransactionIsWagePayment());
    }

    @Test
    public void testCompare() {
        TransactionType bean = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        Set<TransactionType> set = new HashSet<>();
        set.add(bean);
        assertTrue(set.contains(bean));

        TransactionType emptyBean = TransactionType.with().build();
        set.add(emptyBean);
        assertTrue(set.contains(emptyBean));

        assertNotEquals(bean, null);
        TransactionType beanWithNullAmount1 = TransactionType.with().id(1).transactionTypeName("Vaske").transactionIsWork(true).build();
        assertNotEquals(beanWithNullAmount1, bean);
        TransactionType beanWithNullAmount2 = TransactionType.with().id(1).transactionTypeName("Vaske").transactionIsWork(true).build();
        assertEquals(beanWithNullAmount1, beanWithNullAmount2);
        TransactionType beanDifferentAmount = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(42.0)
            .transactionIsWork(true)
            .build();
        assertNotEquals(bean, beanDifferentAmount);
        TransactionType beanCopy = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertEquals(bean, beanCopy);
        TransactionType beanFalseWork = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(false)
            .build();
        assertNotEquals(bean, beanFalseWork);
        TransactionType beanTrueWage = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .transactionIsWagePayment(true)
            .build();
        assertNotEquals(bean, beanTrueWage);
        TransactionType beanWithNullName1 = TransactionType.with()
            .id(1)
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertNotEquals(beanWithNullName1, bean);
        TransactionType beanWithNullName2 = TransactionType.with()
            .id(1)
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertEquals(beanWithNullName1, beanWithNullName2);
        TransactionType beanWithDifferentName = TransactionType.with()
            .id(1)
            .transactionTypeName("Tørke")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertNotEquals(bean, beanWithDifferentName);
    }

    @Test
    public void testToString() {
        TransactionType bean = TransactionType.with().id(1).transactionTypeName("Vaske").transactionAmount(45.0).transactionIsWork(true).build();
        assertThat(bean.toString()).startsWith("TransactionType [");
    }

}
