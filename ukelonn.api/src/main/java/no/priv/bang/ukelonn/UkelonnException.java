package no.priv.bang.ukelonn;

public class UkelonnException extends RuntimeException {
    private static final long serialVersionUID = 5175018860496844806L;

    public UkelonnException(String message) {
        super(message);
    }

    public UkelonnException(String message, Throwable cause) {
        super(message, cause);
    }

    public UkelonnException(Throwable cause) {
        super(cause);
    }

}
