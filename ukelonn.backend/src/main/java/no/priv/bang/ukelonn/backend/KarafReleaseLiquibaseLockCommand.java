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

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Command(scope="ukelonn", name="release-liquibase-lock", description = "Forcibly release the Liquibase changelog lock")
@Service
public class KarafReleaseLiquibaseLockCommand implements Action {
    @Reference
    UkelonnService ukelonn;

    @Override
    public Object execute() throws Exception {
        DataSource datasource = ukelonn.getDataSource();
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        try (Connection connection = datasource.getConnection()) {
            liquibase.forceReleaseLocks(connection);
        }
        System.out.println("Forcibly unlocked the Liquibase changelog lock"); // NOSONAR This is command output and should not go to a logger
        return null;
    }
}
