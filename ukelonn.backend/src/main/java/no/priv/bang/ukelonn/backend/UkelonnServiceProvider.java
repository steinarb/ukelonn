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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.SumYearMonth;
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
    private UserManagementService useradmin;
    private LogService logservice;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Notification>> notificationQueues = new ConcurrentHashMap<>();
    static final String LAST_NAME = "last_name";
    static final String FIRST_NAME = "first_name";
    static final String USERNAME = "username";
    static final int NUMBER_OF_TRANSACTIONS_TO_DISPLAY = 10;
    static final String USER_ID = "user_id";

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
    public void setUserAdmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
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
        List<Account> accounts = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from accounts_view")) {
                try(ResultSet results = statement.executeQuery()) {
                    if (results != null) {
                        while(results.next()) {
                            Account newaccount = mapAccount(results);
                            accounts.add(newaccount);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Log and continue
            logError("Error when getting all accounts from the database", e);
        }

        return accounts;
    }

    @Override
    public Account getAccount(String username) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, username);
                try(ResultSet resultset = statement.executeQuery()) {
                    if (resultset.next())
                    {
                        return mapAccount(resultset);
                    }

                    throw new UkelonnException(String.format("Got an empty ResultSet while fetching account from the database for user \\\"%s\\\"", username));
                }

            }
        } catch (SQLException e) {
            throw new UkelonnException(String.format("Caught SQLException while fetching account from the database for user \"%s\"", username), e);
        }
    }

    @Override
    public Account registerPerformedJob(PerformedTransaction job) {
        int accountId = job.getAccount().getAccountId();
        int jobtypeId = job.getTransactionTypeId();
        double jobamount = job.getTransactionAmount();
        Date timeofjob = job.getTransactionDate();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into transactions (account_id, transaction_type_id,transaction_amount, transaction_time) values (?, ?, ?, ?)")) {
                statement.setInt(1, accountId);
                statement.setInt(2, jobtypeId);
                statement.setDouble(3, jobamount);
                statement.setTimestamp(4, new java.sql.Timestamp(timeofjob.getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            String message = String.format("Failed to register performed job in the database, account: %d  jobtype: %d  amount: %f", accountId, jobtypeId, jobamount);
            logError(message, exception);
        }

        return getAccount(job.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> getJobTypes() {
        List<TransactionType> jobtypes = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from transaction_types where transaction_is_work=true")) {
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            TransactionType transactiontype = UkelonnServiceProvider.mapTransactionType(resultSet);
                            jobtypes.add(transactiontype);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logError("Error getting job types from the database", e);
        }

        return jobtypes;
    }

    @Override
    public List<Transaction> getJobs(int accountId) {
        return getTransactionsFromAccount(accountId, "/sql/query/jobs_last_n.sql", "job");
    }

    @Override
    public List<Transaction> getPayments(int accountId) {
        List<Transaction> payments = getTransactionsFromAccount(accountId, "/sql/query/payments_last_n.sql", "payments");
        UkelonnServiceProvider.makePaymentAmountsPositive(payments); // Payments are negative numbers in the DB, presented as positive numbers in the GUI
        return payments;
    }

    List<Transaction> getTransactionsFromAccount(int accountId,
                                                 String sqlTemplate,
                                                 String transactionType)
    {
        List<Transaction> transactions = new ArrayList<>();
        String sql = String.format(getResourceAsString(sqlTemplate), UkelonnServiceProvider.NUMBER_OF_TRANSACTIONS_TO_DISPLAY);
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, accountId);
                trySettingPreparedStatementParameterThatMayNotBePresent(statement, 2, accountId);
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        transactions.add(UkelonnServiceProvider.mapTransaction(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            logError("Error getting "+transactionType+"s from the database", e);
        }

        return transactions;
    }

    @Override
    public List<Transaction> deleteJobsFromAccount(int accountId, List<Integer> idsOfJobsToDelete) {
        if (!idsOfJobsToDelete.isEmpty()) {
            String deleteQuery = "delete from transactions where transaction_id in (select transaction_id from transactions inner join transaction_types on transactions.transaction_type_id=transaction_types.transaction_type_id where transaction_id in (" + joinIds(idsOfJobsToDelete) + ") and transaction_types.transaction_is_work=? and account_id=?)";
            try(Connection connection = database.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                    addParametersToDeleteJobsStatement(accountId, statement);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                String message = String.format("Failed to delete jobs from accountId: %d", accountId);
                logError(message, e);
            }
        }

        return getJobs(accountId);
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
    public List<Transaction> updateJob(UpdatedTransaction editedJob) {
        String sql = "update transactions set transaction_type_id=?, transaction_time=?, transaction_amount=? where transaction_id=?";
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, editedJob.getTransactionTypeId());
                statement.setTimestamp(2, new java.sql.Timestamp(editedJob.getTransactionTime().getTime()));
                statement.setDouble(3, editedJob.getTransactionAmount());
                statement.setInt(4, editedJob.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new UkelonnException(String.format("Failed to update job with id %d", editedJob.getId()) , e);
        }

        return getJobs(editedJob.getAccountId());
    }

    @Override
    public List<TransactionType> getPaymenttypes() {
        List<TransactionType> paymenttypes = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from transaction_types where transaction_is_wage_payment=true")) {
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            TransactionType transactiontype = UkelonnServiceProvider.mapTransactionType(resultSet);
                            paymenttypes.add(transactiontype);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logError("Error getting payment types from the database", e);
        }

        return paymenttypes;
    }

    @Override
    public Account registerPayment(PerformedTransaction payment) {
        int accountId = payment.getAccount().getAccountId();
        int transactionTypeId = payment.getTransactionTypeId();
        double amount = 0 - payment.getTransactionAmount();
        Date transactionDate = new Date();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into transactions (account_id,transaction_type_id,transaction_amount, transaction_time) values (?, ?, ?, ?)")) {
                statement.setInt(1, accountId);
                statement.setInt(2, transactionTypeId);
                statement.setDouble(3, amount);
                statement.setTimestamp(4, new java.sql.Timestamp(transactionDate.getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Failed to register payment  accountId: %d  transactionTypeId: %d  amount: %f", accountId, transactionTypeId, amount);
            logError(message, e);
            return null;
        }

        return getAccount(payment.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> modifyJobtype(TransactionType jobtype) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update transaction_types set transaction_type_name=?, transaction_amount=?, transaction_is_work=true, transaction_is_wage_payment=false where transaction_type_id=?")) {
                statement.setString(1, jobtype.getTransactionTypeName());
                statement.setDouble(2, jobtype.getTransactionAmount());
                statement.setInt(3, jobtype.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Failed to update jobtype %d in the database", jobtype.getId());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> createJobtype(TransactionType jobtype) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, true, false)")) {
                statement.setString(1, jobtype.getTransactionTypeName());
                statement.setObject(2, jobtype.getTransactionAmount());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Failed to create jobtype \"%s\" in the database", jobtype.getTransactionTypeName());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> modifyPaymenttype(TransactionType paymenttype) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update transaction_types set transaction_type_name=?, transaction_amount=?, transaction_is_work=false, transaction_is_wage_payment=true where transaction_type_id=?")) {
                statement.setString(1, paymenttype.getTransactionTypeName());
                statement.setDouble(2, paymenttype.getTransactionAmount());
                statement.setInt(3, paymenttype.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Failed to update payment type %d in the database", paymenttype.getId());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getPaymenttypes();
    }

    @Override
    public List<TransactionType> createPaymenttype(TransactionType paymenttype) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, false, true)")) {
                statement.setString(1, paymenttype.getTransactionTypeName());
                statement.setObject(2, paymenttype.getTransactionAmount());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Failed to create payment type \"%s\" in the database", paymenttype.getTransactionTypeName());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getPaymenttypes();
    }

    @Override
    public Account addAccount(User user) {
        String username = user.getUsername();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement insertAccountSql = connection.prepareStatement("insert into accounts (username) values (?)")) {
                insertAccountSql.setString(1, username);
                insertAccountSql.executeUpdate();
            }

            addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(username);

            return getAccount(user.getUsername());
        } catch (SQLException e) {
            String message = "Database exception when account for new user";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<SumYear> earningsSumOverYear(String username) {
        List<SumYear> statistics = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(database.sumOverYearQuery())) {
                statement.setString(1, username);
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        double sum = resultSet.getDouble(1);
                        int year = resultSet.getInt(2);
                        statistics.add(new SumYear(sum, year));
                    }
                }
            }
        } catch (SQLException e) {
            logWarning(String.format("Failed to get sum of earnings per year for account \"%s\" from the database", username), e);
        }

        return statistics;
    }

    @Override
    public List<SumYearMonth> earningsSumOverMonth(String username) {
        List<SumYearMonth> statistics = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(database.sumOverMonthQuery())) {
                statement.setString(1, username);
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        double sum = resultSet.getDouble(1);
                        int year = resultSet.getInt(2);
                        int month = resultSet.getInt(3);
                        statistics.add(new SumYearMonth(sum, year, month));
                    }
                }
            }
        } catch (SQLException e) {
            logWarning(String.format("Failed to get sum of earnings per month for account \"%s\" from the database", username), e);
        }

        return statistics;
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
        return notificationQueues.computeIfAbsent(username, k-> new ConcurrentLinkedQueue<>());
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

    private static void trySettingPreparedStatementParameterThatMayNotBePresent(PreparedStatement statement, int parameterId, int parameterValue) {
        try {
            statement.setInt(parameterId, parameterValue);
        } catch(SQLException e) {
            // Oops! The parameter wasn't present!
            // Continue as if nothing happened
        }
    }

    private void logError(String message, Exception e) {
        logservice.log(LogService.LOG_ERROR, message, e);
    }

    private void logWarning(String message, Exception e) {
        logservice.log(LogService.LOG_WARNING, message, e);
    }

    /**
     * Hack!
     * Because of the sum() column of accounts_view, accounts without transactions
     * won't appear in the accounts list, so all accounts are created with a
     * payment of 0 kroner.
     * @param username Used as the key to do the update to the account
     * @return the update status
     */
    int addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(String username) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(getResourceAsString("/sql/query/insert_empty_payment_in_account_keyed_by_username.sql"))) {
                statement.setString(1, username);
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            logError("Failed to set prepared statement argument", e);
        }

        return -1;
    }

    String getResourceAsString(String resourceName) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try(InputStream resourceStream = getClass().getResourceAsStream(resourceName)) {
            while ((length = resourceStream.read(buffer)) != -1) {
                resource.write(buffer, 0, length);
            }

            return resource.toString("UTF-8");
        } catch (Exception e) {
            logError("Error getting resource \"" + resource + "\" from the classpath", e);
        }

        return null;
    }

    public Account mapAccount(ResultSet results) throws SQLException {
        String username = results.getString(UkelonnServiceProvider.USERNAME);
        no.priv.bang.osgiservice.users.User user = useradmin.getUser(username);
        return new Account(
            results.getInt("account_id"),
            username,
            user.getFirstname(),
            user.getLastname(),
            results.getDouble("balance"));
    }

    static Transaction mapTransaction(ResultSet resultset) throws SQLException {
        return
            new Transaction(
                resultset.getInt("transaction_id"),
                mapTransactionType(resultset),
                resultset.getTimestamp("transaction_time"),
                resultset.getDouble("transaction_amount"),
                resultset.getBoolean("paid_out"));
    }

    static void makePaymentAmountsPositive(List<Transaction> payments) {
        for (Transaction payment : payments) {
            double amount = Math.abs(payment.getTransactionAmount());
            payment.setTransactionAmount(amount);
        }
    }

    static TransactionType mapTransactionType(ResultSet resultset) throws SQLException {
        return
            new TransactionType(
                resultset.getInt("transaction_type_id"),
                resultset.getString("transaction_type_name"),
                resultset.getDouble("transaction_amount"),
                resultset.getBoolean("transaction_is_work"),
                resultset.getBoolean("transaction_is_wage_payment"));
    }

}
