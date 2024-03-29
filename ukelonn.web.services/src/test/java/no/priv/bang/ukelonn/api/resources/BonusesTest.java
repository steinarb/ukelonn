/*
 * Copyright 2020-2024 Steinar Bang
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Bonus;

class BonusesTest {

    @Test
    void testGetActiveBonuses() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getActiveBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        var resource = new Bonuses();
        resource.ukelonn = ukelonn;

        var activeBonuses = resource.getActiveBonuses();
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    void testGetAllBonuses() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAllBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        var resource = new Bonuses();
        resource.ukelonn = ukelonn;

        var activeBonuses = resource.getAllBonuses();
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    void testCreateBonus() {
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.createBonus(any())).thenReturn(Collections.singletonList(bonus));

        var resource = new Bonuses();
        resource.ukelonn = ukelonn;

        var bonuses = resource.createBonus(bonus);
        assertEquals(bonus, bonuses.get(0));
    }

    @Test
    void testModifyBonus() {
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyBonus(any())).thenReturn(Collections.singletonList(bonus));

        var resource = new Bonuses();
        resource.ukelonn = ukelonn;

        var bonuses = resource.modifyBonus(bonus);
        assertEquals(bonus, bonuses.get(0));
    }

    @Test
    void testDeleteBonus() {
        var bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteBonus(any())).thenReturn(Collections.singletonList(Bonus.with().build()));

        var resource = new Bonuses();
        resource.ukelonn = ukelonn;

        var bonuses = resource.deleteBonus(bonus);
        assertThat(bonuses).isNotEmpty().doesNotContain(bonus);
    }

}
