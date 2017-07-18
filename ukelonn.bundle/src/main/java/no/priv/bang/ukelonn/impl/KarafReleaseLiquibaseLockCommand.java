package no.priv.bang.ukelonn.impl;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import no.priv.bang.ukelonn.UkelonnDatabase;

@Command(scope="ukelonn", name="release-liquibase-lock", description = "Forcibly release the Liquibase changelog lock")
@Service
public class KarafReleaseLiquibaseLockCommand implements Action {
    @Reference
    UkelonnDatabase database;

    @Override
    public Object execute() throws Exception {
        database.forceReleaseLocks();
        System.out.println("Forcibly unlocked the Liquibase changelog lock");
        return null;
    }
}
