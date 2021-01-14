package no.rutebanken.baba.exceptions;

public class ReferentialAlreadyExistException extends BabaException {
    public ReferentialAlreadyExistException(String schemaName) {
        super("The referential already exists: " + schemaName);
    }
}
