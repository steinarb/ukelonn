package no.priv.bang.ukelonn.bundle.db.postgresql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
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
                liquibase.createInitialSchema(connect);
                insertMockData();
                liquibase.updateSchema(connect);
            } catch (Exception e) {
                logError("Failed to create ukelonn database schema in the PostgreSQL ukelonn database", e);
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

    boolean insertMockData() {
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
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
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connect.getConnection().prepareStatement(sql);
        } catch (Exception e) {
            logError("PostgreSQL database failed to create prepared statement", e);
            return null;
        }
    }

    @Override
    public ResultSet query(PreparedStatement statement) {
        if (statement != null) {
            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                logError("PostgreSQL database query failed", e);
            } finally {
                try {
                    statement.closeOnCompletion();
                } catch (SQLException e) {
                    logError("PostgreSQL database prepared statement closeOnCompletion failed", e);
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
                logError("PostgreSQL database update failed", e);
            } finally {
                try {
                    statement.closeOnCompletion();
                } catch (SQLException e) {
                    logError("PostgreSQL database prepared statement close failed", e);
                }
            }
        }

        return 0;
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
