package no.priv.bang.ukelonn.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.shiro.SecurityUtils;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

@ManagedBean(name = "ukelonn")
@ViewScoped
public class UkelonnController {

    // properties
    private Account account;
    Map<Integer, TransactionType> transactionTypes = Collections.emptyMap();
    TransactionType newJobType;
    double newJobWages;
    private List<Transaction> jobsSelectedForDelete;

    public UkelonnController() {
        super();
        try {
            String principal = (String) SecurityUtils.getSubject().getPrincipal();
            setUsername(principal);
        } catch (Exception e) {
            // Nothing, just proceed without a user name
        }
    }

    public int getUserId() {
        return account != null ? account.getUserId() : 0;
    }

    public int getAccountId() {
        return account != null ? account.getAccountId() : 0;
    }

    public String getUsername() {
        return account != null ? account.getUsername() : null;
    }

    public void setUsername(String username) {
        account = getAccountInfoFromDatabase(getClass(), username);
    	transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
    }

    public String getFornavn() {
        return account != null ? account.getFirstName() : "Ikke innlogget";
    }

    public void setFornavn(String fornavn) {
        account.setFirstName(fornavn);
    }

    public String getEtternavn() {
        return account != null ? account.getLastName() : "";
    }

    public void setEtternavn(String etternavn) {
        account.setLastName(etternavn);
    }

    public void redirectAdministratorsToAdminPage() throws IOException {
    	if (isAdministrator()) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/admin/index.xhtml");
    	}
    }

    private boolean isAdministrator() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("administrator");
    }

    public Double getBalanse() {
        return account != null ? account.getBalance() : 0.0;
    }

    public void setBalanse(Double balanse) {
        account.setBalance(balanse);
    }

    public List<TransactionType> getJobTypes() {
        return getJobTypesFromTransactionTypes(transactionTypes.values());
    }

    public List<Transaction> getJobs() {
        return getJobsFromAccount(account, getClass());
    }

    public List<Transaction> getPayments() {
        return getPaymentsFromAccount(account, getClass());
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
        transactionTypes = registerNewJobInDatabase(getClass(), account, getNewJobType().getId(), getNewJobWages());
        clearNewJobValues();
    }

    private void clearNewJobValues() {
        setNewJobType(null);
        setNewJobWages(0.0);
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
