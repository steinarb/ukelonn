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
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.beans.User;
import no.priv.bang.ukelonn.impl.UkelonnServiceProvider;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class AdminUserResourceTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testModify() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        resource.ukelonn = getUkelonnServiceSingleton();
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Get first user and modify all properties except id
        List<User> users = getUkelonnServiceSingleton().getUsers();
        User user = users.get(0);
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        user.setUsername(modifiedUsername);
        user.setEmail(modifiedEmailaddress);
        user.setFirstname(modifiedFirstname);
        user.setLastname(modifiedLastname);

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
    public void testModifyInternalServerError() {
        AdminUserResource resource = new AdminUserResource();

        // Inject OSGi services into the resource
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Create a mock database that throws exceptions and inject it
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.update(any())).thenThrow(SQLException.class);
        ukelonn.setUkelonnDatabase(database);

        // Create a user bean
        User user = new User();

        // Save the modification
        resource.modify(user);

        // Verify that the update fails
        fail("Should never get here!");
    }

}
