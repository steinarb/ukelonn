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
package no.priv.bang.ukelonn.impl;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.util.List;
import java.util.Map;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;

/**
 * The OSGi component that listens for a {@link WebContainer} service
 * and registers a servlet with the web container.
 *
 * @author Steinar Bang
 *
 */
@Component(service=UkelonnService.class, immediate=true)
public class UkelonnServiceProvider extends UkelonnServiceBase {
    private UkelonnDatabase database;
    private LogService logservice;

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
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    public LogService getLogservice() {
        return logservice;
    }

    public UkelonnService get() {
        return this;
    }

    @Override
    public List<Account> getAccounts() {
        return getAccountsFromDatabase(getClass(), this);
    }

    @Override
    public Account getAccount(String username) {
        return getAccountInfoFromDatabase(getClass(), this, username);
    }

    @Override
    public Account registerPerformedJob(PerformedTransaction job) {
        registerNewJobInDatabase(getClass(), this, job.getAccount(), job.getTransactionTypeId(), job.getTransactionAmount());
        return getAccount(job.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> getJobTypes() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass(), this);
        return getJobTypesFromTransactionTypes(transactionTypes.values());
    }

    @Override
    public List<Transaction> getJobs(int accountId) {
        return getJobsFromAccount(accountId, getClass(), this);
    }

    @Override
    public List<Transaction> getPayments(int accountId) {
        return getPaymentsFromAccount(accountId, getClass(), this);
    }

    @Override
    public List<TransactionType> getPaymenttypes() {
        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass(), this);
        return getPaymentTypesFromTransactionTypes(transactionTypes.values());
    }

    @Override
    public Account registerPayment(PerformedTransaction payment) {
        int result = addNewPaymentToAccountInDatabase(getClass(), this, payment.getAccount(), payment.getTransactionTypeId(), payment.getTransactionAmount());
        if (result < 1) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to register payment of type %d, amount %d for user \"%s\"", payment.getTransactionTypeId(), payment.getTransactionAmount(), payment.getAccount().getUsername()));
            return null;
        }

        return getAccount(payment.getAccount().getUsername());
    }

    @Override
    public List<TransactionType> modifyJobtype(TransactionType jobtype) {
        int result = updateTransactionTypeInDatabase(getClass(), this, jobtype);
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to update jobtype %d in the database", jobtype.getId()));
        }

        return getJobTypes();
    }

    @Override
    public List<TransactionType> createJobtype(TransactionType jobtype) {
        int result = addJobTypeToDatabase(getClass(), this, jobtype.getTransactionTypeName(), jobtype.getTransactionAmount());
        if (result == UPDATE_FAILED) {
            throw new UkelonnException(String.format("Failed to create jobtype \"%s\" in the database", jobtype.getTransactionTypeName()));
        }

        return getJobTypes();
    }

}
