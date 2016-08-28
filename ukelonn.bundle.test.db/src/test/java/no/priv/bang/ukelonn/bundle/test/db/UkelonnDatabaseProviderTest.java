package no.priv.bang.ukelonn.bundle.test.db;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class UkelonnDatabaseProviderTest {

    @Test
    public void testCreateDatabase() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        UkelonnDatabase database = provider.get();
        assertNotNull(database);
        boolean createSchemaStatementsReturnedUpdateResults = database.createSchema();
        assertFalse("Expected no update results on schema creation", createSchemaStatementsReturnedUpdateResults);

        int[] numberOfRowsmodifiedAfterInsertOfMockData = database.insertMockData();
        assertEquals(22, numberOfRowsmodifiedAfterInsertOfMockData.length);

        // Test the database by making a query using a view
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

}
