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

class UpdatedTransactionTest {

    @Test
    void testCreate() {
        var now = new Date();
        var bean = UpdatedTransaction.with()
            .id(31)
            .accountId(2)
            .transactionTypeId(2)
            .transactionTime(now)
            .transactionAmount(3.14)
            .build();
        assertEquals(31, bean.id());
        assertEquals(2, bean.accountId());
        assertEquals(2, bean.transactionTypeId());
        assertEquals(now, bean.transactionTime());
        assertEquals(3.14, bean.transactionAmount(), 0.0);
    }

    @Test
    void testNoArgsConstructor() {
        var bean = UpdatedTransaction.with().build();
        assertEquals(-1, bean.id());
        assertEquals(-1, bean.transactionTypeId());
        assertEquals(-1, bean.transactionTypeId());
        assertNull(bean.transactionTime());
        assertEquals(0.0, bean.transactionAmount(), 0.0);
    }

}
