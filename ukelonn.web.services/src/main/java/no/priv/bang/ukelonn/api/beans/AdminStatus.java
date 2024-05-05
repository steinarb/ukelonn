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
package no.priv.bang.ukelonn.api.beans;

import no.priv.bang.osgiservice.users.User;

public record AdminStatus(User user, boolean administrator) {

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private boolean administrator;

        private Builder() {}

        public AdminStatus build() {
            return new AdminStatus(this.user, this.administrator);
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder administrator(boolean administrator) {
            this.administrator = administrator;
            return this;
        }

    }

}
