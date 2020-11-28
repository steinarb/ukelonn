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
package no.priv.bang.ukelonn.db.liquibase;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;


public class UkelonnLiquibaseTest {

    private static DataSource dataSource;

    @BeforeClass
    static public void beforeAllTests() throws Exception {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        dataSource = derbyDataSourceFactory.createDataSource(properties);
    }

    @Test
    public void testCreateSchema() throws Exception {
        try(Connection connection = createConnection()) {
            UkelonnLiquibase handleregLiquibase = new UkelonnLiquibase();
            handleregLiquibase.createInitialSchema(connection);
            handleregLiquibase.updateSchema(connection);
        }

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
    public void testForceReleaseLocks() throws Exception {
        try(Connection connection = createConnection()) {
            UkelonnLiquibase handleregLiquibase = new UkelonnLiquibase();
            handleregLiquibase.forceReleaseLocks(connection);
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


}
