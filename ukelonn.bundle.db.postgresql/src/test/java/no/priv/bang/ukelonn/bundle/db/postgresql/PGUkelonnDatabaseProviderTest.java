/*
 * Copyright 2016-2018 Steinar Bang
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.junit.Ignore;
import org.junit.Test;
import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.osgi.PGDataSourceFactory;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.LiquibaseException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnDatabaseConstants;
import no.priv.bang.ukelonn.bundle.db.liquibase.UkelonnLiquibase;
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
        assertThat(allUserCount).isGreaterThan(0);

        // Test that the database administrators table has rows
        PreparedStatement statement2 = database.prepareStatement("select * from administrators");
        ResultSet allAdministrators = database.query(statement2);
        int allAdminstratorsCount = 0;
        while (allAdministrators.next()) { ++allAdminstratorsCount; }
        assertThat(allAdminstratorsCount).isGreaterThan(0);

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
    public void testCreateConnection() throws SQLException {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        ConnectionPoolDataSource datasource = mock(ConnectionPoolDataSource.class);
        DataSourceFactory datasourcefactory = mock(DataSourceFactory.class);
        when(datasourcefactory.createConnectionPoolDataSource(any())).thenReturn(datasource);
        provider.setDataSourceFactory(datasourcefactory);
        provider.createConnection(Collections.emptyMap());
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testActivate() throws SQLException, LiquibaseException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock the UkelonnLiquibase and the Liquibase (to avoid having to mock a lot of JDBC)
        UkelonnLiquibase ukelonnLiquibase = mock(UkelonnLiquibase.class);
        UkelonnLiquibaseFactory ukelonnLiquibaseFactory = mock(UkelonnLiquibaseFactory.class);
        when(ukelonnLiquibaseFactory.create()).thenReturn(ukelonnLiquibase);
        provider.setUkelonnLiquibaseFactory(ukelonnLiquibaseFactory);
        Liquibase liquibase = mock(Liquibase.class);
        LiquibaseFactory liquibaseFactory = mock(LiquibaseFactory.class);
        when(liquibaseFactory.create(anyString(), any(), any())).thenReturn(liquibase);
        provider.setLiquibaseFactory(liquibaseFactory);

        // Mock injected OSGi services
        MockLogService logservice = new MockLogService();
        DataSourceFactory datasourcefactory = mock(DataSourceFactory.class);
        ConnectionPoolDataSource datasource = mock(ConnectionPoolDataSource.class);
        PooledConnection connection = mock(PooledConnection.class);
        when(datasource.getPooledConnection()).thenReturn(connection);
        when(datasourcefactory.createConnectionPoolDataSource(any())).thenReturn(datasource);
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourcefactory);

        // Execute the method under test
        provider.activate(Collections.emptyMap());

        // Verify that no errors have been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testActivateFailedConnection() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.activate(Collections.emptyMap());
        assertEquals(2, logservice.getLogmessagecount());
    }

    @Test
    public void testInsertInitialDataInDatabaseFailToCreateLiquibase() {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        boolean successfullyinserteddata = provider.insertInitialDataInDatabase();
        assertFalse(successfullyinserteddata);
    }

    @Test
    public void testPrepareStatement() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi services
        MockLogService logservice = new MockLogService();
        DataSourceFactory datasourcefactory = mockDataSourceFactory();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourcefactory);
        provider.createConnection(Collections.emptyMap());

        // Run the code under test
        PreparedStatement statement = provider.prepareStatement("select * from table");
        assertNull(statement);

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testPrepareStatementFailed() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Run the code under test
        PreparedStatement statement = provider.prepareStatement("select * from table");
        assertNull(statement);

        // Verify that an error has been logged
        assertEquals(1, logservice.getLogmessagecount());
    }

    @Test
    public void testQuery() throws Exception {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Mock the argument
        PreparedStatement statement = mock(PreparedStatement.class);

        // Run the code under test
        ResultSet resultset = provider.query(statement);
        assertNull(resultset);

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=SQLException.class)
    public void testQueryFailOnQuery() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Mock the argument
        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenThrow(SQLException.class);

        // Run the code under test
        ResultSet resultset = provider.query(statement);
        assertNull(resultset);
    }

    @Test
    public void testQueryFailOnClose() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Mock the argument
        PreparedStatement statement = mock(PreparedStatement.class);
        doThrow(SQLException.class).when(statement).closeOnCompletion();

        // Run the code under test
        ResultSet resultset = provider.query(statement);
        assertNull(resultset);

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testQueryOnNullStatement() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Run the code under test
        ResultSet resultset = provider.query(null);
        assertNull(resultset);

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testUpdate() {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Mock the argument
        PreparedStatement statement = mock(PreparedStatement.class);

        // Run the code under test
        int result = provider.update(statement);
        assertEquals(0, result);

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateFailOnUpdate() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Mock the argument
        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenThrow(SQLException.class);

        // Run the code under test
        int result = provider.update(statement);
        assertEquals(0, result);

        // Verify that 1 error has been logged
        assertEquals(1, logservice.getLogmessagecount());
    }

    @Test
    public void testUpdateOnNullStatement() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Run the code under test
        int result = provider.update(null);
        assertEquals(0, result);

        // Verify that an error has been logged
        assertEquals(1, logservice.getLogmessagecount());
    }

    @Test
    public void testForceLiquibaseChangeloglock() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock the UkelonnLiquibase to avoid having to mock a lot of JDBC
        UkelonnLiquibase ukelonnLiquibase = mock(UkelonnLiquibase.class);
        UkelonnLiquibaseFactory ukelonnLiquibaseFactory = mock(UkelonnLiquibaseFactory.class);
        when(ukelonnLiquibaseFactory.create()).thenReturn(ukelonnLiquibase);
        provider.setUkelonnLiquibaseFactory(ukelonnLiquibaseFactory);

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        DataSourceFactory datasourcefactory = mockDataSourceFactory();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourcefactory);
        provider.createConnection(Collections.emptyMap());

        // Run the code under test
        provider.forceReleaseLocks();

        // Verify that no error has been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @Test
    public void testForceLiquibaseChangeloglockFailOnForce() throws SQLException {
        // Create the object under test
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();

        // Mock injected OSGi service
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        // Run the code under test
        provider.forceReleaseLocks();

        // Verify that 1 error has been logged
        assertEquals(1, logservice.getLogmessagecount());
    }

    @Test
    public void testCreateLiquibase() throws LiquibaseException, SQLException {
        PGUkelonnDatabaseProvider provider = new PGUkelonnDatabaseProvider();
        DatabaseConnection connection = mock(DatabaseConnection.class);
        when(connection.getDatabaseProductName()).thenReturn("PostgreSQL");
        Liquibase liquibase = provider.createLiquibase(null, null, connection);
        assertNotNull(liquibase);
    }

    DataSourceFactory mockDataSourceFactory() throws SQLException {
        DataSourceFactory datasourcefactory = mock(DataSourceFactory.class);
        ConnectionPoolDataSource datasource = mock(ConnectionPoolDataSource.class);
        PooledConnection pooledConnection = mock(PooledConnection.class);
        Connection connection = mock(Connection.class);
        when(pooledConnection.getConnection()).thenReturn(connection);
        when(datasource.getPooledConnection()).thenReturn(pooledConnection);
        when(datasourcefactory.createConnectionPoolDataSource(any())).thenReturn(datasource);
        return datasourcefactory;
    }

    CallableStatement mockPostgresqlSchemaQuery() throws SQLException {
        CallableStatement callablestatement = mock(CallableStatement.class);
        ResultSet singleresult = mock(ResultSet.class);
        when(singleresult.next()).thenReturn(true).thenReturn(false);
        ResultSetMetaData singleresultMetadata = mock(ResultSetMetaData.class);
        when(singleresultMetadata.getColumnCount()).thenReturn(1);
        when(singleresult.getString(1)).thenReturn("public");
        when(singleresult.getMetaData()).thenReturn(singleresultMetadata);
        when(callablestatement.executeQuery()).thenReturn(singleresult);
        return callablestatement;
    }

    CallableStatement mockQueryForChangelogLock() throws SQLException {
        CallableStatement callablestatement = mock(CallableStatement.class);
        ResultSetMetaData singleresultMetadata = mock(ResultSetMetaData.class);
        when(singleresultMetadata.getColumnCount()).thenReturn(1);
        ResultSet singleresult = mock(ResultSet.class);
        when(singleresult.next()).thenReturn(true).thenReturn(false);
        when(singleresult.getInt(1)).thenReturn(0);
        when(singleresult.getMetaData()).thenReturn(singleresultMetadata);
        when(callablestatement.executeQuery(anyString())).thenReturn(singleresult);
        return callablestatement;
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
