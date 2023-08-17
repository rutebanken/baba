package no.rutebanken.baba.exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class ReferentialAlreadyExistException extends ClientErrorException {
    public ReferentialAlreadyExistException(String schemaName) {
        super("The referential already exists: " + schemaName, Response.Status.CONFLICT);
    }
}
