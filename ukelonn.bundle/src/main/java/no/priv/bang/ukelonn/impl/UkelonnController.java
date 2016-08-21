package no.priv.bang.ukelonn.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "ukelonn")
@SessionScoped
public class UkelonnController {

    // properties
    private double balanse = 120;
    private String fornavn = "Ola";
    List<TransactionType> transactionTypes;
    List<Transaction> transactions;
    TransactionType newJobType;
    double newJobWages;

    public UkelonnController() {
    	transactionTypes = new ArrayList<TransactionType>();
    	transactionTypes.add(new TransactionType(1, "Støvsuging 1. etasje", 45.0, true, false));
    	transactionTypes.add(new TransactionType(2, "Støvsuging kjeller", 45.0, true, false));
    	transactionTypes.add(new TransactionType(3, "Gå med resirk", 35.0, true, false));
    	transactionTypes.add(new TransactionType(4, "Inn på konto", null, false, true));

    	transactions = new ArrayList<Transaction>();
    	transactions.add(new Transaction(transactionTypes.get(0), new Date(), 45.0));
    	transactions.add(new Transaction(transactionTypes.get(1), new Date(), 45.0));
    	transactions.add(new Transaction(transactionTypes.get(2), new Date(), 35.0));
    	transactions.add(new Transaction(transactionTypes.get(3), new Date(), -125.0));
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public Double getBalanse() {
        return balanse;
    }

    public void setBalanse(Double balanse) {
        this.balanse = balanse;
    }

    public List<TransactionType> getJobTypes() {
        ArrayList<TransactionType> jobTypes = new ArrayList<TransactionType>();
        for (TransactionType transactionType : transactionTypes) {
            if (transactionType.isTransactionIsWork()) {
                jobTypes.add(transactionType);
            }
        }

        return jobTypes;
    }

    public List<Transaction> getJobs() {
        ArrayList<Transaction> jobs = new ArrayList<Transaction>();
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType().isTransactionIsWork()) {
                jobs.add(transaction);
            }
        }

        return jobs;
    }

    public List<Transaction> getPayments() {
        ArrayList<Transaction> payments = new ArrayList<Transaction>();
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType().isTransactionIsWagePayment()) {
                payments.add(transaction);
            }
        }

        return payments;
    }

	public TransactionType getNewJobType() {
		return newJobType;
	}

	public void setNewJobType(TransactionType newJobType) {
		this.newJobType = newJobType;
	}
	
	public void newJobTypeSelected(final AjaxBehaviorEvent event) {
		if (newJobType != null && newJobType.getTransactionAmount() != null) {
			newJobWages = newJobType.getTransactionAmount();
		}
	}

	public double getNewJobWages() {
		return newJobWages;
	}

	public void setNewJobWages(double newJobWages) {
		this.newJobWages = newJobWages;
	}
	
	public void registerNewJob(ActionEvent event) {
		transactions.add(new Transaction(newJobType, new Date(), newJobWages));
	}
}
