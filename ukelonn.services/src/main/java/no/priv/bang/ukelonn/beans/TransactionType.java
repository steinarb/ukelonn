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

import no.priv.bang.beans.immutable.Immutable;

public class TransactionType extends Immutable { // NOSONAR Immutable handles added fields
    private int id;
    private String transactionTypeName;
    private Double transactionAmount;
    private boolean transactionIsWork;
    private boolean transactionIsWagePayment;

    public TransactionType() {
        // Jackson requires no-argument constructor
    }

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
    public String toString() {
        return "TransactionType [id=" + id + ", transactionTypeName=" + transactionTypeName + ", transactionAmount="
            + transactionAmount + ", transactionIsWork=" + transactionIsWork + ", transactionIsWagePayment="
            + transactionIsWagePayment + "]";
    }
}
