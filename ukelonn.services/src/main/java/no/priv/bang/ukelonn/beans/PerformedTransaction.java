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

import static java.util.Optional.ofNullable;

import java.util.Date;

public record PerformedTransaction(Account account, int transactionTypeId, double transactionAmount, Date transactionDate) {

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
            var transactionTypeId = ofNullable(this.transactionTypeId).orElse(-1);
            var transactionAmount = ofNullable(this.transactionAmount).orElse(0.0);
            var transactionDate = ofNullable(this.transactionDate).orElse(new Date());
            return new PerformedTransaction(this.account, transactionTypeId, transactionAmount, transactionDate);
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
