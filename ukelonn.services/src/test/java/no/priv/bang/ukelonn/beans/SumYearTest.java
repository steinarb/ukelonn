/*
 * Copyright 2019-2021 Steinar Bang
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

class SumYearTest {

    @Test
    void testBean() {
        double sum = 250.0;
        int year = 2016;
        SumYear bean = SumYear.with().sum(sum).year(year).build();
        assertEquals(sum, bean.getSum(), 0.0);
        assertEquals(year, bean.getYear());
    }

    @Test
    void testNoArgsConstructor() {
        double sum = 0.0;
        int year = -1;
        SumYear bean = SumYear.with().build();
        assertEquals(sum, bean.getSum(), 0.0);
        assertEquals(year, bean.getYear());
    }

}
