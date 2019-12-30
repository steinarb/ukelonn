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

import java.util.Date;

import no.priv.bang.beans.immutable.Immutable;

public class Transaction extends Immutable {
    private int id = -1;
    private TransactionType transactionType = null;
    private Date transactionTime = null;
    private double transactionAmount = 0.0;
    private boolean paidOut = false;

    public Transaction() {
        // No-argument constructor required by Jackson
    }


    public Transaction(int id,TransactionType transactionType, Date transactionTime, double transactionAmount, boolean paidOut) {
        setId(id);
        setTransactionType(transactionType);
        setTransactionTime(transactionTime);
        setTransactionAmount(transactionAmount);
        setPaidOut(paidOut);
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.transactionType.getTransactionTypeName();
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }


    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }


    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public boolean isPaidOut() {
        return paidOut;
    }

    public void setPaidOut(boolean paidOut) {
        this.paidOut = paidOut;
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", transactionType=" + transactionType + ", transactionTime=" + transactionTime
            + ", transactionAmount=" + transactionAmount + "]";
    }
}
