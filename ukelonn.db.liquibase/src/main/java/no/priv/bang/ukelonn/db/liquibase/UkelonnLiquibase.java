/*
 * Copyright 2016-2022 Steinar Bang
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

import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.ukelonn.UkelonnException;

public class UkelonnLiquibase {

    static final String ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA = "Error closing resource when updating ukelonn schema";

    public void createInitialSchema(DataSource datasource) throws LiquibaseException {
        try (var connect = datasource.getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
                try(var liquibase = new Liquibase("ukelonn-db-changelog/db-changelog-1.0.0.xml", classLoaderResourceAccessor, databaseConnection)) {
                    liquibase.update("");
                }
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException("Error closing resource when creating ukelonn initial schema", e1);
        }
    }

    public void updateSchema(DataSource datasource) throws LiquibaseException {
        try (var connect = datasource.getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
                try(var liquibase = new Liquibase("ukelonn-db-changelog/db-changelog-1.0.1.xml", classLoaderResourceAccessor, databaseConnection)) {
                    liquibase.update("");
                }
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }

        try (var connect = datasource.getConnection()) {
            AuthserviceLiquibase authserviceLiquibase = new AuthserviceLiquibase();
            authserviceLiquibase.createInitialSchema(connect);
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }

        try (var connect = datasource.getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
                try(var liquibase = new Liquibase("ukelonn-db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection)) {
                    liquibase.update("");
                }
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }
    }

    public void forceReleaseLocks(Connection connect) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect);
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
            try(var liquibase = new Liquibase("ukelonn-db-changelog/db-changelog-1.0.0.xml", classLoaderResourceAccessor, databaseConnection)) {
                liquibase.forceReleaseLocks();
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException("Error closing resource when forcibly releasing liquibase lock", e1);
        }
    }

}
