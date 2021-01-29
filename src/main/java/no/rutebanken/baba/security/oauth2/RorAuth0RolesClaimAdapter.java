package no.rutebanken.baba.security.oauth2;

import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Insert a "roles" claim in the JWT token based on the organisationID claim, for compatibility with the existing
 * authorization process (@{@link JwtRoleAssignmentExtractor}).
 */
@Component
public class RorAuth0RolesClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RorAuth0RolesClaimAdapter.class);

    private static final Map<String, String> scopeToRole = Map.of("edit:organisation", AuthorizationConstants.ROLE_ORGANISATION_EDIT);

    private final MappedJwtClaimSetConverter delegate =
            MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = this.delegate.convert(claims);
        String[] scopes = ((String) convertedClaims.get("scope")).split(" ");
        List<String> roleAssignments = Arrays.stream(scopes).map(this::mapScopeToRole).filter(Objects::nonNull).collect(Collectors.toList());
        convertedClaims.put("roles", roleAssignments);
        return convertedClaims;
    }

    private String mapScopeToRole(String scope) {
        String role = scopeToRole.get(scope);
        if (role == null) {
            LOGGER.warn("Ignoring unknown scope: {}", scope);
        }
        return role;
    }


}
