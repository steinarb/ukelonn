package no.priv.bang.ukelonn.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource.Util;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;
import static no.priv.bang.ukelonn.impl.CommonServiceMethods.*;
import static no.priv.bang.ukelonn.impl.CommonStringMethods.*;

public class CommonDatabaseMethods {

    public static UkelonnDatabase connectionCheck(Class<?> clazz) {
        UkelonnService ukelonnService = CommonServiceMethods.connectionCheck(clazz);

        UkelonnDatabase database = ukelonnService.getDatabase();
        if (database == null) {
            String className = clazz.getSimpleName();
            throw new RuntimeException(className + " bean unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
    }

    public static Map<Integer, TransactionType> getTransactionTypesFromUkelonnDatabase(Class<?> clazz) {
        Map<Integer, TransactionType> transactiontypes = new Hashtable<Integer, TransactionType>();
        UkelonnDatabase database = connectionCheck(clazz);
        ResultSet resultSet = database.query("select * from transaction_types");
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    TransactionType transactiontype = mapTransactionType(resultSet);
                    transactiontypes.put(transactiontype.getId(), transactiontype);
                }
            } catch (SQLException e) {
                logError(CommonDatabaseMethods.class, "Error getting transaction types from the database", e);
            }
        }

        return transactiontypes;
    }

    private static TransactionType mapTransactionType(ResultSet resultset) throws SQLException {
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

    public static List<TransactionType> getJobTypesFromTransactionTypes(Collection<TransactionType> transactionTypes) {
        ArrayList<TransactionType> jobTypes = new ArrayList<TransactionType>();
        for (TransactionType transactionType : transactionTypes) {
            if (transactionType.isTransactionIsWork()) {
                jobTypes.add(transactionType);
            }
        }

        return jobTypes;
    }

    public static List<TransactionType> getPaymentTypesFromTransactionTypes(Collection<TransactionType> transactionTypes) {
        ArrayList<TransactionType> jobTypes = new ArrayList<TransactionType>();
        for (TransactionType transactionType : transactionTypes) {
            if (transactionType.isTransactionIsWagePayment()) {
                jobTypes.add(transactionType);
            }
        }

        return jobTypes;
    }

    public static void updateBalanseFromDatabase(Class<?> clazz, Account account) {
        UkelonnDatabase connection = connectionCheck(clazz);
        StringBuilder query = sql("select * from accounts_view where account_id=").append(account.getAccountId());
        ResultSet results = connection.query(query.toString());
        if (results != null) {
            try {
                while (results.next()) {
                    double balance = results.getDouble("balance");
                    account.setBalance(balance);
                }
            } catch (SQLException e) {
                logError(CommonDatabaseMethods.class, "Error getting a user's account balance from the database", e);
            }
        }
    }

    public static void addNewPaymentToAccount(Class<?> clazz, Account account, TransactionType paymentType, double payment) {
        int accountId = account.getAccountId();
        int transactionTypeId = paymentType.getId();
        double amount = 0 - payment;
        StringBuilder query = sql("insert into transactions (account_id,transaction_type_id,transaction_amount) values (").
            append(accountId).append(",").
            append(transactionTypeId).append(",").
            append(amount).append(")");

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(query.toString());
    }

    public static Map<Integer, TransactionType> refreshAccount(Class<?> clazz, Account account) {
        updateBalanseFromDatabase(clazz, account);
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(clazz);
        return transactionTypes;
    }

    public static Account getAccountInfoFromDatabase(Class<?> clazz, String username) {
        UkelonnDatabase database = connectionCheck(clazz);
        StringBuilder query = sql("select * from accounts_view where username='").append(username).append("'");
        ResultSet resultset = database.query(query.toString());
        if (resultset != null) {
            try {
                if (resultset.next()) {
                    Account newaccount = MapAccount(resultset);
                    return newaccount;
                }
            } catch (SQLException e) {
                logError(CommonDatabaseMethods.class, "Error getting a single account from the database", e);
            }
        }

        return new Account(0, 0, username, "Ikke innlogget", null, 0);
    }

    public static AdminUser getAdminUserFromDatabase(Class<?> clazz, String username) {
        UkelonnDatabase database = CommonDatabaseMethods.connectionCheck(clazz);
        StringBuilder query = sql("select * from administrators_view where username='").append(username).append("'");
        ResultSet resultset = database.query(query.toString());
        if (resultset != null) {
            try {
                if (resultset.next()) {
                    AdminUser adminUser = mapAdminUser(resultset);
                    return adminUser;
                }
            } catch (SQLException e) {
                logError(CommonDatabaseMethods.class, "Error getting administrator user info from the database", e);
            }
        }

        return new AdminUser(username, 0, 0, "Ikke innlogget", null);
    }

    public static List<Account> getAccounts(Class<?> clazz) {
        ArrayList<Account> accounts = new ArrayList<Account>();
        UkelonnDatabase connection = connectionCheck(clazz);
        ResultSet results = connection.query("select * from accounts_view");
        if (results != null) {
            try {
                while(results.next()) {
                    Account newaccount = MapAccount(results);
                    accounts.add(newaccount);
                }
            } catch (SQLException e) {
                // Log and continue
                logError(CommonDatabaseMethods.class, "Error when getting all accounts from the database", e);
            }
        }

        return accounts;
    }

    public static List<Transaction> getPaymentsFromAccount(Account account, Class<?> clazz) {
        List<Transaction> payments = getTransactionsFromAccount(account, clazz, "/sql/query/payments_last10.sql", "payments");
        makePaymentAmountsPositive(payments); // Payments are negative numbers in the DB, presented as positive numbers in the GUI
        return payments;
    }

    private static void makePaymentAmountsPositive(List<Transaction> payments) {
    	for (Transaction payment : payments) {
            double amount = Math.abs(payment.getTransactionAmount());
            payment.setTransactionAmount(amount);
        }
    }

    public static List<Transaction> getJobsFromAccount(Account account, Class<?> clazz) {
        return getTransactionsFromAccount(account, clazz, "/sql/query/jobs_last10.sql", "job");
    }

    private static List<Transaction> getTransactionsFromAccount(Account account,
                                                                Class<?> clazz,
                                                                String sqlTemplate,
                                                                String transactionType)
    {
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (null != account) {
            UkelonnDatabase database = connectionCheck(clazz);
            String sql = String.format(getResourceAsString(sqlTemplate), account.getAccountId());
            ResultSet resultSet = database.query(sql.toString());
            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        transactions.add(mapTransaction(resultSet));
                    }
                } catch (SQLException e) {
                    logError(CommonDatabaseMethods.class, "Error getting "+transactionType+"s from the database", e);
                }
            }
        }

        return transactions;
    }

    /***
     * Create a list of dummy transactions used to force the initial size of tables.
     *
     * @return A list of 10 transactions with empty values for everything
     */
    public static Collection<? extends Transaction> getDummyTransactions() {
    	int lengthOfDummyList = 10;
    	TransactionType dummyTransactionType = new TransactionType(0, "", null, true, true);
    	ArrayList<Transaction> dummyTransactions = new ArrayList<Transaction>(lengthOfDummyList);
    	for (int i = 0; i < lengthOfDummyList; i++) {
            Transaction dummyTransaction = new Transaction(0, dummyTransactionType, null, 0.0);
            dummyTransactions.add(dummyTransaction);
        }

    	return (Collection<? extends Transaction>) dummyTransactions;
    }

    private static Transaction mapTransaction(ResultSet resultset) throws SQLException {
        Transaction transaction =
            new Transaction(
                            resultset.getInt("transaction_id"),
                            mapTransactionType(resultset),
                            resultset.getDate("transaction_time"),
                            resultset.getDouble("transaction_amount")
                            );
        return transaction;
    }

    public static Account MapAccount(ResultSet results) throws SQLException {
        return new Account(
                           results.getInt("account_id"),
                           results.getInt("user_id"),
                           results.getString("username"),
                           results.getString("first_name"),
                           results.getString("last_name"),
                           results.getDouble("balance")
                           );
    }

    public static Map<Integer, TransactionType> registerNewJobInDatabase(Class<?> clazz, Account account, int newJobTypeId, double newJobWages) {
        StringBuilder query = sql("insert into transactions (account_id,transaction_type_id,transaction_amount) values (").
            append(account.getAccountId()).append(",").
            append(newJobTypeId).append(",").
            append(newJobWages).append(")");

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(query.toString());

        // Update the list of jobs and the updated balance from the DB
        Map<Integer, TransactionType> transactionTypes = refreshAccount(clazz, account);
        return transactionTypes;
    }

    public static void addJobTypeToDatabase(Class<?> clazz, String newPaymentTypeName, double newPaymentTypeAmount) {
        String sql = String.format(
                                   Locale.US, // Format the double correctly for SQL
                                   getResourceAsString("/sql/query/insert_new_job_type.sql"),
                                   newPaymentTypeName,
                                   newPaymentTypeAmount
                                   );

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(sql);
    }

    public static void updateTransactionTypeInDatabase(Class<?> clazz, TransactionType modifiedJobType) {
        String sql = String.format(
                                   Locale.US, // Format the double correctly for SQL
                                   getResourceAsString("/sql/query/update_transaction_type.sql"),
                                   modifiedJobType.getTransactionTypeName(),
                                   modifiedJobType.getTransactionAmount(),
                                   modifiedJobType.isTransactionIsWork() ? "true" : "false",
                                   modifiedJobType.isTransactionIsWagePayment() ? "true" : "false",
                                   modifiedJobType.getId()
                                   );

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(sql);
    }

    public static void addPaymentTypeToDatabase(Class<?> clazz, String newPaymentTypeName, double newPaymentTypeAmount) {
        String sql = String.format(
                                   Locale.US, // Format the double correctly for SQL
                                   getResourceAsString("/sql/query/insert_new_payment_type.sql"),
                                   newPaymentTypeName,
                                   newPaymentTypeAmount
                                   );

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(sql);
    }

    public static void addUserToDatabase(
                                         Class<?> clazz,
                                         String newUserUsername,
                                         String newUserPassword,
                                         String newUserEmail,
                                         String newUserFirstname,
                                         String newUserLastname
                                         )
    {
      	String salt = getNewSalt();
        String hashedPassword = hashPassword(newUserPassword, salt);

        String insertUserSql = String.format(
                                             getResourceAsString("/sql/query/insert_new_user.sql"),
                                             newUserUsername,
                                             hashedPassword,
                                             salt,
                                             newUserEmail,
                                             newUserFirstname,
                                             newUserLastname
                                             );

        String findUserIdFromUsernameSql = String.format(
                                                         getResourceAsString("/sql/query/find_user_id_from_username.sql"),
                                                         newUserUsername
                                                         );

        UkelonnDatabase database = connectionCheck(clazz);
        database.update(insertUserSql);
        ResultSet userIdResultSet = database.query(findUserIdFromUsernameSql);
        try {
            if (userIdResultSet.next()) {
                int userId = userIdResultSet.getInt("user_id");
                String insertAccountSql = String.format(
                                                        getResourceAsString("/sql/query/insert_new_account.sql"),
                                                        userId
                                                        );
                database.update(insertAccountSql);
                addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(database, userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getUsers(Class<?> clazz) {
        ArrayList<User> users = new ArrayList<User>();
        String sql = "select * from users order by user_id";
        UkelonnDatabase database = connectionCheck(clazz);
        ResultSet resultSet = database.query(sql);
        try {
            while (resultSet.next()) {
                User user = mapUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public static int changePasswordForUser(String username, String password, Class<?> clazz) {
      	String salt = getNewSalt();
        String hashedPassword = hashPassword(password, salt);
        StringBuilder update = sql("update users set password='").append(hashedPassword).append("', salt='").append(salt).append("' where username='").append(username).append("'");
        UkelonnDatabase database = connectionCheck(clazz);
        return database.update(update.toString());
    }

    public static int updateUserInDatabase(Class<?> classForLogging, User userToUpdate) {
        String updateUserSql = String.format(
                                             getResourceAsString("/sql/query/update_user.sql"),
                                             userToUpdate.getUsername(),
                                             userToUpdate.getEmail(),
                                             userToUpdate.getFirstname(),
                                             userToUpdate.getLastname(),
                                             userToUpdate.getUserId()
                                             );

        UkelonnDatabase database = connectionCheck(classForLogging);
        return database.update(updateUserSql.toString());
    }

    public static void deleteTransactions(Class<?> clazz, List<Transaction> transactions) {
    	StringBuilder deleteQuery = sql("delete from transactions where transaction_id in (").append(joinIds(transactions)).append(")");
        UkelonnDatabase database = connectionCheck(clazz);
        database.update(deleteQuery.toString());
    }

    private static StringBuilder joinIds(List<Transaction> transactions) {
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
        String hashedPassword = new Sha256Hash(newUserPassword, decodedSaltUsedWhenHashing, 1024).toBase64();
        return hashedPassword;
    }

    private static String getNewSalt() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        String salt = randomNumberGenerator.nextBytes().toBase64();
        return salt;
    }

    /**
     * Hack!
     * Because of the sum() column of accounts_view, accounts without transactions
     * won't appear in the accounts list, so all accounts are created with a
     * payment of 0 kroner.
     * @param database The {@link UkelonnDatabase} to register the payment in
     * @param userId Used as the key to do the update to the account
     */
    private static void addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(UkelonnDatabase database, int userId) {
        String sql = String.format(
                                   getResourceAsString("/sql/query/insert_empty_payment_in_account_keyed_by_user_id.sql"),
                                   userId
                                   );

        database.update(sql);
    }

    private static User mapUser(ResultSet resultSet) {
        int userId;
        String username;
        String password;
        String email;
        String firstname;
        String lastname;
        try {
            userId = resultSet.getInt("user_id");
            username = resultSet.getString("username");
            password = resultSet.getString("password");
            email = resultSet.getString("email");
            firstname = resultSet.getString("first_name");
            lastname = resultSet.getString("last_name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        User user = new User(userId, username, email, password, firstname, lastname);
        return user;
    }

    private static AdminUser mapAdminUser(ResultSet resultset) throws SQLException {
        AdminUser adminUser;
        adminUser = new AdminUser(
                                  resultset.getString("username"),
                                  resultset.getInt("user_id"),
                                  resultset.getInt("administrator_id"),
                                  resultset.getString("first_name"),
                                  resultset.getString("last_name")
                                  );
        return adminUser;
    }

    private static String getResourceAsString(String resourceName) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        InputStream resourceStream = CommonDatabaseMethods.class.getResourceAsStream(resourceName);
        try {
            while ((length = resourceStream.read(buffer)) != -1) {
                resource.write(buffer, 0, length);
            }

            return resource.toString("UTF-8");
        } catch (Exception e) {
            logError(CommonDatabaseMethods.class, "Error getting resource \"" + resource + "\" from the classpath", e);
        }

        return null;
    }

}
