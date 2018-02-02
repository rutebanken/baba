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
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.UserRepository;
import no.rutebanken.baba.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.baba.organisation.rest.mapper.NotificationConfigurationMapper;
import no.rutebanken.baba.organisation.rest.validation.NotificationConfigurationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Set;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("users/{userName}/notification_configurations")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api(tags = {"Notification configuration resource"}, produces = "application/json")
public class NotificationConfigurationResource {

    @Autowired
    private UserRepository repository;
    @Autowired
    private NotificationConfigurationMapper mapper;
    @Autowired
    private NotificationConfigurationValidator validator;

    @GET
    @PreAuthorize("#userName == authentication.name or hasRole('" + ROLE_ORGANISATION_EDIT + "')")
    public Set<NotificationConfigDTO> get(@PathParam("userName") String userName, @QueryParam("full") boolean fullObject) {
        User entity = getUser(userName);
        return mapper.toDTO(entity.getNotificationConfigurations(),false);
    }


    @PUT
    @PreAuthorize("#userName == authentication.name or hasRole('" + ROLE_ORGANISATION_EDIT + "')")
    public void createOrUpdate(@PathParam("userName") String userName, Set<NotificationConfigDTO> config) {
        validator.validate(userName, config);
        User user = getUser(userName.toLowerCase());
        user.setNotificationConfigurations(mapper.fromDTO(config));
        repository.save(user);
    }


    @DELETE
    @PreAuthorize("#userName == authentication.name or hasRole('" + ROLE_ORGANISATION_EDIT + "')")
    public void delete(@PathParam("userName") String userName) {
        User user = getUser(userName);
        user.getNotificationConfigurations().clear();
        repository.save(user);
    }

    protected User getUser(String userName) {
        User user;
        try {
            user = repository.getUserByUsername(userName);
        } catch (DataRetrievalFailureException e) {
            user = null;
        }

        if (user == null) {
            throw new NotFoundException("User with user name: [" + userName + "] not found");
        }
        return user;
    }
}
