/*
 * Copyright 2020 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Bonus;

public class BonusesTest {

    @Test
    public void testGetActiveBonuses() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getActiveBonuses()).thenReturn(Collections.singletonList(new Bonus()));

        Bonuses resource = new Bonuses();
        resource.ukelonn = ukelonn;

        List<Bonus> activeBonuses = resource.getActiveBonuses();
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    public void testGetAllBonuses() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAllBonuses()).thenReturn(Collections.singletonList(new Bonus()));

        Bonuses resource = new Bonuses();
        resource.ukelonn = ukelonn;

        List<Bonus> activeBonuses = resource.getAllBonuses();
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    public void testCreateBonus() {
        Bonus bonus = new Bonus(1, true, null, "Julebonus", "Dobbelt lønn for jobb", 2.0, new Date(), new Date());
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.createBonus(any())).thenReturn(Collections.singletonList(bonus));

        Bonuses resource = new Bonuses();
        resource.ukelonn = ukelonn;

        List<Bonus> bonuses = resource.createBonus(bonus);
        assertEquals(bonus, bonuses.get(0));
    }

    @Test
    public void testModifyBonus() {
        Bonus bonus = new Bonus(1, true, null, "Julebonus", "Dobbelt lønn for jobb", 2.0, new Date(), new Date());
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyBonus(any())).thenReturn(Collections.singletonList(bonus));

        Bonuses resource = new Bonuses();
        resource.ukelonn = ukelonn;

        List<Bonus> bonuses = resource.modifyBonus(bonus);
        assertEquals(bonus, bonuses.get(0));
    }

    @Test
    public void testDeleteBonus() {
        Bonus bonus = new Bonus(1, true, null, "Julebonus", "Dobbelt lønn for jobb", 2.0, new Date(), new Date());
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteBonus(any())).thenReturn(Collections.singletonList(new Bonus()));

        Bonuses resource = new Bonuses();
        resource.ukelonn = ukelonn;

        List<Bonus> bonuses = resource.deleteBonus(bonus);
        assertThat(bonuses).isNotEmpty().doesNotContain(bonus);
    }

}
