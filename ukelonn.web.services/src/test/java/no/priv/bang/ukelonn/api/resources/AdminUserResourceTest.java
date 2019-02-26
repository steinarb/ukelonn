/*
 * Copyright 2018 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.PasswordsWithUser;
import no.priv.bang.ukelonn.beans.User;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

public class AdminUserResourceTest {

    @Test
    public void testModify() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Get first user and modify all properties except id
        User user = new User();
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        user.setUsername(modifiedUsername);
        user.setEmail(modifiedEmailaddress);
        user.setFirstname(modifiedFirstname);
        user.setLastname(modifiedLastname);
        when(ukelonn.modifyUser(user)).thenReturn(Arrays.asList(user));

        // Save the modification
        List<User> updatedUsers = resource.modify(user);

        // Verify that the first user has the modified values
        User firstUser = updatedUsers.get(0);
        assertEquals(modifiedUsername, firstUser.getUsername());
        assertEquals(modifiedEmailaddress, firstUser.getEmail());
        assertEquals(modifiedFirstname, firstUser.getFirstname());
        assertEquals(modifiedLastname, firstUser.getLastname());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testModifyInternalServerError() throws Exception {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a user bean
        User user = new User();

        // Save the modification
        resource.modify(user);

        // Verify that the update fails
        fail("Should never get here!");
    }

    @Test
    public void testCreate() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Save the number of users before adding a user
        int originalUserCount = getUsers().size();
        List<User> originalUsersPlusOne = new ArrayList<>(getUsers());

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);
        originalUsersPlusOne.add(user);
        when(ukelonn.createUser(any())).thenReturn(originalUsersPlusOne);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Create a user
        List<User> updatedUsers = resource.create(passwords);

        // Verify that the modified user has the modified values
        assertThat(updatedUsers.size()).isGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=BadRequestException.class)
    public void testCreatePasswordsNotIdentical() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.createUser(any())).thenThrow(UkelonnBadRequestException.class);
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Save the number of users before adding a user
        int originalUserCount = getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "secret");

        // Create a user
        List<User> updatedUsers = resource.create(passwords);

        // Verify that the first user has the modified values
        assertThat(updatedUsers.size()).isGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testCreateDatabaseException() throws Exception {
        AdminUserResource resource = new AdminUserResource();

        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a logservice and inject it
        MockLogService logservice = new MockLogService();
        ukelonn.setLogservice(logservice);

        // Inject OSGi services into the resource
        resource.ukelonn = ukelonn;
        resource.logservice = logservice;

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = new User(0, newUsername, newEmailaddress, newFirstname, newLastname);

        // Create a passwords object containing the user
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Creating user should fail
        resource.create(passwords);

        fail("Should never get here");
    }

    @Test
    public void testPassword() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.changePassword(any())).thenReturn(getUsers());
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Get first user and modify all properties except id
        List<User> users = getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Change the password
        List<User> updatedUsers = resource.password(passwords);

        // Verify that the size of the users list hasn't changed
        // (passwords can't be downloaded so we can't check the change)
        assertEquals(users.size(), updatedUsers.size());
    }

    @SuppressWarnings("unchecked")
    @Test(expected=BadRequestException.class)
    public void testPasswordWithEmptyUsername() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.changePassword(any())).thenThrow(UkelonnBadRequestException.class);
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Create a user with an empty username
        User user = new User(0, "", null, null, null);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Changing the password should fail
        resource.password(passwords);

        fail("Should never get here!");
    }

    @SuppressWarnings("unchecked")
    @Test(expected=BadRequestException.class)
    public void testPasswordWhenPasswordsDontMatch() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.changePassword(any())).thenThrow(UkelonnBadRequestException.class);
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;


        // Get first user to get a user with valid username
        List<User> users = getUsers();
        User user = users.get(0);

        // Create a passwords object containing the user and with
        // valid but non-identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "secret");

        // Changing the password should fail
        resource.password(passwords);

        fail("Should never get here!");
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testPasswordDatabaseException() throws Exception {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;
        ukelonn.setLogservice(logservice);

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);


        // Create a user object with a valid username
        User user = new User(0, "validusername", null, null, null);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        PasswordsWithUser passwords = new PasswordsWithUser(user, "zecret", "zecret");

        // Changing the password should fail
        resource.password(passwords);

        fail("Should never get here");
    }

}
