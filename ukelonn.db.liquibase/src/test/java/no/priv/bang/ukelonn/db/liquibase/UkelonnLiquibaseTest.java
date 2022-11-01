/*
 * Copyright 2019-2022 Steinar Bang
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
package no.priv.bang.ukelonn.db.liquibase;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.exception.LiquibaseException;
import no.priv.bang.ukelonn.UkelonnException;


class UkelonnLiquibaseTest {

    private static DataSource dataSource;

    @BeforeAll
    static void beforeAllTests() throws Exception {
        dataSource = createDataSource("ukelonn");
    }

    @Test
    void testCreateSchema() throws Exception {
        var ukelonnLiquibase = new UkelonnLiquibase();
        ukelonnLiquibase.createInitialSchema(dataSource);
        ukelonnLiquibase.updateSchema(dataSource);

        try(Connection connection = createConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from transactions")) {
                ResultSet results = statement.executeQuery();
                int count = 0;
                while(results.next()) {
                    ++count;
                }

                assertEquals(0, count);
            }

            Date fromDate = new Date();
            Date toDate = new Date();
            createBonuses(connection, fromDate, toDate);
            assertBonuses(connection, fromDate, toDate);
        }
    }

    @Test
    void testCreateInitialAndUpdateSchemaFailOnConnectionClose() throws Exception {
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e1 = assertThrows(
            UkelonnException.class,
            () -> ukelonnLiquibase.createInitialSchema(datasource));
        assertThat(e1.getMessage()).startsWith("Error closing resource when creating ukelonn initial schema");

        var e2 = assertThrows(
            UkelonnException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e2.getMessage()).startsWith(UkelonnLiquibase.ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA);
    }

    @Test
    void testCreateInitialAndUpdateSchemaFailOnLiquibaseUpdate() throws Exception {
        Connection connection1 = spy(createConnection("ukelonn1"));
        Connection connection2 = spy(createConnection("ukelonn2"));
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenReturn(connection1).thenReturn(connection2);
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e1 = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.createInitialSchema(datasource));
        assertThat(e1.getMessage()).startsWith("java.sql.SQLException: Cannot set Autocommit On when in a nested connection");

        var e2 = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e2.getMessage()).startsWith("liquibase.exception.MigrationFailedException: Migration failed for changeset");
    }

    @Test
    void testUpdateSchemaFailOnConnectionCloseInAuthserviceSchemaSetup() throws Exception {
        DataSource datasource = spy(dataSource);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenThrow(SQLException.class);
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e = assertThrows(
            UkelonnException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e.getMessage()).startsWith(UkelonnLiquibase.ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA);
    }

    @Test
    void testUpdateSchemaFailOnConnectionCloseOnSchemaUpdateAfterAuthserviceAdd() throws Exception {
        DataSource datasource = spy(dataSource);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenThrow(SQLException.class);
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e = assertThrows(
            UkelonnException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e.getMessage()).startsWith(UkelonnLiquibase.ERROR_CLOSING_RESOURCE_WHEN_UPDATING_UKELONN_SCHEMA);
    }

    @Test
    void testUpdateSchemaFailOnLiqubaseUpdateInAuthserviceSchemaSetup() throws Exception {
        DataSource datasource = spy(dataSource);
        Connection connection = spy(createConnection("ukelonn3"));
        doThrow(SQLException.class).when(connection).setAutoCommit(anyBoolean());
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenReturn(connection);
        var ukelonnLiquibase = new UkelonnLiquibase();

        ukelonnLiquibase.createInitialSchema(dataSource);
        var e = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e.getMessage()).contains("Migration failed for changeset ");
    }

    @Test
    void testUpdateSchemaFailOnLiqubaseUpdateOnSchemaUpdateAfterAuthserviceAdd() throws Exception {
        DataSource datasource = spy(dataSource);
        Connection connection = createConnection("ukelonn4");
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenReturn(connection);
        var ukelonnLiquibase = new UkelonnLiquibase();

        ukelonnLiquibase.createInitialSchema(dataSource);
        var e = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e.getMessage()).startsWith("liquibase.exception.MigrationFailedException: Migration failed for changeset");
    }

    @Test
    void testForceReleaseLocks() throws Exception {
        try(Connection connection = createConnection()) {
            var ukelonnLiquibase = new UkelonnLiquibase();
            ukelonnLiquibase.forceReleaseLocks(connection);
        }

        try(Connection connection = createConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from databasechangeloglock")) {
                try(ResultSet results = statement.executeQuery()) {
                    boolean locked = true;
                    while(results.next()) {
                        locked = results.getBoolean("locked");
                    }

                    assertFalse(locked);
                }
            }
        }
    }

    @Test
    void testForceReleaseLocksFailOnConnectionClose() throws Exception {
        Connection connection = mock(Connection.class);
        doThrow(Exception.class).when(connection).close();
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e = assertThrows(
            UkelonnException.class,
            () -> ukelonnLiquibase.forceReleaseLocks(connection));
        assertThat(e.getMessage()).startsWith("Error closing resource when forcibly releasing liquibase lock");
    }

    private void createBonuses(Connection connection, Date startDate, Date endDate) throws Exception {
        createBonus(connection, true, "Christmas bonus", "To finance presents", 2.0, startDate, endDate);
    }

    private void createBonus(Connection connection, boolean enabled, String title, String description, double bonusFactor, Date startDate, Date endDate) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("insert into bonuses (enabled, title, description, bonus_factor, start_date, end_date) values (?, ?, ?, ?, ?, ?)")) {
            statement.setBoolean(1, enabled);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setDouble(4, bonusFactor);
            statement.setTimestamp(5, new Timestamp(startDate.toInstant().toEpochMilli()));
            statement.setTimestamp(6, new Timestamp(endDate.toInstant().toEpochMilli()));
            statement.executeUpdate();
        }
    }

    private void assertBonuses(Connection connection, Date startDate, Date endDate) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("select * from bonuses")) {
            try(ResultSet results = statement.executeQuery()) {
                assertBonus(results, true, "Christmas bonus", "To finance presents", 2.0, startDate, endDate);
            }
        }
    }

    private void assertBonus(ResultSet results, boolean enabled, String title, String description, double bonusFactor, Date startDate, Date endDate) throws Exception {
        assertTrue(results.next());
        assertEquals(enabled, results.getBoolean("enabled"));
        assertNull(results.getString("iconurl"));
        assertEquals(title, results.getString("title"));
        assertEquals(description, results.getString("description"));
        assertEquals(bonusFactor, results.getDouble("bonus_factor"), 0.0);
        assertEquals(startDate, new Date(results.getTimestamp("start_date").getTime()));
        assertEquals(endDate, new Date(results.getTimestamp("end_date").getTime()));
    }

    static private Connection createConnection() throws Exception {
        return dataSource.getConnection();
    }

    static private Connection createConnection(String dbname) throws Exception {
        return createDataSource(dbname).getConnection();
    }

    private static DataSource createDataSource(String dbname) throws SQLException {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var datasource = derbyDataSourceFactory.createDataSource(properties);
        return datasource;
    }

}
