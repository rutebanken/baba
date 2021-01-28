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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.rutebanken.baba.organisation.service.IamUtils.generatePassword;
import static no.rutebanken.baba.organisation.service.IamUtils.toAtr;

@Service
@Profile("auth0")
public class Auth0IamService implements IamService {

    private static final String AUTH0_CONNECTION = "Username-Password-Authentication";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${iam.auth0.admin.domain}")
    private String domain;

    @Value("${iam.auth0.admin.client.id:baba}")
    private String clientId;

    @Value("${iam.auth0.admin.client.secret}")
    private String clientSecret;


    @Value("#{'${iam.auth0.default.roles:rutebanken}'.split(',')}")
    private List<String> defaultRoles;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public String createUser(User user) {
        logger.info("Creating user in Auth0: {} ", user.getUsername());
        String password = generatePassword();
        try {
            com.auth0.json.mgmt.users.User auth0User = toAuth0User(user);
            auth0User.setPassword(password.toCharArray());
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().create(auth0User);
            com.auth0.json.mgmt.users.User createdUser = request.execute();
            logger.info("Created user {} with Auth0 id {}", user.getUsername(), createdUser.getId());
        } catch (Auth0Exception e) {
            String msg = "Auth0 createUser failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }

        try {
            updateRoles(user, roleRepository.findAll());
        } catch (Exception e) {
            logger.error("Password or role assignment failed for new Auth0 user. Attempting to remove user");
            removeUser(user);
            throw e;
        }
        return password;
    }

    @Override
    public void updateUser(User user) {
        logger.info("Updating user in Auth0: {}", user.getUsername());
        try {
            com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(user.getUsername());
            getManagementAPI().users().update(auth0User.getUsername(), toAuth0User(user)).execute();
            logger.info("User successfully updated in Auth0: {}", user.getUsername());
        } catch (Auth0Exception e) {
            String msg = "Auth0 updateUser failed: " + e.getMessage();
            logger.error(msg, e);
            throw new OrganisationException(msg);
        }
        try {
            updateRoles(user, roleRepository.findAll());
        } catch (Exception e) {
            logger.error("Failed to update user roles", e);
            throw e;
        }
    }

    @Override
    public String resetPassword(User user) {
        logger.info("Resetting password in Auth0 for user: {}", user.getUsername());
        String password = generatePassword();
        try {
            com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(user.getUsername());
            auth0User.setPassword(password.toCharArray());
            getManagementAPI().users().update(user.getUsername(), auth0User).execute();
            logger.info("Succesfully reset password in Auth0 for user: {}", user.getUsername());
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
    }

    private ManagementAPI getManagementAPI() throws Auth0Exception {
        AuthAPI authAPI = new AuthAPI(domain, clientId, clientSecret);
        AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        TokenHolder holder = authRequest.execute();
        return new ManagementAPI(domain, holder.getAccessToken());
    }

    private void updateRoles(User user, List<Role> roles) {
        try {
            String auth0UserId = getAuth0UserByUsername(user.getUsername()).getId();
            List<String> auth0RoleIds = roles.stream()
                    .map(Role::getPrivateCode)
                    .map(this::getAuth0RoleByPrivateCode)
                    .map(com.auth0.json.mgmt.Role::getId)
                    .collect(Collectors.toList());
            getManagementAPI().users().addRoles(auth0UserId, auth0RoleIds).execute();
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
                throw new OrganisationException("User not found: " + username);
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
}
