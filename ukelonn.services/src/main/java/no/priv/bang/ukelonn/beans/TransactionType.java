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

public record TransactionType(
    int id,
    String transactionTypeName,
    Double transactionAmount,
    boolean transactionIsWork,
    boolean transactionIsWagePayment)
{

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
            return new TransactionType(id, transactionTypeName, transactionAmount, transactionIsWork, transactionIsWagePayment);
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
