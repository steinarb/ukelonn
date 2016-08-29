package no.priv.bang.ukelonn.bundle.test.db;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnDatabase;

public class UkelonnDatabaseProvider implements Provider<UkelonnDatabase>, UkelonnDatabase {
    private LogService logService;
    private Connection connect = null;
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
            createSchema();
            insertMockData();
    	}
    }

    void createConnection() {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        try {
            DataSource dataSource = dataSourceFactory.createDataSource(properties);
            connect = dataSource.getConnection();
        } catch (Exception e) {
            logError("Derby mock database failed to create connection", e);
        }
    }

    public UkelonnDatabase get() {
        return this;
    }

    public boolean createSchema() {
        try {
            Statement createSchema = connect.createStatement();
            boolean result = false;
            result |= createSchema.execute(getResourceAsString("/sql/tables/users.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/tables/accounts.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/tables/transaction_types.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/tables/transactions.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/tables/administrators.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/views/accounts_view.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/views/wage_payments_view.sql"));
            result |= createSchema.execute(getResourceAsString("/sql/views/work_done_view.sql"));
            return result;
        } catch (Exception e) {
            logError("Derby mock database failed to create schema", e);
            return false;
        }
    }

    public int[] insertMockData() {
        int[] insertedRowCounts = new int[0];
        int[] insertedRows = insertRows("/sql/data/example_users.sql");
        insertedRowCounts = concatenate(insertedRowCounts, insertedRows);
        insertedRows = insertRows("/sql/data/example_accounts.sql");
        insertedRowCounts = concatenate(insertedRowCounts, insertedRows);
        insertedRows = insertRows("/sql/data/example_administrators.sql");
        insertedRowCounts = concatenate(insertedRowCounts, insertedRows);
        insertedRows = insertRows("/sql/data/example_transaction_types.sql");
        insertedRowCounts = concatenate(insertedRowCounts, insertedRows);
        insertedRows = insertRows("/sql/data/example_transactions.sql");
        insertedRowCounts = concatenate(insertedRowCounts, insertedRows);
        return insertedRowCounts;
    }

    public ResultSet query(String sqlQuery) {
        try {
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);
            return result;
        } catch (Exception e) {
            logError("Derby mock database query failed", e);
        }

        return null;
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

    private int[] insertRows(String resourceName) {
        String sql = getResourceAsString(resourceName);
        String[] insertStatements = sql.split(";\r?\n");
        try {
            Statement statement = connect.createStatement();
            statement.clearBatch();
            for (String insertStatement : insertStatements) {
                statement.addBatch(insertStatement);
            }

            int[] insertedRows = statement.executeBatch();
            statement.clearBatch();
            return insertedRows;
        } catch (Exception e) {
            logError("Derby mock database failed to create insert mock data", e);
        }

        return null;
    }

    private int[] concatenate(int[] array1, int[] array2) {
        int[] retval = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, retval, 0, array1.length);
        System.arraycopy(array2, 0, retval, array1.length, array2.length);
        return retval;
    }

    private String getResourceAsString(String resourceName) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        InputStream resourceStream = getClass().getResourceAsStream(resourceName);
        try {
            while ((length = resourceStream.read(buffer)) != -1) {
                resource.write(buffer, 0, length);
            }

            return resource.toString("UTF-8");
        } catch (Exception e) {
            logError("Derby mock database read resource \"" + resourceName + "\"", e);
        }

        return null;
    }

}
