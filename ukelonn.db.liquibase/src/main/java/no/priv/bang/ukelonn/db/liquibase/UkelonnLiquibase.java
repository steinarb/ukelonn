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

import static liquibase.Scope.Attr.resourceAccessor;
import static liquibase.command.core.UpdateCommandStep.CHANGELOG_FILE_ARG;
import static liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep.DATABASE_ARG;

import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.ukelonn.UkelonnException;

public class UkelonnLiquibase {

    private static final String UPDATE = "update";
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
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }

        try (var connect = datasource.getConnection()) {
            var authserviceLiquibase = new AuthserviceLiquibase();
            authserviceLiquibase.createInitialSchema(connect);
        } catch (LiquibaseException | AuthserviceException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }

        try (var connect = datasource.getConnection()) {
            applyLiquibaseChangelist(connect, "ukelonn-db-changelog/db-changelog.xml");
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e1) {
            throw new UkelonnException(ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA, e1);
        }
    }

    private void applyLiquibaseChangelist(Connection connect, String liquibaseChangeLogClassPathResource) throws Exception, DatabaseException {
        applyLiquibaseChangelist(connect, liquibaseChangeLogClassPathResource, getClass().getClassLoader());
    }

    public void applyLiquibaseChangelist(Connection connect, String liquibaseChangeLogClassPathResource, ClassLoader classLoader) throws Exception {
        try (var database = findCorrectDatabaseImplementation(connect)) {
            Scope.child(scopeObjectsWithClassPathResourceAccessor(classLoader), () -> new CommandScope(UPDATE)
                .addArgumentValue(DATABASE_ARG, database)
                .addArgumentValue(CHANGELOG_FILE_ARG, liquibaseChangeLogClassPathResource)
                .execute());
        }
    }

    private Database findCorrectDatabaseImplementation(Connection connect) throws DatabaseException {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connect));
    }

    private Map<String, Object> scopeObjectsWithClassPathResourceAccessor(ClassLoader classLoader) {
        return Map.of(resourceAccessor.name(), new ClassLoaderResourceAccessor(classLoader));
    }

}
