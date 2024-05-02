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
package no.priv.bang.ukelonn.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdatedTransaction {

    private int id;
    private int accountId;
    private int transactionTypeId;
    private Date transactionTime;
    private double transactionAmount;

    private UpdatedTransaction() {}

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int id = -1;
        private int accountId = -1;
        private int transactionTypeId = -1;
        private Date transactionTime = null;
        private double transactionAmount = 0.0;

        private Builder() {}

        public UpdatedTransaction build() {
            UpdatedTransaction updatedTransaction = new UpdatedTransaction();
            updatedTransaction.id = this.id;
            updatedTransaction.accountId = this.accountId;
            updatedTransaction.transactionTypeId = this.transactionTypeId;
            updatedTransaction.transactionTime = this.transactionTime;
            updatedTransaction.transactionAmount = this.transactionAmount;
            return updatedTransaction;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder transactionTypeId(int transactionTypeId) {
            this.transactionTypeId = transactionTypeId;
            return this;
        }

        public Builder transactionTime(Date transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public Builder transactionAmount(double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }
    }

}
