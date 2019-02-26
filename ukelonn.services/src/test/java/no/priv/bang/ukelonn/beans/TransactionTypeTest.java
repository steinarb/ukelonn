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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TransactionTypeTest {

    @Test
    public void testNoArgumentConstructor() {
        TransactionType bean = new TransactionType();
        assertEquals(Integer.valueOf(0), bean.getId());
        assertNull(bean.getTransactionTypeName());
        assertNull(bean.getTransactionAmount());
        assertFalse(bean.isTransactionIsWork());
        assertFalse(bean.isTransactionIsWagePayment());
    }

    @Test
    public void testConstructorWithArguments() {
        TransactionType bean = new TransactionType(1, "Vaske", 45.0, true, false);
        assertEquals(Integer.valueOf(1), bean.getId());
        assertEquals("Vaske", bean.getTransactionTypeName());
        assertEquals(Double.valueOf(45), bean.getTransactionAmount());
        assertTrue(bean.isTransactionIsWork());
        assertFalse(bean.isTransactionIsWagePayment());
    }

    @Test
    public void testCompare() {
        TransactionType bean = new TransactionType(1, "Vaske", 45.0, true, false);
        Set<TransactionType> set = new HashSet<>();
        set.add(bean);
        assertTrue(set.contains(bean));

        TransactionType emptyBean = new TransactionType();
        set.add(emptyBean);
        assertTrue(set.contains(emptyBean));

        assertNotEquals(bean, null);
        assertNotEquals(bean, "bean");
        TransactionType beanWithNullAmount1 = new TransactionType(1, "Vaske", null, true, false);
        assertNotEquals(beanWithNullAmount1, bean);
        TransactionType beanWithNullAmount2 = new TransactionType(1, "Vaske", null, true, false);
        assertEquals(beanWithNullAmount1, beanWithNullAmount2);
        TransactionType beanDifferentAmount = new TransactionType(1, "Vaske", 42.0, true, false);
        assertNotEquals(bean, beanDifferentAmount);
        TransactionType beanCopy = new TransactionType(1, "Vaske", 45.0, true, false);
        assertEquals(bean, beanCopy);
        TransactionType beanFalseWork = new TransactionType(1, "Vaske", 45.0, false, false);
        assertNotEquals(bean, beanFalseWork);
        TransactionType beanTrueWage = new TransactionType(1, "Vaske", 45.0, true, true);
        assertNotEquals(bean, beanTrueWage);
        TransactionType beanWithNullName1 = new TransactionType(1, null, 45.0, true, false);
        assertNotEquals(beanWithNullName1, bean);
        TransactionType beanWithNullName2 = new TransactionType(1, null, 45.0, true, false);
        assertEquals(beanWithNullName1, beanWithNullName2);
        TransactionType beanWithDifferentName = new TransactionType(1, "Tørke", 45.0, true, false);
        assertNotEquals(bean, beanWithDifferentName);
    }

    @Test
    public void testToString() {
        TransactionType bean = new TransactionType(1, "Vaske", 45.0, true, false);
        assertThat(bean.toString()).startsWith("TransactionType [");
    }

}
