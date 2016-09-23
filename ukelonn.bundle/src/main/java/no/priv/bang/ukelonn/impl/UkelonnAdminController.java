package no.priv.bang.ukelonn.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.shiro.SecurityUtils;

import no.priv.bang.ukelonn.UkelonnDatabase;
import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

@ManagedBean(name = "ukelonnAdmin")
@ViewScoped
public class UkelonnAdminController {
    // properties
    private String administratorUsername;
    private int administratorUserId = 0;
    private int administratorId = 0;
    private String administratorFornavn = "Ikke innlogget";
    private String administratorEtternavn = "";
    private Account account;
    private Map<Integer, TransactionType> transactionTypes = Collections.emptyMap();
    private double newPayment;
    private TransactionType paymentType;

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
        UkelonnDatabase database = connectionCheck(getClass());
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

                transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
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
        transactionTypes = refreshAccount(getClass(), account);
    }

    public List<Account> getAccounts() {
    	return CommonDatabaseMethods.getAccounts(getClass());
    }

    public double getBalanse() {
        return account != null ? account.getBalance() : 0.0;
    }

    public List<Transaction> getJobs() {
        ArrayList<Transaction> jobs = new ArrayList<Transaction>();
        if (account != null) {
            for (Transaction transaction : account.getTransactions()) {
                if (transaction.getTransactionType().isTransactionIsWork()) {
                    jobs.add(transaction);
                }
            }
        }

        return jobs;
    }

    public List<Transaction> getPayments() {
        ArrayList<Transaction> payments = new ArrayList<Transaction>();
        if (account != null) {
            for (Transaction transaction : account.getTransactions()) {
                if (transaction.getTransactionType().isTransactionIsWagePayment()) {
                    payments.add(transaction);
                }
            }
        }

        return payments;
    }

    public ArrayList<TransactionType> getPaymentTypes() {
        ArrayList<TransactionType> paymentTypes = new ArrayList<TransactionType>();
        for (TransactionType transactionType : transactionTypes.values()) {
            if (transactionType.isTransactionIsWagePayment()) {
                paymentTypes.add(transactionType);
            }
        }

        return paymentTypes;
    }

    public void newPaymentTypeSelected(final AjaxBehaviorEvent event) {
    }

    public TransactionType getNewPaymentType() {
        return paymentType;
    }

    public void setNewPaymentType(TransactionType paymentType) {
        // TODO Auto-generated method stub
        this.paymentType = paymentType;
    }

    public double getNewPayment() {
        return newPayment;
    }

    public void setNewPayment(double newAmount) {
        newPayment = Math.abs(newAmount);
    }

    public void registerNewPayment(ActionEvent event) {
        if (account != null && getNewPaymentType() != null && getNewPayment() > 0.0) {
            addNewPaymentToAccount(getClass(), getAccount(), getNewPaymentType(), getNewPayment());
            transactionTypes = refreshAccount(getClass(), account);
        }

        setNewPaymentType(null);
        setNewPayment(0.0);
    }

}
