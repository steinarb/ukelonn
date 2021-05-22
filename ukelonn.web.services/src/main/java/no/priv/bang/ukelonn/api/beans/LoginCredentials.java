/*
 * Copyright 2018-2021 Steinar Bang
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

public class LoginCredentials {
    private String username;
    private String password;

    private LoginCredentials() {}

    public static LoginCredentialsBuilder with() {
        return new LoginCredentialsBuilder();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class LoginCredentialsBuilder {
        private String username;
        private String password;

        private LoginCredentialsBuilder() {}

        public LoginCredentials build() {
            LoginCredentials loginCredentials = new LoginCredentials();
            loginCredentials.username = this.username;
            loginCredentials.password = this.password;
            return loginCredentials;
        }

        public LoginCredentialsBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginCredentialsBuilder password(String password) {
            this.password = password;
            return this;
        }
    }
}
