/*
 * Copyright 2016-2020 Steinar Bang
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
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.SumYearMonth;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
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

    DataSource getDataSource();

    LogService getLogservice();

    List<Account> getAccounts();

    Account getAccount(String username);

    Account registerPerformedJob(PerformedTransaction job);

    List<Transaction> updateJob(UpdatedTransaction editedJob);

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

    Account addAccount(User user);

    List<Notification> notificationsTo(String username);

    void notificationTo(String username, Notification notification);

    List<SumYear> earningsSumOverYear(String username);

    List<SumYearMonth> earningsSumOverMonth(String username);

    List<Bonus> getActiveBonuses();

    List<Bonus> getAllBonuses();

    List<Bonus> createBonus(Bonus newBonus);

    List<Bonus> modifyBonus(Bonus updatedBonus);

    List<Bonus> deleteBonus(Bonus removedBonus);

    String defaultLocale();

    List<String> availableLocales();

    Map<String, String> displayTexts(String locale);

}
