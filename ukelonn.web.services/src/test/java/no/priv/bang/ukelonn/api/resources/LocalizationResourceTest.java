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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;

public class LocalizationResourceTest {

    @Test
    public void testDefaultLocale() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn("nb_NO");
        LocalizationResource resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        String defaultLocale = resource.defaultLocale();
        assertEquals("nb_NO", defaultLocale);
    }

    @Test
    public void testAvailableLocales() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn("nb_NO");
        when(ukelonn.availableLocales()).thenReturn(Arrays.asList("nb_NO", "en_GB"));
        LocalizationResource resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        List<String> availableLocales = resource.availableLocales();
        assertThat(availableLocales).isNotEmpty().contains(ukelonn.defaultLocale());
    }

    @Test
    public void testDisplayTextsForDefaultLocale() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn("nb_NO");
        Map<String, String> texts = new HashMap<>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(anyString())).thenReturn(texts);
        LocalizationResource resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        Map<String, String> displayTexts = resource.displayTexts(ukelonn.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

}
