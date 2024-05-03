/*
 * Copyright 2016-2024 Steinar Bang
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

public record PasswordsWithUser(User user, String password, String password2) {

    public static Builder with() {
        return new Builder();
    }

    public static Builder with(PasswordsWithUser passwordsWithUser) {
        var builder = new Builder();
        builder.user = passwordsWithUser.user();
        builder.password = passwordsWithUser.password();
        builder.password2 = passwordsWithUser.password2;
        return builder;
    }

    public static class Builder {
        private User user;
        private String password;
        private String password2;

        private Builder() {}

        public PasswordsWithUser build() {
            return new PasswordsWithUser(this.user, this.password, this.password2);
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder password2(String password2) {
            this.password2 = password2;
            return this;
        }
    }

}
