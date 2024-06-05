package no.rutebanken.baba.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.entur.oauth2.RoROAuth2Claims;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Extract @{@link RoleAssignment}s from @{@link JwtAuthenticationToken}.
 * Role assignments are expected to be defined in the claim {@link RoROAuth2Claims#OAUTH2_CLAIM_ROLE_ASSIGNMENTS}, in JSON format.
 */
public class JwtRoleAssignmentExtractor implements RoleAssignmentExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DEFAULT_ADMIN_ORG = "RB";

    private final String adminOrganization;

    public JwtRoleAssignmentExtractor() {
        this(DEFAULT_ADMIN_ORG);
    }

    public JwtRoleAssignmentExtractor(String adminOrganization) {
       this.adminOrganization = adminOrganization;
    }


    public List<RoleAssignment> getRoleAssignmentsForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getRoleAssignmentsForUser(auth);
    }

    @Override
    public List<RoleAssignment> getRoleAssignmentsForUser(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new AccessDeniedException("Not authenticated with token");
        }

        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();

        Object roleAssignmentsClaim = jwt.getClaim(RoROAuth2Claims.OAUTH2_CLAIM_ROLE_ASSIGNMENTS);
        if (roleAssignmentsClaim != null) {
            return parseRoleAssignmentsClaim(roleAssignmentsClaim);
        }

        Object rolesClaim = jwt.getClaim("permissions");
        if (rolesClaim != null) {
            return parsePermissionsClaim(rolesClaim);
        }

        return List.of();

    }

    /**
     * Extract RoleAssignments from the role_assignments claim.
     * User tokens contains json-encoded RoleAssignments under this claim.
     */
    private static List<RoleAssignment> parseRoleAssignmentsClaim(Object roleAssignmentClaim) {
        if (roleAssignmentClaim instanceof List roleAssignmentList) {
            return roleAssignmentList.stream().map(JwtRoleAssignmentExtractor::parse).toList();
        } else {
            throw new IllegalArgumentException("Unsupported claim type: " + roleAssignmentClaim);
        }
    }

    /**
     * Extract RoleAssignments from the permissions claim.
     * Internal machine-to-machine tokens contains admin-level roles under this claim.
     */
    private List<RoleAssignment> parsePermissionsClaim(Object permissionsClaim) {
        if (permissionsClaim instanceof List claimPermissionAsList) {
            List<String> claimPermissionAsStringList =  claimPermissionAsList;
            return claimPermissionAsStringList.stream().map(o -> RoleAssignment.builder().withRole(o).withOrganisation(adminOrganization).build()).toList();
        } else {
            throw new IllegalArgumentException("Unsupported claim type: " + permissionsClaim);
        }
    }

    private static RoleAssignment parse(Object roleAssignment) {
        if (roleAssignment instanceof Map) {
            return MAPPER.convertValue(roleAssignment, RoleAssignment.class);
        }
        try {
            return MAPPER.readValue((String) roleAssignment, RoleAssignment.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception while parsing role assignments from JSON", e);
        }
    }
}
