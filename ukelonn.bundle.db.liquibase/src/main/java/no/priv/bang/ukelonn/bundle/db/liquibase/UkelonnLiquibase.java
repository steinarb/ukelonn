package no.priv.bang.ukelonn.bundle.db.liquibase;

import java.sql.Connection;

import javax.inject.Provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.osgi.OSGiResourceAccessor;
import liquibase.resource.ResourceAccessor;
import no.priv.bang.ukelonn.LiquibaseService;

public class UkelonnLiquibase implements Provider<LiquibaseService>, LiquibaseService {
    private Bundle bundle;

    public UkelonnLiquibase() {
        super();
        this.bundle = FrameworkUtil.getBundle(getClass());
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void createSchema(Connection connection) {
    	try {
            DatabaseConnection databaseConnection = new JdbcConnection(connection);
            ResourceAccessor classLoaderResourceAccessor = new OSGiResourceAccessor(bundle);
            Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
            liquibase.update("");
    	} catch(Exception e) {
            // Capture the LiquibaseException to avoid having to declare it as a checked exception.
            // Avoid the need to have liquibase available in the bundle defining the LiquibaseService
            throw new RuntimeException(e);
    	}
    }

    @Override
    public void runDBChangelog(Connection connection, DatabaseChangeLog changelog) {
    	try {
            DatabaseConnection databaseConnection = new JdbcConnection(connection);
            ResourceAccessor classLoaderResourceAccessor = new OSGiResourceAccessor(bundle);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
            Liquibase liquibase = new Liquibase(changelog, classLoaderResourceAccessor, database);
            liquibase.update("");
    	} catch(Exception e) {
            throw new RuntimeException(e);
    	}
    }

    @Override
    public LiquibaseService get() {
        return this;
    }

}
