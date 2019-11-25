/*
 * Copyright 2016-2019 Steinar Bang
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
package no.priv.bang.ukelonn.db.liquibase.production;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Test;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;
import no.priv.bang.ukelonn.db.liquibase.production.mocks.MockLogService;

public class ProductionLiquibaseRunnerTest {

    @Test
    public void testPrepare() throws Exception {
        // Create the object under test
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();

        // Mock the UkelonnLiquibase and the Liquibase (to avoid having to mock a lot of JDBC)
        UkelonnLiquibase ukelonnLiquibase = mock(UkelonnLiquibase.class);
        UkelonnLiquibaseFactory ukelonnLiquibaseFactory = mock(UkelonnLiquibaseFactory.class);
        when(ukelonnLiquibaseFactory.create()).thenReturn(ukelonnLiquibase);
        runner.setUkelonnLiquibaseFactory(ukelonnLiquibaseFactory);
        Liquibase liquibase = mock(Liquibase.class);
        LiquibaseFactory liquibaseFactory = mock(LiquibaseFactory.class);
        when(liquibaseFactory.create(anyString(), any(), any())).thenReturn(liquibase);
        runner.setLiquibaseFactory(liquibaseFactory);

        // Mock injected OSGi services
        MockLogService logservice = new MockLogService();
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        runner.setLogService(logservice);
        runner.activate();

        // Execute the method under test
        runner.prepare(datasource);

        // Verify that no errors have been logged
        assertEquals(0, logservice.getLogmessagecount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPrepareWhenSQLExceptionIsThrown() throws Exception {
        // Create the object under test
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate();

        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        // Execute the method under test
        runner.prepare(datasource);

        // Verify that no errors have been logged
        assertEquals(1, logservice.getLogmessagecount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInsertInitialDataInDatabaseFailToCreateLiquibase() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        boolean successfullyinserteddata = runner.insertInitialDataInDatabase(datasource );
        assertFalse(successfullyinserteddata);
    }

    @Test
    public void testCreateLiquibase() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate();
        DatabaseConnection connection = mock(DatabaseConnection.class);
        when(connection.getDatabaseProductName()).thenReturn("PostgreSQL");
        Liquibase liquibase = runner.createLiquibase(null, null, connection);
        assertNotNull(liquibase);
    }

    @Test
    public void testCreateUkelonnLiquibaseDefault() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate();
        UkelonnLiquibase liquibase = runner.createUkelonnLiquibase();
        assertNotNull(liquibase);
    }

}
