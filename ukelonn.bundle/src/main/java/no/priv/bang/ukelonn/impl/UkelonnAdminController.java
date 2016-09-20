package no.priv.bang.ukelonn.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.shiro.SecurityUtils;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

@ManagedBean(name = "ukelonnAdmin")
@SessionScoped
public class UkelonnAdminController {
    // properties
    private String administratorUsername;
    private int administratorUserId = 0;
    private int administratorId = 0;
    private String administratorFornavn = "Ikke innlogget";
    private String administratorEtternavn = "";
    private Account account;
    private List<Transaction> transactions = Collections.emptyList();
    private Map<Integer, TransactionType> transactionTypes;

    public UkelonnAdminController() {
        super();
        try {
            String principal = (String) SecurityUtils.getSubject().getPrincipal();
            setAdministratorUsername(principal);
        } catch (Exception e) {
            // Nothing, just proceed without a user name
        }
    }

    public String getAdministratorUsername() {
        return administratorUsername;
    }

    private void getAdministratorUserInfoFromDatabase(String username) {
        UkelonnDatabase database = connectionCheck();
        StringBuffer sql = new StringBuffer("select * from administrators_view where username='");
        sql.append(username);
        sql.append("'");
        ResultSet resultset = database.query(sql.toString());
        if (resultset != null) {
            try {
                if (resultset.next()) {
                    setAdministratorUserId(resultset.getInt("user_id"));
                    setAdministratorId(resultset.getInt("administrator_id"));
                    setAdministratorFornavn(resultset.getString("first_name"));
                    setAdministratorEtternavn(resultset.getString("last_name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAdministratorUsername(String administratorUsername) {
        this.administratorUsername = administratorUsername;
        getAdministratorUserInfoFromDatabase(administratorUsername);
    }
    public int getAdministratorUserId() {
        return administratorUserId;
    }
    public void setAdministratorUserId(int administratorUserId) {
        this.administratorUserId = administratorUserId;
    }
    public int getAdministratorId() {
        return administratorId;
    }
    public void setAdministratorId(int administratorId) {
        this.administratorId = administratorId;
    }
    public String getAdministratorFornavn() {
        return administratorFornavn;
    }
    public void setAdministratorFornavn(String administratorFornavn) {
        this.administratorFornavn = administratorFornavn;
    }
    public String getAdministratorEtternavn() {
        return administratorEtternavn;
    }
    public void setAdministratorEtternavn(String administratorEtternavn) {
        this.administratorEtternavn = administratorEtternavn;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        transactions = new ArrayList<Transaction>();
        updateBalanseFromDatabase(account);
        transactionTypes = getTransactionTypesFromUkelonnDatabase();
        transactions = getTransactionsFromUkelonnDatabase(transactionTypes, account.getAccountId());
    }

    public void updateBalanseFromDatabase(Account account) {
        UkelonnDatabase connection = connectionCheck();
        StringBuffer sql = new StringBuffer("select * from accounts_view where account_id=");
        sql.append(account.getAccountId());
        ResultSet results = connection.query(sql.toString());
        if (results != null) {
            try {
                while (results.next()) {
                    double balance = results.getDouble("balance");
                    account.setBalance(balance);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void newAccountSelected(final AjaxBehaviorEvent event) {
    }

    public List<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();
        UkelonnDatabase connection = connectionCheck();
        ResultSet results = connection.query("select * from accounts_view");
        if (results != null) {
            try {
                while(results.next()) {
                    Account newaccount = new Account(
                                                     results.getInt("account_id"),
                                                     results.getInt("user_id"),
                                                     results.getString("username"),
                                                     results.getString("first_name"),
                                                     results.getString("last_name"),
                                                     results.getDouble("balance")
                                                     );
                    accounts.add(newaccount);
                }
            } catch (SQLException e) {
                // Skip and continue
                e.printStackTrace();
            }
        }

        return accounts;
    }

    public double getBalanse() {
        return 0.0;
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

    private UkelonnDatabase connectionCheck() {
        UkelonnService ukelonnService = UkelonnServiceProvider.getInstance();
        if (ukelonnService == null) {
            throw new RuntimeException("UkelonnAdminController bean unable to find OSGi service Ukelonnservice, giving up");
        }

        UkelonnDatabase database = ukelonnService.getDatabase();
        if (database == null) {
            throw new RuntimeException("UkelonnAdminController bean unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
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

    private List<Transaction> getTransactionsFromUkelonnDatabase(Map<Integer, TransactionType> transactionTypes, int accountid) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        UkelonnDatabase database = connectionCheck();
        StringBuffer sql = new StringBuffer("select * from transactions where account_id=");
        sql.append(accountid);
        ResultSet resultSet = database.query(sql.toString());
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(transactionTypes, resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }

    private Transaction mapTransaction(Map<Integer, TransactionType> transactionTypes, ResultSet resultset) throws SQLException {
        Transaction transaction =
            new Transaction(
                            resultset.getInt("transaction_id"),
                            transactionTypes.get(resultset.getInt("transaction_type_id")),
                            resultset.getDate("transaction_time"),
                            resultset.getDouble("transaction_amount")
                            );
        return transaction;
    }

}
