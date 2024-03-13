package no.rutebanken.baba.organisation.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.filter.RolesFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import no.rutebanken.baba.organisation.model.OrganisationException;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.RoleRepository;
import no.rutebanken.baba.organisation.repository.UserRepository;
import no.rutebanken.baba.permissionstore.service.PermissionStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("auth0")
public class Auth0IamService implements IamService {

    private static final String ROR_ROLES = "ror_roles";
    private static final String ROR_CREATED_BY_ROR = "ror_created_by_ror";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<String> defaultRoles;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Auth0ManagementApi auth0ManagementAPI;
    private final Auth0UserMapper auth0UserMapper;
    private final PermissionStoreService permissionStoreService;

    public Auth0IamService(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AuthAPI authAPI,
                           Auth0UserMapper auth0UserMapper,
                           PermissionStoreService permissionStoreService,
                           @Value("#{'${iam.auth0.default.roles:rutebanken}'.split(',')}") List<String> defaultRoles,
                           @Value("${iam.auth0.admin.domain}") String domain) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.defaultRoles = defaultRoles;
        this.auth0ManagementAPI = new Auth0ManagementApi(authAPI, domain);
        this.auth0UserMapper = auth0UserMapper;
        this.permissionStoreService = permissionStoreService;
    }

    @Override
    public void createOrUpdate(User user) {
        if (isFederated(user.getContactDetails().getEmail())) {
            if (hasUser(user)) {
                updateFederatedUser(user);
            } else {
                preProvisionFederatedUser(user);
            }
        } else {
            if (hasUser(user)) {
                updatePrimaryUser(user);
            } else {
                createPrimaryUser(user);
            }
        }
    }

    @Override
    public void removeUser(User user) {
        logger.info("Removing user {} from Auth0", user.getUsername());
        com.auth0.json.mgmt.users.User existingAuth0User;
        try {
            existingAuth0User = getAuth0UserByEmail(user.getContactDetails().getEmail());
        } catch (OAuth2UserNotFoundException nfe) {
            logger.warn("Ignoring user removal for user {} that does not exist in the Auth0 tenant", user.getUsername());
            return;
        }

        // delete RoR metadata
        try {
            com.auth0.json.mgmt.users.User updatedAuth0User = new com.auth0.json.mgmt.users.User();
            updatedAuth0User.setAppMetadata(Map.of(ROR_ROLES, List.of()));
            auth0ManagementAPI.getManagementAPI().users().update(existingAuth0User.getId(), updatedAuth0User).execute();
            logger.info("RoR metadata for user {} successfully removed from Auth0", user.getUsername());
        } catch (Auth0Exception e) {
            logger.error("Failed to remove RoR metadata of user {} from Auth0", user.getUsername(), e);
            throw new OrganisationException("Failed to remove user metadata from Auth0");
        }

        // delete RoR roles.
        user.setResponsibilitySets(Set.of());
        updateRoles(user, existingAuth0User, getAllSystemRoles());

        // delete user only if it was created by RoR
        if (isCreatedByRoR(existingAuth0User)) {
            try {
                auth0ManagementAPI.getManagementAPI().users().delete(existingAuth0User.getId()).execute();
                logger.info("User {} successfully removed from Auth0", user.getUsername());
            } catch (Auth0Exception e) {
                logger.error("Failed to remove user {} from Auth0", user.getUsername(), e);
                throw new OrganisationException("Failed to remove user from Auth0");
            }
        }
    }

    @Override
    public void createRole(Role role) {
        logger.info("Creating role {} in Auth0: ", role.getId());
        com.auth0.json.mgmt.roles.Role auth0Role = toAuth0Role(role);
        try {
            com.auth0.json.mgmt.roles.Role createdRole = auth0ManagementAPI.getManagementAPI().roles().create(auth0Role).execute().getBody();
            logger.info("Role successfully created in Auth0: {}", createdRole.getId());
        } catch (Exception e) {
            logger.error("Failed to create role {}", role.getId(), e);
            throw new OrganisationException("Failed to create role");
        }
    }

    @Override
    public void removeRole(Role role) {
        logger.info("Removing role in Auth0: {}", role.getId());
        com.auth0.json.mgmt.roles.Role auth0Role = getAuth0RoleByPrivateCode(role.getPrivateCode());
        try {
            auth0ManagementAPI.getManagementAPI().roles().delete(auth0Role.getId()).execute();
            logger.info("Role {} successfully removed from Auth0", role.getId());
        } catch (Exception e) {
            logger.error("Failed to remove role {} from Auth0", role.getId(), e);
            throw new OrganisationException("Failed to remove role from Auth0");
        }
    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
        logger.info("Updating responsibility set {} in Auth0", responsibilitySet.getId());
        List<Role> systemRoles = getAllSystemRoles();
        try {
            userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> {
                com.auth0.json.mgmt.users.User auth0User = getAuth0UserByEmail(u.getContactDetails().getEmail());
                updateRoles(u, auth0User, systemRoles);
            });
        } catch (Exception e) {
            logger.info("Failed to update responsibility set {} in Auth0", responsibilitySet.getId(), e);
            throw new OrganisationException("Failed to update responsibility set in Auth0");
        }
    }

    /**
     * Return true if the Auth0 user account was created by RoR.
     * This excludes primary users that were created directly in Entur Partner and federated users.
     */
    private static boolean isCreatedByRoR(com.auth0.json.mgmt.users.User existingAuth0User) {
        return "true".equals(existingAuth0User.getAppMetadata().get(ROR_CREATED_BY_ROR));
    }

    /**
     * Return true if the email address belongs to a federated domain.
     */
    private boolean isFederated(String email) {
        Objects.requireNonNull(email);
        return permissionStoreService.isFederated(email);

    }

    /**
     * Return true if the user exists already in the Auth0 tenant, either as a primary user or a federated user.
     */
    private boolean hasUser(User user) {
        try {
            getAuth0UserByEmail(user.getContactDetails().getEmail());
            return true;
        } catch (OAuth2UserNotFoundException nfe) {
            return false;
        }
    }

    /**
     * Create a primary user in the local Auth0 user-password database.
     */
    private void createPrimaryUser(User user) {
        logger.info("Creating a primary user {} in Auth0", user.getUsername());
        com.auth0.json.mgmt.users.User auth0User = auth0UserMapper.mapToNewPrimaryAuth0User(user);
        com.auth0.json.mgmt.users.User createdUser = null;
        try {
            createdUser = auth0ManagementAPI.getManagementAPI().users().create(auth0User).execute().getBody();
            logger.info("Created user {} with Auth0 id {}", user.getUsername(), createdUser.getId());
            updateRoles(user, createdUser, getAllSystemRoles());
        } catch (Exception e) {
            logger.error("User creation failed for user {}", user, e);
            if (createdUser != null) {
                logger.info("Attempting to remove user {}", user);
                removeUser(user);
            }
            throw new OrganisationException("User creation failed");
        }
    }


    /**
     * Update a primary user in the local Auth0 user-password database.
     */
    private void updatePrimaryUser(User user) {
        logger.info("Updating primary user {} in Auth0", user.getUsername());
        com.auth0.json.mgmt.users.User existingAuth0User = getAuth0UserByEmail(user.getContactDetails().getEmail());
        com.auth0.json.mgmt.users.User updatedAuth0User = auth0UserMapper.mapToUpdatedPrimaryAuth0User(user, existingAuth0User);
        try {

            //  TODO update only metadata if user not created by RoR
            auth0ManagementAPI.getManagementAPI().users().update(existingAuth0User.getId(), updatedAuth0User).execute();
            updateRoles(user, existingAuth0User, getAllSystemRoles());
            logger.info("User {} successfully updated in Auth0", user.getUsername());

        } catch (Exception e) {
            logger.error("User update in Auth0 failed for user {}", user.getUsername(), e);
            throw new OrganisationException("User update in Auth0 failed");
        }
    }

    /**
     * Copy roles and metadata for the given federated user into the pre-provisioning database.
     * When the user logs in for the first time, Auth0 will automatically apply the roles and metadata to the user account.
     * Other user fields are ignored (email, name, ...)
     */
    private void preProvisionFederatedUser(User user) {
        logger.info("Creating a pre-provisioned federated user {} in Auth0", user.getUsername());
        com.auth0.json.mgmt.users.User auth0User = auth0UserMapper.mapToPreProvisionedFederatedAuth0User(user);
        com.auth0.json.mgmt.users.User createdUser = null;
        try {
            createdUser = auth0ManagementAPI.getManagementAPI().users().create(auth0User).execute().getBody();
            logger.info("Created user {} with Auth0 id {}", user.getUsername(), createdUser.getId());
            updateRoles(user, createdUser, getAllSystemRoles());
        } catch (Exception e) {
            logger.error("User creation failed for user {}", user, e);
            if (createdUser != null) {
                logger.info("Attempting to remove user {}", user);
                removeUser(user);
            }
            throw new OrganisationException("User creation failed");
        }

    }

    /**
     * Update roles and metadata for the given federated user.
     * Other fields are ignored (email, name, ...)
     */
    private void updateFederatedUser(User user) {
        logger.info("Updating federated user {} in Auth0", user.getUsername());
        com.auth0.json.mgmt.users.User existingAuth0User = getAuth0UserByEmail(user.getContactDetails().getEmail());
        com.auth0.json.mgmt.users.User updatedAuth0User = auth0UserMapper.mapToUpdatedFederatedAuth0User(user, existingAuth0User);
        try {
            auth0ManagementAPI.getManagementAPI().users().update(existingAuth0User.getId(), updatedAuth0User).execute();
            updateRoles(user, existingAuth0User, getAllSystemRoles());
            logger.info("User {} successfully updated in Auth0", user.getUsername());

        } catch (Exception e) {
            logger.error("User update in Auth0 failed for user {}", user.getUsername(), e);
            throw new OrganisationException("User update in Auth0 failed");
        }

    }

    /**
     * Return all roles defined in the role repository.
     */
    private List<Role> getAllSystemRoles() {
        return roleRepository.findAll();
    }


    /**
     * Update the user roles.
     * Only roles that are defined in the organisation repository are added or removed.
     * Roles that are assigned to the user in Auth0 but that are not defined in the organisation repository are ignored.
     *
     * @param user        the user in the organisation repository whose roles should be updated in Auth0.
     * @param auth0User   the user in Auth0.
     * @param systemRoles all the roles that are defined in the organisation repository.
     */
    private void updateRoles(User user, com.auth0.json.mgmt.users.User auth0User, List<Role> systemRoles) {
        logger.info("Updating roles for user {}", user.getUsername());
        try {
            // all the role names defined in the organisation repository
            Set<String> systemRoleNames = systemRoles.stream().map(Role::getId).collect(Collectors.toSet());
            // the role names assigned to the user in the organisation repository
            Set<String> newRoleNames = getRoleNames(user);
            // the Auth0 roles currently assigned to the Auth0 user
            List<com.auth0.json.mgmt.roles.Role> existingAuth0UserRoles = auth0ManagementAPI.getManagementAPI().users()
                    .listRoles(auth0User.getId(), null)
                    .execute()
                    .getBody()
                    .getItems();
            // Auth0 role ids that are currently assigned to the Auth0 user and that should be removed
            List<String> auth0RoleIdsToBeRemoved = existingAuth0UserRoles.stream().filter(r -> systemRoleNames.contains(r.getName()))
                    .filter(r -> !newRoleNames.remove(r.getName())).map(com.auth0.json.mgmt.roles.Role::getId).toList();

            if (!auth0RoleIdsToBeRemoved.isEmpty()) {
                auth0ManagementAPI.getManagementAPI().users().removeRoles(auth0User.getId(), auth0RoleIdsToBeRemoved).execute();
            }

            if (!newRoleNames.isEmpty()) {
                List<String> rolesToBeAdded = newRoleNames.stream().map(this::getAuth0RoleByPrivateCode).map(com.auth0.json.mgmt.roles.Role::getId).toList();
                auth0ManagementAPI.getManagementAPI().users().addRoles(auth0User.getId(), rolesToBeAdded).execute();
            }

        } catch (Auth0Exception e) {
            logger.error("Failed to update roles for user {}", user.getUsername(), e);
            throw new OrganisationException("Failed to update roles");
        }

    }

    private com.auth0.json.mgmt.roles.Role toAuth0Role(Role role) {
        com.auth0.json.mgmt.roles.Role auth0Role = new com.auth0.json.mgmt.roles.Role();
        auth0Role.setName(role.getId());
        auth0Role.setDescription(role.getName());
        return auth0Role;
    }

    private com.auth0.json.mgmt.users.User getAuth0UserByEmail(String email) {
        try {
            List<com.auth0.json.mgmt.users.User> matchingUsers = auth0ManagementAPI.getManagementAPI().users().list(new UserFilter().withQuery("email:\"" + email + "\"")).execute().getBody().getItems();
            if (matchingUsers.isEmpty()) {
                throw new OAuth2UserNotFoundException("User not found in Auth0: " + email);
            } else if (matchingUsers.size() > 1) {
                logger.error("More than one user found in Auth0 tenant with email: {}", email);
                throw new OrganisationException("More than one user found with email: " + email);
            }
            return matchingUsers.getFirst();
        } catch (Auth0Exception e) {
            logger.error("Failed to retrieve the user {} in Auth0", email, e);
            throw new OrganisationException("Failed to retrieve the user");
        }
    }

    private com.auth0.json.mgmt.roles.Role getAuth0RoleByPrivateCode(String privateCode) {
        try {
            // filtering twice by private code since the Auth0 filter accept all names that contain the filter.
            List<com.auth0.json.mgmt.roles.Role> matchingRoles = auth0ManagementAPI.getManagementAPI().roles()
                    .list(new RolesFilter().withName(privateCode))
                    .execute().getBody()
                    .getItems()
                    .stream().filter(r -> privateCode.equals(r.getName())).toList();
            if (matchingRoles.isEmpty()) {
                logger.warn("Role not found: {}", privateCode);
                throw new OrganisationException("Role not found: " + privateCode);
            } else if (matchingRoles.size() > 1) {
                logger.error("More than one role found with private code: {}", privateCode);
                throw new OrganisationException("More than one role found with private code:" + privateCode);
            }
            return matchingRoles.getFirst();
        } catch (Auth0Exception e) {
            logger.error("Failed to retrieve the role {} in Auth0", privateCode, e);
            throw new OrganisationException("Failed to retrieve role in Auth0");
        }
    }

    private Set<String> getRoleNames(User user) {
        Set<String> roleNames = new HashSet<>(defaultRoles);
        for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
            roleNames.addAll(responsibilitySet.getRoles().stream().map(r -> r.getTypeOfResponsibilityRole().getId()).collect(Collectors.toSet()));
        }
        return roleNames;
    }
}
