package no.priv.bang.ukelonn.bundle.db.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.sql.PooledConnection;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;
import org.junit.After;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.changelog.RanChangeSet;
import liquibase.exception.DatabaseException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.bundle.db.test.mocks.MockLogService;

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
        ResultSet allUsers = database.query("select * from users");
        int allUserCount = 0;
        while (allUsers.next()) { ++allUserCount; }
        assertEquals(4, allUserCount);

        // Test that the administrators_view is present
        ResultSet allAdministrators = database.query("select * from administrators");
        int allAdminstratorsCount = 0;
        while (allAdministrators.next()) { ++allAdminstratorsCount; }
        assertEquals(2, allAdminstratorsCount);

        // Test that the administrators_view is present
        ResultSet allAdministratorsView = database.query("select * from administrators_view");
        int allAdminstratorsViewCount = 0;
        while (allAdministratorsView.next()) { ++allAdminstratorsViewCount; }
        assertEquals(2, allAdminstratorsViewCount);
    }

    @Test
    public void testInsert() throws SQLException {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        provider.setDataSourceFactory(dataSourceFactory); // Simulate injection, this will create the database

        UkelonnDatabase database = provider.get();

        // Verify that the user isn't present
        ResultSet userJjdBeforeInsert = database.query("select * from users where username='jjd'");
        int numberOfUserJjdBeforeInsert = 0;
        while (userJjdBeforeInsert.next()) { ++numberOfUserJjdBeforeInsert; }
        assertEquals(0, numberOfUserJjdBeforeInsert);

        int count = provider.update("insert into users (username,password,salt,email,first_name,last_name) values ('jjd','sU4vKCNpoS6AuWAzZhkNk7BdXSNkW2tmOP53nfotDjE=', '9SFDvohxZkZ9eWHiSEoMDw==','jjd@gmail.com','James','Davies')");
        assertEquals(1, count);

        // Verify that the user is now present
        ResultSet userJjd = database.query("select * from users where username='jjd'");
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

        // A bad select returns a null instead of a resultset
        ResultSet result = database.query("zelect * from uzers");
        assertNull(result);

        // A bad update returns 0 instead of the number of rows inserted
        int updateResult = database.update("inzert into uzers (username) values ('zed')");
        assertEquals(0, updateResult);
    }

    @Test
    public void testNullDataSourceFactory() {
        UkelonnDatabaseProvider provider = new UkelonnDatabaseProvider();
        provider.setLogService(new MockLogService());
        provider.setDataSourceFactory(null); // Test what happens with a null datasource injection

        UkelonnDatabase database = provider.get();
        ResultSet result = database.query("select * from users");
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
        ResultSet result = database.query("select * from users");
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
