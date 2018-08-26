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
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class UsersTest {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGet() {
        // Create the resource to be tested
        Users resource = new Users();

        // Inject the UkelonnService
        resource.ukelonn = getUkelonnServiceSingleton();

        List<User> users = resource.get();

        assertThat(users.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=InternalServerErrorException.class)
    public void testGetWithSqlException() throws Exception {
        // Create the resource to be tested
        Users resource = new Users();

        // Create an UkelonnService with a mock database
        // that throws SqlException
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenThrow(SQLException.class);
        UkelonnDatabase database = mock(UkelonnDatabase.class);
        when(database.prepareStatement(anyString())).thenReturn(statement);
        when(database.query(any())).thenReturn(results);
        ukelonn.setUkelonnDatabase(database);

        // Inject the UkelonnService
        resource.ukelonn = ukelonn;

        // Inject a log service
        MockLogService logservice = new MockLogService();
        resource.logservice = logservice;

        // Do a get operation that will fail
        resource.get();

        fail("Should never get here");
    }

}
