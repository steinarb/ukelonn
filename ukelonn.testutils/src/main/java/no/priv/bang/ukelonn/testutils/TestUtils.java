/*
 * Copyright 2016-2021 Steinar Bang
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
import java.lang.reflect.Method;
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

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import no.priv.bang.ukelonn.beans.User;

/**
 * Contains static methods used in more than one unit test.
 *
 * @author Steinar Bang
 *
 */
public class TestUtils {

    private static User jadUser = User.with().userId(1).username("jad").email("jane1203@gmail.no").firstname("Jane").lastname("Doe").build();
    private static User jodUser = User.with().userId(1).username("jod").email("john1203@gmail.no").firstname("John").lastname("Doe").build();
    private static Account jadAccount = Account.with().accountid(1).username("jad").firstName("Jane").lastName("Doe").balance(673.0).build();
    private static Account jodAccount = Account.with().accountid(1).username("jod").firstName("John").lastName("Doe").balance(278.0).build();
    private static TransactionType jobtype1 = TransactionType.with()
        .id(1)
        .transactionTypeName("Støvsuging")
        .transactionAmount(45.0)
        .transactionIsWork(true)
        .build();
    private static TransactionType jobtype2 = TransactionType.with()
        .id(3)
        .transactionTypeName("Tømme oppvaskmaskin")
        .transactionAmount(35.0)
        .transactionIsWork(true)
        .build();
    private static TransactionType jobtype3 = TransactionType.with()
        .id(5)
        .transactionTypeName("Gå med resirk")
        .transactionAmount(20.0)
        .transactionIsWork(true)
        .build();
    private static TransactionType jobtype4 = TransactionType.with()
        .id(6)
        .transactionTypeName("Støvsuge rommet")
        .transactionAmount(15.0)
        .transactionIsWork(true)
        .build();
    private static List<TransactionType> jobtypes = Arrays.asList(jobtype1, jobtype2, jobtype3, jobtype4);
    private static TransactionType paymenttype1 = TransactionType.with()
        .id(2)
        .transactionTypeName("Inn på konto")
        .transactionIsWagePayment(true)
        .build();
    private static TransactionType paymenttype2 = TransactionType.with()
        .id(4)
        .transactionTypeName("Mobildata")
        .transactionAmount(100.0)
        .transactionIsWagePayment(true)
        .build();
    private static List<TransactionType> paymenttypes = Arrays.asList(paymenttype1, paymenttype2);
    private static Map<Integer, TransactionType> transactionttypes = Collections.unmodifiableMap(
        Stream.of(jobtypes, paymenttypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(TransactionType::getId, t -> t)));
    private static List<Transaction> jadJobs = Arrays.asList(
        Transaction.with().id(3).transactionType(jobtype1).transactionTime(new Date()).transactionAmount(45.0).paidOut(true).build(),
        Transaction.with().id(4).transactionType(jobtype2).transactionTime(new Date()).transactionAmount(35.0).paidOut(true).build(),
        Transaction.with().id(5).transactionType(jobtype1).transactionTime(new Date()).transactionAmount(45.0).build(),
        Transaction.with().id(6).transactionType(jobtype3).transactionTime(new Date()).transactionAmount(20.0).build(),
        Transaction.with().id(7).transactionType(jobtype1).transactionTime(new Date()).transactionAmount(45.0).build(),
        Transaction.with().id(8).transactionType(jobtype4).transactionTime(new Date()).transactionAmount(15.0).build(),
        Transaction.with().id(9).transactionType(jobtype1).transactionTime(new Date()).transactionAmount(45.0).build(),
        Transaction.with().id(10).transactionType(jobtype2).transactionTime(new Date()).transactionAmount(35.0).build(),
        Transaction.with().id(11).transactionType(jobtype3).transactionTime(new Date()).transactionAmount(20.0).build(),
        Transaction.with().id(12).transactionType(jobtype2).transactionTime(new Date()).transactionAmount(35.0).build());
    private static List<Transaction> jadPayments = Arrays.asList(
        Transaction.with().id(13).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(210.0).build(),
        Transaction.with().id(14).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(130.0).build(),
        Transaction.with().id(15).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(120.0).build(),
        Transaction.with().id(16).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(270.0).build(),
        Transaction.with().id(17).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(300.0).build(),
        Transaction.with().id(18).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(210.0).build(),
        Transaction.with().id(19).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(180.0).build(),
        Transaction.with().id(20).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(70.0).build(),
        Transaction.with().id(21).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(200.0).build(),
        Transaction.with().id(22).transactionType(paymenttype1).transactionTime(new Date()).transactionAmount(250.0).build());
    private static List<Transaction> jodJobs = Arrays.asList(
        Transaction.with().id(1).transactionType(jobtype1).transactionTime(new Date()).transactionAmount(45.0).build(),
        Transaction.with().id(2).transactionType(jobtype2).transactionTime(new Date()).transactionAmount(35.0).build());
    private static WebSecurityManager securitymanager;
    private static SimpleAccountRealm realm;

    public static WebSecurityManager getSecurityManager() {
        if (securitymanager == null) {
            WebIniSecurityManagerFactory securityManagerFactory = new WebIniSecurityManagerFactory(Ini.fromResourcePath("classpath:test.shiro.ini"));
            securitymanager = (WebSecurityManager) securityManagerFactory.getInstance();
            realm = findRealmFromSecurityManager(securitymanager);
        }

        return securitymanager;
    }

    public static SimpleAccount getShiroAccountFromRealm(String username) {
        if (realm == null) {
            getSecurityManager();
        }

        return findUserFromRealm(realm, username);
    }

    private static SimpleAccountRealm findRealmFromSecurityManager(WebSecurityManager securitymanager) {
        RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securitymanager;
        Collection<Realm> realms = realmSecurityManager.getRealms();
        return (SimpleAccountRealm) realms.iterator().next();
    }

    private static SimpleAccount findUserFromRealm(SimpleAccountRealm realm, String username) {
        try {
            Method getUserMethod = SimpleAccountRealm.class.getDeclaredMethod("getUser", String.class);
            getUserMethod.setAccessible(true);
            return (SimpleAccount) getUserMethod.invoke(realm, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private static User copyUser(User user) {
        return User.with(user).build();
    }

    private static no.priv.bang.osgiservice.users.User copyUserForUserManagement(User user) {
        return no.priv.bang.osgiservice.users.User.with()
            .userid(user.getUserId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .build();
    }

    public static Account copyAccount(Account account) {
        return Account.with()
            .accountid(account.getAccountId())
            .username(account.getUsername())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .balance(account.getBalance())
            .build();
    }

    public static TransactionType copyTransactionType(TransactionType transactiontype) {
        return TransactionType.with(transactiontype).build();
    }

    public static List<TransactionType> copyTransactiontypes(List<TransactionType> transactiontypes) {
        return transactiontypes.stream().map(transactiontype -> copyTransactionType(transactiontype)).collect(Collectors.toList());
    }

    public static Transaction convertUpdatedTransaction(UpdatedTransaction transaction) {
        return Transaction.with()
            .id(transaction.getId())
            .transactionType(transactionttypes.get(transaction.getTransactionTypeId()))
            .transactionTime(transaction.getTransactionTime())
            .transactionAmount(transaction.getTransactionAmount())
            .build();
    }

    public static Transaction copyTransaction(Transaction transaction) {
        return Transaction.with(transaction).build();
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

    public static List<no.priv.bang.osgiservice.users.User> getUsersForUserManagement() {
        return Arrays.asList(copyUserForUserManagement(jadUser), copyUserForUserManagement(jodUser));
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
