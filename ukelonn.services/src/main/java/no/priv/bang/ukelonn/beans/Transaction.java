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

import java.util.Date;
import static java.util.Optional.*;

public record Transaction(
    int id,
    TransactionType transactionType,
    Date transactionTime,
    double transactionAmount,
    boolean paidOut)
{
    public String getName() {
        return ofNullable(transactionType).map(TransactionType::transactionTypeName).orElse(null);
    }

    public static Builder with(Transaction transaction) {
        Builder builder = new Builder();
        builder.id = transaction.id;
        builder.transactionType = transaction.transactionType;
        builder.transactionTime = transaction.transactionTime;
        builder.transactionAmount = transaction.transactionAmount;
        builder.paidOut = transaction.paidOut;
        return builder;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int id = -1;
        private TransactionType transactionType = null;
        private Date transactionTime = null;
        private double transactionAmount = 0.0;
        private boolean paidOut = false;

        private Builder() {}

        public Transaction build() {
            return new Transaction(id, transactionType, transactionTime, transactionAmount, paidOut);
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
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

        public Builder paidOut(boolean paidOut) {
            this.paidOut = paidOut;
            return this;
        }
    }
}
