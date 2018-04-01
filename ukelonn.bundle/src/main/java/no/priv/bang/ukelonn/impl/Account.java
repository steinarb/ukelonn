/*
 * Copyright 2016-2018 Steinar Bang
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
package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonStringMethods.*;

public class Account {
    int accountId;
    int userId;
    String username;
    String firstName;
    String lastName;
    double balance;

    public Account(int accountId, int userId, String username, String firstName, String lastName, double balance) {
        super();
        this.accountId = accountId;
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accountId;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + userId;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null ||
            getClass() != obj.getClass())
        {
            return false;
        }

        Account other = (Account) obj;
        return
            accountId == other.accountId &&
            nullSafeEquals(firstName, other.firstName) &&
            nullSafeEquals(lastName, other.lastName) &&
            userId == other.userId &&
            nullSafeEquals(username, other.username);
    }

    @Override
    public String toString() {
        return "Account [getAccountId()=" + getAccountId() + ", getUserId()=" + getUserId() + ", getUsername()=" + getUsername() + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName() + "]";
    }

}
