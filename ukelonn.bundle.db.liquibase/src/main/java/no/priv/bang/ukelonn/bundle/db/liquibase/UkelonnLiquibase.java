/*
 * Copyright 2016-2018 Steinar Bang
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
package no.priv.bang.ukelonn.bundle.db.liquibase;

import java.sql.SQLException;
import javax.sql.PooledConnection;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class UkelonnLiquibase {

    public void createInitialSchema(PooledConnection connect) throws SQLException, LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase("db-changelog/db-changelog-1.0.0.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase.clearCheckSums();
        liquibase.update("");
    }

    public void updateSchema(PooledConnection connect) throws SQLException, LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase.update("");
    }

    public void forceReleaseLocks(PooledConnection connect) throws SQLException, LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase("db-changelog/db-changelog-1.0.0.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase.forceReleaseLocks();
    }

}
