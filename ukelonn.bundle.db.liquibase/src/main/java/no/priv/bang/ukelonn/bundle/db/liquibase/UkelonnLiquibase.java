package no.priv.bang.ukelonn.bundle.db.liquibase;

import java.sql.SQLException;
import javax.sql.PooledConnection;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class UkelonnLiquibase {

    public void createSchema(PooledConnection connect) throws SQLException, LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase.update("");
    }

}
