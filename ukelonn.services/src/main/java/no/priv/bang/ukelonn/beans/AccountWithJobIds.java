/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.ukelonn.beans;

import java.util.Collections;
import java.util.List;

public class AccountWithJobIds {
    Account account;
    List<Integer> jobIds;

    private AccountWithJobIds() {
        // No-args constructor required by jackson
    }

    public Account getAccount() {
        return account;
    }
    public List<Integer> getJobIds() {
        return jobIds;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private Account account;
        List<Integer> jobIds = Collections.emptyList();

        public AccountWithJobIds build() {
            var accountWithJobIds = new AccountWithJobIds();
            accountWithJobIds.account = this.account;
            accountWithJobIds.jobIds = this.jobIds;
            return accountWithJobIds;
        }

        public Builder account(Account account) {
            this.account = account;
            return this;
        }

        public Builder jobIds(List<Integer> jobIds) {
            this.jobIds = jobIds;
            return this;
        }

    }
}
