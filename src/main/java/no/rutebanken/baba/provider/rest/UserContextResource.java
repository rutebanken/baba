package no.rutebanken.baba.provider.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import no.rutebanken.baba.provider.usercontext.domain.UserContext;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * API endpoint that provides information about the current user.
 */
@Component
@Produces("application/json")
@Path("/usercontext")
@Tags(value = {
        @Tag(name = "UserContextResource", description = "User context resource")
})
public class UserContextResource {

    private final AuthorizationService<Long> authorizationService;
    private final UserInfoExtractor userInfoExtractor;

    public UserContextResource(AuthorizationService<Long> authorizationService, UserInfoExtractor userInfoExtractor) {
        this.authorizationService = authorizationService;
        this.userInfoExtractor = userInfoExtractor;
    }

    @GET
    @PreAuthorize("isAuthenticated()")
    public UserContext getUserContext() {
        return new UserContext(
                userInfoExtractor.getPreferredName(),
                authorizationService.isRouteDataAdmin(),
                authorizationService.isOrganisationAdmin());
    }

}
