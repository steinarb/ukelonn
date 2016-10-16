package no.priv.bang.ukelonn.impl;

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

}
