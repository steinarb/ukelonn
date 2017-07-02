package no.priv.bang.ukelonn.impl;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;

public class CommonServiceMethods {

    public static UkelonnService connectionCheck(UkelonnService provider, Class<?> clazz) {
        String className = clazz.getSimpleName();
        if (provider == null) {
            throw new RuntimeException(className + " unable to find OSGi service Ukelonnservice, giving up");
        }

        return provider;
    }

    public static void logError(UkelonnService provider, Class<?> clazz, String message) {
        LogService logservice = logserviceConnectionCheck(provider, clazz);
        if (logservice != null) {
            logservice.log(LogService.LOG_ERROR, message);
        }
    }

    public static void logError(UkelonnService provider, Class<?> clazz, String message, Throwable exception) {
        LogService logservice = logserviceConnectionCheck(provider, clazz);
        if (logservice != null) {
            logservice.log(LogService.LOG_ERROR, message, exception);
        }
    }

    private static LogService logserviceConnectionCheck(UkelonnService provider, Class<?> clazz) {
        try {
            UkelonnService ukelonnService = connectionCheck(provider, clazz);

            return ukelonnService.getLogservice();
        } catch (RuntimeException e) {
            return null; // Don't fail if service is missing, just don't log anything
        }
    }

}
