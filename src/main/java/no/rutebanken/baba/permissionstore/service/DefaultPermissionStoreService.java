package no.rutebanken.baba.permissionstore.service;

public class DefaultPermissionStoreService implements PermissionStoreService {
    private final PermissionStoreResource permissionStoreResource;

    public DefaultPermissionStoreService(PermissionStoreResource permissionStoreResource) {
        this.permissionStoreResource = permissionStoreResource;
    }

    @Override
    public PermissionStoreUser getUser(String userId) {

        return permissionStoreResource.getUser(userId);

    }

    @Override
    public boolean isFederated(String email) {
        return false;
    }


}
