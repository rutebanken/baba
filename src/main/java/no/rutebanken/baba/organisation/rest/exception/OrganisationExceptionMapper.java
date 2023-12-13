/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.rest.exception;

import no.rutebanken.baba.organisation.model.OrganisationException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.http.HttpStatus;

@Provider
public class OrganisationExceptionMapper implements ExceptionMapper<OrganisationException> {

	@Override
	public Response toResponse(OrganisationException e) {
		return Response
				       .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				       .type(MediaType.TEXT_PLAIN)
				       .entity(e.getMessage())
				       .build();
	}
}
