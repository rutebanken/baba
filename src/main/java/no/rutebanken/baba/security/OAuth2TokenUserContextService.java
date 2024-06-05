package no.rutebanken.baba.security;

import no.rutebanken.baba.provider.domain.Provider;
import no.rutebanken.baba.provider.repository.ProviderRepository;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;

import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.*;

/**
 * Implementation of the UserContextService that retrieves user privileges from a OAuth2 user token
 */
public class OAuth2TokenUserContextService implements UserContextService {

    private static final String ENTUR_ORG = "RB";

    private final ProviderRepository providerRepository;
    private final RoleAssignmentExtractor roleAssignmentExtractor;

    public OAuth2TokenUserContextService(ProviderRepository providerRepository,
                                         RoleAssignmentExtractor roleAssignmentExtractor) {
        this.providerRepository = providerRepository;
        this.roleAssignmentExtractor = roleAssignmentExtractor;
    }

    @Override
    public boolean isRouteDataAdmin() {
        return isAdminFor(ROLE_ROUTE_DATA_ADMIN);
    }

    @Override
    public boolean isOrganizationAdmin() {
        // ROLE_ORGANISATION_EDIT provides admin privilege on all organizations
        return isAdminFor(ROLE_ORGANISATION_EDIT);
    }

    @Override
    public boolean canViewProvider(Long providerId) {
        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null) {
            return false;
        }
        List<RoleAssignment> roleAssignments = roleAssignmentExtractor.getRoleAssignmentsForUser();

        return roleAssignments
                .stream()
                .anyMatch(roleAssignment -> matchAdminRole(roleAssignment, ROLE_ROUTE_DATA_ADMIN)
                        || matchAdminRole(roleAssignment, ROLE_ROUTE_DATA_VIEW_ALL)
                        || matchProviderRole(roleAssignment, ROLE_ROUTE_DATA_EDIT, provider)
                );
    }


    private boolean isAdminFor(String role) {
        List<RoleAssignment> roleAssignments = roleAssignmentExtractor.getRoleAssignmentsForUser();

        return roleAssignments
                .stream()
                .anyMatch(roleAssignment -> matchAdminRole(roleAssignment, role));
    }

    /**
     * Return true if the role assignment gives access to the given role for the Entur organization
     */
    private static boolean matchAdminRole(RoleAssignment roleAssignment, String role) {
        return roleAssignment.getRole().equals(role) &&
                roleAssignment.getOrganisation().equals(ENTUR_ORG);
    }

    /**
     * Return true if the role assignment gives access to the given role for the given provider.
     */
    private boolean matchProviderRole(RoleAssignment roleAssignment, String role, Provider provider) {
        return (
                role.equals(roleAssignment.getRole()) &&
                        provider.getChouetteInfo().xmlns.equals(roleAssignment.getOrganisation())
        );
    }


}
