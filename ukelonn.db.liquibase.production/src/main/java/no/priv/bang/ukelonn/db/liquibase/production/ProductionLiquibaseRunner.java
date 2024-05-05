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
package no.priv.bang.ukelonn.db.liquibase.production;

import static liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep.DATABASE_ARG;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.ThreadLocalScopeManager;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Component(immediate=true, property = "name=ukelonndb")
public class ProductionLiquibaseRunner implements PreHook {
    static final String INITIAL_DATA_DEFAULT_RESOURCE_NAME = "db-changelog/db-changelog.xml";
    private Logger logger;
    private UkelonnLiquibaseFactory ukelonnLiquibaseFactory;
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
        try {
            var liquibase = createUkelonnLiquibase();
            liquibase.createInitialSchema(datasource);
            insertInitialDataInDatabase(datasource);
            liquibase.updateSchema(datasource);
        } catch (Exception e) {
            logger.error("Failed to create ukelonn database schema in the PostgreSQL ukelonn database", e);
        }
    }

    boolean insertInitialDataInDatabase(DataSource datasource) {
        try(var connect = datasource.getConnection()) {
            try (var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connect))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor(getClass().getClassLoader()));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                            .addArgumentValue(DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, initialDataResourceName())
                            .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                            .execute());
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to fill ukelonn PostgreSQL database with initial data.", e);
            return false;
        }
    }

    UkelonnLiquibase createUkelonnLiquibase() {
        if (ukelonnLiquibaseFactory == null) {
            ukelonnLiquibaseFactory = new UkelonnLiquibaseFactory() { // NOSONAR
                    @Override
                    public UkelonnLiquibase create() {
                        return new UkelonnLiquibase();
                    }
                };
        }

        return ukelonnLiquibaseFactory.create();
    }

    void setUkelonnLiquibaseFactory(UkelonnLiquibaseFactory ukelonnLiquibaseFactory) {
        this.ukelonnLiquibaseFactory = ukelonnLiquibaseFactory;
    }

    String initialDataResourceName() {
        if (databaselanguage == null) {
            return INITIAL_DATA_DEFAULT_RESOURCE_NAME;
        }

        String resourceName = INITIAL_DATA_DEFAULT_RESOURCE_NAME.replace(".xml", "_" + databaselanguage + ".xml");
        if (getClass().getClassLoader().getResource(resourceName) == null) {
            logger.warn(String.format("Failed to find data for %s defaulting to Norwegian", databaselanguage));
            return INITIAL_DATA_DEFAULT_RESOURCE_NAME;
        }

        return resourceName;
    }

}
