/*
 * Copyright 2018 Steinar Bang
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

    public UpdatedTransaction(int id, int accountId, int transactionTypeId, Date transactionTime, double transactionAmount) {
        this.id = id;
        this.accountId = accountId;
        this.transactionTypeId = transactionTypeId;
        this.transactionTime = transactionTime;
        this.transactionAmount = transactionAmount;
    }

    public UpdatedTransaction() {
        this(-1, -1, -1, null, 0.0);
        // No-args constructor required by jackson
    }

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

}
