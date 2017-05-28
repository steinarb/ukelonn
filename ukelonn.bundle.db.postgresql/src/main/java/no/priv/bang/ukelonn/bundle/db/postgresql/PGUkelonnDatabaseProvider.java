package no.priv.bang.ukelonn.bundle.db.postgresql;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.bundle.db.liquibase.UkelonnLiquibase;

public class PGUkelonnDatabaseProvider implements Provider<UkelonnDatabase>, UkelonnDatabase {
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
    	if (dataSourceFactory != null) {
            createConnection();
            UkelonnLiquibase liquibase = new UkelonnLiquibase();
            try {
                liquibase.createSchema(connect);
            } catch (Exception e) {
                logError("Failed to create derby test database schema", e);
            }
    	}
    }

    void createConnection() {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:postgresql:///ukelonn");
        try {
            ConnectionPoolDataSource dataSource = dataSourceFactory.createConnectionPoolDataSource(properties);
            connect = dataSource.getPooledConnection();
        } catch (Exception e) {
            logError("PostgreSQL database service failed to create connection to local DB server", e);
        }
    }

    public UkelonnDatabase get() {
        return this;
    }

    @Override
    public String getName() {
        return "Ukelonn PostgreSQL database";
    }

    public ResultSet query(String sqlQuery) {
        try {
            Statement statement = connect.getConnection().createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);
            return result;
        } catch (Exception e) {
            logError("PostgreSQL database query failed", e);
        }

        return null;
    }

    public int update(String sql) {
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(sql);
            return result;
        } catch (Exception e) {
            logError("PostgreSQL database update failed", e);
        }

        return 0;
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
