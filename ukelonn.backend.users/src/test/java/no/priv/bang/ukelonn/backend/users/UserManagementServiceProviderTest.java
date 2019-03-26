package no.priv.bang.ukelonn.backend.users;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.db.derbytest.UkelonnDatabaseProvider;

public class UserManagementServiceProviderTest  {

    private static UkelonnDatabaseProvider database;

    @BeforeClass
    static public void beforeAllTests() {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        database = new UkelonnDatabaseProvider();
        MockLogService logservice = new MockLogService();
        database.setLogService(logservice);
        database.setDataSourceFactory(derbyDataSourceFactory);
        database.activate();
    }

    @Test
    public void testGetUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        String username = "jod";
        User user = provider.getUser(username);
        assertEquals(username, user.getUsername());
    }

    @Test(expected=UkelonnException.class)
    public void testGetUserWhenUserIsNotFound() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        UkelonnDatabase mockdatabase = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdatabase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockdatabase);
        provider.activate();

        String username = "jod";
        User user = provider.getUser(username);
        assertEquals(username, user.getUsername());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testGetUserWhenSQLExceptionIsThrown() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        UkelonnDatabase mockdatabase = mock(UkelonnDatabase.class);
        when(mockdatabase.getConnection()).thenThrow(SQLException.class);
        provider.setDatabase(mockdatabase);
        provider.activate();

        String username = "jod";
        User user = provider.getUser(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    public void testGetUsers() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        assertThat(users.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testGetUsersWhenSqlExceptionIsThrown() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        // Should never get here
        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testModifyUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> originalUsers = provider.getUsers();
        int userCountBeforeModify = originalUsers.size();
        User originalUser = originalUsers.get(0);
        int userid = originalUser.getUserid();
        String username = originalUser.getUsername();
        String newEmail = "newuser@hotmail.com";
        String firstname = originalUser.getFirstname();
        String lastname = originalUser.getLastname();
        User updatedUser = new User(userid, username, newEmail, firstname, lastname);
        List<User> updatedUsers = provider.modifyUser(updatedUser);
        assertEquals(userCountBeforeModify, updatedUsers.size());
        User user = updatedUsers.get(0);
        assertEquals(newEmail, user.getEmail());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testModifyUserFailure() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        provider.setDatabase(database);
        provider.activate();

        // Create a user bean
        User user = new User();

        // Save the modification
        provider.modifyUser(user);

        // Verify that the update fails
        fail("Should never get here!");
    }

    @Test
    public void testUpdatePasswords() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        // Get first user and modify all properties except id
        List<User> users = provider.getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Save the modification
        List<User> updatedUsers = provider.updatePassword(passwords);

        // Verify that the size of the users list hasn't changed
        // (passwords can't be downloaded so we can't check the change)
        assertEquals(users.size(), updatedUsers.size());
    }

    @Test(expected=UkelonnBadRequestException.class)
    public void testUpdatePasswordsWithEmptyUsername() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        // Create a user with an empty username
        User user = new User(0, "", null, null, null);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Save the modification and cause an exception
        provider.updatePassword(passwords);

        fail("Should never get here");
    }

    @Test(expected=UkelonnBadRequestException.class)
    public void testUpdatePasswordWhenPasswordsDontMatch() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        // Get first user to get a user with valid username
        List<User> users = provider.getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid but non-identical passwords
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "secret", true);

        // Change the passwords and cause the exception
        provider.updatePassword(passwords);

        fail("Should never get here");
    }

    @Test
    public void testPasswordsEqualAndNotEmpty() {
        UserAndPasswords samePasswords = new UserAndPasswords(null, "secret", "secret", false);
        assertTrue(UserManagementServiceProvider.passwordsEqualsAndNotEmpty(samePasswords));
        UserAndPasswords firstPasswordIsNull = new UserAndPasswords(null, null, "secret", true);
        assertFalse(UserManagementServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordIsNull));
        UserAndPasswords firstPasswordIsEmpty = new UserAndPasswords(null, "", "secret", true);
        assertFalse(UserManagementServiceProvider.passwordsEqualsAndNotEmpty(firstPasswordIsEmpty));
    }

    @Test
    public void testHasUserWithNonEmptyUsername() {
        User userWithUsername = new User(1, "foo", null, null, null);
        UserAndPasswords passwords = new UserAndPasswords(userWithUsername, null, null, false);
        assertTrue(UserManagementServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithEmptyUsername = new User(1, "", null, null, null);
        passwords = new UserAndPasswords(userWithEmptyUsername, null, null, false);
        assertFalse(UserManagementServiceProvider.hasUserWithNonEmptyUsername(passwords));
        User userWithNullUsername = new User(1, null, null, null, null);
        passwords = new UserAndPasswords(userWithNullUsername, null, null, false);
        assertFalse(UserManagementServiceProvider.hasUserWithNonEmptyUsername(passwords));
        passwords = new UserAndPasswords(null, null, null, false);
        assertFalse(UserManagementServiceProvider.hasUserWithNonEmptyUsername(passwords));
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testUpdatePasswordDatabaseException() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        provider.setDatabase(database);
        provider.activate();

        // Create a user object with a valid username
        User user = new User(0, "validusername", null, null, null);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Changing the password should fail
        provider.updatePassword(passwords);

        fail("Should never get here");
    }

    @Test
    public void testAddUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        // Save the number of users before adding a user
        int originalUserCount = provider.getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Create a user
        List<User> updatedUsers = provider.addUser(passwords);

        // Verify that the first user has the modified values
        assertThat(updatedUsers.size()).isGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @Test(expected=UkelonnBadRequestException.class)
    public void testAddUserPasswordsNotIdentical() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDatabase(database);
        provider.activate();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "secret", true);

        // Creating user should fail
        provider.addUser(passwords);

        fail("Should never get here");
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UkelonnException.class)
    public void testAddUserWithSqlError() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        provider.setDatabase(database);
        provider.activate();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        UserAndPasswords passwords = new UserAndPasswords(user, "zecret", "zecret", false);

        // Creating user should fail
        provider.addUser(passwords);

        fail("Should never get here");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetRoles() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        List<Role> roles = provider.getRoles();
        assertEquals(0, roles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testModifyRole() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Role role = new Role();
        List<Role> roles = provider.modifyRole(role);
        assertEquals(0, roles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddRole() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Role role = new Role();
        List<Role> roles = provider.addRole(role);
        assertEquals(0, roles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetPermissions() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        List<Permission> permission = provider.getPermissions();
        assertEquals(0, permission.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testModifyPermission() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Permission permission = new Permission();
        List<Permission> roles = provider.modifyPermission(permission);
        assertEquals(0, roles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddPermission() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Permission permission = new Permission();
        List<Permission> roles = provider.addPermission(permission);
        assertEquals(0, roles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetUserRoles() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Map<String, List<Role>> userroles = provider.getUserRoles();
        assertEquals(0, userroles.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddUserRoles() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        UserRoles userroles = new UserRoles();
        Map<String, List<Role>> roletousermappings = provider.addUserRoles(userroles);
        assertEquals(0, roletousermappings.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testRemoveUserRoles() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        UserRoles userroles = new UserRoles();
        Map<String, List<Role>> roletousermappings = provider.removeUserRoles(userroles);
        assertEquals(0, roletousermappings.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetRolesPermissions() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        Map<String, List<Permission>> roletopermissionmappings = provider.getRolesPermissions();
        assertEquals(0, roletopermissionmappings.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddRolePermissions() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        RolePermissions rolepermissions = new RolePermissions();
        Map<String, List<Permission>> roletopermissionmappings = provider.addRolePermissions(rolepermissions);
        assertEquals(0, roletopermissionmappings.size());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testRemoveRolePermissions() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();

        RolePermissions rolepermissions = new RolePermissions();
        Map<String, List<Permission>> roletopermissionmappings = provider.removeRolePermissions(rolepermissions);
        assertEquals(0, roletopermissionmappings.size());
    }

}
