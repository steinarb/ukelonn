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

class PerformedTransactionTest {

    @Test
    void testNoArgsConstructor() {
        var bean = PerformedTransaction.with().build();
        assertNull(bean.account());
        assertEquals(-1, bean.transactionTypeId());
        assertEquals(0.0, bean.transactionAmount(), 0.0);
    }

    @Test
    void testConstructorWithArgs() {
        var account = Account.with().build();
        var now = new Date();
        var bean = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(1)
            .transactionAmount(3.14)
            .transactionDate(now)
            .build();
        assertEquals(account, bean.account());
        assertEquals(1, bean.transactionTypeId());
        assertEquals(3.14, bean.transactionAmount(), 0.0);
        assertEquals(now, bean.transactionDate());
    }

}
