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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.LocaleBean;

class LocalizationResourceTest {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");

    @Test
    void testDefaultLocale() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn(NB_NO);
        var resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        var defaultLocale = resource.defaultLocale();
        assertEquals(NB_NO, defaultLocale);
    }

    @Test
    void testAvailableLocales() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn(NB_NO);
        when(ukelonn.availableLocales()).thenReturn(Arrays.asList(Locale.forLanguageTag("nb-NO"), Locale.UK).stream().map(l -> LocaleBean.with().locale(l).build()).collect(Collectors.toList()));
        var resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        var availableLocales = resource.availableLocales();
        assertThat(availableLocales).isNotEmpty().contains(LocaleBean.with().locale(ukelonn.defaultLocale()).build());
    }

    @Test
    void testDisplayTextsForDefaultLocale() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn(NB_NO);
        var texts = new HashMap<String, String>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(any())).thenReturn(texts);
        var resource = new LocalizationResource();
        resource.ukelonn = ukelonn;
        var displayTexts = resource.displayTexts(ukelonn.defaultLocale().toString());
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayTextsWithUnknownLocale() {
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.displayTexts(any())).thenThrow(MissingResourceException.class);
        var resource = new LocalizationResource();
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        resource.ukelonn = ukelonn;
        assertThrows(WebApplicationException.class, () -> resource.displayTexts("en_UK"));
    }

}
