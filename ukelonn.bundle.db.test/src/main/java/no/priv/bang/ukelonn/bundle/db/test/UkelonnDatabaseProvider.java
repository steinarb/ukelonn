package no.priv.bang.ukelonn.bundle.db.test;

import java.sql.Connection;
import java.sql.ResultSet;
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
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.LiquibaseService;
import no.priv.bang.ukelonn.UkelonnDatabase;

public class UkelonnDatabaseProvider implements Provider<UkelonnDatabase>, UkelonnDatabase {
    private LogService logService;
    private PooledConnection connect = null;
    private DataSourceFactory dataSourceFactory;
    private LiquibaseService liquibase;

    @Inject
    public void setLogService(LogService logService) {
    	this.logService = logService;
    	initializeWhenAllOsgiServicesArePresent();
    }

    @Inject
    public void setLiquibase(LiquibaseService liquibase) {
        this.liquibase = liquibase;
    	initializeWhenAllOsgiServicesArePresent();
    }

    @Inject
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
    	this.dataSourceFactory = dataSourceFactory;
    	initializeWhenAllOsgiServicesArePresent();
    }

    private void initializeWhenAllOsgiServicesArePresent() {
    	if (dataSourceFactory != null && liquibase != null && logService != null) {
            Connection connection = createConnection();
            try {
                liquibase.createSchema(connection);
                insertMockData();
            } catch (Exception e) {
                logError("Failed to create derby test database schema", e);
            }
    	}
    }

    Connection createConnection() {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        try {
            ConnectionPoolDataSource dataSource = dataSourceFactory.createConnectionPoolDataSource(properties);
            connect = dataSource.getPooledConnection();
            return connect.getConnection();
        } catch (Exception e) {
            logError("Derby mock database failed to create connection", e);
        }

        return null;
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
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase changelogFinder = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, (Database)null);
            liquibase.runDBChangelog(connect.getConnection(), changelogFinder.getDatabaseChangeLog());
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
