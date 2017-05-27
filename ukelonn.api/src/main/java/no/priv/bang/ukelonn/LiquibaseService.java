package no.priv.bang.ukelonn;

import java.sql.Connection;

import liquibase.changelog.DatabaseChangeLog;

/**
 * This service provides access to Liquibase for setting up RDBMS schema
 * and sample data.
 *
 * @author Steinar Bang
 *
 */
public interface LiquibaseService {

    public void createSchema(Connection connection);

    public void runDBChangelog(Connection connection, DatabaseChangeLog changelog);

}
