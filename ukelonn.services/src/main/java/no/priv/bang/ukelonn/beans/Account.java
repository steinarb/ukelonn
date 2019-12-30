/*
 * Copyright 2016-2019 Steinar Bang
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
public class Account extends Immutable {
    int accountId;
    String username;
    String firstName;
    String lastName;
    double balance;

    public Account() {
        // No-arg constructor required by jackson
    }

    public Account(int accountId, String username, String firstName, String lastName, double balance) {
        super();
        this.accountId = accountId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
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
        return getFirstName() + " " + getLastName();
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

}
