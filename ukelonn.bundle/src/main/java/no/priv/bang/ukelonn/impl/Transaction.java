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

import java.util.Date;

public class Transaction {
    private int id;
    private TransactionType transactionType;
    private Date transactionTime;
    private double transactionAmount;
    private boolean paidOut;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        long temp;
        temp = Double.doubleToLongBits(transactionAmount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((transactionTime == null) ? 0 : transactionTime.hashCode());
        result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Transaction other = (Transaction) obj;
        if (id != other.id)
            return false;
        if (Double.doubleToLongBits(transactionAmount) != Double.doubleToLongBits(other.transactionAmount))
            return false;
        if (transactionTime == null) {
            if (other.transactionTime != null)
                return false;
        } else if (!transactionTime.equals(other.transactionTime))
            return false;
        if (transactionType == null) {
            if (other.transactionType != null)
                return false;
        } else if (!transactionType.equals(other.transactionType))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Transaction [id=" + id + ", transactionType=" + transactionType + ", transactionTime=" + transactionTime
            + ", transactionAmount=" + transactionAmount + "]";
    }
}
