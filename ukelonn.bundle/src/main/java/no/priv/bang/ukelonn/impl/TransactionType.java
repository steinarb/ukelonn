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

        TransactionTypeConverter.registerTransactionType(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        return true;
    }


    @Override
    public String toString() {
        return "TransactionType [id=" + id + "]";
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
}
