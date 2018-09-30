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
package no.priv.bang.ukelonn;

import java.util.List;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.User;

/**
 * This is the service exposed by the ukelonn.bundle
 * after it gets all of its injections, and activates.
 *
 * The plan is to make this interface a place to access various
 * aspects of the web application, e.g. JDBC storage.
 *
 * @author Steinar Bang
 *
 */
public interface UkelonnService {

    String getMessage();

    UkelonnDatabase getDatabase();

    LogService getLogservice();

    List<Account> getAccounts();

    Account getAccount(String username);

    Account registerPerformedJob(PerformedTransaction job);

    List<TransactionType> getJobTypes();

    List<Transaction> getJobs(int accountId);

    List<Transaction> deleteJobsFromAccount(int accountId, List<Integer> idsOfJobsToDelete);

    List<Transaction> getPayments(int accountId);

    List<TransactionType> getPaymenttypes();

    Account registerPayment(PerformedTransaction payment);

    List<TransactionType> modifyJobtype(TransactionType jobtype);

    List<TransactionType> createJobtype(TransactionType jobtype);

    List<TransactionType> modifyPaymenttype(TransactionType paymenttype);

    List<TransactionType> createPaymenttype(TransactionType paymenttype);

    List<User> getUsers();

    List<User> modifyUser(User user);

    List<User> createUser(PasswordsWithUser passwords);

    List<User> changePassword(PasswordsWithUser passwords);

    List<Notification> notificationsTo(String username);

    void notificationTo(String username, Notification notification);

}
