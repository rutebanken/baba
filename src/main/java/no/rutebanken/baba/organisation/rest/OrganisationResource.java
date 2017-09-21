package no.rutebanken.baba.organisation.rest;


import io.swagger.annotations.Api;
import no.rutebanken.baba.organisation.model.organisation.Organisation;
import no.rutebanken.baba.organisation.repository.OrganisationRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.mapper.OrganisationMapper;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import no.rutebanken.baba.organisation.rest.validation.OrganisationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Organisation resource"}, produces = "application/json")
public class OrganisationResource extends AnnotatedBaseResource<Organisation, OrganisationDTO> {


	@Autowired
	private OrganisationRepository repository;
	@Autowired
	private OrganisationMapper mapper;
	@Autowired
	private OrganisationValidator validator;

	@Override
	protected Class<Organisation> getEntityClass() {
		return Organisation.class;
	}

	@Override
	protected VersionedEntityRepository<Organisation> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<Organisation, OrganisationDTO> getMapper() {
		return mapper;
	}

	@Override
	protected DTOValidator<Organisation, OrganisationDTO> getValidator() {
		return validator;
	}
}
