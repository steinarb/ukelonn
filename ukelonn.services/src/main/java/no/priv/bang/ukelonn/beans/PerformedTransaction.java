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

public class PerformedTransaction {

    private Account account = null;
    private int transactionTypeId;
    private double transactionAmount;
    private Date transactionDate;

    private PerformedTransaction() {}

    public Account getAccount() {
        return account;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private Account account;
        private Integer transactionTypeId;
        private Double transactionAmount;
        private Date transactionDate;

        private Builder() {}

        public PerformedTransaction build() {
            PerformedTransaction performedTransaction = new PerformedTransaction();
            performedTransaction.account = this.account;
            performedTransaction.transactionTypeId = this.transactionTypeId != null ? this.transactionTypeId : -1;
            performedTransaction.transactionAmount = this.transactionAmount != null ? this.transactionAmount : 0.0;
            performedTransaction.transactionDate = this.transactionDate != null ? this.transactionDate : new Date();
            return performedTransaction;
        }

        public Builder account(Account account) {
            this.account = account;
            return this;
        }

        public Builder transactionTypeId(Integer transactionTypeId) {
            this.transactionTypeId = transactionTypeId;
            return this;
        }

        public Builder transactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder transactionDate(Date transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }
    }

}
