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
package no.priv.bang.ukelonn.api.resources;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.SumYearMonth;

public class StatisticsTest {

    @Test
    public void testEarningsSumOverYear() {
        Statistics resource = new Statistics();
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.earningsSumOverYear("jad")).thenReturn(Arrays.asList(SumYear.with().sum(1250.0).year(2016).build()));
        resource.ukelonn = ukelonn;

        String username = "jad";
        List<SumYear> earningsSumOverYear = resource.earningsSumOverYear(username);
        assertThat(earningsSumOverYear).isNotEmpty();
        assertEquals(2016, earningsSumOverYear.get(0).getYear());
    }

    @Test
    public void testEarningsSumOverMonth() {
        Statistics resource = new Statistics();
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.earningsSumOverMonth("jad")).thenReturn(Arrays.asList(SumYearMonth.with().sum(125.0).year(2016).month(7).build()));
        resource.ukelonn = ukelonn;

        String username = "jad";
        List<SumYearMonth> earningsSumOverYear = resource.earningsSumOverMonth(username);
        assertThat(earningsSumOverYear).isNotEmpty();
        assertEquals(2016, earningsSumOverYear.get(0).getYear());
    }

}
