package no.priv.bang.ukelonn.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.shiro.SecurityUtils;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

@ManagedBean(name = "ukelonn")
@SessionScoped
public class UkelonnController {

    // properties
    private String username;
    private int userId = 0;
    private int accountId = 0;
    private double balanse = 0;
    private String fornavn = "";
    private String etternavn = "";
    Map<Integer, TransactionType> transactionTypes;
    List<Transaction> transactions;
    TransactionType newJobType;
    double newJobWages;

    public UkelonnController() {
        super();
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        setUsername(principal);
    }

    public int getUserId() {
        return userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        getAccountInfoFromDatabase(username);
    	transactionTypes = getTransactionTypesFromUkelonnDatabase();
    	transactions = getTransactionsFromUkelonnDatabase(getAccountId());
    }

    private void getAccountInfoFromDatabase(String username) {
        UkelonnDatabase database = connectionCheck();
        StringBuffer sql = new StringBuffer("select * from accounts_view where username='");
        sql.append(username);
        sql.append("'");
        ResultSet resultset = database.query(sql.toString());
        if (resultset != null) {
            try {
                if (resultset.next()) {
                    userId = resultset.getInt("user_id");
                    accountId = resultset.getInt("account_id");
                    balanse = resultset.getDouble("balance");
                    fornavn = resultset.getString("first_name");
                    etternavn = resultset.getString("last_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public Double getBalanse() {
        return balanse;
    }

    public void setBalanse(Double balanse) {
        this.balanse = balanse;
    }

    public List<TransactionType> getJobTypes() {
        ArrayList<TransactionType> jobTypes = new ArrayList<TransactionType>();
        for (TransactionType transactionType : transactionTypes.values()) {
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
        StringBuffer sql = new StringBuffer("insert into transactions (account_id,transaction_type_id,transaction_amount) values (");
        sql.append(getAccountId());
        sql.append(",");
        sql.append(getNewJobType().getId());
        sql.append(",");
        sql.append(getNewJobWages());
        sql.append(")");

        UkelonnDatabase database = connectionCheck();
        database.update(sql.toString());

        // Update the list of jobs and the updated balance from the DB
        getAccountInfoFromDatabase(username);
    	transactions = getTransactionsFromUkelonnDatabase(getAccountId());
    }

    private Map<Integer, TransactionType> getTransactionTypesFromUkelonnDatabase() {
        Map<Integer, TransactionType> transactiontypes = new Hashtable<Integer, TransactionType>();
        UkelonnDatabase database = connectionCheck();
        ResultSet resultSet = database.query("select * from transaction_types");
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    TransactionType transactiontype = mapTransactionType(resultSet);
                    transactiontypes.put(transactiontype.getId(), transactiontype);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return transactiontypes;
    }

    private TransactionType mapTransactionType(ResultSet resultset) throws SQLException {
        TransactionType transactionType =
            new TransactionType(
                                resultset.getInt("transaction_type_id"),
                                resultset.getString("transaction_type_name"),
                                resultset.getDouble("transaction_amount"),
                                resultset.getBoolean("transaction_is_work"),
                                resultset.getBoolean("transaction_is_wage_payment")
                                );
        return transactionType;
    }

    private List<Transaction> getTransactionsFromUkelonnDatabase(int accountid) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        UkelonnDatabase database = connectionCheck();
        StringBuffer sql = new StringBuffer("select * from transactions where account_id=");
        sql.append(accountid);
        ResultSet resultSet = database.query(sql.toString());
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }

    private Transaction mapTransaction(ResultSet resultset) throws SQLException {
        Transaction transaction =
            new Transaction(
                            resultset.getInt("transaction_id"),
                            transactionTypes.get(resultset.getInt("transaction_type_id")),
                            resultset.getDate("transaction_time"),
                            resultset.getDouble("transaction_amount")
                            );
        return transaction;
    }

    private UkelonnDatabase connectionCheck() {
        UkelonnService ukelonnService = UkelonnServiceProvider.getInstance();
        if (ukelonnService == null) {
            throw new RuntimeException("UkelonnController bean unable to find OSGi service Ukelonnservice, giving up");
        }

        UkelonnDatabase database = ukelonnService.getDatabase();
        if (database == null) {
            throw new RuntimeException("UkelonnController bean unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
    }
}
