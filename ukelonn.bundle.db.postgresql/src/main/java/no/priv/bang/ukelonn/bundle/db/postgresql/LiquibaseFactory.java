package no.priv.bang.ukelonn.bundle.db.postgresql;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

public interface LiquibaseFactory {

    Liquibase create(String changelogfile, ResourceAccessor resourceAccessor, DatabaseConnection databaseConnection) throws LiquibaseException;

}
