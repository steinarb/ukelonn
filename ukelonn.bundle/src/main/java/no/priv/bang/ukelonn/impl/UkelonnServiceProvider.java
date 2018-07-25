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
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedJob;
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
    public Account getAccount(String username) {
        return getAccountInfoFromDatabase(getClass(), this, username);
    }

    @Override
    public Account registerPerformedJob(PerformedJob job) {
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

}
