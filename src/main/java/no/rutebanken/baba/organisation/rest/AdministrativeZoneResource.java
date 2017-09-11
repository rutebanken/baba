package no.rutebanken.baba.organisation.rest;

import io.swagger.annotations.Api;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZoneType;
import no.rutebanken.baba.organisation.repository.AdministrativeZoneRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.baba.organisation.rest.mapper.AdministrativeZoneMapper;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.validation.AdministrativeZoneValidator;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("services/organisations/administrative_zones")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Administrative zone resource"}, produces = "application/json")
public class AdministrativeZoneResource extends AnnotatedBaseResource<AdministrativeZone, AdministrativeZoneDTO> {


    @Autowired
    private AdministrativeZoneRepository repository;
    @Autowired
    private AdministrativeZoneMapper mapper;
    @Autowired
    private AdministrativeZoneValidator validator;

    @Override
    protected VersionedEntityRepository<AdministrativeZone> getRepository() {
        return repository;
    }

    @Override
    protected DTOMapper<AdministrativeZone, AdministrativeZoneDTO> getMapper() {
        return mapper;
    }

    @Override
    protected Class<AdministrativeZone> getEntityClass() {
        return AdministrativeZone.class;
    }

    @Override
    protected DTOValidator<AdministrativeZone, AdministrativeZoneDTO> getValidator() {
        return validator;
    }


    @GET
    @Path("types")
    public AdministrativeZoneType[] getAdministrativeZoneTypes() {
        return AdministrativeZoneType.values();
    }

}
