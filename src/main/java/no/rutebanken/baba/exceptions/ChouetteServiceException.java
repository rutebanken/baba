package no.rutebanken.baba.exceptions;

public class ChouetteServiceException extends BabaException {


    public ChouetteServiceException(String message) {
        super(message);
    }

    public ChouetteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
