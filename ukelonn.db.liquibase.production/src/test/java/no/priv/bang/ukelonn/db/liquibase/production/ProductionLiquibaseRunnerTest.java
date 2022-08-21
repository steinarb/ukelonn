/*
 * Copyright 2016-2021 Steinar Bang
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;
import static no.priv.bang.ukelonn.db.liquibase.production.ProductionLiquibaseRunner.*;

class ProductionLiquibaseRunnerTest {

    @Test
    void testPrepare() throws Exception {
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
        runner.activate(Collections.emptyMap());

        // Execute the method under test
        runner.prepare(datasource);

        // Verify that no errors have been logged
        assertEquals(0, logservice.getLogmessages().size());
    }

    @Test
    void testPrepareWhenSQLExceptionIsThrown() throws Exception {
        // Create the object under test
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());

        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        // Execute the method under test
        runner.prepare(datasource);

        // Verify that no errors have been logged
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertInitialDataInDatabaseFailToCreateLiquibase() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        boolean successfullyinserteddata = runner.insertInitialDataInDatabase(datasource );
        assertFalse(successfullyinserteddata);
    }

    @Test
    void testCreateLiquibase() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());
        DatabaseConnection connection = mock(DatabaseConnection.class);
        when(connection.getDatabaseProductName()).thenReturn("mockdb");
        when(connection.getURL()).thenReturn("jdbc:mock:///ukelonn");
        Liquibase liquibase = runner.createLiquibase(null, null, connection);
        assertNotNull(liquibase);
    }

    @Test
    void testCreateUkelonnLiquibaseDefault() throws Exception {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());
        UkelonnLiquibase liquibase = runner.createUkelonnLiquibase();
        assertNotNull(liquibase);
    }

    @Test
    void testInitialDataResourceNameNoLanguageSet() {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());

        assertEquals(INITIAL_DATA_DEFAULT_RESOURCE_NAME, runner.initialDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    void testInitialDataResourceNameWithLanguageSet() {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.singletonMap("databaselanguage", "en_GB"));

        assertEquals(INITIAL_DATA_DEFAULT_RESOURCE_NAME.replace(".xml", "_en_GB.xml"), runner.initialDataResourceName());
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    void testInitialDataResourceNameWithNotFoundLanguageSet() {
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        MockLogService logservice = new MockLogService();
        runner.setLogService(logservice);
        runner.activate(Collections.singletonMap("databaselanguage", "en_UK"));

        assertEquals(INITIAL_DATA_DEFAULT_RESOURCE_NAME, runner.initialDataResourceName());
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

}
