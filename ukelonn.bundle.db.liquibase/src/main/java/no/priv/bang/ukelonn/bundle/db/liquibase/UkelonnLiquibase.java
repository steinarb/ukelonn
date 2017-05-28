package no.priv.bang.ukelonn.bundle.db.liquibase;

import java.sql.Connection;

import javax.inject.Provider;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ukelonn.LiquibaseService;

public class UkelonnLiquibase implements Provider<LiquibaseService>, LiquibaseService {

    public void createSchema(Connection connection) {
    	try {
            DatabaseConnection databaseConnection = new JdbcConnection(connection);
            ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.update("");
    	} catch(Exception e) {
            // Capture the LiquibaseException to avoid having to declare it as a checked exception.
            // Avoid the need to have liquibase available in the bundle defining the LiquibaseService
            throw new RuntimeException(e);
    	}
    }

    @Override
    public LiquibaseService get() {
        return this;
    }

}
