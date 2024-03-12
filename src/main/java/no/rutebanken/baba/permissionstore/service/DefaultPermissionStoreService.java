package no.rutebanken.baba.permissionstore.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DefaultPermissionStoreService implements PermissionStoreService {
    private final PermissionStoreClient permissionStoreClient;

    public DefaultPermissionStoreService(PermissionStoreClient permissionStoreClient) {
        this.permissionStoreClient = permissionStoreClient;
    }

    @Override
    public boolean isFederated(String email) {
        Objects.requireNonNull(email);
        String domain = StringUtils.substringAfter(email, "@");
        // TODO
        //return domain.toLowerCase(Locale.ROOT).equals("entur.org") || domain.toLowerCase(Locale.ROOT).equals("gmail.com");
        return permissionStoreClient.isFederated(domain);
    }


}
