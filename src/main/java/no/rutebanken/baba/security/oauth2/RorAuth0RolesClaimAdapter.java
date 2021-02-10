package no.rutebanken.baba.security.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;


/**
 * Insert a "roles" and "realm_access" claims in the JWT token based on the namespaced "roles" and "role_assignments" claims, for compatibility with the existing
 * authorization process (@{@link JwtRoleAssignmentExtractor}).
 */
@Component
public class RorAuth0RolesClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {

    private final MappedJwtClaimSetConverter delegate =
            MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Value("${baba.oauth2.resourceserver.auth0.ror.claim.namespace:https://ror.api.dev.entur.io/claims/}")
    private String rorAuth0ClaimNamespace;

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = this.delegate.convert(claims);

        Object roles = convertedClaims.get(rorAuth0ClaimNamespace + "role");
        if (roles != null) {
            convertedClaims.put("realm_access", Map.of("roles", roles));
        }

        Object roleAssignments = convertedClaims.get(rorAuth0ClaimNamespace + "role_assignments");
        if (roleAssignments != null) {
            convertedClaims.put("roles", roleAssignments);
        }

        return convertedClaims;
    }


}
