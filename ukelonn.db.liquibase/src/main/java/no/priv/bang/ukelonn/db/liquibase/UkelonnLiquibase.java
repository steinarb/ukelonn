/*
 * Copyright 2016-2024 Steinar Bang
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
package no.priv.bang.ukelonn.db.liquibase;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import liquibase.exception.LiquibaseException;
import no.priv.bang.karaf.liquibase.runner.LiquibaseClassPathChangeLogRunner;
import no.priv.bang.ukelonn.UkelonnException;

public class UkelonnLiquibase extends LiquibaseClassPathChangeLogRunner {

    static final String ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA = "Error closing resource when updating ukelonn schema";

    public void createInitialSchema(DataSource datasource) throws LiquibaseException {
        try (var connect = datasource.getConnection()) {
            applyLiquibaseChangelist(connect, "ukelonn-db-changelog/db-changelog-1.0.0.xml");
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException("Error closing resource when creating ukelonn initial schema", e1);
        }
    }

    public void updateSchema(DataSource datasource) throws LiquibaseException {
        try (var connect = datasource.getConnection()) {
            applyLiquibaseChangelist(connect, "ukelonn-db-changelog/db-changelog-1.0.1.xml");
        } catch (LiquibaseException e) {
            throw e;
        } catch (SQLException e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }

        try (var connect = datasource.getConnection()) {
            applyLiquibaseChangelist(connect, "ukelonn-db-changelog/db-changelog.xml");
        } catch (LiquibaseException e) {
            throw e;
        } catch (SQLException e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }
    }

    private void applyLiquibaseChangelist(Connection connect, String liquibaseChangeLogClassPathResource) throws LiquibaseException {
        applyLiquibaseChangelist(connect, liquibaseChangeLogClassPathResource, getClass().getClassLoader());
    }

}
