package no.priv.bang.ukelonn.impl;

import java.util.Date;

public class Transaction {
    private TransactionType transactionType;
    private Date transactionTime;
    private double transactionAmount;

    public Transaction(TransactionType transactionType, Date transactionTime, double transactionAmount) {
        setTransactionType(transactionType);
        setTransactionTime(transactionTime);
        setTransactionAmount(transactionAmount);
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
}
