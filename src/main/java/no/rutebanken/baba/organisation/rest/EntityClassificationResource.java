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
import io.swagger.annotations.ApiOperation;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassification;
import no.rutebanken.baba.organisation.model.responsibility.EntityType;
import no.rutebanken.baba.organisation.repository.EntityClassificationRepository;
import no.rutebanken.baba.organisation.repository.EntityTypeRepository;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
import no.rutebanken.baba.organisation.rest.mapper.TypeMapper;
import no.rutebanken.baba.organisation.rest.validation.TypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("entity_types/{entityTypeId}/entity_classifications")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Entity classification resource"}, produces = "application/json")
public class EntityClassificationResource {

    @Autowired
    private EntityClassificationRepository repository;

    @Autowired
    private EntityTypeRepository entityTypeRepository;

    @Autowired
    private TypeMapper<EntityClassification> mapper;
    @Autowired
    private TypeValidator<EntityClassification> validator;

    @POST
    @ApiOperation(value = "Create a new entity classification")
    public Response create(@PathParam("entityTypeId") String entityTypeId, TypeDTO dto, @Context UriInfo uriInfo) {
        EntityType entityType = getEntityType(entityTypeId);
        dto.codeSpace = entityType.getCodeSpace().getId();
        validator.validateCreate(dto);
        EntityClassification entity = mapper.createFromDTO(dto, EntityClassification.class);
        entity.setEntityType(entityType);
        entity = repository.save(entity);
        return buildCreatedResponse(uriInfo, entity);
    }


    @PUT
    @Path("{id}")
    public void update(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id, TypeDTO dto) {
        EntityClassification entity = getExisting(id, entityTypeId);
        validator.validateUpdate(dto, entity);
        repository.save(mapper.updateFromDTO(dto, entity));
    }


    @GET
    @Path("{id}")
    public TypeDTO get(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id) {
        EntityClassification entity = getExisting(id, entityTypeId);
        return mapper.toDTO(entity, true);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id) {
        repository.delete(getExisting(id, entityTypeId));
    }

    @GET
    public List<TypeDTO> listAll(@PathParam("entityTypeId") String entityTypeId) {
        EntityType entityType = getEntityType(entityTypeId);
        if (CollectionUtils.isEmpty(entityType.getClassifications())) {
            return new ArrayList<>();
        }
        return entityType.getClassifications().stream().map(r -> mapper.toDTO(r, false)).collect(Collectors.toList());
    }


    protected Response buildCreatedResponse(UriInfo uriInfo, EntityClassification entity) {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId());
        return Response.created(builder.build()).build();
    }

    protected EntityClassification getExisting(String id, String entityTypeId) {
        try {
            EntityClassification entity = repository.getOneByPublicId(id);
            if (!entity.getEntityType().getId().equals(entityTypeId)) {
                throw new NotFoundException("EntityClassification with id: [" + id + "] not found for entity type with id: " + entityTypeId);
            }
            return entity;
        } catch (DataRetrievalFailureException e) {
            throw new NotFoundException("EntityClassification with id: [" + id + "] not found");
        }
    }

    protected EntityType getEntityType(String id) {
        try {
            return entityTypeRepository.getOneByPublicId(id);
        } catch (DataRetrievalFailureException e) {
            throw new NotFoundException("EntityType with id: [" + id + "] not found");
        }
    }


}
