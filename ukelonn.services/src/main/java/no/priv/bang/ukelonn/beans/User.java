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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.priv.bang.beans.immutable.Immutable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User extends Immutable { // NOSONAR Immutable handles added fields
    private int userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    private User() {}

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return new StringBuilder(getFirstname()).append(" ").append(getLastname()).toString();
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", firstname=" + firstname + ", lastname=" + lastname + "]";
    }

    public static Builder with() {
        return new Builder();
    }

    public static Builder with(User user) {
        Builder builder = new Builder();
        builder.userId = user.userId;
        builder.username = user.username;
        builder.email = user.email;
        builder.firstname = user.firstname;
        builder.lastname = user.lastname;
        return builder;
    }

    public static class Builder {
        private int userId = -1;
        private String username = "";
        private String email = "";
        private String firstname = "";
        private String lastname = "";

        private Builder() {}

        public User build() {
            User user = new User();
            user.userId = this.userId;
            user.username = this.username;
            user.email = this.email;
            user.firstname = this.firstname;
            user.lastname = this.lastname;
            return user;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }
    }
}
