/*
 * Copyright 2018-2020 Steinar Bang
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
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.junit.Test;
import no.priv.bang.authservice.users.UserManagementServiceProvider;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

public class UsersTest {

    @Test
    public void testGet() {
        // Create the resource to be tested
        Users resource = new Users();

        // Inject the user management service
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(getUsersForUserManagement());
        resource.useradmin = useradmin;

        List<User> users = resource.get();

        assertThat(users).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testGetWithSqlException() throws Exception {
        // Create the resource to be tested
        Users resource = new Users();

        // Create an UkelonnService with a mock database
        // that throws SqlException
        UserManagementServiceProvider useradmin = new UserManagementServiceProvider();
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenThrow(SQLException.class);
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        useradmin.setDataSource(datasource);

        // Inject the UkelonnService
        resource.useradmin = useradmin;

        // Inject a log service
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;
        useradmin.setLogservice(logservice);

        // Do a get operation that will fail
        resource.get();

        fail("Should never get here");
    }

}
