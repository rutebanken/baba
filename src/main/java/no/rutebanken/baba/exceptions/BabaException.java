package no.rutebanken.baba.exceptions;

public class BabaException extends RuntimeException {


    public BabaException(String message) {
        super(message);
    }

    public BabaException(String message, Throwable cause) {
        super(message, cause);
    }
}
