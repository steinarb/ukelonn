package no.priv.bang.ukelonn.impl;

import java.sql.PreparedStatement;
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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import no.priv.bang.ukelonn.UkelonnDatabase;
import static no.priv.bang.ukelonn.impl.CommonServiceMethods.*;
import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import static no.priv.bang.ukelonn.impl.CommonStringMethods.*;

@ManagedBean(name = "ukelonnAdmin")
@ViewScoped
public class UkelonnAdminController {
    static final Integer PAY_TO_BANK_ID = 4;
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
    private String newJobTypeName;
    private double amount;
    private String newPaymentTypeName;
    private double newPaymentTypeAmount;
    private String newUserUsername;
    private String newUserPassword1;
    private String newUserPassword2;
    private String newUserEmail;
    private String newUserFirstname;
    private String newUserLastname;
    private User changePasswordForUser;
    private String changePasswordForUserPassword1;
    private String changePasswordForUserPassword2;
    private List<Transaction> jobsSelectedForDelete;

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
        UkelonnDatabase database = CommonDatabaseMethods.connectionCheck(getClass());
        PreparedStatement statement = database.prepareStatement("select * from administrators_view where username=?");
        try {
            statement.setString(1, username);
            ResultSet resultset = database.query(statement);
            if (resultset != null) {
                if (resultset.next()) {
                    setAdministratorUserId(resultset.getInt("user_id"));
                    setAdministratorId(resultset.getInt("administrator_id"));
                    setAdministratorFornavn(resultset.getString("first_name"));
                    setAdministratorEtternavn(resultset.getString("last_name"));
                }

                transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
            }
        } catch (SQLException e) {
            logError(UkelonnAdminController.class, "Error getting administrator user info from the database", e);
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
        setBankAsDefaultPaymentTypeWithBalanceAsAmount();
    }

    private void setBankAsDefaultPaymentTypeWithBalanceAsAmount() {
        TransactionType payToBank = findPayToBank(getPaymentTypes());
        setNewPaymentType(payToBank);
        setNewPayment(getBalanse());
    }

    TransactionType findPayToBank(List<TransactionType> paymentTypes) {
        // TODO Auto-generated method stub
        for (TransactionType transactionType : paymentTypes) {
            if (transactionType.getId() == PAY_TO_BANK_ID) {
                return transactionType;
            }
        }

        return null;
    }

    public List<Account> getAccounts() {
        return CommonDatabaseMethods.getAccounts(getClass());
    }

    public double getBalanse() {
        return account != null ? account.getBalance() : 0.0;
    }

    public List<Transaction> getJobs() {
        return getJobsFromAccount(account, getClass());
    }

    public List<Transaction> getPayments() {
        return getPaymentsFromAccount(account, getClass());
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

    public String getNewJobTypeName() {
        return newJobTypeName;
    }

    public void setNewJobTypeName(String newJobTypeName) {
        this.newJobTypeName = newJobTypeName;
    }

    public double getNewJobTypeAmount() {
        return amount;
    }

    public void setNewJobTypeAmount(double amount) {
        this.amount = amount;
    }

    public void registerNewJobType(ActionEvent event) {
        if (getNewJobTypeName() != null && getNewJobTypeName().length() > 0 && getNewJobTypeAmount() > 0.0) {
            addJobTypeToDatabase(getClass(), getNewJobTypeName(), getNewJobTypeAmount());
            clearNewJobTypeValues();
            transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        }
    }

    private void clearNewJobTypeValues() {
        setNewJobTypeName(null);
        setNewJobTypeAmount(0.0);
    }

    public List<TransactionType> getJobTypes() {
        return getJobTypesFromTransactionTypes(transactionTypes.values());
    }

    public void onJobTypeCellEdit(CellEditEvent event) {
        /*     if (event.getNewValue() == event.getOldValue()) {
               return; // No value change means no updates done
               }
        */
        DataTable dataTable = (DataTable) event.getComponent();
        TransactionType editedJobType = (TransactionType) dataTable.getRowData();
        /*     String headerText = event.getColumn().getHeaderText();
               String newValue = (String) event.getNewValue();
               if (headerText == "Navn") {
               editedJobType.setTransactionTypeName(newValue);
               } else if (headerText == "Beløp") {
               editedJobType.setTransactionAmount(Double.valueOf(newValue));
               }
        */
        updateTransactionTypeInDatabase(getClass(), editedJobType);
    }

    public String getNewPaymentTypeName() {
        return newPaymentTypeName;
    }

    public void setNewPaymentTypeName(String newPaymentTypeName) {
        this.newPaymentTypeName = newPaymentTypeName;
    }

    public double getNewPaymentTypeAmount() {
        return newPaymentTypeAmount;
    }

    public void setNewPaymentTypeAmount(double amount) {
        this.newPaymentTypeAmount = amount;
    }

    public void registerNewPaymentType(ActionEvent event) {
        if (getNewPaymentTypeName() != null && getNewPaymentTypeName().length() > 0) {
            addPaymentTypeToDatabase(getClass(), getNewPaymentTypeName(), getNewPaymentTypeAmount());
            clearNewPaymentTypeValues();
            transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        }
    }

    private void clearNewPaymentTypeValues() {
        newPaymentTypeName = null;
        newPaymentTypeAmount = 0.0;
    }

    public String getNewUserUsername() {
        return newUserUsername;
    }

    public void setNewUserUsername(String username) {
        newUserUsername = safeTrim(username);
    }

    public String getNewUserPassword1() {
        return newUserPassword1;
    }

    public void setNewUserPassword1(String password) {
        newUserPassword1 = safeTrim(password);
    }

    public String getNewUserPassword2() {
        return newUserPassword2;
    }

    public void setNewUserPassword2(String password) {
        newUserPassword2 = safeTrim(password);
    }

    public String getNewUserEmail() {
        return newUserEmail;
    }

    public void setNewUserEmail(String emailAddress) {
        newUserEmail = safeTrim(emailAddress);
    }

    public String getNewUserFirstname() {
        return newUserFirstname;
    }

    public void setNewUserFirstname(String firstname) {
        newUserFirstname = safeTrim(firstname);
    }

    public String getNewUserLastname() {
        return newUserLastname;
    }

    public void setNewUserLastname(String lastname) {
        newUserLastname = safeTrim(lastname);
    }

    public void registerNewUser(ActionEvent event) {
        if (!isNullEmptyOrBlank(getNewUserUsername()) &&
            !isNullEmptyOrBlank(getNewUserEmail()) &&
            !isNullEmptyOrBlank(getNewUserPassword1()) &&
            !isNullEmptyOrBlank(getNewUserPassword2()) &&
            getNewUserPassword1().equals(getNewUserPassword2()) &&
            !isNullEmptyOrBlank(getNewUserFirstname()) &&
            !isNullEmptyOrBlank(getNewUserLastname()))
        {
            addUserToDatabase(
                getClass(),
                getNewUserUsername(),
                getNewUserPassword1(),
                getNewUserEmail(),
                getNewUserFirstname(),
                getNewUserLastname()
                              );
            clearNewUserValues();
        }
    }

    private void clearNewUserValues() {
        newUserUsername = null;
        newUserPassword1 = null;
        newUserPassword2 = null;
        newUserEmail = null;
        newUserFirstname = null;
        newUserLastname = null;
    }

    public List<User> getUsers() {
        return CommonDatabaseMethods.getUsers(getClass());
    }

    public User getChangePasswordForUser() {
        return changePasswordForUser;
    }

    public void setChangePasswordForUser(User user) {
        if (!isTheSameUser(changePasswordForUser, user))
        {
            this.changePasswordForUser = user;
            blankPasswords();
        }
    }

    private void blankPasswords() {
        setChangePasswordForUserPassword1(null);
        setChangePasswordForUserPassword2(null);
    }

    public String getChangePasswordForUserPassword1() {
        return changePasswordForUserPassword1;
    }

    public void setChangePasswordForUserPassword1(String password) {
        this.changePasswordForUserPassword1 = password;
    }

    public String getChangePasswordForUserPassword2() {
        return changePasswordForUserPassword2;
    }

    public void setChangePasswordForUserPassword2(String password) {
        this.changePasswordForUserPassword2 = password;
    }

    public void changeUserPassword(ActionEvent event) {
        if (getChangePasswordForUser() != null &&
            getChangePasswordForUserPassword1() != null &&
            getChangePasswordForUserPassword2() != null &&
            getChangePasswordForUserPassword1().equals(getChangePasswordForUserPassword2()))
        {
            CommonDatabaseMethods.changePasswordForUser(getChangePasswordForUser().getUsername(), getChangePasswordForUserPassword1(), getClass());
            setChangePasswordForUser(null); // Null user and passwords
        }
    }

    public static boolean isTheSameUser(User user1, User user2) {
        if  (user1 == user2) {
            return true;
        }

        if (user1 != null && user1.equals(user2)) {
            return true;
        }

        return false;
    }

    public void setJobsSelectedForDelete(List<Transaction> jobsWithCheckboxChecked) {
        this.jobsSelectedForDelete = jobsWithCheckboxChecked;
    }

    public List<Transaction> getJobsSelectedForDelete() {
        return jobsSelectedForDelete;
    }

    public void deleteSelectedJobs(ActionEvent event) {
        CommonDatabaseMethods.deleteTransactions(getClass(), getJobsSelectedForDelete());
        transactionTypes = refreshAccount(getClass(), account);
        if (jobsSelectedForDelete != null) {
            try {
                jobsSelectedForDelete.clear();
            } catch(UnsupportedOperationException e) {
                // The list of jobs to delete was unmodifiable, skip and continue
            }
        }
    }

}
