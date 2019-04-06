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

    static private Connection createConnection() throws Exception {
        return dataSource.getConnection();
    }


}
