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

public class PerformedTransactionTest {

    @Test
    public void testNoArgsConstructor() {
        PerformedTransaction bean = new PerformedTransaction();
        assertNull(bean.getAccount());
        assertEquals(-1, bean.getTransactionTypeId());
        assertEquals(0.0, bean.getTransactionAmount(), 0.0);
    }

    @Test
    public void testConstructorWithArgs() {
        Account account = new Account();
        PerformedTransaction bean = new PerformedTransaction(account, 1, 3.14);
        assertEquals(account, bean.getAccount());
        assertEquals(1, bean.getTransactionTypeId());
        assertEquals(3.14, bean.getTransactionAmount(), 0.0);
    }

}
