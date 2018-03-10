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
package no.priv.bang.ukelonn.bundle.db.postgresql;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.osgi.PGDataSourceFactory;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnDatabaseConstants;
import no.priv.bang.ukelonn.bundle.db.postgresql.mocks.MockLogService;

public class PGUkelonnDatabaseProviderTest {

    @Test
    public void testGetName() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        UkelonnDatabase database = provider.get();

        String databaseName = database.getName();
        assertEquals("Ukelonn PostgreSQL database", databaseName);
    }

    @Ignore("Test requires a running PostgreSQL server, so more of an integration test")
    @Test
    public void testDatabase() throws SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DataSourceFactory dataSourceFactory = new PGDataSourceFactory();
        setPrivateField(provider, "dataSourceFactory", dataSourceFactory); // Avoid side effects of the public setter
        provider.createConnection(null);

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

    @Ignore("Test requires a running PostgreSQL server, so more of an integration test")
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

    @Test
    public void testCreateDatabaseConnectionPropertiesDefaultValues() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        Properties connectionProperties = provider.createDatabaseConnectionProperties(Collections.emptyMap());
        assertEquals(1, connectionProperties.size());
    }

    @Test
    public void testCreateDatabaseConnectionPropertiesRemoteDatabase() {
        Map<String, Object> config = new HashMap<>();
        config.put(UkelonnDatabaseConstants.UKELONN_JDBC_URL, "jdbc:postgresql://lorenzo.hjemme.lan/sonarcollector");
        config.put(UkelonnDatabaseConstants.UKELONN_JDBC_USER, "karaf");
        config.put(UkelonnDatabaseConstants.UKELONN_JDBC_PASSWORD, "karaf");
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        Properties connectionProperties = provider.createDatabaseConnectionProperties(config);
        assertEquals(3, connectionProperties.size());
    }

    @Test
    public void testActivate() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.activate(Collections.emptyMap());
        assertEquals(2, logservice.getLogmessagecount());
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
