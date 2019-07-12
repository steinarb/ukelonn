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
package no.priv.bang.ukelonn.db.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;

import static no.priv.bang.ukelonn.UkelonnDatabaseConstants.*;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Component(service=UkelonnDatabase.class, immediate=true)
public class PGUkelonnDatabaseProvider implements UkelonnDatabase {
    private LogService logService;
    private DataSourceFactory dataSourceFactory;
    private UkelonnLiquibaseFactory ukelonnLiquibaseFactory;
    private LiquibaseFactory liquibaseFactory;
    private DataSource datasource;

    @Reference
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Reference
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Activate
    public void activate(Map<String, Object> config) {
        createDatasource(config);
        try(Connection connect = getConnection()) {
            UkelonnLiquibase liquibase = createUkelonnLiquibase();
            try {
                liquibase.createInitialSchema(connect);
                insertInitialDataInDatabase();
                liquibase.updateSchema(connect);
            } finally {
                // Liquibase sets autocommit to false
                connect.setAutoCommit(true);
            }
        } catch (Exception e) {
            logError("Failed to create ukelonn database schema in the PostgreSQL ukelonn database", e);
        }
    }

    void createDatasource(Map<String, Object> config) {
        Properties properties = createDatabaseConnectionProperties(config);

        try {
            datasource = dataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logError("PostgreSQL database service failed to create connection to local DB server", e);
        }
    }

    Properties createDatabaseConnectionProperties(Map<String, Object> config) {
        String jdbcUrl = (String) config.getOrDefault(UKELONN_JDBC_URL, "jdbc:postgresql:///ukelonn");
        String jdbcUser = (String) config.get(UKELONN_JDBC_USER);
        String jdbcPassword = (String) config.get(UKELONN_JDBC_PASSWORD);
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, jdbcUrl);
        if (jdbcUser != null) {
            properties.setProperty(DataSourceFactory.JDBC_USER, jdbcUser);
        }

        if (jdbcPassword != null) {
            properties.setProperty(DataSourceFactory.JDBC_PASSWORD, jdbcPassword);
        }

        return properties;
    }

    boolean insertInitialDataInDatabase() {
        try(Connection connect = getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = createLiquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.update("");
            return true;
        } catch (Exception e) {
            logError("Failed to fill PostgreSQL database with initial data.", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "Ukelonn PostgreSQL database";
    }

    @Override
    public DataSource getDatasource() {
        return datasource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (datasource == null) {
            throw new UkelonnException("Couldn't create connection to Ukelonn Derby test database because the Derby datasource was null");
        }

        return datasource.getConnection();
    }

    @Override
    public void forceReleaseLocks() {
        UkelonnLiquibase liquibase = createUkelonnLiquibase();
        try(Connection connect = getConnection()) {
            liquibase.forceReleaseLocks(connect);
        } catch (Exception e) {
            logError("Failed to force release Liquibase changelog lock on PostgreSQL database", e);
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

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

    @Override
    public String sumOverYearQuery() {
        return "select sum(t.transaction_amount), extract(year from t.transaction_time) as year from transactions t join transaction_types tt on tt.transaction_type_id=t.transaction_type_id join accounts a on a.account_id=t.account_id where tt.transaction_is_work and a.username=? group by extract(year from t.transaction_time) order by extract(year from t.transaction_time)";
    }

    @Override
    public String sumOverMonthQuery() {
        return "select sum(t.transaction_amount), extract(year from t.transaction_time) as year, extract(month from t.transaction_time) as month from transactions t join transaction_types tt on tt.transaction_type_id=t.transaction_type_id join accounts a on a.account_id=t.account_id where tt.transaction_is_work and a.username=? group by extract(year from t.transaction_time), extract(month from t.transaction_time) order by extract(year from t.transaction_time), extract(month from t.transaction_time)";
    }

}
