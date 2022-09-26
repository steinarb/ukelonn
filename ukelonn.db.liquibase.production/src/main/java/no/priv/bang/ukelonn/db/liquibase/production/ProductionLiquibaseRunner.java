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
package no.priv.bang.ukelonn.db.liquibase.production;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Component(immediate=true, property = "name=ukelonndb")
public class ProductionLiquibaseRunner implements PreHook {
    static final String INITIAL_DATA_DEFAULT_RESOURCE_NAME = "db-changelog/db-changelog.xml";
    private Logger logger;
    private UkelonnLiquibaseFactory ukelonnLiquibaseFactory;
    private LiquibaseFactory liquibaseFactory;
    private String databaselanguage;

    @Reference
    public void setLogService(LogService logService) {
        this.logger = logService.getLogger(getClass());
    }

    @Activate
    public void activate(Map<String, Object> config) {
        databaselanguage = (String) config.get("databaselanguage");
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        try {
            UkelonnLiquibase liquibase = createUkelonnLiquibase();
            liquibase.createInitialSchema(datasource);
            insertInitialDataInDatabase(datasource);
            liquibase.updateSchema(datasource);
        } catch (Exception e) {
            logger.error("Failed to create ukelonn database schema in the PostgreSQL ukelonn database", e);
        }
    }

    boolean insertInitialDataInDatabase(DataSource datasource) {
        try(Connection connect = datasource.getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
                try(var liquibase = createLiquibase(initialDataResourceName(), classLoaderResourceAccessor, databaseConnection)) {
                    liquibase.update("");
                }
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

    Liquibase createLiquibase(String changelogfile, ResourceAccessor resourceAccessor, DatabaseConnection databaseConnection) throws LiquibaseException {
        if (liquibaseFactory == null) {
            liquibaseFactory = new LiquibaseFactory() {
                    @Override
                    public Liquibase create(String changelogfile, ResourceAccessor resourceAccessor, DatabaseConnection databaseConnection) throws LiquibaseException {
                        return new Liquibase(changelogfile, resourceAccessor, databaseConnection);
                    }
                };
        }

        return liquibaseFactory.create(changelogfile, resourceAccessor, databaseConnection);
    }

    void setLiquibaseFactory(LiquibaseFactory liquibaseFactory) {
        this.liquibaseFactory = liquibaseFactory;
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
