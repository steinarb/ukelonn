/*
 * Copyright 2016-2024 Steinar Bang
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
import org.osgi.service.log.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sql.DataSource;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.LocaleBean;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.SumYearMonth;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

/**
 * The OSGi component that provides the business logic of the ukelonn
 * webapp.
 *
 * @author Steinar Bang
 *
 */
@Component(service=UkelonnService.class, immediate=true, property= { "defaultlocale=nb_NO" })
public class UkelonnServiceProvider extends UkelonnServiceBase {
    private static final String RESOURCES_BASENAME = "i18n.ApplicationResources";
    private DataSource datasource;
    private UserManagementService useradmin;
    private LogService logservice;
    private Logger logger;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Notification>> notificationQueues = new ConcurrentHashMap<>();
    private Locale defaultLocale;
    static final String LAST_NAME = "last_name";
    static final String FIRST_NAME = "first_name";
    static final String USERNAME = "username";
    static final int NUMBER_OF_TRANSACTIONS_TO_DISPLAY = 10;
    static final String USER_ID = "user_id";

    @Activate
    public void activate(Map<String, Object> config) {
        defaultLocale = Locale.forLanguageTag(((String) config.get("defaultlocale")).replace('_', '-'));
        addRolesIfNotPresent();
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/ukelonn)")
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public DataSource getDataSource() {
        return datasource;
    }

    @Reference
    public void setUserAdmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
        this.logger = logservice.getLogger(getClass());
    }

    @Override
    public LogService getLogservice() {
        return logservice;
    }

    @Override
    public List<Account> getAccounts() {
        var accounts = new ArrayList<Account>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from accounts_view")) {
                try(var results = statement.executeQuery()) {
                    if (results != null) {
                        while(results.next()) {
                            var newaccount = mapAccount(results);
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
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, username);
                try(var resultset = statement.executeQuery()) {
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
        var accountId = job.account().accountId();
        var jobtypeId = job.transactionTypeId();
        var jobamount = addBonus(job.transactionAmount());
        var timeofjob = job.transactionDate();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into transactions (account_id, transaction_type_id,transaction_amount, transaction_time) values (?, ?, ?, ?)")) {
                statement.setInt(1, accountId);
                statement.setInt(2, jobtypeId);
                statement.setDouble(3, jobamount);
                statement.setTimestamp(4, new java.sql.Timestamp(timeofjob.getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            var message = String.format("Failed to register performed job in the database, account: %d  jobtype: %d  amount: %f", accountId, jobtypeId, jobamount);
            logError(message, exception);
        }

        return getAccount(job.account().username());
    }

    @Override
    public List<TransactionType> getJobTypes() {
        var jobtypes = new ArrayList<TransactionType>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from transaction_types where transaction_is_work=true")) {
                try(var resultSet = statement.executeQuery()) {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            var transactiontype = UkelonnServiceProvider.mapTransactionType(resultSet);
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
        var payments = getTransactionsFromAccount(accountId, "/sql/query/payments_last_n.sql", "payments");
        payments = UkelonnServiceProvider.makePaymentAmountsPositive(payments); // Payments are negative numbers in the DB, presented as positive numbers in the GUI
        return payments;
    }

    List<Transaction> getTransactionsFromAccount(int accountId,
                                                 String sqlTemplate,
                                                 String transactionType)
    {
        var transactions = new ArrayList<Transaction>();
        var sql = String.format(getResourceAsString(sqlTemplate), UkelonnServiceProvider.NUMBER_OF_TRANSACTIONS_TO_DISPLAY);
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, accountId);
                trySettingPreparedStatementParameterThatMayNotBePresent(statement, 2, accountId);
                try(var resultSet = statement.executeQuery()) {
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
            var deleteQuery = "delete from transactions where transaction_id in (select transaction_id from transactions inner join transaction_types on transactions.transaction_type_id=transaction_types.transaction_type_id where transaction_id in (" + joinIds(idsOfJobsToDelete) + ") and transaction_types.transaction_is_work=? and account_id=?)";
            try(var connection = datasource.getConnection()) {
                try (var statement = connection.prepareStatement(deleteQuery)) { // NOSONAR This string manipulation is OK and the only way to do it
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
            logger.error(message, e);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<Transaction> updateJob(UpdatedTransaction editedJob) {
        var sql = "update transactions set transaction_type_id=?, transaction_time=?, transaction_amount=? where transaction_id=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, editedJob.transactionTypeId());
                statement.setTimestamp(2, new java.sql.Timestamp(editedJob.transactionTime().getTime()));
                statement.setDouble(3, editedJob.transactionAmount());
                statement.setInt(4, editedJob.id());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new UkelonnException(String.format("Failed to update job with id %d", editedJob.id()) , e);
        }

        return getJobs(editedJob.accountId());
    }

    @Override
    public List<TransactionType> getPaymenttypes() {
        var paymenttypes = new ArrayList<TransactionType>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from transaction_types where transaction_is_wage_payment=true")) {
                try(var resultSet = statement.executeQuery()) {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            var transactiontype = UkelonnServiceProvider.mapTransactionType(resultSet);
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
        var accountId = payment.account().accountId();
        var transactionTypeId = payment.transactionTypeId();
        var amount = 0 - payment.transactionAmount();
        var transactionDate = new Date();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into transactions (account_id,transaction_type_id,transaction_amount, transaction_time) values (?, ?, ?, ?)")) {
                statement.setInt(1, accountId);
                statement.setInt(2, transactionTypeId);
                statement.setDouble(3, amount);
                statement.setTimestamp(4, new java.sql.Timestamp(transactionDate.getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("Failed to register payment  accountId: %d  transactionTypeId: %d  amount: %f", accountId, transactionTypeId, amount);
            logError(message, e);
            return null;
        }

        return getAccount(payment.account().username());
    }

    @Override
    public List<TransactionType> modifyJobtype(TransactionType jobtype) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update transaction_types set transaction_type_name=?, transaction_amount=?, transaction_is_work=true, transaction_is_wage_payment=false where transaction_type_id=?")) {
                statement.setString(1, jobtype.transactionTypeName());
                statement.setDouble(2, jobtype.transactionAmount());
                statement.setInt(3, jobtype.id());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("Failed to update jobtype %d in the database", jobtype.id());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> createJobtype(TransactionType jobtype) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, true, false)")) {
                statement.setString(1, jobtype.transactionTypeName());
                statement.setObject(2, jobtype.transactionAmount());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("Failed to create jobtype \"%s\" in the database", jobtype.transactionTypeName());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> modifyPaymenttype(TransactionType paymenttype) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update transaction_types set transaction_type_name=?, transaction_amount=?, transaction_is_work=false, transaction_is_wage_payment=true where transaction_type_id=?")) {
                statement.setString(1, paymenttype.transactionTypeName());
                statement.setDouble(2, paymenttype.transactionAmount());
                statement.setInt(3, paymenttype.id());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("Failed to update payment type %d in the database", paymenttype.id());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getPaymenttypes();
    }

    @Override
    public List<TransactionType> createPaymenttype(TransactionType paymenttype) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into transaction_types (transaction_type_name, transaction_amount, transaction_is_work, transaction_is_wage_payment) values (?, ?, false, true)")) {
                statement.setString(1, paymenttype.transactionTypeName());
                statement.setObject(2, paymenttype.transactionAmount());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("Failed to create payment type \"%s\" in the database", paymenttype.transactionTypeName());
            logError(message, e);
            throw new UkelonnException(message, e);
        }

        return getPaymenttypes();
    }

    @Override
    public Account addAccount(User user) {
        var username = user.username();
        try(var connection = datasource.getConnection()) {
            try(var insertAccountSql = connection.prepareStatement("insert into accounts (username) values (?)")) {
                insertAccountSql.setString(1, username);
                insertAccountSql.executeUpdate();
            }

            addDummyPaymentToAccountSoThatAccountWillAppearInAccountsView(username);

            return getAccount(user.username());
        } catch (SQLException e) {
            var message = "Database exception when account for new user";
            logger.error(message, e);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<SumYear> earningsSumOverYear(String username) {
        var statistics = new ArrayList<SumYear>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select aggregate_amount, aggregate_year from sum_over_year_view where username=?")) {
                statement.setString(1, username);
                try(var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        var sumYear = SumYear.with()
                            .sum(resultSet.getDouble(1))
                            .year(resultSet.getInt(2))
                            .build();
                        statistics.add(sumYear);
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
        var statistics = new ArrayList<SumYearMonth>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select aggregate_amount, aggregate_year, aggregate_month from sum_over_year_and_month_view where username=?")) {
                statement.setString(1, username);
                try(var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        var sumYearMonth = SumYearMonth.with()
                            .sum(resultSet.getDouble(1))
                            .year(resultSet.getInt(2))
                            .month(resultSet.getInt(3))
                            .build();
                        statistics.add(sumYearMonth);
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
        var notifications = getNotificationQueueForUser(username);
        var notification = notifications.poll();
        if (notification == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(notification);
    }

    @Override
    public void notificationTo(String username, Notification notification) {
        var notifications = getNotificationQueueForUser(username);
        notifications.add(notification);
    }

    @Override
    public List<Bonus> getActiveBonuses() {
        var activebonuses = new ArrayList<Bonus>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from bonuses where enabled and start_date <= ? and end_date >= ?")) {
                var today = Timestamp.from(new Date().toInstant());
                statement.setTimestamp(1, today);
                statement.setTimestamp(2, today);
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        buildBonusFromResultSetAndAddToList(activebonuses, results);
                    }
                }
            }
        } catch (SQLException e) {
            logWarning("Failed to get list of active bonuses", e);
        }

        return activebonuses;
    }

    @Override
    public List<Bonus> getAllBonuses() {
        var allbonuses = new ArrayList<Bonus>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from bonuses")) {
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        buildBonusFromResultSetAndAddToList(allbonuses, results);
                    }
                }
            }
        } catch (SQLException e) {
            logWarning("Failed to get list of all bonuses", e);
        }

        return allbonuses;
    }

    @Override
    public List<Bonus> createBonus(Bonus newBonus) {
        var title = newBonus.title();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into bonuses (enabled, iconurl, title, description, bonus_factor, start_date, end_date) values (?, ?, ?, ?, ?, ?, ?)")) {
                statement.setBoolean(1, newBonus.enabled());
                statement.setString(2, newBonus.iconurl());
                statement.setString(3, title);
                statement.setString(4, newBonus.description());
                statement.setDouble(5, newBonus.bonusFactor());
                var startDate = newBonus.startDate() != null ? newBonus.startDate() : new Date();
                statement.setTimestamp(6, Timestamp.from(startDate.toInstant()));
                var endDate = newBonus.endDate() != null ? newBonus.endDate() : new Date();
                statement.setTimestamp(7, Timestamp.from(endDate.toInstant()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logWarning(String.format("Failed to add Bonus with title \"%s\"", title), e);
        }

        return getAllBonuses();
    }

    @Override
    public List<Bonus> modifyBonus(Bonus updatedBonus) {
        var id = updatedBonus.bonusId();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update bonuses set enabled=?, iconurl=?, title=?, description=?, bonus_factor=?, start_date=?, end_date=? where bonus_id=?")) {
                statement.setBoolean(1, updatedBonus.enabled());
                statement.setString(2, updatedBonus.iconurl());
                statement.setString(3, updatedBonus.title());
                statement.setString(4, updatedBonus.description());
                statement.setDouble(5, updatedBonus.bonusFactor());
                statement.setTimestamp(6, Timestamp.from(updatedBonus.startDate().toInstant()));
                statement.setTimestamp(7, Timestamp.from(updatedBonus.endDate().toInstant()));
                statement.setInt(8, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logWarning(String.format("Failed to update Bonus with database id %d", id), e);
        }

        return getAllBonuses();
    }

    @Override
    public List<Bonus> deleteBonus(Bonus removedBonus) {
        var id = removedBonus.bonusId();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("delete from bonuses where bonus_id=?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logWarning(String.format("Failed to delete Bonus with database id %d", id), e);
        }

        return getAllBonuses();
    }

    @Override
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @Override
    public List<LocaleBean> availableLocales() {
        return Arrays.asList(Locale.forLanguageTag("nb-NO"), Locale.UK).stream().map(l -> LocaleBean.with().locale(l).build()).toList();
    }

    @Override
    public Map<String, String> displayTexts(Locale locale) {
        return transformResourceBundleToMap(locale);
    }

    private ConcurrentLinkedQueue<Notification> getNotificationQueueForUser(String username) {
        return notificationQueues.computeIfAbsent(username, k-> new ConcurrentLinkedQueue<>());
    }

    double addBonus(double transactionAmount) {
        var activebonuses = getActiveBonuses();
        if (activebonuses.isEmpty()) {
            return transactionAmount;
        }

        var bonus = activebonuses.stream().mapToDouble(b -> b.bonusFactor() * transactionAmount - transactionAmount).sum();
        return transactionAmount + bonus;
    }

    void buildBonusFromResultSetAndAddToList(List<Bonus> allbonuses, ResultSet results) throws SQLException {
        var bonus = Bonus.with()
            .bonusId(results.getInt("bonus_id"))
            .enabled(results.getBoolean("enabled"))
            .iconurl(results.getString("iconurl"))
            .title(results.getString("title"))
            .description(results.getString("description"))
            .bonusFactor(results.getDouble("bonus_factor"))
            .startDate(Date.from(results.getTimestamp("start_date").toInstant()))
            .endDate(Date.from(results.getTimestamp("end_date").toInstant()))
            .build();
        allbonuses.add(bonus);
    }

    static boolean passwordsEqualsAndNotEmpty(PasswordsWithUser passwords) {
        if (passwords.password() == null || passwords.password().isEmpty()) {
            return false;
        }

        return passwords.password().equals(passwords.password2());
    }

    static StringBuilder joinIds(List<Integer> ids) {
        var commaList = new StringBuilder();
        if (ids == null) {
            return commaList;
        }

        var iterator = ids.iterator();
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
        var user = passwords.user();
        if (user == null) {
            return false;
        }

        var username = user.username();
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
        logger.error(message, e);
    }

    private void logWarning(String message, Exception e) {
        logger.warn(message, e);
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
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(getResourceAsString("/sql/query/insert_empty_payment_in_account_keyed_by_username.sql"))) {
                statement.setString(1, username);
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            logError("Failed to set prepared statement argument", e);
        }

        return -1;
    }

    String getResourceAsString(String resourceName) {
        var resource = new ByteArrayOutputStream();
        var buffer = new byte[1024];
        int length;
        try(var resourceStream = getClass().getResourceAsStream(resourceName)) {
            while ((length = resourceStream.read(buffer)) != -1) {
                resource.write(buffer, 0, length);
            }

            return resource.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logError("Error getting resource \"" + resource + "\" from the classpath", e);
        }

        return null;
    }

    public Account mapAccount(ResultSet results) throws SQLException {
        var username = results.getString(UkelonnServiceProvider.USERNAME);
        try {
            var user = useradmin.getUser(username);
            return Account.with()
                .accountid(results.getInt("account_id"))
                .username(username)
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .balance(results.getDouble("balance"))
                .build();
        } catch (AuthserviceException e) {
            logWarning(String.format("No authservice user for username \"%s\" when fetching account", username), e);
            return Account.with()
                .accountid(results.getInt("account_id"))
                .username(username)
                .balance(results.getDouble("balance"))
                .build();
        }
    }

    private void addRolesIfNotPresent() {
        var ukelonnadmin = addRoleIfNotPresent(UKELONNADMIN_ROLE, "Administrator av applikasjonen ukelonn");
        addRoleIfNotPresent(UKELONNUSER_ROLE, "Bruker av applikasjonen ukelonn");
        addAdminroleToUserAdmin(ukelonnadmin);
    }

    Optional<Role> addRoleIfNotPresent(String rolename, String description) {
        var roles = useradmin.getRoles();
        var existingRole = roles.stream().filter(r -> rolename.equals(r.getRolename())).findFirst();
        if (!existingRole.isPresent()) {
            roles = useradmin.addRole(Role.with().rolename(rolename).description(description).build());
            return roles.stream().filter(r -> rolename.equals(r.getRolename())).findFirst();
        }

        return existingRole;
    }

    void addAdminroleToUserAdmin(Optional<Role> ukelonnadmin) {
        if (ukelonnadmin.isPresent()) {
            try {
                var admin = useradmin.getUser("admin");
                var roles = useradmin.getRolesForUser("admin");
                if (roles.stream().noneMatch(r -> ukelonnadmin.get().equals(r))) {
                    useradmin.addUserRoles(UserRoles.with().user(admin).roles(Arrays.asList(ukelonnadmin.get())).build());
                }
            } catch (AuthserviceException e) {
                // No admin user, skip and continue
            }
        }
    }

    static Transaction mapTransaction(ResultSet resultset) throws SQLException {
        return Transaction.with()
            .id(resultset.getInt("transaction_id"))
            .transactionType(mapTransactionType(resultset))
            .transactionTime(resultset.getTimestamp("transaction_time"))
            .transactionAmount(resultset.getDouble("transaction_amount"))
            .paidOut(resultset.getBoolean("paid_out"))
            .build();
    }

    static List<Transaction> makePaymentAmountsPositive(List<Transaction> payments) {
        var paymentsWithPositiveAmounts = new ArrayList<Transaction>(payments.size());
        for (var payment : payments) {
            var amount = Math.abs(payment.transactionAmount());
            paymentsWithPositiveAmounts.add(Transaction.with(payment).transactionAmount(amount).build());
        }

        return paymentsWithPositiveAmounts;
    }

    static TransactionType mapTransactionType(ResultSet resultset) throws SQLException {
        return TransactionType.with()
            .id(resultset.getInt("transaction_type_id"))
            .transactionTypeName(resultset.getString("transaction_type_name"))
            .transactionAmount(resultset.getDouble("transaction_amount"))
            .transactionIsWork(resultset.getBoolean("transaction_is_work"))
            .transactionIsWagePayment(resultset.getBoolean("transaction_is_wage_payment"))
            .build();
    }

    Map<String, String> transformResourceBundleToMap(Locale locale) {
        var map = new HashMap<String, String>();
        var bundle = ResourceBundle.getBundle(RESOURCES_BASENAME, locale);
        var keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, bundle.getString(key));
        }

        return map;
    }

}
