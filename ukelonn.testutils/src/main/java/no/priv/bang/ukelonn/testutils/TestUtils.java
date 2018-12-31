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
package no.priv.bang.ukelonn.testutils;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.shiro.SecurityUtils;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.db.derbytest.UkelonnDatabaseProvider;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;
import no.priv.bang.ukelonn.web.security.UkelonnShiroFilter;
import no.priv.bang.ukelonn.web.security.dbrealm.UkelonnRealm;
import no.priv.bang.ukelonn.web.security.memorysession.MemorySession;

/**
 * Contains static methods used in more than one unit test.
 *
 * @author Steinar Bang
 *
 */
public class TestUtils {

    private static UkelonnServiceProvider ukelonnServiceSingleton;
    private static UkelonnShiroFilter shirofilter;
    private static User jadUser = new User(1, "jad", "jane1203@gmail.no", "Jane", "Doe");
    private static User jodUser = new User(1, "jod", "john1203@gmail.no", "John", "Doe");
    private static Account jadAccount = new Account(1, 1, "jad", "Jane", "Doe", 673.0);
    private static Account jodAccount = new Account(1, 1, "jod", "John", "Doe", 278.0);
    private static TransactionType jobtype1 = new TransactionType(1, "Støvsuging", 45.0, true, false);
    private static TransactionType jobtype2 = new TransactionType(3, "Tømme oppvaskmaskin", 35.0, true, false);
    private static TransactionType jobtype3 = new TransactionType(5, "Gå med resirk", 20.0, true, false);
    private static TransactionType jobtype4 = new TransactionType(6, "Støvsuge rommet", 15.0, true, false);
    private static List<TransactionType> jobtypes = Arrays.asList(jobtype1, jobtype2, jobtype3, jobtype4);
    private static TransactionType paymenttype1 = new TransactionType(2, "Inn på konto", null, false, true);
    private static TransactionType paymenttype2 = new TransactionType(4, "Mobildata", 100.0, false, true);
    private static List<TransactionType> paymenttypes = Arrays.asList(paymenttype1, paymenttype2);
    private static Map<Integer, TransactionType> transactionttypes = Collections.unmodifiableMap(
        Stream.of(jobtypes, paymenttypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(TransactionType::getId, t -> t)));
    private static List<Transaction> jadJobs = Arrays.asList(
        new Transaction(3, jobtype1, new Date(), 45.0, true),
        new Transaction(4, jobtype2, new Date(), 35.0, true),
        new Transaction(5, jobtype1, new Date(), 45.0, false),
        new Transaction(6, jobtype3, new Date(), 20.0, false),
        new Transaction(7, jobtype1, new Date(), 45.0, false),
        new Transaction(8, jobtype4, new Date(), 15.0, false),
        new Transaction(9, jobtype1, new Date(), 45.0, false),
        new Transaction(10, jobtype2, new Date(), 35.0, false),
        new Transaction(11, jobtype3, new Date(), 20.0, false),
        new Transaction(12, jobtype2, new Date(), 35.0, false));
    private static List<Transaction> jadPayments = Arrays.asList(
        new Transaction(13, paymenttype1, new Date(), 210.0, false),
        new Transaction(14, paymenttype1, new Date(), 130.0, false),
        new Transaction(15, paymenttype1, new Date(), 120.0, false),
        new Transaction(16, paymenttype1, new Date(), 270.0, false),
        new Transaction(17, paymenttype1, new Date(), 300.0, false),
        new Transaction(18, paymenttype1, new Date(), 210.0, false),
        new Transaction(19, paymenttype1, new Date(), 180.0, false),
        new Transaction(20, paymenttype1, new Date(), 70.0, false),
        new Transaction(21, paymenttype1, new Date(), 200.0, false),
        new Transaction(22, paymenttype1, new Date(), 250.0, false));
    private static List<Transaction> jodJobs = Arrays.asList(new Transaction(1, jobtype1, new Date(), 45.0, false), new Transaction(2, jobtype2, new Date(), 35.0, false));
    public static UkelonnServiceProvider getUkelonnServiceSingleton() {
        return ukelonnServiceSingleton;
    }

    public static UkelonnShiroFilter getShirofilter() {
        return shirofilter;
    }

    /**
     * Get a {@link File} referencing a resource.
     *
     * @param resource the name of the resource to get a File for
     * @return a {@link File} object referencing the resource
     * @throws URISyntaxException
     */
    public static File getResourceAsFile(String resource) throws URISyntaxException {
        return Paths.get(TestUtils.class.getResource(resource).toURI()).toFile();
    }

    /***
     * Fake injected OSGi services.
     * @return the serviceprovider implmenting the UkelonnService
     */
    public static UkelonnServiceProvider setupFakeOsgiServices() {
        ukelonnServiceSingleton = new UkelonnServiceProvider();
        ukelonnServiceSingleton.activate();
        UkelonnDatabaseProvider ukelonnDatabaseProvider = new UkelonnDatabaseProvider();
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        ukelonnDatabaseProvider.setDataSourceFactory(derbyDataSourceFactory);
        LogService logservice = new MockLogService();
        ukelonnDatabaseProvider.setLogService(logservice);
        ukelonnDatabaseProvider.activate();

        // Set up shiro
        MemorySession session = new MemorySession();
        session.activate();
        UkelonnRealm realm = new UkelonnRealm();
        realm.setDatabase(ukelonnDatabaseProvider.get());
        realm.activate();
        shirofilter = new UkelonnShiroFilter();
        shirofilter.setRealm(realm);
        shirofilter.setSession(session);
        shirofilter.activate();
        SecurityUtils.setSecurityManager(shirofilter.getSecurityManager());

        ukelonnServiceSingleton.setUkelonnDatabase(ukelonnDatabaseProvider.get());
        ukelonnServiceSingleton.setLogservice(logservice);
        return ukelonnServiceSingleton;
    }

    /***
     * Clear any (fake or non-fake) injected OSGi services.
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void releaseFakeOsgiServices() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        rollbackMockDataInTestDatabase();
        UkelonnServiceProvider ukelonnService = new UkelonnServiceProvider();
        if (ukelonnService != null) {
            ukelonnService.setUkelonnDatabase(null); // Release the database
        }
    }

    public static void rollbackMockDataInTestDatabase() {
        UkelonnDatabaseProvider ukelonnDatabaseProvider = null;
        try {
            ukelonnDatabaseProvider = (UkelonnDatabaseProvider) ukelonnServiceSingleton.getDatabase();
        } catch (Exception e) {
            // Swallow exception and continue
        }

        if (ukelonnDatabaseProvider == null) {
            ukelonnDatabaseProvider = new UkelonnDatabaseProvider();
        }

        ukelonnDatabaseProvider.rollbackMockData();
    }

    public static void restoreTestDatabase() {
        rollbackMockDataInTestDatabase();
        UkelonnDatabaseProvider ukelonnDatabaseProvider = (UkelonnDatabaseProvider) ukelonnServiceSingleton.getDatabase();
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        ukelonnDatabaseProvider.setDataSourceFactory(derbyDataSourceFactory);
        ukelonnDatabaseProvider.setLogService(ukelonnServiceSingleton.getLogservice());
        ukelonnDatabaseProvider.activate();
    }

    private static User copyUser(User user) {
        return new User(user.getUserId(), user.getUsername(), user.getEmail(), user.getFirstname(), user.getLastname());
    }

    public static Account copyAccount(Account account) {
        return new Account(account.getAccountId(), account.getUserId(), account.getUsername(), account.getFirstName(), account.getLastName(), account.getBalance());
    }

    public static TransactionType copyTransactionType(TransactionType transactiontype) {
        return new TransactionType(transactiontype.getId(), transactiontype.getTransactionTypeName(), transactiontype.getTransactionAmount(), transactiontype.isTransactionIsWork(), transactiontype.isTransactionIsWagePayment());
    }

    public static List<TransactionType> copyTransactiontypes(List<TransactionType> transactiontypes) {
        return transactiontypes.stream().map(transactiontype -> copyTransactionType(transactiontype)).collect(Collectors.toList());
    }

    public static Transaction convertUpdatedTransaction(UpdatedTransaction transaction) {
        return new Transaction(transaction.getId(), transactionttypes.get(transaction.getTransactionTypeId()), transaction.getTransactionTime(), transaction.getTransactionAmount(), false);
    }

    public static Transaction copyTransaction(Transaction transaction) {
        return new Transaction(transaction.getId(), transaction.getTransactionType(), transaction.getTransactionTime(), transaction.getTransactionAmount(), transaction.isPaidOut());
    }

    public static List<Transaction> copyTransactions(List<Transaction> transactions) {
        return transactions.stream().map(transaction -> copyTransaction(transaction)).collect(Collectors.toList());
    }

    public static List<TransactionType> getJobtypes() {
        return copyTransactiontypes(jobtypes);
    }

    public static List<TransactionType> getPaymenttypes() {
        return copyTransactiontypes(paymenttypes);
    }

    public static List<User> getUsers() {
        return Arrays.asList(copyUser(jadUser), copyUser(jodUser));
    }

    public static Account getJadAccount() {
        return copyAccount(jadAccount);
    }

    public static Account getJodAccount() {
        return copyAccount(jodAccount);
    }

    public static List<Transaction> getJadJobs() {
        return copyTransactions(jadJobs);
    }

    public static List<Transaction> getJadPayments() {
        return copyTransactions(jadPayments);
    }

    public static List<Transaction> getJodJobs() {
        return copyTransactions(jodJobs);
    }

    public static List<Transaction> getFirstJodJob() {
        return jodJobs.stream().limit(1).collect(Collectors.toList());
    }

    public static List<Account> getDummyAccounts() {
        return Arrays.asList(getJadAccount(), getJodAccount());
    }

}
