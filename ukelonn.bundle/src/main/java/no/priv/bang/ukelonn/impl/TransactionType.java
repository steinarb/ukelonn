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

public class TransactionType {
    private int id;
    private String transactionTypeName;
    private Double transactionAmount;
    private boolean transactionIsWork;
    private boolean transactionIsWagePayment;

    public TransactionType(int id, String transactionTypeName, Double transactionAmount, boolean transactionIsWork, boolean transactionIsWagePayment) {
        this.id = id;
        setTransactionTypeName(transactionTypeName);
        setTransactionAmount(transactionAmount);
        setTransactionIsWork(transactionIsWork);
        setTransactionIsWagePayment(transactionIsWagePayment);
    }

    public Integer getId() {
        return Integer.valueOf(id);
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }


    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    public boolean isTransactionIsWork() {
        return transactionIsWork;
    }

    public void setTransactionIsWork(boolean transactionIsWork) {
        this.transactionIsWork = transactionIsWork;
    }


    public boolean isTransactionIsWagePayment() {
        return transactionIsWagePayment;
    }

    public void setTransactionIsWagePayment(boolean transactionIsWagePayment) {
        this.transactionIsWagePayment = transactionIsWagePayment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((transactionAmount == null) ? 0 : transactionAmount.hashCode());
        result = prime * result + (transactionIsWagePayment ? 1231 : 1237);
        result = prime * result + (transactionIsWork ? 1231 : 1237);
        result = prime * result + ((transactionTypeName == null) ? 0 : transactionTypeName.hashCode());
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
        TransactionType other = (TransactionType) obj;
        if (id != other.id)
            return false;
        if (transactionAmount == null) {
            if (other.transactionAmount != null)
                return false;
        } else if (!transactionAmount.equals(other.transactionAmount))
            return false;
        if (transactionIsWagePayment != other.transactionIsWagePayment)
            return false;
        if (transactionIsWork != other.transactionIsWork)
            return false;
        if (transactionTypeName == null) {
            if (other.transactionTypeName != null)
                return false;
        } else if (!transactionTypeName.equals(other.transactionTypeName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TransactionType [id=" + id + ", transactionTypeName=" + transactionTypeName + ", transactionAmount="
            + transactionAmount + ", transactionIsWork=" + transactionIsWork + ", transactionIsWagePayment="
            + transactionIsWagePayment + "]";
    }
}
