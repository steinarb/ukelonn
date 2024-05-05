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
package no.priv.bang.ukelonn.beans;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class BonusTest {

    @Test
    void testCreate() {
        var bonusId = 1;
        var enabled = true;
        var iconurl = "http//images.com/juletre.jpg";
        var title = "Julebonus";
        var description = "Dobbel betaling for jobber";
        var bonusFactor = 2.0;
        var startDate = new Date();
        var endDate = new Date();
        var bean = Bonus.with()
            .bonusId(bonusId)
            .enabled(enabled)
            .iconurl(iconurl)
            .title(title)
            .description(description)
            .bonusFactor(bonusFactor)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        assertEquals(bonusId, bean.bonusId());
        assertTrue(bean.enabled());
        assertEquals(iconurl, bean.iconurl());
        assertEquals(title, bean.title());
        assertEquals(description, bean.description());
        assertEquals(bonusFactor, bean.bonusFactor(), 0.0);
        assertEquals(startDate, bean.startDate());
        assertEquals(endDate, bean.endDate());
    }

    @Test
    void testNoArgsConstructor() {
        var bean = Bonus.with().build();
        assertEquals(0, bean.bonusId());
        assertFalse(bean.enabled());
        assertNull(bean.iconurl());
        assertNull(bean.title());
        assertNull(bean.description());
        assertEquals(0.0, bean.bonusFactor(), 0.0);
        assertNull(bean.startDate());
        assertNull(bean.endDate());
    }

}
