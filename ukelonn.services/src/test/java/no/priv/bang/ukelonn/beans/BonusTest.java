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
package no.priv.bang.ukelonn.beans;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class BonusTest {

    @Test
    public void testCreate() {
        int bonusId = 1;
        boolean enabled = true;
        String iconurl = "http//images.com/juletre.jpg";
        String title = "Julebonus";
        String description = "Dobbel betaling for jobber";
        double bonusFactor = 2.0;
        Date startDate = new Date();
        Date endDate = new Date();
        Bonus bean = new Bonus(bonusId, enabled, iconurl, title, description, bonusFactor, startDate, endDate);
        assertEquals(bonusId, bean.getBonusId());
        assertTrue(bean.isEnabled());
        assertEquals(iconurl, bean.getIconurl());
        assertEquals(title, bean.getTitle());
        assertEquals(description, bean.getDescription());
        assertEquals(bonusFactor, bean.getBonusFactor(), 0.0);
        assertEquals(startDate, bean.getStartDate());
        assertEquals(endDate, bean.getEndDate());
    }

    @Test
    public void testNoArgsConstructor() {
        Bonus bean = new Bonus();
        assertEquals(0, bean.getBonusId());
        assertFalse(bean.isEnabled());
        assertNull(bean.getIconurl());
        assertNull(bean.getTitle());
        assertNull(bean.getDescription());
        assertEquals(0.0, bean.getBonusFactor(), 0.0);
        assertNull(bean.getStartDate());
        assertNull(bean.getEndDate());
    }

}
