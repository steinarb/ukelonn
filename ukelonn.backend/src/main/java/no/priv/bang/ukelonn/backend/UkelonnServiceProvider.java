/*
 * Copyright 2016-2017 Steinar Bang
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
package no.priv.bang.ukelonn.backend;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import static no.priv.bang.ukelonn.backend.CommonDatabaseMethods.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;

/**
 * The OSGi component that provides the business logic of the ukelonn
 * webapp.
 *
 * @author Steinar Bang
 *
 */
@Component(service=UkelonnService.class, immediate=true)
public class UkelonnServiceProvider extends UkelonnServiceBase {
    private UkelonnDatabase database;
    private LogService logservice;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Notification>> notificationQueues = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Notification>>();;

    @Activate
    public void activate() {
        // Nothing to do here
    }

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    @Override
    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    public LogService getLogservice() {
        return logservice;
    }

    @Override
    public List<Account> getAccounts() {
        return getAccountsFromDatabase(getClass(), this);
    }

    @Override
    public Account getAccount(String username) {
        return getAccountInfoFromDatabase(getClass(), this, username);
    }

    @Override
    public Account registerPerformedJob(PerformedTransaction job) {
        registerNewJobInDatabase(getClass(), this, job.getAccount(), job.getTransactionTypeId(), job.getTransactionAmount(), job.getTransactionDate());
        return getAccount(job.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> getJobTypes() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass(), this);
        return getJobTypesFromTransactionTypes(transactionTypes.values());
    }

    @Override
    public List<Transaction> getJobs(int accountId) {
        return getJobsFromAccount(accountId, getClass(), this);
    }

    @Override
    public List<Transaction> deleteJobsFromAccount(int accountId, List<Integer> idsOfJobsToDelete) {
        if (!idsOfJobsToDelete.isEmpty()) {
            String deleteQuery = "delete from transactions where transaction_id in (select transaction_id from transactions inner join transaction_types on transactions.transaction_type_id=transaction_types.transaction_type_id where transaction_id in (" + joinIds(idsOfJobsToDelete) + ") and transaction_types.transaction_is_work=? and account_id=?)";
            PreparedStatement statement = database.prepareStatement(deleteQuery);
            addParametersToDeleteJobsStatement(accountId, statement);
            database.update(statement);
        }

        return getJobs(accountId);
    }

    @Override
    public List<Transaction> updateJob(UpdatedTransaction editedJob) {
        String sql = "update transactions set transaction_type_id=?, transaction_time=?, transaction_amount=? where transaction_id=?";
        try(PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setInt(1, editedJob.getTransactionTypeId());
            statement.setTimestamp(2, new java.sql.Timestamp(editedJob.getTransactionTime().getTime()));
            statement.setDouble(3, editedJob.getTransactionAmount());
            statement.setInt(4, editedJob.getId());
            database.update(statement);
        } catch (SQLException e) {
            throw new UkelonnException(String.format("Failed to update job with id %d", editedJob.getId()) , e);
        }

        return getJobs(editedJob.getAccountId());
    }

    void addParametersToDeleteJobsStatement(int accountId, PreparedStatement statement) {
        try {
            statement.setBoolean(1, true);
            statement.setInt(2, accountId);
        } catch (SQLException e) {
            String message = "Caught exception adding parameters to job delete statement";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<Transaction> getPayments(int accountId) {
        return getPaymentsFromAccount(accountId, getClass(), this);
    }

    @Override
    public List<TransactionType> getPaymenttypes() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass(), this);
        return getPaymentTypesFromTransactionTypes(transactionTypes.values());
    }

    @Override
    public Account registerPayment(PerformedTransaction payment) {
        int result = addNewPaymentToAccountInDatabase(getClass(), this, payment.getAccount(), payment.getTransactionTypeId(), payment.getTransactionAmount(), new Date());
        if (result < 1) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to register payment of type %d, amount %f for user \"%s\"", payment.getTransactionTypeId(), payment.getTransactionAmount(), payment.getAccount().getUsername()));
            return null;
        }

        return getAccount(payment.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> modifyJobtype(TransactionType jobtype) {
        int result = updateTransactionTypeInDatabase(getClass(), this, jobtype);
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to update jobtype %d in the database", jobtype.getId()));
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> createJobtype(TransactionType jobtype) {
        int result = addJobTypeToDatabase(getClass(), this, jobtype.getTransactionTypeName(), jobtype.getTransactionAmount());
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to create jobtype \"%s\" in the database", jobtype.getTransactionTypeName()));
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> modifyPaymenttype(TransactionType paymenttype) {
        int result = updateTransactionTypeInDatabase(getClass(), this, paymenttype);
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to update payment type %d in the database", paymenttype.getId()));
        }

        return getPaymenttypes();
    }

    @Override
    public List<TransactionType> createPaymenttype(TransactionType paymenttype) {
        int result = addPaymentTypeToDatabase(getClass(), this, paymenttype.getTransactionTypeName(), paymenttype.getTransactionAmount());
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to create paymen type \"%s\" in the database", paymenttype.getTransactionTypeName()));
        }

        return getPaymenttypes();
    }

    @Override
    public List<User> getUsers() {
        return CommonDatabaseMethods.getUsers(getClass(), this);
    }

    @Override
    public List<User> modifyUser(User user) {
        int status = updateUserInDatabase(getClass(), this, user);
        if (status == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to update user %d in the database", user.getUserId()));
        }

        return getUsers();
    }

    @Override
    public List<User> createUser(PasswordsWithUser passwords) {
        if (!passwordsEqualsAndNotEmpty(passwords)) {
            throw new UkelonnBadRequestException("Passwords are not identical and/or empty");
        }

        try {
            addUserToDatabase(
                getClass(),
                this,
                passwords.getUser().getUsername(),
                passwords.getPassword(),
                passwords.getUser().getEmail(),
                passwords.getUser().getFirstname(),
                passwords.getUser().getLastname());

            return getUsers();
        } catch (UkelonnException e) {
            logservice.log(LogService.LOG_ERROR, "Database exception when creating user", e);
            throw e;
        }
    }

    @Override
    public List<User> changePassword(PasswordsWithUser passwords) {
        if (!hasUserWithNonEmptyUsername(passwords)) {
            String message = "Empty username when changing password";
            logservice.log(LogService.LOG_WARNING, String.format("Bad request: %s", message));
            throw new UkelonnBadRequestException(message);
        }

        if (!passwordsEqualsAndNotEmpty(passwords)) {
            String message = String.format("Passwords don't match and/or are empty when changing passwords for user \"%s\"", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_WARNING, String.format("Bad request: %s", message));
            throw new UkelonnBadRequestException(message);
        }

        int status = changePasswordForUser(passwords.getUser().getUsername(), passwords.getPassword(),getClass(), this);
        if (status == UPDATE_FAILED) {
            String message = String.format("Database failure when changing password for user \"%s\"", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message);
            throw new UkelonnException(message);
        }

        return getUsers();
    }

    @Override
    public List<Notification> notificationsTo(String username) {
        ConcurrentLinkedQueue<Notification> notifications = getNotificationQueueForUser(username);
        Notification notification = notifications.poll();
        if (notification == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(notification);
    }

    @Override
    public void notificationTo(String username, Notification notification) {
        ConcurrentLinkedQueue<Notification> notifications = getNotificationQueueForUser(username);
        notifications.add(notification);
    }

    private ConcurrentLinkedQueue<Notification> getNotificationQueueForUser(String username) {
        ConcurrentLinkedQueue<Notification> queue = notificationQueues.get(username);
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<>();
            notificationQueues.put(username, queue);
        }

        return queue;
    }

    static boolean passwordsEqualsAndNotEmpty(PasswordsWithUser passwords) {
        if (passwords.getPassword() == null || passwords.getPassword().isEmpty()) {
            return false;
        }

        return passwords.getPassword().equals(passwords.getPassword2());
    }

    static StringBuilder joinIds(List<Integer> ids) {
        StringBuilder commaList = new StringBuilder();
        if (ids == null) {
            return commaList;
        }

        Iterator<Integer> iterator = ids.iterator();
        if (!iterator.hasNext()) {
            return commaList; // Return an empty string builder instead of a null
        }

        commaList.append(iterator.next());
        while(iterator.hasNext()) {
            commaList.append(", ").append(iterator.next());
        }

        return commaList;
    }

    static boolean hasUserWithNonEmptyUsername(PasswordsWithUser passwords) {
        User user = passwords.getUser();
        if (user == null) {
            return false;
        }

        String username = user.getUsername();
        if (username == null) {
            return false;
        }

        return !username.isEmpty();
    }

}
