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
import java.util.Date;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testNoArgConstructor() {
        var bean = Transaction.with().build();
        assertEquals(-1, bean.id());
        assertNull(bean.transactionType());
        assertNull(bean.transactionTime());
        assertEquals(0.0, bean.transactionAmount(), 0.0);
        assertFalse(bean.paidOut());
    }

    @Test
    void testConstructorWithArgs() {
        var id = 5;
        var transactionType = TransactionType.with().build();
        var transactionTime = new Date();
        var transactionAmount = 100.0;
        var paidOut = true;
        var bean = Transaction.with()
            .id(id)
            .transactionType(transactionType)
            .transactionTime(transactionTime)
            .transactionAmount(transactionAmount)
            .paidOut(paidOut)
            .build();
        assertEquals(id, bean.id());
        assertEquals(transactionType, bean.transactionType());
        assertEquals(transactionTime, bean.transactionTime());
        assertEquals(transactionAmount, bean.transactionAmount(), 0.0);
        assertTrue(bean.paidOut());
    }

}
