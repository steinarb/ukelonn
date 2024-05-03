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
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransactionTypeTest {

    @Test
    void testNoArgumentConstructor() {
        var bean = TransactionType.with().build();
        assertEquals(Integer.valueOf(0), bean.id());
        assertNull(bean.transactionTypeName());
        assertNull(bean.transactionAmount());
        assertFalse(bean.transactionIsWork());
        assertFalse(bean.transactionIsWagePayment());
    }

    @Test
    void testConstructorWithArguments() {
        var bean = TransactionType.with()
            .id(1)
            .transactionTypeName("Vaske")
            .transactionAmount(45.0)
            .transactionIsWork(true)
            .build();
        assertEquals(Integer.valueOf(1), bean.id());
        assertEquals("Vaske", bean.transactionTypeName());
        assertEquals(Double.valueOf(45), bean.transactionAmount());
        assertTrue(bean.transactionIsWork());
        assertFalse(bean.transactionIsWagePayment());
    }

    @Test
    void testToString() {
        var bean = TransactionType.with().id(1).transactionTypeName("Vaske").transactionAmount(45.0).transactionIsWork(true).build();
        assertThat(bean.toString()).startsWith("TransactionType[");
    }

}
