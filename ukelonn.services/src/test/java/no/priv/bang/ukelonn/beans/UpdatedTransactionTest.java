package no.priv.bang.ukelonn.beans;

import static org.junit.Assert.*;

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
import java.util.Date;

import org.junit.Test;

public class UpdatedTransactionTest {

    @Test
    public void testCreate() {
        Date now = new Date();
        UpdatedTransaction bean = new UpdatedTransaction(31, 2, 2, now, 3.14);
        assertEquals(31, bean.getId());
        assertEquals(2, bean.getAccountId());
        assertEquals(2, bean.getTransactionTypeId());
        assertEquals(now, bean.getTransactionTime());
        assertEquals(3.14, bean.getTransactionAmount(), 0.0);
    }

    @Test
    public void testNoArgsConstructor() {
        UpdatedTransaction bean = new UpdatedTransaction();
        assertEquals(-1, bean.getId());
        assertEquals(-1, bean.getTransactionTypeId());
        assertEquals(-1, bean.getTransactionTypeId());
        assertNull(bean.getTransactionTime());
        assertEquals(0.0, bean.getTransactionAmount(), 0.0);
    }

}
