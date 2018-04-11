/*
 * Copyright 2018 Steinar Bang
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
package no.priv.bang.ukelonn.impl.data;

import java.util.Collections;
import java.util.List;

import no.priv.bang.ukelonn.impl.Transaction;

public class AccountHistory {

    private List<Transaction> recentJobs = Collections.emptyList();
    private List<Transaction> recentPayments = Collections.emptyList();

    public void setRecentJobs(List<Transaction> recentJobs) {
        this.recentJobs = recentJobs;
    }

    public List<Transaction> getRecentJobs() {
        return recentJobs;
    }

    public void setRecentPayments(List<Transaction> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<Transaction> getRecentPayments() {
        return recentPayments;
    }

}
