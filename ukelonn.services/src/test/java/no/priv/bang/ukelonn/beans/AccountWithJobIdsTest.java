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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AccountWithJobIdsTest {

    @Test
    public void testDefaultValues() {
        AccountWithJobIds bean = AccountWithJobIds.with().build();
        assertNull(bean.getAccount());
        assertEquals(0, bean.getJobIds().size());
    }

    @Test
    public void testConstructorWithParameters() {
        Account account = Account.with().build();
        List<Integer> ids = Arrays.asList(1, 2, 3, 4);
        AccountWithJobIds bean = AccountWithJobIds.with().account(account).jobIds(ids).build();
        assertEquals(account, bean.getAccount());
        assertEquals(4, bean.getJobIds().size());
    }

}
