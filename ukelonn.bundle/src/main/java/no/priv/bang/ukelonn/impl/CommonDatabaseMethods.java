/*
 * Copyright 2016-2018 Steinar Bang
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
package no.priv.bang.ukelonn.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource.Util;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import static no.priv.bang.ukelonn.impl.CommonServiceMethods.*;

public class CommonDatabaseMethods {

    private static final String FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT = "Failed to set value in prepared statement";
    private static final String LAST_NAME = "last_name";
    private static final String FIRST_NAME = "first_name";
    private static final String USERNAME = "username";
    private static final String USER_ID = "user_id";
    static final int NUMBER_OF_TRANSACTIONS_TO_DISPLAY = 10;
    public static final int UPDATE_FAILED = -1;

    private CommonDatabaseMethods() {}

    public static UkelonnDatabase connectionCheck(UkelonnService ukelonnServletProvider, Class<?> clazz) {
        UkelonnService provider = CommonServiceMethods.connectionCheck(ukelonnServletProvider, clazz);

        UkelonnDatabase database = provider.getDatabase();
        if (database == null) {
            String className = clazz.getSimpleName();
            throw new UkelonnException(className + " bean unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
    }

    public static Map<Integer, TransactionType> getTransactionTypesFromUkelonnDatabase(UkelonnService provider, Class<?> clazz) {
        Map<Integer, TransactionType> transactiontypes = new HashMap<>();
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("select * from transaction_types")) {
            try(ResultSet resultSet = database.query(statement)) {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        TransactionType transactiontype = mapTransactionType(resultSet);
                        transactiontypes.put(transactiontype.getId(), transactiontype);
                    }
                }
            }
        } catch (SQLException e) {
            logError(provider, CommonDatabaseMethods.class, "Error getting transaction types from the database", e);
        }

        return transactiontypes;
    }

    private static TransactionType mapTransactionType(ResultSet resultset) throws SQLException {
        return
            new TransactionType(
                resultset.getInt("transaction_type_id"),
                resultset.getString("transaction_type_name"),
                resultset.getDouble("transaction_amount"),
                resultset.getBoolean("transaction_is_work"),
                resultset.getBoolean("transaction_is_wage_payment"));
    }

    public static List<TransactionType> getJobTypesFromTransactionTypes(Collection<TransactionType> transactionTypes) {
        ArrayList<TransactionType> jobTypes = new ArrayList<>();
        for (TransactionType transactionType : transactionTypes) {
            if (transactionType.isTransactionIsWork()) {
                jobTypes.add(transactionType);
            }
        }

        return jobTypes;
    }

    public static List<TransactionType> getPaymentTypesFromTransactionTypes(Collection<TransactionType> transactionTypes) {
        ArrayList<TransactionType> jobTypes = new ArrayList<>();
        for (TransactionType transactionType : transactionTypes) {
            if (transactionType.isTransactionIsWagePayment()) {
                jobTypes.add(transactionType);
            }
        }

        return jobTypes;
    }

    public static void updateBalanseFromDatabase(UkelonnService provider, Class<?> clazz, Account account) {
        UkelonnDatabase connection = connectionCheck(provider, clazz);
        try(PreparedStatement statement = connection.prepareStatement("select * from accounts_view where account_id=?")) {
            statement.setInt(1, account.getAccountId());
            try(ResultSet results = connection.query(statement)) {
                if (results != null) {
                    while (results.next()) {
                        double balance = results.getDouble("balance");
                        account.setBalance(balance);
                    }
                }
            }
        } catch (SQLException e) {
            logError(provider, CommonDatabaseMethods.class, "Error getting a user's account balance from the database", e);
        }
    }

    public static int addNewPaymentToAccount(UkelonnService provider, Class<?> clazz, Account account, TransactionType paymentType, double payment) {
        int updateResult = UPDATE_FAILED;
        int accountId = account.getAccountId();
        int transactionTypeId = paymentType.getId();
        double amount = 0 - payment;
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("insert into transactions (account_id,transaction_type_id,transaction_amount) values (?, ?, ?)")) {
            statement.setInt(1, accountId);
            statement.setInt(2, transactionTypeId);
            statement.setDouble(3, amount);
            updateResult = database.update(statement);
        } catch (SQLException e) {
            logError(provider, clazz, "Failed to set prepared statements value", e);
        }

        return updateResult;
    }

    public static Map<Integer, TransactionType> refreshAccount(UkelonnService provider, Class<?> clazz, Account account) {
        updateBalanseFromDatabase(provider, clazz, account);
        return getTransactionTypesFromUkelonnDatabase(provider, clazz);
    }

    public static Account getAccountInfoFromDatabase(UkelonnService provider, Class<?> clazz, String username) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("select * from accounts_view where username=?")) {
            statement.setString(1, username);
            try(ResultSet resultset = database.query(statement)) {
                if (resultset != null && resultset.next())
                {
                    return mapAccount(resultset);
                }
            }
        } catch (SQLException e) {
            logError(provider, CommonDatabaseMethods.class, "Error getting a single account from the database", e);
        }

        return new Account(0, 0, username, "Ikke innlogget", null, 0);
    }

    public static AdminUser getAdminUserFromDatabase(UkelonnService provider, Class<?> clazz, String username) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("select * from administrators_view where username=?")) {
            statement.setString(1, username);
            try(ResultSet resultset = database.query(statement)) {
                if (resultset != null && resultset.next())
                {
                    return mapAdminUser(resultset);
                }
            }
        } catch (SQLException e) {
            logError(provider, CommonDatabaseMethods.class, "Error getting administrator user info from the database", e);
        }

        return new AdminUser(username, 0, 0, "Ikke innlogget", null);
    }

    public static List<Account> getAccounts(UkelonnService provider, Class<?> clazz) {
        ArrayList<Account> accounts = new ArrayList<>();
        UkelonnDatabase connection = connectionCheck(provider, clazz);
        try(PreparedStatement statement = connection.prepareStatement("select * from accounts_view")) {
            try(ResultSet results = connection.query(statement)) {
                if (results != null) {
                    while(results.next()) {
                        Account newaccount = mapAccount(results);
                        accounts.add(newaccount);
                    }
                }
            }
        } catch (SQLException e) {
            // Log and continue
            logError(provider, CommonDatabaseMethods.class, "Error when getting all accounts from the database", e);
        }

        return accounts;
    }

    public static List<Transaction> getPaymentsFromAccount(UkelonnService provider, Account account, Class<?> clazz) {
        List<Transaction> payments = getTransactionsFromAccount(provider, account, clazz, "/sql/query/payments_last_n.sql", "payments");
        makePaymentAmountsPositive(payments); // Payments are negative numbers in the DB, presented as positive numbers in the GUI
        return payments;
    }

    private static void makePaymentAmountsPositive(List<Transaction> payments) {
        for (Transaction payment : payments) {
            double amount = Math.abs(payment.getTransactionAmount());
            payment.setTransactionAmount(amount);
        }
    }

    public static List<Transaction> getJobsFromAccount(UkelonnService provider, Account account, Class<?> clazz) {
        return getTransactionsFromAccount(provider, account, clazz, "/sql/query/jobs_last_n.sql", "job");
    }

    static List<Transaction> getTransactionsFromAccount(
        UkelonnService provider,
        Account account,
        Class<?> clazz,
        String sqlTemplate,
        String transactionType)
    {
        List<Transaction> transactions = new ArrayList<>();
        if (null != account) {
            UkelonnDatabase database = connectionCheck(provider, clazz);
            String sql = String.format(getResourceAsString(provider, sqlTemplate), NUMBER_OF_TRANSACTIONS_TO_DISPLAY);
            try(PreparedStatement statement = database.prepareStatement(sql)) {
                statement.setInt(1, account.getAccountId());
                trySettingPreparedStatementParameterThatMayNotBePresent(statement, 2, account.getAccountId());
                try(ResultSet resultSet = database.query(statement)) {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            transactions.add(mapTransaction(resultSet));
                        }
                    }
                }
            } catch (SQLException e) {
                logError(provider, CommonDatabaseMethods.class, "Error getting "+transactionType+"s from the database", e);
            }
        }

        return transactions;
    }

    private static void trySettingPreparedStatementParameterThatMayNotBePresent(PreparedStatement statement, int parameterId, int parameterValue) {
        try {
            statement.setInt(parameterId, parameterValue);
        } catch(SQLException e) {
            // Oops! The parameter wasn't present!
            // Continue as if nothing happened
        }
    }

    /***
     * Create a list of dummy transactions used to force the initial size of tables.
     *
     * @return A list of 10 transactions with empty values for everything
     */
    public static Collection<Transaction> getDummyTransactions() {
        int lengthOfDummyList = 10;
        TransactionType dummyTransactionType = new TransactionType(0, "", null, true, true);
        ArrayList<Transaction> dummyTransactions = new ArrayList<>(lengthOfDummyList);
        for (int i = 0; i < lengthOfDummyList; i++) {
            Transaction dummyTransaction = new Transaction(0, dummyTransactionType, null, 0.0, false);
            dummyTransactions.add(dummyTransaction);
        }

        return dummyTransactions;
    }

    private static Transaction mapTransaction(ResultSet resultset) throws SQLException {
        return
            new Transaction(
                resultset.getInt("transaction_id"),
                mapTransactionType(resultset),
                resultset.getDate("transaction_time"),
                resultset.getDouble("transaction_amount"),
                resultset.getBoolean("paid_out"));
    }

    public static Account mapAccount(ResultSet results) throws SQLException {
        return new Account(
            results.getInt("account_id"),
            results.getInt(USER_ID),
            results.getString(USERNAME),
            results.getString(FIRST_NAME),
            results.getString(LAST_NAME),
            results.getDouble("balance"));
    }

    public static Map<Integer, TransactionType> registerNewJobInDatabase(UkelonnService provider, Class<?> clazz, Account account, int newJobTypeId, double newJobWages) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("insert into transactions (account_id,transaction_type_id,transaction_amount) values (?, ?, ?)")) {
            statement.setInt(1, account.getAccountId());
            statement.setInt(2, newJobTypeId);
            statement.setDouble(3, newJobWages);
            database.update(statement);

            // Update the list of jobs and the updated balance from the DB
            return refreshAccount(provider, clazz, account);
        } catch (SQLException exception) {
            logError(provider, clazz, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, exception);
        }

        return Collections.emptyMap();
    }

    public static int addJobTypeToDatabase(UkelonnService provider, Class<?> clazz, String newPaymentTypeName, double newPaymentTypeAmount) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, true, false)")) {
            statement.setString(1, newPaymentTypeName);
            statement.setDouble(2, newPaymentTypeAmount);
            return database.update(statement);
        } catch (SQLException e) {
            logError(provider, clazz, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, e);
        }

        return UPDATE_FAILED;
    }

    public static int updateTransactionTypeInDatabase(UkelonnService provider, Class<?> clazz, TransactionType modifiedJobType) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("update transaction_types set transaction_type_name=?, transaction_amount=?, transaction_is_work=?, transaction_is_wage_payment=? where transaction_type_id=?")) {
            statement.setString(1, modifiedJobType.getTransactionTypeName());
            statement.setDouble(2, modifiedJobType.getTransactionAmount());
            statement.setBoolean(3, modifiedJobType.isTransactionIsWork());
            statement.setBoolean(4, modifiedJobType.isTransactionIsWagePayment());
            statement.setInt(5, modifiedJobType.getId());
            return database.update(statement);
        } catch (SQLException e) {
            logError(provider, clazz, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, e);
        }

        return UPDATE_FAILED;
    }

    public static int addPaymentTypeToDatabase(UkelonnService provider, Class<?> clazz, String newPaymentTypeName, Double newPaymentTypeAmount) {
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, false, true)")) {
            statement.setString(1, newPaymentTypeName);
            statement.setObject(2, newPaymentTypeAmount);
            return database.update(statement);
        } catch (SQLException e) {
            logError(provider, clazz, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, e);
        }

        return UPDATE_FAILED;
    }

    public static void addUserToDatabase(
        UkelonnService provider,
        Class<?> clazz,
        String newUserUsername,
        String newUserPassword,
        String newUserEmail,
        String newUserFirstname,
        String newUserLastname)
    {
        String salt = getNewSalt();
        String hashedPassword = hashPassword(newUserPassword, salt);

        UkelonnDatabase database = connectionCheck(provider, clazz);
        try {
            try(PreparedStatement insertUserSql = database.prepareStatement("insert into users (username, password, salt, email, first_name, last_name) values (?, ?, ?, ?, ?, ?)")) {
                insertUserSql.setString(1, newUserUsername);
                insertUserSql.setString(2, hashedPassword);
                insertUserSql.setString(3, salt);
                insertUserSql.setString(4, newUserEmail);
                insertUserSql.setString(5, newUserFirstname);
                insertUserSql.setString(6, newUserLastname);
                database.update(insertUserSql);
            }

            try(PreparedStatement findUserIdFromUsernameSql = database.prepareStatement("select user_id from users where username=?")) {
                findUserIdFromUsernameSql.setString(1, newUserUsername);
                try(ResultSet userIdResultSet = database.query(findUserIdFromUsernameSql)) {
                    if (userIdResultSet.next()) {
                        int userId = userIdResultSet.getInt(USER_ID);
                        PreparedStatement insertAccountSql = database.prepareStatement("insert into accounts (user_id) values (?)");
                        insertAccountSql.setInt(1, userId);
                        database.update(insertAccountSql);
                        addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(provider, database, userId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new UkelonnException(e);
        }
    }

    public static List<User> getUsers(UkelonnService provider, Class<?> clazz) {
        ArrayList<User> users = new ArrayList<>();
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("select * from users order by user_id")) {
            try(ResultSet resultSet = database.query(statement)) {
                while (resultSet.next()) {
                    User user = mapUser(resultSet);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new UkelonnException(e);
        }

        return users;
    }

    public static int changePasswordForUser(UkelonnService provider, String username, String password, Class<?> clazz) {
        String salt = getNewSalt();
        String hashedPassword = hashPassword(password, salt);
        UkelonnDatabase database = connectionCheck(provider, clazz);
        try(PreparedStatement statement = database.prepareStatement("update users set password=?, salt=? where username=?")) { // NOSONAR
            statement.setString(1, hashedPassword);
            statement.setString(2, salt);
            statement.setString(3, username);
            return database.update(statement);
        } catch (SQLException e) {
            logError(provider, clazz, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, e);
        }

        return UPDATE_FAILED;
    }

    public static int updateUserInDatabase(UkelonnService provider, Class<?> classForLogging, User userToUpdate) {
        UkelonnDatabase database = connectionCheck(provider, classForLogging);
        try(PreparedStatement updateUserSql = database.prepareStatement("update users set username=?, email=?, first_name=?, last_name=? where user_id=?")) {
            updateUserSql.setString(1, userToUpdate.getUsername());
            updateUserSql.setString(2, userToUpdate.getEmail());
            updateUserSql.setString(3, userToUpdate.getFirstname());
            updateUserSql.setString(4, userToUpdate.getLastname());
            updateUserSql.setInt(5, userToUpdate.getUserId());
            return database.update(updateUserSql);
        } catch (SQLException e) {
            logError(provider, classForLogging, FAILED_TO_SET_VALUE_IN_PREPARED_STATEMENT, e);
        }

        return UPDATE_FAILED;
    }

    public static void deleteTransactions(UkelonnService provider, Class<?> clazz, List<Transaction> transactions) {
        String deleteQuery = "delete from transactions where transaction_id in (" + joinIds(transactions) + ")";
        UkelonnDatabase database = connectionCheck(provider, clazz);
        PreparedStatement statement = database.prepareStatement(deleteQuery);
        database.update(statement);
    }

    static StringBuilder joinIds(List<Transaction> transactions) {
        StringBuilder commaList = new StringBuilder();
        if (transactions == null) {
            return commaList;
        }

        Iterator<Transaction> iterator = transactions.iterator();
        if (!iterator.hasNext()) {
            return commaList; // Return an empty string builder instead of a null
        }

        commaList.append(iterator.next().getId());
        while(iterator.hasNext()) {
            commaList.append(", ").append(iterator.next().getId());
        }

        return commaList;
    }

    private static String hashPassword(String newUserPassword, String salt) {
        Object decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
        return new Sha256Hash(newUserPassword, decodedSaltUsedWhenHashing, 1024).toBase64();
    }

    private static String getNewSalt() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toBase64();
    }

    /**
     * Hack!
     * Because of the sum() column of accounts_view, accounts without transactions
     * won't appear in the accounts list, so all accounts are created with a
     * payment of 0 kroner.
     * @param provider an object holding references to the database and log OSGi services
     * @param database The {@link UkelonnDatabase} to register the payment in
     * @param userId Used as the key to do the update to the account
     * @return the update status
     */
    static int addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(UkelonnService provider, UkelonnDatabase database, int userId) {
        try(PreparedStatement statement = database.prepareStatement(getResourceAsString(provider, "/sql/query/insert_empty_payment_in_account_keyed_by_user_id.sql"))) {
            statement.setInt(1, userId);
            return database.update(statement);
        } catch (SQLException e) {
            logError(provider, CommonDatabaseMethods.class, "Failed to set prepared statement argument", e);
        }

        return UPDATE_FAILED;
    }

    static User mapUser(ResultSet resultSet) {
        int userId;
        String username;
        String email;
        String firstname;
        String lastname;
        try {
            userId = resultSet.getInt(USER_ID);
            username = resultSet.getString(USERNAME);
            email = resultSet.getString("email");
            firstname = resultSet.getString(FIRST_NAME);
            lastname = resultSet.getString(LAST_NAME);
        } catch (SQLException e) {
            throw new UkelonnException(e);
        }

        return new User(userId, username, email, firstname, lastname);
    }

    private static AdminUser mapAdminUser(ResultSet resultset) throws SQLException {
        AdminUser adminUser;
        adminUser = new AdminUser(
            resultset.getString(USERNAME),
            resultset.getInt(USER_ID),
            resultset.getInt("administrator_id"),
            resultset.getString(FIRST_NAME),
            resultset.getString(LAST_NAME));
        return adminUser;
    }

    static String getResourceAsString(UkelonnService provider, String resourceName) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try(InputStream resourceStream = CommonDatabaseMethods.class.getResourceAsStream(resourceName)) {
            while ((length = resourceStream.read(buffer)) != -1) {
                resource.write(buffer, 0, length);
            }

            return resource.toString("UTF-8");
        } catch (Exception e) {
            logError(provider, CommonDatabaseMethods.class, "Error getting resource \"" + resource + "\" from the classpath", e);
        }

        return null;
    }

}
