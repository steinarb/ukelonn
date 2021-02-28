/*
 * Copyright 2016-2021 Steinar Bang
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

import no.priv.bang.beans.immutable.Immutable;

public class Transaction extends Immutable { // NOSONAR Immutable handles added fields
    private int id;
    private TransactionType transactionType;
    private Date transactionTime;
    private double transactionAmount;
    private boolean paidOut;

    private Transaction() {}

    public int getId() {
        return id;
    }

    public String getName() {
        return this.transactionType.getTransactionTypeName();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public boolean isPaidOut() {
        return paidOut;
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", transactionType=" + transactionType + ", transactionTime=" + transactionTime
            + ", transactionAmount=" + transactionAmount + "]";
    }

    public static TransactionBuilder with(Transaction transaction) {
        TransactionBuilder builder = new TransactionBuilder();
        builder.id = transaction.id;
        builder.transactionType = transaction.transactionType;
        builder.transactionTime = transaction.transactionTime;
        builder.transactionAmount = transaction.transactionAmount;
        builder.paidOut = transaction.paidOut;
        return builder;
    }

    public static TransactionBuilder with() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private int id = -1;
        private TransactionType transactionType = null;
        private Date transactionTime = null;
        private double transactionAmount = 0.0;
        private boolean paidOut = false;

        private TransactionBuilder() {}

        public Transaction build() {
            Transaction transaction = new Transaction();
            transaction.id = this.id;
            transaction.transactionType = this.transactionType;
            transaction.transactionTime = this.transactionTime;
            transaction.transactionAmount = this.transactionAmount;
            transaction.paidOut = this.paidOut;
            return transaction;
        }

        public TransactionBuilder id(int id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public TransactionBuilder transactionTime(Date transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public TransactionBuilder transactionAmount(double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public TransactionBuilder paidOut(boolean paidOut) {
            this.paidOut = paidOut;
            return this;
        }
    }
}
