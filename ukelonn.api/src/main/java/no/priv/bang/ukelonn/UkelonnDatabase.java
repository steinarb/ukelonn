package no.priv.bang.ukelonn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface UkelonnDatabase {

    String getName();

    PreparedStatement prepareStatement(String sql);

    ResultSet query(PreparedStatement statement);

    int update(PreparedStatement statement);

    void forceReleaseLocks();

}
