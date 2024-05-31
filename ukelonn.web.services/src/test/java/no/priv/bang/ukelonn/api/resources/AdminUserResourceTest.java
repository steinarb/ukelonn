/*
 * Copyright 2018-2024 Steinar Bang
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.users.UserManagementServiceProvider;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.AdminStatus;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

class AdminUserResourceTest {

    @Test
    void testModify() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = mock(UserManagementService.class);
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Get first user and modify all properties except id
        var modifiedUsername = "gandalf";
        var modifiedEmailaddress = "wizard@hotmail.com";
        var modifiedFirstname = "Gandalf";
        var modifiedLastname = "Grey";
        var user = User.with()
            .userid(1)
            .username(modifiedUsername)
            .email(modifiedEmailaddress)
            .firstname(modifiedFirstname)
            .lastname(modifiedLastname)
            .build();
        when(useradmin.modifyUser(user)).thenReturn(Arrays.asList(user));

        // Save the modification
        var updatedUsers = resource.modify(user);

        // Verify that the first user has the modified values
        var firstUser = updatedUsers.get(0);
        assertEquals(modifiedUsername, firstUser.username());
        assertEquals(modifiedEmailaddress, firstUser.email());
        assertEquals(modifiedFirstname, firstUser.firstname());
        assertEquals(modifiedLastname, firstUser.lastname());
    }

    @Test
    void testModifyInternalServerError() throws Exception {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;
        var useradmin = new UserManagementServiceProvider();
        resource.useradmin = useradmin;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Create a mock datasource that throws exceptions and inject the datasource
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        useradmin.setDataSource(datasource);

        // Create a user bean
        var user = User.with().build();

        // Save the modification
        assertThrows(InternalServerErrorException.class, () -> resource.modify(user));
    }

    @Test
    void testCreate() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = mock(UserManagementService.class);
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Save the number of users before adding a user
        var originalUserCount = getUsers().size();
        var originalUsersPlusOne = new ArrayList<>(getUsersForUserManagement());

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        originalUsersPlusOne.add(user);
        when(useradmin.addUser(any())).thenReturn(originalUsersPlusOne);

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("zecret")
            .password2("zecret")
            .build();

        // Create a user
        var updatedUsers = resource.create(passwords);

        // Verify that the modified user has the modified values
        assertThat(updatedUsers).hasSizeGreaterThan(originalUserCount);
        var lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.username());
        assertEquals(newEmailaddress, lastUser.email());
        assertEquals(newFirstname, lastUser.firstname());
        assertEquals(newLastname, lastUser.lastname());
    }

    @Test
    void testCreateAndFailToFindUser() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = mock(UserManagementService.class);
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with()
            .user(User.with().build())
            .password1("zecret")
            .password2("zecret")
            .build();

        // Create a user
        assertThrows(UkelonnException.class, () -> resource.create(passwords));
    }

    @Test
    void testAdminStatusWhenUserIsAdministrator() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        var status = resource.adminStatus(user);
        assertEquals(user, status.user());
        assertTrue(status.administrator());
    }

    @Test
    void testAdminStatusWhenUserIsNotAdministrator() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename("ukelonnuser").description("ukelonn user").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        var status = resource.adminStatus(user);
        assertEquals(user, status.user());
        assertFalse(status.administrator());
    }

    @Test
    void testChangeAdminStatusMakeUserAdministrator() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(adminrole));
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.emptyList()).thenReturn(Collections.singletonList(adminrole));
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        var status = AdminStatus.with().user(user).administrator(true).build();
        var changedStatus = resource.changeAdminStatus(status);
        assertEquals(user, changedStatus.user());
        assertTrue(changedStatus.administrator());
    }

    @Test
    void testChangeAdminStatusMakeUserNotAdministrator() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(adminrole));
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole)).thenReturn(Collections.emptyList());
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        var status = AdminStatus.with().user(user).administrator(false).build();
        var changedStatus = resource.changeAdminStatus(status);
        assertEquals(user, changedStatus.user());
        assertFalse(changedStatus.administrator());
    }

    @Test
    void testChangeAdminStatusSetUserAdministratorWhenUserAlreadyAdministrator() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        var adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(adminrole));
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        var status = AdminStatus.with().user(user).administrator(true).build();
        var changedStatus = resource.changeAdminStatus(status);
        assertEquals(user, changedStatus.user());
        assertTrue(changedStatus.administrator());
    }

    @Test
    void testChangeAdminStatusAdminRoleNotPresent() {
        var resource = new AdminUserResource();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getRoles()).thenReturn(Collections.emptyList());
        resource.useradmin = useradmin;

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        var status = AdminStatus.with().user(user).administrator(true).build();
        var changedStatus = resource.changeAdminStatus(status);
        assertEquals(user, changedStatus.user());
        assertFalse(changedStatus.administrator());
    }

    @Test
    void testCreatePasswordsNotIdentical() throws Exception {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = new UserManagementServiceProvider();
        var authservicedatasource = mock(DataSource.class);
        when(authservicedatasource.getConnection()).thenThrow(SQLException.class);
        useradmin.setDataSource(authservicedatasource);
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("zecret")
            .password2("secret")
            .passwordsNotIdentical(true)
            .build();

        // Create a user
        assertThrows(InternalServerErrorException.class, () -> resource.create(passwords));
    }

    @Test
    void testCreateDatabaseException() throws Exception {
        var resource = new AdminUserResource();

        var ukelonn = new UkelonnServiceProvider();
        var useradmin = new UserManagementServiceProvider();

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        useradmin.setDataSource(datasource);

        // Create a logservice and inject it
        var logservice = new MockLogService();
        useradmin.setLogservice(logservice);

        // Inject OSGi services into the resource
        resource.ukelonn = ukelonn;
        resource.useradmin = useradmin;
        resource.setLogservice(logservice);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Creating user should fail
        assertThrows(InternalServerErrorException.class, () -> resource.create(passwords));
    }

    @Test
    void testCreateWhenUseridToCreateAccountCantBeFound() throws Exception {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = new UserManagementServiceProvider();
        var authservicedatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(authservicedatasource.getConnection()).thenReturn(connection);
        useradmin.setDataSource(authservicedatasource);
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Create a user object
        var newUsername = "aragorn";
        var newEmailaddress = "strider@hotmail.com";
        var newFirstname = "Aragorn";
        var newLastname = "McArathorn";
        var user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create a user
        assertThrows(InternalServerErrorException.class, () -> resource.create(passwords));
    }

    @Test
    void testPassword() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = mock(UserManagementService.class);
        when(useradmin.updatePassword(any())).thenReturn(getUsersForUserManagement());
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Get first user and modify all properties except id
        var users = getUsersForUserManagement();
        var user = users.get(0);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Change the password
        var updatedUsers = resource.password(passwords);

        // Verify that the size of the users list hasn't changed
        // (passwords can't be downloaded so we can't check the change)
        assertEquals(users.size(), updatedUsers.size());
    }

    @Test
    void testPasswordWithEmptyUsername() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = new UserManagementServiceProvider();
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Create a passwords object containing the user and with
        // valid and identical passwords
        var passwords = UserAndPasswords.with().password1("zecret").password2("zecret").build();

        // Changing the password should fail
        assertThrows(BadRequestException.class, () -> resource.password(passwords));
    }

    @Test
    void testPasswordWhenPasswordsDontMatch() {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = new UserManagementServiceProvider();
        resource.useradmin = useradmin;
        var ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Get first user to get a user with valid username
        var users = getUsersForUserManagement();
        var user = users.get(0);

        // Create a passwords object containing the user and with
        // valid but non-identical passwords
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("secret").build();

        // Changing the password should fail
        assertThrows(BadRequestException.class, () -> resource.password(passwords));
    }

    @Test
    void testPasswordDatabaseException() throws Exception {
        var resource = new AdminUserResource();

        // Inject OSGi services into the resource
        var useradmin = new UserManagementServiceProvider();
        resource.useradmin = useradmin;
        var ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;
        var logservice = new MockLogService();
        resource.setLogservice(logservice);
        ukelonn.setLogservice(logservice);
        useradmin.setLogservice(logservice);

        // Create a mock database that throws exceptions and inject it
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        useradmin.setDataSource(datasource);

        // Create a user object with a valid username
        var user = User.with().username("validusername").build();

        // Create a passwords object containing the user and with
        // valid and identical passwords
        var passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Changing the password should fail
        assertThrows(InternalServerErrorException.class, () -> resource.password(passwords));
    }

}
