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
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.repository.RoleRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.mapper.TypeMapper;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import no.rutebanken.baba.organisation.rest.validation.TypeValidator;
import no.rutebanken.baba.organisation.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;


@Component
@Path("roles")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Role resource"}, produces = "application/json")
public class RoleResource extends BaseResource<Role, TypeDTO> {

	@Autowired
	private TypeMapper<Role> mapper;

	@Autowired
	private RoleRepository repository;

	@Autowired
	private TypeValidator<Role> validator;


	@Autowired
	private IamService iamService;

	@GET
	@Path("{id}")
	public TypeDTO get(@PathParam("id") String id) {
		return super.getEntity(id);
	}

	@POST
	public Response create(TypeDTO dto, @Context UriInfo uriInfo) {
		Role Role = createEntity(dto);
		iamService.createRole(Role);
		return buildCreatedResponse(uriInfo, Role);
	}

	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, TypeDTO dto) {
		updateEntity(id, dto);
	}


	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) {
		Role Role = deleteEntity(id);
		iamService.removeRole(Role);
	}

	@GET
	public List<TypeDTO> listAll() {
		return super.listAllEntities();
	}


	@Override
	protected VersionedEntityRepository<Role> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<Role, TypeDTO> getMapper() {
		return mapper;
	}

	@Override
	protected Class<Role> getEntityClass() {
		return Role.class;
	}

	@Override
	protected DTOValidator<Role, TypeDTO> getValidator() {
		return validator;
	}
}
