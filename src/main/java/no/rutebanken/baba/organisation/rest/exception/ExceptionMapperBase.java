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

import com.google.common.collect.Sets;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ExceptionMapperBase {
	private Map<Response.Status, Set<Class<?>>> mapping;

	public ExceptionMapperBase() {
		mapping = new HashMap<>();
		mapping.put(Response.Status.BAD_REQUEST,
				Sets.newHashSet(ValidationException.class, OptimisticLockException.class, EntityNotFoundException.class, DataIntegrityViolationException.class));
		mapping.put(Response.Status.CONFLICT, Sets.newHashSet(EntityExistsException.class));
	}

	protected Response buildResponse(Throwable t) {
		return Response
				       .status(toStatus(t))
				       .type(MediaType.TEXT_PLAIN)
				       .entity(t.getMessage())
				       .build();
	}

	protected int toStatus(Throwable e) {
		for (Map.Entry<Response.Status, Set<Class<?>>> entry : mapping.entrySet()) {
			if (entry.getValue().stream().anyMatch(c -> c.isAssignableFrom(e.getClass()))) {
				return entry.getKey().getStatusCode();
			}
		}
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}
}
