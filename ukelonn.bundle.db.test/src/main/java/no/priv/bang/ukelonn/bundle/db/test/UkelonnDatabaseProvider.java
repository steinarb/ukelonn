package no.priv.bang.ukelonn.bundle.db.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import liquibase.exception.DatabaseException;
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
                liquibase.createSchema(connect);
                insertMockData();
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
     * @throws SQLException
     * @throws DatabaseException
     */
    List<RanChangeSet> getChangeLogHistory() throws SQLException, DatabaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
        StandardChangeLogHistoryService logHistoryService = ((StandardChangeLogHistoryService) ChangeLogHistoryServiceFactory.getInstance().getChangeLogService(database));
        return logHistoryService.getRanChangeSets();
    }

    public UkelonnDatabase get() {
        return this;
    }

    public void insertMockData() {
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor();
            Liquibase liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.update("");
        } catch (Exception e) {
            logError("Failed to fill derby test database with data.", e);
        }
    }

    @Override
    public String getName() {
        return "Ukelonn Derby test database";
    }

    public ResultSet query(String sqlQuery) {
        try {
            Statement statement = connect.getConnection().createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);
            return result;
        } catch (Exception e) {
            logError("Derby mock database query failed", e);
        }

        return null;
    }

    public int update(String sql) {
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(sql);
            return result;
        } catch (Exception e) {
            logError("Derby mock database query failed", e);
        }

        return 0;
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
