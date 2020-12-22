/*
 * Copyright 2016-2020 Steinar Bang
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
package no.priv.bang.ukelonn.db.liquibase.test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
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
import org.assertj.core.api.SoftAssertions;
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
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;
import static no.priv.bang.ukelonn.db.liquibase.test.TestLiquibaseRunner.*;

public class TestLiquibaseRunnerTest {

    @Test
    public void testPrepareDatabase() throws SQLException, DatabaseException {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties derbyMemoryCredentials = createDerbyMemoryCredentials("no");
        DataSource datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(ResultSet onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    int account_id = onAccount.getInt("account_id");
                    String username = onAccount.getString("username");
                    float balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (Statement statement = connection.createStatement()) {
                try (ResultSet transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    String transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Støvsuging 1. etasje", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        List<RanChangeSet> ranChangeSets = runner.getChangeLogHistory(datasource);
        assertEquals(49, ranChangeSets.size());
    }

    @Test
    public void testPrepareDatabaseWithConfiguredLanguage() throws SQLException, DatabaseException {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties derbyMemoryCredentials = createDerbyMemoryCredentials("en");
        DataSource datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.singletonMap("databaselanguage", "en_GB")); // Create the database
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(ResultSet onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    int account_id = onAccount.getInt("account_id");
                    String username = onAccount.getString("username");
                    float balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (Statement statement = connection.createStatement()) {
                try (ResultSet transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    String transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Vacuuming 1st floor", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        List<RanChangeSet> ranChangeSets = runner.getChangeLogHistory(datasource);
        assertEquals(49, ranChangeSets.size());
    }

    @Test
    public void testPrepareDatabaseWithConfiguredLanguageNotFound() throws SQLException, DatabaseException {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties derbyMemoryCredentials = createDerbyMemoryCredentials("uk");
        DataSource datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.singletonMap("databaselanguage", "en_UK")); // Create the database
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(ResultSet onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    int account_id = onAccount.getInt("account_id");
                    String username = onAccount.getString("username");
                    float balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (Statement statement = connection.createStatement()) {
                try (ResultSet transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    String transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Støvsuging 1. etasje", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        List<RanChangeSet> ranChangeSets = runner.getChangeLogHistory(datasource);
        assertEquals(49, ranChangeSets.size());
    }

    @Test
    public void testInsert() throws SQLException {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties derbyMemoryCredentials = createDerbyMemoryCredentials("no");
        DataSource datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Verify that the user isn't present
        try(Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select * from users where username=?");
            statement.setString(1, "jjd");
            ResultSet userJjdBeforeInsert = statement.executeQuery();
            int numberOfUserJjdBeforeInsert = 0;
            while (userJjdBeforeInsert.next()) { ++numberOfUserJjdBeforeInsert; }
            assertEquals(0, numberOfUserJjdBeforeInsert);

            PreparedStatement updateStatement = connection.prepareStatement("insert into users (username,password,password_salt,email,firstname,lastname) values (?, ?, ?, ?, ?, ?)");
            updateStatement.setString(1, "jjd");
            updateStatement.setString(2, "sU4vKCNpoS6AuWAzZhkNk7BdXSNkW2tmOP53nfotDjE=");
            updateStatement.setString(3, "9SFDvohxZkZ9eWHiSEoMDw==");
            updateStatement.setString(4, "jjd@gmail.com");
            updateStatement.setString(5, "James");
            updateStatement.setString(6, "Davies");
            int count = updateStatement.executeUpdate();
            assertEquals(1, count);

            // Verify that the user is now present
            PreparedStatement statement2 = connection.prepareStatement("select * from users where username=?");
            statement2.setString(1, "jjd");
            ResultSet userJjd = statement2.executeQuery();
            int numberOfUserJjd = 0;
            while (userJjd.next()) { ++numberOfUserJjd; }
            assertEquals(1, numberOfUserJjd);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailToInsertMockData() throws SQLException {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        boolean result = runner.insertMockData(datasource);
        assertFalse(result);
    }

    @Test
    public void testRollbackMockData() throws Exception {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties derbyMemoryCredentials = createDerbyMemoryCredentials("no");
        DataSource datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Check that database has the mock data in place
        SoftAssertions expectedStatusBeforeRollback = new SoftAssertions();
        int numberOfTransactionTypesBeforeRollback = findTheNumberOfRowsInTable(datasource, "transaction_types");
        expectedStatusBeforeRollback.assertThat(numberOfTransactionTypesBeforeRollback).isPositive();
        int numberOfUsersBeforeRollback = findTheNumberOfRowsInTable(datasource, "users");
        expectedStatusBeforeRollback.assertThat(numberOfUsersBeforeRollback).isPositive();
        int numberOfAccountsBeforeRollback = findTheNumberOfRowsInTable(datasource, "accounts");
        expectedStatusBeforeRollback.assertThat(numberOfAccountsBeforeRollback).isPositive();
        int numberOfTransactionsBeforeRollback = findTheNumberOfRowsInTable(datasource, "transactions");
        expectedStatusBeforeRollback.assertThat(numberOfTransactionsBeforeRollback).isPositive();
        expectedStatusBeforeRollback.assertAll();

        int sizeOfDbchangelogBeforeRollback = findTheNumberOfRowsInTable(datasource, "databasechangelog");

        // Do the rollback
        boolean rollbackSuccessful = runner.rollbackMockData(datasource);
        assertTrue(rollbackSuccessful);

        int sizeOfDbchangelogAfterRollback = findTheNumberOfRowsInTable(datasource, "databasechangelog");
        assertThat(sizeOfDbchangelogAfterRollback).isLessThan(sizeOfDbchangelogBeforeRollback);

        // Verify that the database tables are empty
        SoftAssertions expectedStatusAfterRollback = new SoftAssertions();
        int numberOfTransactionTypesAfterRollback = findTheNumberOfRowsInTable(datasource, "transaction_types");
        expectedStatusAfterRollback.assertThat(numberOfTransactionTypesAfterRollback).isEqualTo(0);
        int numberOfUsersAfterRollback = findTheNumberOfRowsInTable(datasource, "users");
        expectedStatusAfterRollback.assertThat(numberOfUsersAfterRollback).isEqualTo(0);
        int numberOfAccountsAfterRollback = findTheNumberOfRowsInTable(datasource, "accounts");
        expectedStatusAfterRollback.assertThat(numberOfAccountsAfterRollback).isEqualTo(0);
        int numberOfTransactionsAfterRollback = findTheNumberOfRowsInTable(datasource, "transactions");
        expectedStatusAfterRollback.assertThat(numberOfTransactionsAfterRollback).isEqualTo(0);
        expectedStatusAfterRollback.assertAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailToRollbackMockData() throws Exception {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        boolean rollbackSuccessful = runner.rollbackMockData(datasource);
        assertFalse(rollbackSuccessful);
    }

    @Test
    public void testDummyDataResourceNameNoLanguageSet() {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());

        assertEquals(DEFAULT_DUMMY_DATA_CHANGELOG, runner.dummyDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    public void testDummyDataResourceNameWithLanguageSet() {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.singletonMap("databaselanguage", "en_GB"));

        assertEquals(DEFAULT_DUMMY_DATA_CHANGELOG.replace(".xml", "_en_GB.xml"), runner.dummyDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    public void testDummyDataResourceNameWithNotFoundLanguageSet() {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.singletonMap("databaselanguage", "en_UK"));

        assertEquals(DEFAULT_DUMMY_DATA_CHANGELOG, runner.dummyDataResourceName());
        assertThat(logservice.getLogmessages()).isNotEmpty();
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
     */
    @Ignore("Not an actual unit test. This test is a convenient way to populate a derby network server running on localhost, with the ukelonn schema and test data, using liquibase.")
    @Test
    public void addUkelonnSchemaAndDataToDerbyServer() throws SQLException, LiquibaseException { // NOSONAR This isn't an actual test, see the comments
        boolean createUkelonnDatabase = true;
        ClientConnectionPoolDataSource dataSource = new ClientConnectionPoolDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("ukelonn");
        dataSource.setPortNumber(1527);
        if (createUkelonnDatabase) {
            dataSource.setCreateDatabase("create");
        }

        Connection connect = dataSource.getConnection();
        UkelonnLiquibase liquibase = new UkelonnLiquibase();
        liquibase.createInitialSchema(connect);
        DatabaseConnection databaseConnection = new JdbcConnection(connect);
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase2 = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase2.update("");
        liquibase.updateSchema(connect);
    }

    private Properties createDerbyMemoryCredentials(String language) {
        Properties properties = new Properties();
        properties.put(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn" + language + ";create=true");
        return properties;
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

    private int findTheNumberOfRowsInTable(DataSource datasource, String tableName) throws Exception {
        String selectAllRowsStatement = String.format("select * from %s", tableName);
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement selectAllRowsInTable = connection.prepareStatement(selectAllRowsStatement)) {
                ResultSet userResults = selectAllRowsInTable.executeQuery();
                int numberOfUsers = countResults(userResults);
                return numberOfUsers;
            }
        }
    }

    private int countResults(ResultSet results) throws Exception {
        int numberOfResultsInResultSet = 0;
        while(results.next()) {
            ++numberOfResultsInResultSet;
        }

        return numberOfResultsInResultSet;
    }

}
