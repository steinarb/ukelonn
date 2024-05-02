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
public class Account extends Immutable { // NOSONAR Immutable handles added fields
    private int accountId;
    private String username;
    private String firstName;
    private String lastName;
    private double balance;

    private Account() {
        // No-arg constructor required by jackson
    }

    public int getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (getFirstName() != null && getLastName() != null) {
            return getFirstName() + " " + getLastName();
        }

        if (getFirstName() != null) {
            return getFirstName();
        }

        return getUsername();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account [getAccountId()=" + getAccountId() + ", getUsername()=" + getUsername() + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName() + "]";
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int accountid;
        private String username;
        private String firstName;
        private String lastName;
        private double balance;

        private Builder() {}

        public Account build() {
            Account account = new Account();
            account.accountId = this.accountid;
            account.username = this.username;
            account.firstName = this.firstName;
            account.lastName = this.lastName;
            account.balance = this.balance;
            return account;
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
