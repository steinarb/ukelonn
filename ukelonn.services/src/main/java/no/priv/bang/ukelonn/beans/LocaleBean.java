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

import static java.util.Optional.ofNullable;

import java.util.Locale;

public record LocaleBean(String code, String displayLanguage) {

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private Locale locale;

        private Builder() {}

        public LocaleBean build() {
            var localeCode = ofNullable(this.locale).map(Object::toString).orElse(null);
            var displayLanguage = ofNullable(this.locale).map(l -> l.getDisplayLanguage(l)).orElse(null);
            return new LocaleBean(localeCode, displayLanguage);
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }
    }
}
