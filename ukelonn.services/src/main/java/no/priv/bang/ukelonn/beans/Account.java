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

@JsonIgnoreProperties(ignoreUnknown=true)
public record Account(int accountId, String username,String firstName, String lastName, double balance) {

    public String getFullName() {
        if (firstName() != null && lastName() != null) {
            return firstName() + " " + lastName();
        }

        if (firstName() != null) {
            return firstName();
        }

        return username();
    }

    public static Builder with() {
        return new Builder();
    }

    public static Builder with(Account account) {
        var builder = new Builder();
        builder.accountid = account.accountId;
        builder.username = account.username;
        builder.firstName = account.firstName;
        builder.lastName = account.lastName;
        builder.balance = account.balance;
        return builder;
    }

    public static class Builder {
        private int accountid;
        private String username;
        private String firstName;
        private String lastName;
        private double balance;

        private Builder() {}

        public Account build() {
            return new Account(this.accountid, this.username, this.firstName, this.lastName, this.balance);
        }

        public Builder accountid(int accountid) {
            this.accountid = accountid;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder balance(double balance) {
            this.balance = balance;
            return this;
        }
    }

}
