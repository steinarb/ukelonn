package no.priv.bang.ukelonn.impl;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

public class UkelonnServiceBase implements UkelonnService {

    public String getMessage() {
        return "Hello world!";
    }

    public UkelonnDatabase getDatabase() {
        return null;
    }

    public LogService getLogservice() {
        return null;
    }

}
