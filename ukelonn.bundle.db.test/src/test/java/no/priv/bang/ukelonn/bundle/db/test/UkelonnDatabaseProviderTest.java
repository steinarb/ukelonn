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
package no.priv.bang.ukelonn.bundle.db.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.sql.PooledConnection;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;
import org.junit.Ignore;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.Liquibase;
import liquibase.changelog.RanChangeSet;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.bundle.db.liquibase.UkelonnLiquibase;
import no.priv.bang.ukelonn.bundle.db.test.mocks.MockLogService;

public class UkelonnDatabaseProviderTest {

    @Test
    public void testGetName() {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        UkelonnDatabase database = provider.get();

        String databaseName = database.getName();
        assertEquals("Ukelonn Derby test database", databaseName);
    }

    @Test
    public void testThatActivatorCreatesDatabase() throws SQLException, DatabaseException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        // Test the database by making a query using a view
        UkelonnDatabase database = provider.get();
        PreparedStatement statement = database.prepareStatement("select * from accounts_view where username=?");
        statement.setString(1, "jad");
        ResultSet onAccount = database.query(statement);
        assertNotNull(onAccount);
        assertTrue(onAccount.next());
        int account_id = onAccount.getInt("account_id");
        int user_id = onAccount.getInt("user_id");
        String username = onAccount.getString("username");
        String first_name = onAccount.getString("first_name");
        String last_name = onAccount.getString("last_name");
        assertEquals(4, account_id);
        assertEquals(4, user_id);
        assertEquals("jad", username);
        assertEquals("Jane", first_name);
        assertEquals("Doe", last_name);

        // Verify that the schema changeset as well as all of the test data change sets has been run
        List<RanChangeSet> ranChangeSets = provider.getChangeLogHistory();
        assertEquals(6, ranChangeSets.size());
    }

    @Test
    public void testAdministratorsView() throws SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        UkelonnDatabase database = provider.get();
        // Test that the administrators_view is present
        PreparedStatement statement1 = database.prepareStatement("select * from users");
        ResultSet allUsers = database.query(statement1);
        int allUserCount = 0;
        while (allUsers.next()) { ++allUserCount; }
        assertEquals(5, allUserCount);

        // Test that the administrators_view is present
        PreparedStatement statement2 = database.prepareStatement("select * from administrators");
        ResultSet allAdministrators = database.query(statement2);
        int allAdminstratorsCount = 0;
        while (allAdministrators.next()) { ++allAdminstratorsCount; }
        assertEquals(3, allAdminstratorsCount);

        // Test that the administrators_view is present
        PreparedStatement statement3 = database.prepareStatement("select * from administrators_view");
        ResultSet allAdministratorsView = database.query(statement3);
        int allAdminstratorsViewCount = 0;
        while (allAdministratorsView.next()) { ++allAdminstratorsViewCount; }
        assertEquals(3, allAdminstratorsViewCount);
    }

    @Test
    public void testInsert() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        UkelonnDatabase database = provider.get();

        // Verify that the user isn't present
        PreparedStatement statement = database.prepareStatement("select * from users where username=?");
        statement.setString(1, "jjd");
        ResultSet userJjdBeforeInsert = database.query(statement);
        int numberOfUserJjdBeforeInsert = 0;
        while (userJjdBeforeInsert.next()) { ++numberOfUserJjdBeforeInsert; }
        assertEquals(0, numberOfUserJjdBeforeInsert);

        PreparedStatement updateStatement = database.prepareStatement("insert into users (username,password,salt,email,first_name,last_name) values (?, ?, ?, ?, ?, ?)");
        updateStatement.setString(1, "jjd");
        updateStatement.setString(2, "sU4vKCNpoS6AuWAzZhkNk7BdXSNkW2tmOP53nfotDjE=");
        updateStatement.setString(3, "9SFDvohxZkZ9eWHiSEoMDw==");
        updateStatement.setString(4, "jjd@gmail.com");
        updateStatement.setString(5, "James");
        updateStatement.setString(6, "Davies");
        int count = provider.update(updateStatement);
        assertEquals(1, count);

        // Verify that the user is now present
        PreparedStatement statement2 = database.prepareStatement("select * from users where username=?");
        statement2.setString(1, "jjd");
        ResultSet userJjd = database.query(statement2);
        int numberOfUserJjd = 0;
        while (userJjd.next()) { ++numberOfUserJjd; }
        assertEquals(1, numberOfUserJjd);
    }

    @Test
    public void testBadSql() {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        UkelonnDatabase database = provider.get();

        // A bad select returns a null instead of a prepared statement
        PreparedStatement statement = database.prepareStatement("zelect * from uzers");
        assertNull(statement);
        // A null statement in a query results in a null result (and no other errors)
        ResultSet result = database.query(statement);
        assertNull(result);

        // A bad update returns 0 instead of the number of rows inserted
        PreparedStatement statement2 = database.prepareStatement("inzert into uzers (username) values ('zed')");
        assertNull(statement2);
        int updateResult = database.update(statement2);
        assertEquals(0, updateResult);
    }

    @Test
    public void testNullDataSourceFactory() {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        provider.setDataSourceFactory(null); // Test what happens with a null datasource injection

        UkelonnDatabase database = provider.get();
        PreparedStatement statement = database.prepareStatement("select * from users");
        ResultSet result = database.query(statement);
        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailToCreateDatabaseConnection() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DataSourceFactory dataSourceFactory = mock(DataSourceFactory.class);
        when(dataSourceFactory.createConnectionPoolDataSource(any(Properties.class))).thenThrow(SQLException.class);
        provider.setDataSourceFactory(dataSourceFactory); // Test what happens with failing datasource injection

        UkelonnDatabase database = provider.get();
        PreparedStatement statement = database.prepareStatement("select * from users");
        ResultSet result = database.query(statement);
        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailToInsertMockData() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DataSourceFactory dataSourceFactory = mock(DataSourceFactory.class);
        PooledConnection pooledconnection = mock(PooledConnection.class);
        when(pooledconnection.getConnection()).thenThrow(SQLException.class);
        when(dataSourceFactory.createConnectionPoolDataSource(any(Properties.class))).thenThrow(SQLException.class);

        // Bypass injection to skip schema creation and be able to test
        // database failure on data insertion
        setInternalState(provider, "dataSourceFactory", dataSourceFactory);

        boolean result = provider.insertMockData();
        assertFalse(result);
    }

    /**
     * Not a real unit test, just a way to hash cleartext passwords for
     * the test database and generate salt.
     */
    @Test
    public void testCreateHashedPasswords() {
        String[] usernames = { "on", "kn", "jad", "jod" };
        String[] unhashedPasswords = { "ola12", "KaRi", "1ad", "johnnyBoi" };
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        System.out.println("username, password, salt");
        for (int i=0; i<usernames.length; ++i) {
            // First hash the password
            String username = usernames[i];
            String password = unhashedPasswords[i];
            String salt = randomNumberGenerator.nextBytes().toBase64();
            Object decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
            String hashedPassword = new Sha256Hash(password, decodedSaltUsedWhenHashing, 1024).toBase64();

            // Check the cleartext password against the hashed password
            UsernamePasswordToken usenamePasswordToken = new UsernamePasswordToken(username, password.toCharArray());
            SimpleAuthenticationInfo saltedAuthenticationInfo = createAuthenticationInfo(usernames[i], hashedPassword, salt);
            CredentialsMatcher credentialsMatcher = createSha256HashMatcher(1024);
            assertTrue(credentialsMatcher.doCredentialsMatch(usenamePasswordToken, saltedAuthenticationInfo));

            // Print out the username, hashed password, and salt
            System.out.println(String.format("'%s', '%s', '%s'", username, hashedPassword, salt));
        }
    }

    /**
     * Not an actual unit test.
     *
     * This test is a convenient way to populate a derby network server
     * running on localhost, with the ukelonn schema and test data, using
     * liquibase.
     *
     * To use this test:
     *  1. Start a derby network server
     *  2. Remove the @Ignore annotation of this test
     *  3. Run the test
     *
     * After this test has been run the derby network server will have
     * a database named "ukelonn" containing the ukelonn schema
     * and the test data used by unit tests.
     *
     * @throws SQLException
     * @throws LiquibaseException
     * @throws ClassNotFoundException
     */
    @Ignore
    @Test
    public void addUkelonnSchemaAndDataToDerbyServer() throws SQLException, LiquibaseException {
        boolean createUkelonnDatabase = true;
        ClientConnectionPoolDataSource dataSource = new ClientConnectionPoolDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("ukelonn");
        dataSource.setPortNumber(1527);
        if (createUkelonnDatabase) {
            dataSource.setCreateDatabase("create");
        }

        PooledConnection connect = dataSource.getPooledConnection();
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        liquibase.createInitialSchema(connect);
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase2 = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase2.update("");
        liquibase.updateSchema(connect);
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

    private SimpleAuthenticationInfo createAuthenticationInfo(String principal, String hashedPassword, String salt) {
        Object decodedPassword = Sha256Hash.fromBase64String(hashedPassword);
        ByteSource decodedSalt = Util.bytes(Base64.getDecoder().decode(salt));
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal, decodedPassword, decodedSalt, "ukelonn");
        return authenticationInfo;
    }

}
