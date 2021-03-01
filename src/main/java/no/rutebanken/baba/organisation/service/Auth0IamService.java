package no.rutebanken.baba.organisation.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.RolesFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;
import com.auth0.net.Request;
import no.rutebanken.baba.organisation.model.OrganisationException;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.RoleRepository;
import no.rutebanken.baba.organisation.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static no.rutebanken.baba.organisation.service.IamUtils.generatePassword;
import static no.rutebanken.baba.organisation.service.IamUtils.toAtr;

@Service
@Profile("auth0")
public class Auth0IamService implements IamService {

    private static final String AUTH0_CONNECTION = "Username-Password-Authentication";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{'${iam.auth0.default.roles:rutebanken}'.split(',')}")
    private List<String> defaultRoles;

    @Value("${iam.auth0.admin.domain}")
    private String domain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthAPI authAPI;

    private TokenHolder tokenHolder;
    private Instant accessTokenRetrievedAt;
    private ManagementAPI managementAPI;
    private Clock clock = Clock.systemUTC();

    @Override
    public String createUser(User user) {
        logger.info("Creating user in Auth0: {} ", user.getUsername());
        String password = generatePassword();
        com.auth0.json.mgmt.users.User createdUser = null;
        try {
            com.auth0.json.mgmt.users.User auth0User = toAuth0User(user);
            auth0User.setPassword(password.toCharArray());
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().create(auth0User);
            createdUser = request.execute();
            logger.info("Created user {} with Auth0 id {}", user.getUsername(), createdUser.getId());
        } catch (Auth0Exception e) {
            String msg = "Auth0 createUser failed";
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }

        try {
            updateRoles(user, createdUser, roleRepository.findAll());
        } catch (Exception e) {
            logger.error("Password or role assignment failed for new Auth0 user. Attempting to remove user: ", e);
            removeUser(user);
            throw new OrganisationException("Password or role assignment failed for new Auth0 user");
        }
        return password;
    }

    @Override
    public void updateUser(User user) {
        logger.info("Updating user in Auth0: {}", user.getUsername());
        try {
            com.auth0.json.mgmt.users.User existingAuth0User = getAuth0UserByUsername(user.getUsername());
            com.auth0.json.mgmt.users.User updatedAuth0User = toAuth0User(user);
            // The Auth0 API refuses to update both the username and the email at the same time
            updatedAuth0User.setUsername(null);
            getManagementAPI().users().update(existingAuth0User.getId(), updatedAuth0User).execute();
            updateRoles(user, existingAuth0User, roleRepository.findAll());
            logger.info("User successfully updated in Auth0: {}", user.getUsername());
        } catch (OAuth2UserNotFoundException e) {
            logger.info("User {} not found in Auth0 tenant. Creating a new user", user.getUsername());
            createUser(user);
            return;
        } catch (Auth0Exception e) {
            String msg = "Auth0 updateUser failed";
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public String resetPassword(User user) {
        logger.info("Resetting password in Auth0 for user: {}", user.getUsername());
        String password = generatePassword();
        try {
            com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User();
            auth0User.setPassword(password.toCharArray());
            getManagementAPI().users().update(getAuth0UserByUsername(user.getUsername()).getId(), auth0User).execute();
            logger.info("Successfully reset password in Auth0 for user: {}", user.getUsername());
        } catch (Auth0Exception e) {
            String msg = "Auth0 resetPassword failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
        return password;
    }

    @Override
    public void removeUser(User user) {
        logger.info("Removing user from Auth0: {}", user.getUsername());
        try {
            com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(user.getUsername());
            getManagementAPI().users().delete(auth0User.getId()).execute();
            logger.info("User successfully removed from Auth0: {}", user.getUsername());
        } catch (Auth0Exception e) {
            String msg = "Auth0 removeUser failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public void createRole(Role role) {
        logger.info("Creating role in Auth0: {}", role.getId());
        try {
            com.auth0.json.mgmt.Role createdRole = getManagementAPI().roles().create(toAuth0Role(role)).execute();
            logger.info("Role successfully created in Auth0: {}", createdRole.getId());
        } catch (Exception e) {
            String msg = "Auth0 createRole failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public void removeRole(Role role) {
        logger.info("Removing role in Auth0: {}", role.getId());
        try {
            com.auth0.json.mgmt.Role auth0Role = getAuth0RoleByPrivateCode(role.getPrivateCode());
            getManagementAPI().roles().delete(auth0Role.getId()).execute();
            logger.info("Role successfully removed from Auth0: {}", role.getId());
        } catch (Exception e) {
            String msg = "Auth0 removeRole failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
        logger.info("Updating responsibility sets in Auth0: {}", responsibilitySet.getId());
        List<Role> systemRoles = roleRepository.findAll();
        try {
            userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> {
                com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(u.getUsername());
                updateRoles(u, auth0User, systemRoles);
            });
        } catch (Exception e) {
            String msg = "Auth0 updateResponsibilitySet failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }
    }

    private synchronized ManagementAPI getManagementAPI() throws Auth0Exception {
        if (managementAPI == null) {
            refreshToken();
            managementAPI = new ManagementAPI(domain, tokenHolder.getAccessToken());
        }
        if (hasTokenExpired()) {
            refreshToken();
            managementAPI.setApiToken(tokenHolder.getAccessToken());
        }
        return managementAPI;
    }

    /**
     * The token is considered expired 60 seconds before its actual expiration date.
     *
     * @return true if the token is about to expire.
     */
    private boolean hasTokenExpired() {
        return clock.instant().isAfter(accessTokenRetrievedAt.plus(tokenHolder.getExpiresIn() - 60, ChronoUnit.SECONDS));
    }

    private void refreshToken() throws Auth0Exception {
        logger.debug("Refreshing Admin API token");
        AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        tokenHolder = authRequest.execute();
        accessTokenRetrievedAt = clock.instant();
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
        try {
            // all the role names defined in the organisation repository
            Set<String> systemRoleNames = systemRoles.stream().map(Role::getId).collect(Collectors.toSet());
            // the role names assigned to the user in the organisation repository
            Set<String> newRoleNames = getRoleNames(user);
            // the Auth0 roles currently assigned to the Auth0 user
            List<com.auth0.json.mgmt.Role> existingAuth0UserRoles = getManagementAPI().users()
                    .listRoles(auth0User.getId(), null)
                    .execute()
                    .getItems();
            // Auth0 role ids that are currently assigned to the Auth0 user and that should be removed
            List<String> auth0RoleIdsToBeRemoved = existingAuth0UserRoles.stream().filter(r -> systemRoleNames.contains(r.getName()))
                    .filter(r -> !newRoleNames.remove(r.getName())).map(com.auth0.json.mgmt.Role::getId).collect(Collectors.toList());

            if (!auth0RoleIdsToBeRemoved.isEmpty()) {
                getManagementAPI().users().removeRoles(auth0User.getId(), auth0RoleIdsToBeRemoved).execute();
            }

            if (!newRoleNames.isEmpty()) {
                List<String> rolesToBeAdded = newRoleNames.stream().map(this::getAuth0RoleByPrivateCode).map(com.auth0.json.mgmt.Role::getId).collect(Collectors.toList());
                getManagementAPI().users().addRoles(auth0User.getId(), rolesToBeAdded).execute();
            }

        } catch (Auth0Exception e) {
            String msg = "Auth0 updateRoles failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }

    }

    private com.auth0.json.mgmt.users.User toAuth0User(User user) {

        com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User(AUTH0_CONNECTION);
        auth0User.setUsername(user.getUsername());

        if (user.getContactDetails() != null) {
            auth0User.setGivenName(user.getContactDetails().getFirstName());
            auth0User.setFamilyName(user.getContactDetails().getLastName());
            auth0User.setEmail(user.getContactDetails().getEmail());
            auth0User.setName(auth0User.getGivenName() + ' ' + auth0User.getFamilyName());
        }

        if (user.getResponsibilitySets() != null) {
            Map<String, Object> attributes = new HashMap<>();
            List<String> roleAssignments = new ArrayList<>();

            for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
                if (responsibilitySet.getRoles() != null) {
                    responsibilitySet.getRoles().forEach(rra -> roleAssignments.add(toAtr(rra)));
                }
            }

            attributes.put("roles", roleAssignments);
            auth0User.setAppMetadata(attributes);
        }

        return auth0User;
    }

    private com.auth0.json.mgmt.Role toAuth0Role(Role role) {
        com.auth0.json.mgmt.Role auth0Role = new com.auth0.json.mgmt.Role();
        auth0Role.setName(role.getId());
        auth0Role.setDescription(role.getName());
        return auth0Role;
    }

    private com.auth0.json.mgmt.users.User getAuth0UserByUsername(String username) {
        try {
            List<com.auth0.json.mgmt.users.User> matchingUsers = getManagementAPI().users().list(new UserFilter().withQuery("username:\"" + username + "\"")).execute().getItems();
            if (matchingUsers.isEmpty()) {
                logger.warn("User not found: {}", username);
                throw new OAuth2UserNotFoundException("User not found: " + username);
            } else if (matchingUsers.size() > 1) {
                logger.error("More than one user found with username: {}", username);
                throw new OrganisationException("More than one user found with username: " + username);
            }
            return matchingUsers.get(0);
        } catch (Auth0Exception e) {
            String msg = "Auth0 getAuth0UserByUsername failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
    }

    private com.auth0.json.mgmt.Role getAuth0RoleByPrivateCode(String privateCode) {
        try {
            // filtering twice by private code since the Auth0 filter accept all names that contain the filter.
            List<com.auth0.json.mgmt.Role> matchingRoles = getManagementAPI().roles()
                    .list(new RolesFilter().withName(privateCode))
                    .execute()
                    .getItems()
                    .stream().filter(r -> privateCode.equals(r.getName())).collect(Collectors.toList());
            if (matchingRoles.isEmpty()) {
                logger.warn("Role not found: {}", privateCode);
                throw new OrganisationException("Role not found: " + privateCode);
            } else if (matchingRoles.size() > 1) {
                logger.error("More than one role found with username: {}", privateCode);
                throw new OrganisationException("More than one role found with private code:" + privateCode);
            }
            return matchingRoles.get(0);
        } catch (Auth0Exception e) {
            logger.error("Exception while retrieving the user details", e);
            throw new OrganisationException("Failed to retrieve user in Auth0");
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
