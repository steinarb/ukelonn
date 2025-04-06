/*
 * Copyright 2019-2025 Steinar Bang
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnectionFactory;
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
        var assertjConnection = AssertDbConnectionFactory.of(dataSource).create();

        var transactions = assertjConnection.table("transactions").build();
        assertThat(transactions).exists().isEmpty();

        var bonuses1 = assertjConnection.table("bonuses").build();
        assertThat(bonuses1).exists().isEmpty();

        var fromDate = new Date();
        var toDate = new Date();
        createBonuses(dataSource, fromDate, toDate);

        var bonuses2 = assertjConnection.table("bonuses").build();
        assertThat(bonuses2).exists().hasNumberOfRows(1)
            .row(0)
            .value("enabled").isTrue()
            .value("iconurl").isNull()
            .value("title").isEqualTo("Christmas bonus")
            .value("description").isEqualTo("To finance presents")
            .value("bonus_factor").isEqualTo(2.0)
            .value("start_date").isEqualTo(Timestamp.from(fromDate.toInstant()))
            .value("end_date").isEqualTo(Timestamp.from(toDate.toInstant()));
    }

    @Test
    void testCreateInitialAndUpdateSchemaFailOnConnectionClose() throws Exception {
        var datasource = mock(DataSource.class);
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
        var connection1 = spy(createConnection("ukelonn1"));
        var connection2 = spy(createConnection("ukelonn2"));
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenReturn(connection1).thenReturn(connection2);
        var ukelonnLiquibase = new UkelonnLiquibase();

        var e1 = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.createInitialSchema(datasource));
        assertThat(e1.getMessage()).startsWith("java.sql.SQLException: Cannot set Autocommit On when in a nested connection");

        var e2 = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e2.getMessage()).contains("liquibase.exception.MigrationFailedException: Migration failed for changeset");
    }

    @Test
    void testUpdateSchemaFailOnConnectionCloseInAuthserviceSchemaSetup() throws Exception {
        var datasource = spy(dataSource);
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
    void testUpdateSchemaFailOnLiqubaseUpdateInAuthserviceSchemaSetup() throws Exception {
        var datasource = spy(dataSource);
        var connection = spy(createConnection("ukelonn3"));
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
        var datasource = spy(dataSource);
        var connection = createConnection("ukelonn4");
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenReturn(connection);
        var ukelonnLiquibase = new UkelonnLiquibase();

        ukelonnLiquibase.createInitialSchema(dataSource);
        var e = assertThrows(
            LiquibaseException.class,
            () -> ukelonnLiquibase.updateSchema(datasource));
        assertThat(e.getMessage()).contains("liquibase.exception.MigrationFailedException: Migration failed for changeset");
    }

    private void createBonuses(DataSource datasource, Date startDate, Date endDate) throws Exception {
        try(Connection connection = datasource.getConnection()) {
            createBonus(connection, true, "Christmas bonus", "To finance presents", 2.0, startDate, endDate);
        }
    }

    private void createBonus(Connection connection, boolean enabled, String title, String description, double bonusFactor, Date startDate, Date endDate) throws Exception {
        try (var statement = connection.prepareStatement("insert into bonuses (enabled, title, description, bonus_factor, start_date, end_date) values (?, ?, ?, ?, ?, ?)")) {
            statement.setBoolean(1, enabled);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setDouble(4, bonusFactor);
            statement.setTimestamp(5, new Timestamp(startDate.toInstant().toEpochMilli()));
            statement.setTimestamp(6, new Timestamp(endDate.toInstant().toEpochMilli()));
            statement.executeUpdate();
        }
    }

    private static Connection createConnection(String dbname) throws Exception {
        return createDataSource(dbname).getConnection();
    }

    private static DataSource createDataSource(String dbname) throws SQLException {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
