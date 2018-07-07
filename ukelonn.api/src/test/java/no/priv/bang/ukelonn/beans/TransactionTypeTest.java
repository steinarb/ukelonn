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

}
