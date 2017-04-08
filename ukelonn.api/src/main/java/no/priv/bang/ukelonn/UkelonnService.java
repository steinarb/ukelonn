package no.priv.bang.ukelonn;

import org.osgi.service.log.LogService;

/**
 * This is the service exposed by the ukelonn.bundle
 * after it gets all of its injections, and activates.
 *
 * The plan is to make this interface a place to access various
 * aspects of the web application, e.g. JDBC storage.
 *
 * @author Steinar Bang
 *
 */
public interface UkelonnService {

    String getMessage();

    UkelonnDatabase getDatabase();

    LogService getLogservice();

}
