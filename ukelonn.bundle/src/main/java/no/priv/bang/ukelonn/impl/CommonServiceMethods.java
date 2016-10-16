package no.priv.bang.ukelonn.impl;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;

public class CommonServiceMethods {

    public static UkelonnService connectionCheck(Class<?> clazz) {
        String className = clazz.getSimpleName();
        UkelonnService ukelonnService = UkelonnServiceProvider.getInstance();
        if (ukelonnService == null) {
            throw new RuntimeException(className + " bean unable to find OSGi service Ukelonnservice, giving up");
        }

        return ukelonnService;
    }

    public static void errorLog(Class<?> clazz, String message) {
        LogService logservice = logserviceConnectionCheck(clazz);
        if (logservice != null) {
            logservice.log(LogService.LOG_ERROR, message);
        }
    }

    private static LogService logserviceConnectionCheck(Class<?> clazz) {
        try {
            UkelonnService ukelonnService = connectionCheck(clazz);

            return ukelonnService.getLogservice();
        } catch (RuntimeException e) {
            return null; // Don't fail if service is missing, just don't log anything
        }
    }

}
