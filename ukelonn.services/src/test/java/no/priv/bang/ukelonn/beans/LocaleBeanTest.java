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

import java.util.Locale;

import org.junit.jupiter.api.Test;

class LocaleBeanTest {

    @Test
    void testUK() {
        var bean = LocaleBean.with().locale(Locale.UK).build();
        assertEquals("en_GB", bean.code());
        assertEquals("English", bean.displayLanguage());
    }

    @Test
    void testNO() {
        var norsk = Locale.forLanguageTag("nb-NO");
        var bean = LocaleBean.with().locale(norsk).build();
        assertEquals("nb_NO", bean.code());
        assertEquals(norsk.getDisplayLanguage(norsk), bean.displayLanguage());
    }

    @Test
    void testNoArgsConstructor() {
        var bean = LocaleBean.with().build();
        assertNull(bean.code());
        assertNull(bean.displayLanguage());
    }

}
