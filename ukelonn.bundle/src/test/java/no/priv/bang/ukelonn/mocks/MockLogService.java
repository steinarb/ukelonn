package no.priv.bang.ukelonn.mocks;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

@SuppressWarnings("rawtypes")
public class MockLogService implements LogService {
    final String[] errorLevel = {"", "[ERROR] ", "[WARNING] ", "[INFO] ", "[DEBUG] "};
    List<String> logmessages = new ArrayList<String>();

    public List<String> getLogmessages() {
        return logmessages;
    }

    public void log(int level, String message) {
    	String messageWithLevel = errorLevel[level] + message;
    	logmessages.add(messageWithLevel);
        System.err.println(messageWithLevel);
    }

    public void log(int level, String message, Throwable exception) {
    	String messageWithLevel = errorLevel[level] + message;
    	logmessages.add(messageWithLevel);
        System.err.println(messageWithLevel);
        exception.printStackTrace();
    }

    public void log(ServiceReference sr, int level, String message) {
        // TODO Auto-generated method stub

    }

    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        // TODO Auto-generated method stub

    }

}
