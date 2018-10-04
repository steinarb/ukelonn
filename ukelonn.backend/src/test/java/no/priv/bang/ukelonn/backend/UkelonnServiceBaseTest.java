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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
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
                public List<User> modifyUser(User user) {
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
                public List<User> getUsers() {
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
                public List<User> createUser(PasswordsWithUser passwords) {
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
                public List<User> changePassword(PasswordsWithUser passwords) {
                    return null;
                }

                @Override
                public List<Transaction> deleteJobsFromAccount(int accountId, List<Integer> idsOfJobsToDelete) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<Notification> notificationsTo(String username) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void notificationTo(String username, Notification notification) {
                    // TODO Auto-generated method stub

                }

                @Override
                public List<Transaction> updateJob(UpdatedTransaction editedJob) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

        assertEquals("Hello world!", ukelonn.getMessage());
        assertNull(ukelonn.getDatabase());
        assertNull(ukelonn.getLogservice());
    }

}
