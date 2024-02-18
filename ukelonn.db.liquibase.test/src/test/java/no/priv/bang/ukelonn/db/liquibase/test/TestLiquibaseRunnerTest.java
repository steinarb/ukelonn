/*
 * Copyright 2016-2024 Steinar Bang
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.derby.jdbc.ClientConnectionPoolDataSource;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource.Util;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;
import static no.priv.bang.ukelonn.db.liquibase.test.TestLiquibaseRunner.*;

class TestLiquibaseRunnerTest {

    @Test
    void testPrepareDatabase() throws SQLException, DatabaseException {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var derbyMemoryCredentials = createDerbyMemoryCredentials("ukelonn_pure", "no");
        var datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        var runner = new TestLiquibaseRunner();
        assertThat(runner.getChangeLogHistory(datasource)).isEmpty();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(var onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    var account_id = onAccount.getInt("account_id");
                    var username = onAccount.getString("username");
                    var balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (var statement = connection.createStatement()) {
                try (var transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    var transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Støvsuging 1. etasje", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        assertThat(runner.getChangeLogHistory(datasource)).as("changelog history").hasSize(49);
    }

    @Test
    void testPrepareDatabaseWithConfiguredLanguage() throws SQLException, DatabaseException {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var derbyMemoryCredentials = createDerbyMemoryCredentials("ukelonn", "en");
        var datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.singletonMap("databaselanguage", "en_GB")); // Create the database
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(var onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    var account_id = onAccount.getInt("account_id");
                    var username = onAccount.getString("username");
                    var balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (var statement = connection.createStatement()) {
                try (var transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    var transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Vacuuming 1st floor", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        var ranChangeSets = runner.getChangeLogHistory(datasource);
        assertEquals(49, ranChangeSets.size());
    }

    @Test
    void testPrepareDatabaseWithConfiguredLanguageNotFound() throws SQLException, DatabaseException {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var derbyMemoryCredentials = createDerbyMemoryCredentials("ukelonn", "uk");
        var datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.singletonMap("databaselanguage", "en_UK")); // Create the database
        runner.prepare(datasource); // Create the database

        // Test the database by making a query using a view
        try(var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement("select * from accounts_view where username=?")) {
                statement.setString(1, "jad");
                try(var onAccount = statement.executeQuery()) {
                    assertNotNull(onAccount);
                    assertTrue(onAccount.next());
                    var account_id = onAccount.getInt("account_id");
                    var username = onAccount.getString("username");
                    var balance = onAccount.getFloat("balance");
                    assertEquals(4, account_id);
                    assertEquals("jad", username);
                    assertThat(balance).isPositive();
                }
            }
            // Verify that the texts in the database are in the default language  (i.e. Norwegian)
            try (var statement = connection.createStatement()) {
                try (var transactionTypes = statement.executeQuery("select * from transaction_types where transaction_type_id=1")) {
                    assertNotNull(transactionTypes);
                    assertTrue(transactionTypes.next());
                    var transactionTypeName = transactionTypes.getString("transaction_type_name");
                    assertEquals("Støvsuging 1. etasje", transactionTypeName);
                }
            }
        }

        // Verify that the schema changeset as well as all of the test data change sets has been run
        var ranChangeSets = runner.getChangeLogHistory(datasource);
        assertEquals(49, ranChangeSets.size());
    }

    @Test
    void testFailWhenPrepareDatabase() throws SQLException, DatabaseException {
        var datasource = mock(DataSource.class);
        var runner = new TestLiquibaseRunner();
        var logservice = new MockLogService();
        runner.setLogService(logservice);
        assertThat(logservice.getLogmessages()).isEmpty();
        runner.prepare(datasource); // Create the database
        assertThat(logservice.getLogmessages()).hasSize(1);
    }

    @Test
    void testInsert() throws SQLException {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var derbyMemoryCredentials = createDerbyMemoryCredentials("ukelonn", "no");
        var datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Verify that the user isn't present
        try(var connection = datasource.getConnection()) {
            var statement = connection.prepareStatement("select * from users where username=?");
            statement.setString(1, "jjd");
            var userJjdBeforeInsert = statement.executeQuery();
            var numberOfUserJjdBeforeInsert = 0;
            while (userJjdBeforeInsert.next()) { ++numberOfUserJjdBeforeInsert; }
            assertEquals(0, numberOfUserJjdBeforeInsert);

            var updateStatement = connection.prepareStatement("insert into users (username,password,password_salt,email,firstname,lastname) values (?, ?, ?, ?, ?, ?)");
            updateStatement.setString(1, "jjd");
            updateStatement.setString(2, "sU4vKCNpoS6AuWAzZhkNk7BdXSNkW2tmOP53nfotDjE=");
            updateStatement.setString(3, "9SFDvohxZkZ9eWHiSEoMDw==");
            updateStatement.setString(4, "jjd@gmail.com");
            updateStatement.setString(5, "James");
            updateStatement.setString(6, "Davies");
            int count = updateStatement.executeUpdate();
            assertEquals(1, count);

            // Verify that the user is now present
            var statement2 = connection.prepareStatement("select * from users where username=?");
            statement2.setString(1, "jjd");
            ResultSet userJjd = statement2.executeQuery();
            int numberOfUserJjd = 0;
            while (userJjd.next()) { ++numberOfUserJjd; }
            assertEquals(1, numberOfUserJjd);
        }
    }

    @Test
    void testFailToInsertMockData() throws SQLException {
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        var result = runner.insertMockData(datasource);
        assertFalse(result);
    }

    @Test
    void testRollbackMockData() throws Exception {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var derbyMemoryCredentials = createDerbyMemoryCredentials("ukelonn_rollback", "no");
        var datasource = dataSourceFactory.createDataSource(derbyMemoryCredentials);
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource); // Create the database

        // Check that database has the mock data in place
        var expectedStatusBeforeRollback = new SoftAssertions();
        var numberOfTransactionTypesBeforeRollback = findTheNumberOfRowsInTable(datasource, "transaction_types");
        expectedStatusBeforeRollback.assertThat(numberOfTransactionTypesBeforeRollback).isPositive();
        var numberOfUsersBeforeRollback = findTheNumberOfRowsInTable(datasource, "users");
        expectedStatusBeforeRollback.assertThat(numberOfUsersBeforeRollback).isPositive();
        var numberOfAccountsBeforeRollback = findTheNumberOfRowsInTable(datasource, "accounts");
        expectedStatusBeforeRollback.assertThat(numberOfAccountsBeforeRollback).isPositive();
        var numberOfTransactionsBeforeRollback = findTheNumberOfRowsInTable(datasource, "transactions");
        expectedStatusBeforeRollback.assertThat(numberOfTransactionsBeforeRollback).isPositive();
        expectedStatusBeforeRollback.assertAll();

        var sizeOfDbchangelogBeforeRollback = findTheNumberOfRowsInTable(datasource, "databasechangelog");

        // Do the rollback
        var rollbackSuccessful = runner.rollbackMockData(datasource);
        assertTrue(rollbackSuccessful);

        var sizeOfDbchangelogAfterRollback = findTheNumberOfRowsInTable(datasource, "databasechangelog");
        assertThat(sizeOfDbchangelogAfterRollback).isLessThan(sizeOfDbchangelogBeforeRollback);

        // Verify that the database tables are empty
        var expectedStatusAfterRollback = new SoftAssertions();
        var numberOfTransactionTypesAfterRollback = findTheNumberOfRowsInTable(datasource, "transaction_types");
        expectedStatusAfterRollback.assertThat(numberOfTransactionTypesAfterRollback).isEqualTo(0);
        var numberOfUsersAfterRollback = findTheNumberOfRowsInTable(datasource, "users");
        expectedStatusAfterRollback.assertThat(numberOfUsersAfterRollback).isEqualTo(0);
        var numberOfAccountsAfterRollback = findTheNumberOfRowsInTable(datasource, "accounts");
        expectedStatusAfterRollback.assertThat(numberOfAccountsAfterRollback).isEqualTo(0);
        var numberOfTransactionsAfterRollback = findTheNumberOfRowsInTable(datasource, "transactions");
        expectedStatusAfterRollback.assertThat(numberOfTransactionsAfterRollback).isEqualTo(0);
        expectedStatusAfterRollback.assertAll();
    }

    @Test
    void testFailToRollbackMockData() throws Exception {
        var runner = new TestLiquibaseRunner();
        runner.setLogService(new MockLogService());
        runner.activate(Collections.emptyMap());
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        var rollbackSuccessful = runner.rollbackMockData(datasource);
        assertFalse(rollbackSuccessful);
    }

    @Test
    void testGetChangelogHistoryWhenFailing() throws Exception {
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var logservice = new MockLogService();

        var runner = new TestLiquibaseRunner();
        runner.setLogService(logservice);
        assertThat(logservice.getLogmessages()).isEmpty();
        assertThat(runner.getChangeLogHistory(datasource)).isEmpty();
        assertThat(logservice.getLogmessages()).hasSize(1);
    }

    @Test
    void testDummyDataResourceNameNoLanguageSet() {
        var runner = new TestLiquibaseRunner();
        var logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());

        assertEquals(DEFAULT_DUMMY_DATA_CHANGELOG, runner.dummyDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    void testDummyDataResourceNameWithLanguageSet() {
        var runner = new TestLiquibaseRunner();
        var logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.singletonMap("databaselanguage", "en_GB"));

        assertEquals(DEFAULT_DUMMY_DATA_CHANGELOG.replace(".xml", "_en_GB.xml"), runner.dummyDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    void testDummyDataResourceNameWithNotFoundLanguageSet() {
        var runner = new TestLiquibaseRunner();
        var logservice = new MockLogService();
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
    void testCreateHashedPasswords() {
        String[] usernames = { "on", "kn", "jad", "jod" };
        String[] unhashedPasswords = { "ola12", "KaRi", "1ad", "johnnyBoi" };
        var randomNumberGenerator = new SecureRandomNumberGenerator();
        System.out.println("username, password, salt");
        for (int i=0; i<usernames.length; ++i) {
            // First hash the password
            var username = usernames[i];
            var password = unhashedPasswords[i];
            var salt = randomNumberGenerator.nextBytes().toBase64();
            var decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
            var hashedPassword = new Sha256Hash(password, decodedSaltUsedWhenHashing, 1024).toBase64();

            // Check the cleartext password against the hashed password
            var usenamePasswordToken = new UsernamePasswordToken(username, password.toCharArray());
            var saltedAuthenticationInfo = createAuthenticationInfo(usernames[i], hashedPassword, salt);
            var credentialsMatcher = createSha256HashMatcher(1024);
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
    @Disabled("Not an actual unit test. This test is a convenient way to populate a derby network server running on localhost, with the ukelonn schema and test data, using liquibase.")
    @Test
    void addUkelonnSchemaAndDataToDerbyServer() throws SQLException, LiquibaseException { // NOSONAR This isn't an actual test, see the comments
        var createUkelonnDatabase = true;
        var dataSource = new ClientConnectionPoolDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("ukelonn");
        dataSource.setPortNumber(1527);
        if (createUkelonnDatabase) {
            dataSource.setCreateDatabase("create");
        }

        var liquibase = new UkelonnLiquibase();
        liquibase.createInitialSchema(dataSource);

        try(var connect = dataSource.getConnection()) {
            try (var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connect))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor(getClass().getClassLoader()));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                            .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "sql/data/db-changelog.xml")
                            .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                            .execute());
            } catch (LiquibaseException e) {
                throw e;
            } catch (Exception e) {
                throw new UkelonnException("Failed to close resource when inserting data");
            }
        }

        liquibase.updateSchema(dataSource);
    }

    private Properties createDerbyMemoryCredentials(String dbname, String language) {
        var properties = new Properties();
        properties.put(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + language + ";create=true");
        return properties;
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        var credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

    private SimpleAuthenticationInfo createAuthenticationInfo(String principal, String hashedPassword, String salt) {
        var decodedPassword = Sha256Hash.fromBase64String(hashedPassword);
        var decodedSalt = Util.bytes(Base64.getDecoder().decode(salt));
        var authenticationInfo = new SimpleAuthenticationInfo(principal, decodedPassword, decodedSalt, "ukelonn");
        return authenticationInfo;
    }

    private int findTheNumberOfRowsInTable(DataSource datasource, String tableName) throws Exception {
        var selectAllRowsStatement = String.format("select * from %s", tableName);
        try(var connection = datasource.getConnection()) {
            try(var selectAllRowsInTable = connection.prepareStatement(selectAllRowsStatement)) {
                var userResults = selectAllRowsInTable.executeQuery();
                var numberOfRows = countResults(userResults);
                return numberOfRows;
            }
        }
    }

    private int countResults(ResultSet results) throws Exception {
        var numberOfResultsInResultSet = 0;
        while(results.next()) {
            ++numberOfResultsInResultSet;
        }

        return numberOfResultsInResultSet;
    }

}
