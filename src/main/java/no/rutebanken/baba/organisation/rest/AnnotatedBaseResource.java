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

package no.rutebanken.baba.organisation.rest;

import io.swagger.annotations.Api;
import no.rutebanken.baba.organisation.model.VersionedEntity;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api
public abstract class AnnotatedBaseResource<E extends VersionedEntity, D extends BaseDTO> extends BaseResource<E, D> {

	@POST
	public Response create(D dto, @Context UriInfo uriInfo) {
		return super.createEntity(dto, uriInfo);
	}


	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, D dto) {
		super.updateEntity(id, dto);
	}


	@GET
	@Path("{id}")
	public D get(@PathParam("id") String id) {
		return super.getEntity(id);
	}

	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) {
		super.deleteEntity(id);
	}

	@GET
	public List<D> listAll() {
		return super.listAllEntities();
	}
}
