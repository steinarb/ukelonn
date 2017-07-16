package no.priv.bang.ukelonn.bundle.db.postgresql;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;
import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.osgi.PGDataSourceFactory;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.bundle.db.postgresql.mocks.MockLogService;

public class PGUkelonnDatabaseProviderTest {

    @Test
    public void testGetName() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        UkelonnDatabase database = provider.get();

        String databaseName = database.getName();
        assertEquals("Ukelonn PostgreSQL database", databaseName);
    }

    @Ignore
    @Test
    public void testDatabase() throws SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DataSourceFactory dataSourceFactory = new PGDataSourceFactory();
        setPrivateField(provider, "dataSourceFactory", dataSourceFactory); // Avoid side effects of the public setter
        provider.createConnection();

        // Test the database by making a query using a view
        UkelonnDatabase database = provider.get();
        PreparedStatement statement = database.prepareStatement("select * from accounts_view where username=?");
        statement.setString(1, "jad");
        ResultSet onAccount = database.query(statement);
        assertNotNull("Expected returned account JDBC resultset not to be null", onAccount);
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

    @Ignore
    @Test
    public void testAdministratorsView() throws SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DataSourceFactory dataSourceFactory = new PGDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection

        UkelonnDatabase database = provider.get();

        // Test that the database has users
        PreparedStatement statement = database.prepareStatement("select * from users");
        ResultSet allUsers = database.query(statement);
        assertNotNull("Expected returned allUsers JDBC resultset not to be null", allUsers);
        int allUserCount = 0;
        while (allUsers.next()) { ++allUserCount; }
        assertThat(allUserCount, greaterThan(0));

        // Test that the database administrators table has rows
        PreparedStatement statement2 = database.prepareStatement("select * from administrators");
        ResultSet allAdministrators = database.query(statement2);
        int allAdminstratorsCount = 0;
        while (allAdministrators.next()) { ++allAdminstratorsCount; }
        assertThat(allAdminstratorsCount, greaterThan(0));

        // Test that the administrators_view is present
        PreparedStatement statement3 = database.prepareStatement("select * from administrators_view");
        ResultSet allAdministratorsView = database.query(statement3);
        int allAdminstratorsViewCount = 0;
        while (allAdministratorsView.next()) { ++allAdminstratorsViewCount; }
        assertEquals(1, allAdminstratorsViewCount);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
