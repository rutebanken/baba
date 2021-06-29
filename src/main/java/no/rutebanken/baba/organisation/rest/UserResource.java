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
import no.rutebanken.baba.organisation.email.NewUserEmailSender;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.UserRepository;
import no.rutebanken.baba.organisation.repository.VersionedEntityRepository;
import no.rutebanken.baba.organisation.rest.dto.user.UserDTO;
import no.rutebanken.baba.organisation.rest.mapper.DTOMapper;
import no.rutebanken.baba.organisation.rest.mapper.UserMapper;
import no.rutebanken.baba.organisation.rest.validation.DTOValidator;
import no.rutebanken.baba.organisation.rest.validation.UserValidator;
import no.rutebanken.baba.organisation.service.IamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("users")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"User resource"}, produces = "application/json")
public class UserResource extends BaseResource<User, UserDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserValidator validator;
    @Autowired
    private IamService iamService;

    @Autowired
    private NewUserEmailSender newUserEmailSender;

    @GET
    @Path("{id}")
    public UserDTO get(@PathParam("id") String id, @QueryParam("full") boolean fullObject) {
        User entity = getExisting(id);
        return getMapper().toDTO(entity, fullObject);
    }

    /**
     * Do not wrap method in a single transaction. Need to commit local storage before creating user in IAM, to avoid having users in IAM that does not exist in local storage.
     */
    @Transactional(propagation = Propagation.NEVER)
    @POST
    public Response create(UserDTO dto, @Context UriInfo uriInfo) {
        User user = createEntity(dto);
        String password;
        try {
            password = iamService.createUser(user);
        } catch (RuntimeException e) {
            LOGGER.warn("Creation of new user in IAM failed. Removing user from local storage. Exception: {}", e.getMessage(), e);
            deleteEntity(user.getId());
            throw e;
        }
        newUserEmailSender.sendEmail(user);
        return buildCreatedResponse(uriInfo, user, password);
    }

    @PUT
    @Path("{id}")
    public void update(@PathParam("id") String id, UserDTO dto) {
        User user = updateEntity(id, dto);
        iamService.updateUser(user);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") String id) {
        User user = deleteEntity(id);
        iamService.removeUser(user);
    }


    @POST
    @Path("{id}/resetPassword")
    public String resetPassword(@PathParam("id") String id) {
        User user = getExisting(id);
        String password = iamService.resetPassword(user);
        newUserEmailSender.sendEmail(user);
        return password;
    }

    @GET
    public List<UserDTO> listAll(@QueryParam("full") boolean fullObject) {
        return super.listAllEntities(fullObject);
    }

    @Override
    protected VersionedEntityRepository<User> getRepository() {
        return repository;
    }

    @Override
    protected DTOMapper<User, UserDTO> getMapper() {
        return mapper;
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected DTOValidator<User, UserDTO> getValidator() {
        return validator;
    }


    protected Response buildCreatedResponse(UriInfo uriInfo, User entity, String newPassword) {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId());
        return Response.created(builder.build()).entity(newPassword).build();
    }
}
