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
package no.priv.bang.ukelonn.db.liquibase.test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.ThreadLocalScopeManager;
import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.changelog.RanChangeSet;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Component(immediate=true, property = "name=ukelonndb")
public class TestLiquibaseRunner implements PreHook {
    static final String DEFAULT_DUMMY_DATA_CHANGELOG = "sql/data/db-initial-changelog.xml";
    private Logger logger;
    private String databaselanguage;

    @Reference
    public void setLogService(LogService logService) {
        this.logger = logService.getLogger(getClass());
    }

    @Activate
    public void activate(Map<String, Object> config) {
        databaselanguage = (String) config.get("databaselanguage");
        Scope.setScopeManager(new ThreadLocalScopeManager());
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        var liquibase = new UkelonnLiquibase();
        try {
            liquibase.createInitialSchema(datasource);
            insertMockData(datasource, liquibase);
            liquibase.updateSchema(datasource);
        } catch (Exception e) {
            logger.error("Failed to create derby test database schema", e);
        }
    }

    public boolean insertMockData(DataSource datasource, UkelonnLiquibase liquibase) {
        try(var connect = datasource.getConnection()) {
            liquibase.applyLiquibaseChangelist(connect, dummyDataResourceName(), getClass().getClassLoader());
            return true;
        } catch (Exception e) {
            logger.error("Failed to fill derby test database with data.", e);
            return false;
        }
    }

    public boolean rollbackMockData(DataSource datasource) {
        try(var connect = datasource.getConnection()) {
            var databaseConnection = new JdbcConnection(connect);
            try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
                try(var statement = connect.prepareStatement("delete from user_roles")) {
                    statement.executeUpdate();
                }
                try(var statement = connect.prepareStatement("delete from users")) {
                    statement.executeUpdate();
                }
                var liquibase = new Liquibase(dummyDataResourceName(), classLoaderResourceAccessor, databaseConnection);
                liquibase.rollback(3, "");
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to roll back mock data from derby test database.", e);
            return false;
        }
    }

    /**
     * Package private method to let the unit test determine if the Liquibase changesets have
     * been run.
     *
     * @return A list of all changesets run by liqubase in the derby database
     * @throws DatabaseException
     * @throws SQLException
     */
    List<RanChangeSet> getChangeLogHistory(DataSource datasource) throws DatabaseException, SQLException {
        try(var connect = datasource.getConnection()) {
            try(var databaseConnection = new JdbcConnection(connect)) {
                var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
                var logHistoryService = Scope.getCurrentScope().getSingleton(ChangeLogHistoryServiceFactory.class).getChangeLogService(database);
                return logHistoryService.getRanChangeSets();
            } catch (Exception e) {
                logger.error("Failed to create derby test database schema", e);
            }
        }

        return Collections.emptyList();
    }

    String dummyDataResourceName() {
        if (databaselanguage == null) {
            return DEFAULT_DUMMY_DATA_CHANGELOG;
        }

        String resourceName = DEFAULT_DUMMY_DATA_CHANGELOG.replace(".xml", "_" + databaselanguage + ".xml");
        if (getClass().getClassLoader().getResource(resourceName) == null) {
            logger.warn(String.format("Failed to find data for %s defaulting to Norwegian", databaselanguage));
            return DEFAULT_DUMMY_DATA_CHANGELOG;
        }

        return resourceName;
    }

}
