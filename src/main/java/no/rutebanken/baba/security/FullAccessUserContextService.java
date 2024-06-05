package no.rutebanken.baba.security;

/**
 * Fallback implementation giving full access to all operations for authenticated users,
 * enable by setting property baba.security.user-context-service=full-access
 */
public class FullAccessUserContextService implements UserContextService {
    @Override
    public boolean isRouteDataAdmin() {
        return true;
    }

    @Override
    public boolean isOrganizationAdmin() {
        return true;
    }

    @Override
    public boolean canViewProvider(Long providerId) {
        return true;
    }
}
