package no.priv.bang.ukelonn.bundle.db.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

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
import no.priv.bang.ukelonn.bundle.db.liquibase.UkelonnLiquibase;

public class UkelonnDatabaseProvider implements Provider<UkelonnDatabase>, UkelonnDatabase {
    private LogService logService;
    private PooledConnection connect = null;
    private DataSourceFactory dataSourceFactory;

    @Inject
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Inject
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
            throw new RuntimeException(e);
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
    public ResultSet query(PreparedStatement statement) {
        if (statement != null) {
            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                logError("Derby mock database query failed", e);
            } finally {
                try {
                    statement.closeOnCompletion();
                } catch (SQLException e) {
                    logError("Derby mock database prepared statement closeOnCompletion failed", e);
                }
            }
        }

        return null;
    }

    @Override
    public int update(PreparedStatement statement) {
        if (statement != null) {
            try {
                return statement.executeUpdate();
            } catch (SQLException e) {
                logError("Derby mock database update failed", e);
            } finally {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logError("Derby mock database prepared statement close failed", e);
                }
            }
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
