/*
 * Copyright 2018-2024 Steinar Bang
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

public class LoginResult {
    private String username;
    private String[] roles;
    private String errorMessage;

    private LoginResult() {}

    public static Builder with() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Builder {
        private String username = "";
        private String[] roles = {};
        private String errorMessage = "";

        private Builder() {}

        public LoginResult build() {
            LoginResult loginResult = new LoginResult();
            loginResult.username = this.username;
            loginResult.roles = this.roles;
            loginResult.errorMessage = this.errorMessage;
            return loginResult;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(String[] roles) {
            this.roles = roles;
            return this;
        }
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

    }

}
