/*
 * Copyright 2016-2023 Steinar Bang
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

import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.database.DatabaseConnection;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;
import static no.priv.bang.ukelonn.db.liquibase.production.ProductionLiquibaseRunner.*;

class ProductionLiquibaseRunnerTest {

    @Test
    void testPrepare() throws Exception {
        // Create the object under test
        var runner = new ProductionLiquibaseRunner();

        // Mock injected OSGi services
        var logservice = new MockLogService();
        var datasource = createDataSource("ukelonn1");
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());

        // Execute the method under test
        runner.prepare(datasource);

        // Verify that no errors have been logged
        assertThat(logservice.getLogmessages()).isEmpty();
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
        assertThat(logservice.getLogmessages()).hasSize(1);
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

    private static DataSource createDataSource(String dbname) throws SQLException {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var datasource = derbyDataSourceFactory.createDataSource(properties);
        return datasource;
    }

}
