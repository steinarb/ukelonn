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
package no.priv.bang.ukelonn.impl;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import no.priv.bang.ukelonn.UkelonnDatabase;

@Command(scope="ukelonn", name="release-liquibase-lock", description = "Forcibly release the Liquibase changelog lock")
@Service
public class KarafReleaseLiquibaseLockCommand implements Action {
    @Reference
    UkelonnDatabase database;

    @Override
    public Object execute() throws Exception {
        database.forceReleaseLocks();
        System.out.println("Forcibly unlocked the Liquibase changelog lock"); // NOSONAR
        return null;
    }
}
