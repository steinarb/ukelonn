/*
 * Copyright 2019 Steinar Bang
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
package no.priv.bang.ukelonn.db.authservicedbadapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnDatabase;

public class UkelonnDatabaseToAuthserviceDatabaseAdapterTest {

    @Test
    public void testCreateAndActivate() throws Exception {
        UkelonnDatabase ukelonnDatabase = mock(UkelonnDatabase.class);
        DataSource datasource = mock(DataSource.class);
        when(ukelonnDatabase.getDatasource()).thenReturn(datasource);
        Connection connection = mock(Connection.class);
        when(ukelonnDatabase.getConnection()).thenReturn(connection);

        UkelonnDatabaseToAuthserviceDatabaseAdapter adapter = new UkelonnDatabaseToAuthserviceDatabaseAdapter();
        adapter.setUkelonnDatabase(ukelonnDatabase);

        assertEquals(datasource, adapter.getDatasource());
        assertEquals(connection, adapter.getConnection());
    }

}
