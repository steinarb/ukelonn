package no.priv.bang.ukelonn.db.postgresql;

import no.priv.bang.ukelonn.db.liquibase.UkelonnLiquibase;

public interface UkelonnLiquibaseFactory {

    UkelonnLiquibase create();

}
