package no.rutebanken.baba.organisation.rest;

import io.swagger.annotations.Api;
import no.rutebanken.baba.organisation.model.responsibility.EntityType;
import no.rutebanken.baba.organisation.repository.EntityTypeRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.responsibility.EntityTypeDTO;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.mapper.EntityTypeMapper;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import no.rutebanken.baba.organisation.rest.validation.EntityTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("entity_types")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Entity type resource"}, produces = "application/json")
public class EntityTypeResource extends AnnotatedBaseResource<EntityType, EntityTypeDTO> {

	@Autowired
	private EntityTypeRepository repository;
	@Autowired
	private EntityTypeMapper mapper;
	@Autowired
	private EntityTypeValidator validator;

	@Override
	protected Class<EntityType> getEntityClass() {
		return EntityType.class;
	}

	@Override
	protected VersionedEntityRepository<EntityType> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<EntityType, EntityTypeDTO> getMapper() {
		return mapper;
	}

	@Override
	protected DTOValidator<EntityType, EntityTypeDTO> getValidator() {
		return validator;
	}
}
