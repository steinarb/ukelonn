package no.priv.bang.ukelonn;

import java.sql.Connection;

/**
 * This service provides access to Liquibase for setting up RDBMS schema
 * and sample data.
 *
 * @author Steinar Bang
 *
 */
public interface LiquibaseService {

    public void createSchema(Connection connection);

}
