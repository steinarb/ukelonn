package no.priv.bang.ukelonn.bundle.test.db;

import java.sql.ResultSet;

public interface UkelonnDatabase {

    boolean createSchema();

    int[] insertMockData();

    ResultSet query(String string);

}
