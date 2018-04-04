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
package no.priv.bang.ukelonn.bundle.db.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.changelog.RanChangeSet;
import liquibase.changelog.StandardChangeLogHistoryService;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.bundle.db.liquibase.UkelonnLiquibase;

@Component(service=UkelonnDatabase.class, immediate=true)
public class UkelonnDatabaseProvider implements UkelonnDatabase {
    private LogService logService;
    private PooledConnection connect = null;
    private DataSourceFactory dataSourceFactory;

    @Reference
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Reference
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
        if (this.dataSourceFactory != null) {
            createConnection();
            UkelonnLiquibase liquibase = new UkelonnLiquibase();
            try {
                liquibase.createInitialSchema(connect);
                insertMockData();
                liquibase.updateSchema(connect);
            } catch (Exception e) {
                logError("Failed to create derby test database schema", e);
            }
        }
    }

    void createConnection() {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        try {
            ConnectionPoolDataSource dataSource = dataSourceFactory.createConnectionPoolDataSource(properties);
            connect = dataSource.getPooledConnection();
        } catch (Exception e) {
            logError("Derby mock database failed to create connection", e);
        }
    }

    /**
     * Package private method to let the unit test determine if the Liquibase changesets have
     * been run.
     *
     * @return A list of all changesets run by liqubase in the derby database
     */
    List<RanChangeSet> getChangeLogHistory() {
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
            StandardChangeLogHistoryService logHistoryService = ((StandardChangeLogHistoryService) ChangeLogHistoryServiceFactory.getInstance().getChangeLogService(database));
            return logHistoryService.getRanChangeSets();
        } catch (Exception e) {
            throw new UkelonnException(e);
        }
    }

    public UkelonnDatabase get() {
        return this;
    }

    public boolean insertMockData() {
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.update("");
            return true;
        } catch (Exception e) {
            logError("Failed to fill derby test database with data.", e);
            return false;
        }
    }

    public boolean rollbackMockData() {
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.rollback(5, ""); // Note this number must be increased if additional change lists are added
            // Note also that all of those change lists will need to implement rollback (at least those changing the schema)
            return true;
        } catch (Exception e) {
            logError("Failed to roll back mock data from derby test database.", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "Ukelonn Derby test database";
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connect.getConnection().prepareStatement(sql);
        } catch (Exception e) {
            logError("Derby mock database failed to create prepared statement", e);
            return null;
        }
    }

    @Override
    public ResultSet query(PreparedStatement statement) throws SQLException {
        if (statement != null) {
            return statement.executeQuery();
        }

        return null;
    }

    @Override
    public int update(PreparedStatement statement) {
        try(PreparedStatement closableStatement = statement) {
            return closableStatement.executeUpdate();
        } catch (Exception e) {
            logError("Derby mock database update failed", e);
        }

        return 0;
    }

    @Override
    public void forceReleaseLocks() {
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        try {
            liquibase.forceReleaseLocks(connect);
        } catch (Exception e) {
            logError("Failed to force release Liquibase changelog lock on PostgreSQL database", e);
        }
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
