package no.priv.bang.ukelonn.impl;

public class CommonStringMethods {

    public static boolean isNullEmptyOrBlank(String string) {
        if (string == null) {
            return true;
        }

        if (string.trim().isEmpty()) {
            return true;
        }

        return false;
    }

    public static String safeTrim(String string) {
        if (string == null) {
            return null;
        }

        return string.trim();
    }

    public static StringBuilder sql(String initialStatement) {
    	return new StringBuilder(initialStatement);
    }

}
