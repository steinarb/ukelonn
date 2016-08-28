package no.priv.bang.ukelonn.bundle.test.db;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.bundle.test.db.mocks.MockLogService;

public class UkelonnDatabaseProviderTest {

    @After
    public void tearDown() {
        try {
            DriverManager.getConnection("jdbc:derby:memory:ukelonn;drop=true");
        } catch (SQLException e) {
            // Just eat any exceptions quietly. The database will be cleaned up
        }
    }

    @Test
    public void testCreateDatabase() throws SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        setPrivateField(provider, "dataSourceFactory", dataSourceFactory); // Avoid side effects of the public setter
        provider.createConnection();
        boolean createSchemaStatementsReturnedUpdateResults = provider.createSchema();
        assertFalse("Expected no update results on schema creation", createSchemaStatementsReturnedUpdateResults);

        int[] numberOfRowsmodifiedAfterInsertOfMockData = provider.insertMockData();
        assertEquals(22, numberOfRowsmodifiedAfterInsertOfMockData.length);

        // Test the database by making a query using a view
        UkelonnDatabase database = provider.get();
        ResultSet onAccount = database.query("select * from accounts_view where username='jad'");
        assertNotNull(onAccount);
        while (onAccount.next()) {
            int account_id = onAccount.getInt("account_id");
            int user_id = onAccount.getInt("user_id");
            String username = onAccount.getString("username");
            String first_name = onAccount.getString("first_name");
            String last_name = onAccount.getString("last_name");
            assertEquals(3, account_id);
            assertEquals(3, user_id);
            assertEquals("jad", username);
            assertEquals("Jane", first_name);
            assertEquals("Doe", last_name);
        }
    }

    @Test
    public void testThatActivatorCreatesDatabase() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        // Test the database by making a query using a view
        UkelonnDatabase database = provider.get();
        ResultSet onAccount = database.query("select * from accounts_view where username='jad'");
        assertNotNull(onAccount);
        while (onAccount.next()) {
            int account_id = onAccount.getInt("account_id");
            int user_id = onAccount.getInt("user_id");
            String username = onAccount.getString("username");
            String first_name = onAccount.getString("first_name");
            String last_name = onAccount.getString("last_name");
            assertEquals(3, account_id);
            assertEquals(3, user_id);
            assertEquals("jad", username);
            assertEquals("Jane", first_name);
            assertEquals("Doe", last_name);
        }
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
