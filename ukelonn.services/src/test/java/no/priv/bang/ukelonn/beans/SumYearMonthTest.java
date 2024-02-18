/*
 * Copyright 2019-2024 Steinar Bang
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

import org.junit.jupiter.api.Test;

class SumYearMonthTest {

    @Test
    void test() {
        var sum = 250.0;
        var year = 2016;
        var month = 11;
        var bean = SumYearMonth.with().sum(sum).year(year).month(month).build();
        assertEquals(sum, bean.getSum(), 0.0);
        assertEquals(year, bean.getYear());
        assertEquals(month, bean.getMonth());
    }

    @Test
    void testNoArgsConstructor() {
        var sum = 0.0;
        var year = -1;
        var month = -1;
        var bean = SumYearMonth.with().build();
        assertEquals(sum, bean.getSum(), 0.0);
        assertEquals(year, bean.getYear());
        assertEquals(month, bean.getMonth());
    }

}
