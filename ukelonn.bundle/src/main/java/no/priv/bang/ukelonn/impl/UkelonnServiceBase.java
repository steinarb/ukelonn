package no.priv.bang.ukelonn.impl;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

public class UkelonnServiceBase implements UkelonnService {

    public String getMessage() {
        return "Hello world!";
    }

    public UkelonnDatabase getDatabase() {
        return null;
    }

}
