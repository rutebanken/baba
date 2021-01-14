package no.rutebanken.baba.exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class ReferentialAlreadyExistException extends ClientErrorException {
    public ReferentialAlreadyExistException(String schemaName) {
        super("The referential already exists: " + schemaName, Response.Status.CONFLICT);
    }
}
