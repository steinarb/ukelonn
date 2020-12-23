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

import java.util.Locale;

import org.junit.Test;

public class LocaleBeanTest {

    @Test
    public void testUK() {
        LocaleBean bean = new LocaleBean(Locale.UK);
        assertEquals("en_GB", bean.getCode());
        assertEquals("English", bean.getDisplayLanguage());
    }

    @Test
    public void testNO() {
        Locale norsk = Locale.forLanguageTag("nb-NO");
        LocaleBean bean = new LocaleBean(norsk);
        assertEquals("nb_NO", bean.getCode());
        assertEquals(norsk.getDisplayLanguage(norsk), bean.getDisplayLanguage());
    }

    @Test
    public void testNoArgsConstructor() {
        LocaleBean bean = new LocaleBean();
        assertNull(bean.getCode());
        assertNull(bean.getDisplayLanguage());
    }

}
