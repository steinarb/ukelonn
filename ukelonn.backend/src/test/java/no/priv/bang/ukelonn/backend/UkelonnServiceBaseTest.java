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
package no.priv.bang.ukelonn.backend;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
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

public class UkelonnServiceBaseTest {

    @Test
    public void testDefaults() {
        UkelonnService ukelonn = new UkelonnServiceBase() {

                @Override
                public Account registerPerformedJob(PerformedTransaction job) {
                    return null;
                }

                @Override
                public Account registerPayment(PerformedTransaction payment) {
                    return null;
                }

                @Override
                public List<TransactionType> modifyPaymenttype(TransactionType paymenttype) {
                    return null;
                }

                @Override
                public List<TransactionType> modifyJobtype(TransactionType jobtype) {
                    return null;
                }

                @Override
                public List<TransactionType> getPaymenttypes() {
                    return null;
                }

                @Override
                public List<Transaction> getPayments(int accountId) {
                    return null;
                }

                @Override
                public List<Transaction> getJobs(int accountId) {
                    return null;
                }

                @Override
                public List<TransactionType> getJobTypes() {
                    return null;
                }

                @Override
                public List<Account> getAccounts() {
                    return null;
                }

                @Override
                public Account getAccount(String username) {
                    return null;
                }

                @Override
                public List<TransactionType> createPaymenttype(TransactionType paymenttype) {
                    return null;
                }

                @Override
                public List<TransactionType> createJobtype(TransactionType jobtype) {
                    return null;
                }

                @Override
                public List<Transaction> deleteJobsFromAccount(int accountId, List<Integer> idsOfJobsToDelete) {
                    return null;
                }

                @Override
                public List<Notification> notificationsTo(String username) {
                    return null;
                }

                @Override
                public void notificationTo(String username, Notification notification) {
                    // Empty method
                }

                @Override
                public List<Transaction> updateJob(UpdatedTransaction editedJob) {
                    return null;
                }

                @Override
                public Account addAccount(User user) {
                    return null;
                }

                @Override
                public List<SumYear> earningsSumOverYear(String username) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<SumYearMonth> earningsSumOverMonth(String username) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Bonus> getActiveBonuses() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Bonus> getAllBonuses() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Bonus> createBonus(Bonus newBonus) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Bonus> modifyBonus(Bonus updatedBonus) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Bonus> deleteBonus(Bonus removedBonus) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String defaultLocale() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<String> availableLocales() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public Map<String, String> displayTexts(String locale) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

        assertEquals("Hello world!", ukelonn.getMessage());
        assertNull(ukelonn.getDataSource());
        assertNull(ukelonn.getLogservice());
    }

}
