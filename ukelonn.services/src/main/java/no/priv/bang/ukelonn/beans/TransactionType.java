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

import no.priv.bang.beans.immutable.Immutable;

public class TransactionType extends Immutable { // NOSONAR Immutable handles added fields
    private int id;
    private String transactionTypeName;
    private Double transactionAmount;
    private boolean transactionIsWork;
    private boolean transactionIsWagePayment;

    private TransactionType() {}

    public Integer getId() {
        return Integer.valueOf(id);
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public boolean isTransactionIsWork() {
        return transactionIsWork;
    }

    public boolean isTransactionIsWagePayment() {
        return transactionIsWagePayment;
    }

    @Override
    public String toString() {
        return "TransactionType [id=" + id + ", transactionTypeName=" + transactionTypeName + ", transactionAmount="
            + transactionAmount + ", transactionIsWork=" + transactionIsWork + ", transactionIsWagePayment="
            + transactionIsWagePayment + "]";
    }

    public static Builder with(TransactionType transactiontype) {
        Builder builder = new Builder();
        builder.id = transactiontype.id;
        builder.transactionTypeName = transactiontype.transactionTypeName;
        builder.transactionAmount = transactiontype.transactionAmount;
        builder.transactionIsWork = transactiontype.transactionIsWork;
        builder.transactionIsWagePayment = transactiontype.transactionIsWagePayment;
        return builder;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String transactionTypeName;
        private Double transactionAmount;
        private boolean transactionIsWork;
        private boolean transactionIsWagePayment;

        private Builder() {}

        public TransactionType build() {
            TransactionType transactionType = new TransactionType();
            transactionType.id = this.id;
            transactionType.transactionTypeName = this.transactionTypeName;
            transactionType.transactionAmount = this.transactionAmount;
            transactionType.transactionIsWork = this.transactionIsWork;
            transactionType.transactionIsWagePayment = this.transactionIsWagePayment;
            return transactionType;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder transactionTypeName(String transactionTypeName) {
            this.transactionTypeName = transactionTypeName;
            return this;
        }

        public Builder transactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder transactionIsWork(boolean transactionIsWork) {
            this.transactionIsWork = transactionIsWork;
            return this;
        }

        public Builder transactionIsWagePayment(boolean transactionIsWagePayment) {
            this.transactionIsWagePayment = transactionIsWagePayment;
            return this;
        }
    }
}
