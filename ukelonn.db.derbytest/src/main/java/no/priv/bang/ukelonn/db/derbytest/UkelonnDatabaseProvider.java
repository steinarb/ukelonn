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
package no.priv.bang.ukelonn.db.derbytest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import org.osgi.service.component.annotations.Activate;
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
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

@Component(service=UkelonnDatabase.class, immediate=true)
public class UkelonnDatabaseProvider implements UkelonnDatabase {
    private LogService logService;
    private DataSourceFactory dataSourceFactory;
    private DataSource datasource;
    private boolean initialChangelog = false;

    @Reference
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Reference
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Activate
    public void activate() {
        createDatasource();
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        try(Connection connect = getConnection()) {
            try {
                liquibase.createInitialSchema(connect);
                insertMockData();
                liquibase.updateSchema(connect);
            } finally {
                // Liquibase sets Connection.autoCommit to false, set it back to true
                connect.setAutoCommit(true);
            }
        } catch (Exception e) {
            logError("Failed to create derby test database schema", e);
        }
    }

    void createDatasource() {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        try {
            datasource = dataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logError("Derby mock database failed to create datasource", e);
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
    List<RanChangeSet> getChangeLogHistory() throws DatabaseException, SQLException {
        try(Connection connect = getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            try {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
                StandardChangeLogHistoryService logHistoryService = ((StandardChangeLogHistoryService) ChangeLogHistoryServiceFactory.getInstance().getChangeLogService(database));
                return logHistoryService.getRanChangeSets();
            } finally {
                databaseConnection.close();
            }
        }
    }

    public boolean insertMockData() {
        try(Connection connect = getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            if (hasTable(connect, "user_roles")) {
                initialChangelog = false;
                Liquibase liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
                liquibase.update("");
            } else {
                // Schema before authservice schema applied
                initialChangelog = true;
                Liquibase liquibase = new Liquibase("sql/data/db-initial-changelog.xml", classLoaderResourceAccessor, databaseConnection);
                liquibase.update("");
            }
            return true;
        } catch (Exception e) {
            logError("Failed to fill derby test database with data.", e);
            return false;
        }
    }

    private boolean hasTable(Connection connection, String tablename) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet tables = metadata.getTables(null, null, "%", null);
        while(tables.next()) {
            if (tablename.equals(tables.getString(3))) {
                return true;
            }
        }

        return false;
    }

    public boolean rollbackMockData() {
        try(Connection connect = getConnection()) {
            DatabaseConnection databaseConnection = new JdbcConnection(connect);
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            if (initialChangelog) {
                try(PreparedStatement statement = connect.prepareStatement("delete from user_roles")) {
                    statement.executeUpdate();
                }
                try(PreparedStatement statement = connect.prepareStatement("delete from users")) {
                    statement.executeUpdate();
                }
                Liquibase liquibase = new Liquibase("sql/data/db-initial-changelog.xml", classLoaderResourceAccessor, databaseConnection);
                liquibase.rollback(3, "");
            } else {
                Liquibase liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
                liquibase.rollback(5, ""); // Note this number must be increased if additional change lists are added
                // Note also that all of those change lists will need to implement rollback (at least those changing the schema)
            }
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
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        try(Connection connect = getConnection()) {
            liquibase.forceReleaseLocks(connect);
        } catch (Exception e) {
            logError("Failed to force release Liquibase changelog lock on derby database", e);
        }
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

    @Override
    public String sumOverYearQuery() {
        return "select sum(t.transaction_amount), YEAR(t.transaction_time) from transactions t join transaction_types tt on tt.transaction_type_id=t.transaction_type_id join accounts a on a.account_id=t.account_id where tt.transaction_is_work and a.username=? group by YEAR(t.transaction_time) order by YEAR(t.transaction_time)";
    }

    @Override
    public String sumOverMonthQuery() {
        return "select sum(t.transaction_amount), YEAR(t.transaction_time), MONTH(t.transaction_time) from transactions t join transaction_types tt on tt.transaction_type_id=t.transaction_type_id join accounts a on a.account_id=t.account_id where tt.transaction_is_work and a.username=? group by YEAR(t.transaction_time), MONTH(t.transaction_time) order by YEAR(t.transaction_time), MONTH(t.transaction_time)";
    }

}
