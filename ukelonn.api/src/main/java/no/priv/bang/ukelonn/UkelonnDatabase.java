package no.priv.bang.ukelonn;

import java.sql.ResultSet;

public interface UkelonnDatabase {

    ResultSet query(String string);

}
