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

import java.util.Locale;

import no.priv.bang.beans.immutable.Immutable;

public class LocaleBean extends Immutable { // NOSONAR Immutable handles added fields

    private String code;
    private String displayLanguage;

    private LocaleBean() {}

    public String getCode() {
        return code;
    }

    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private Locale locale;

        private Builder() {}

        public LocaleBean build() {
            var localeBean = new LocaleBean();
            localeBean.code = locale != null ? locale.toString() : null;
            localeBean.displayLanguage = locale != null ? locale.getDisplayLanguage(locale) : null;
            return localeBean;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }
    }
}
