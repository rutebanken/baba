package no.rutebanken.baba.organisation.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
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

import java.util.List;
import java.util.stream.Collectors;

import static no.rutebanken.baba.organisation.service.IamUtils.generatePassword;

@Service
@Profile("auth0")
public class Auth0IamService implements IamService {

    private static final String AUTH0_CONNECTION = "Username-Password-Authentication";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${iam.auth0.integration.enabled:true}")
    private boolean enabled;

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
        String password = generatePassword();
        if (!enabled) {
            logger.info("Auth0 disabled! Ignored createUser: " + user.getUsername());
            return password;
        }

        try {
            com.auth0.json.mgmt.users.User auth0User = toAuth0User(user);
            auth0User.setPassword(password.toCharArray());
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().create(auth0User);
            com.auth0.json.mgmt.users.User response = request.execute();
        } catch (Auth0Exception exception) {
            throw new OrganisationException("Failed to create user in Keycloak", 0);
        }

        try {
            updateRoles(user, roleRepository.findAll());
        } catch (Exception e) {
            logger.info("Password or role assignment failed for new Keycloak user. Attempting to remove user");
            removeUser(user);
            throw e;
        }

        logger.info("User successfully created in Auth0: " + user.getUsername());
        return password;
    }

    @Override
    public void updateUser(User user) {

        if (!enabled) {
            logger.info("Auth0 disabled! Ignored updateUser: " + user.getUsername());
            return;
        }

        com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(user.getUsername());

        try {
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().update(user.getUsername(), toAuth0User(user));
            com.auth0.json.mgmt.users.User response = request.execute();
        } catch (Auth0Exception exception) {
            throw new OrganisationException("Failed to create user in Keycloak", 0);
        }

        //updateRoles(user, systemRoles);

        logger.info("User successfully updated in Auth0: " + user.getUsername());

    }

    @Override
    public String resetPassword(User user) {
        String password = generatePassword();
        if (!enabled) {
            logger.info("Auth0 disabled! Ignored resetPassword: " + user.getUsername());
            return password;
        }
        com.auth0.json.mgmt.users.User auth0User = getAuth0UserByUsername(user.getUsername());
        auth0User.setPassword(password.toCharArray());
        try {
            getManagementAPI().users().update(user.getUsername(), auth0User).execute();
        } catch (Auth0Exception exception) {
            throw new OrganisationException("Failed to reset password in Auth0", 0);
        }
        return password;
    }

    @Override
    public void removeUser(User user) {

        if (!enabled) {
            logger.info("Auth0 disabled! Ignored removeUser: " + user.getUsername());
            return;
        }

        try {
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().delete(user.getUsername());
            com.auth0.json.mgmt.users.User response = request.execute();
        } catch (Auth0Exception exception) {
            throw new OrganisationException("Failed to remove user from Auth0", 0);
        }

        logger.info("User successfully removed from Auth0: " + user.getUsername());

    }

    @Override
    public void createRole(Role role) {

        if (!enabled) {
            logger.info("Auth0 disabled! Ignored createRole: " + role.getId());
            return;
        }

        try {
            getManagementAPI().roles().create(toAuth0Role(role));
        } catch (Exception e) {
            String msg = "Auth0 createRole failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

        logger.info("Role successfully created in Auth0: " + role.getId());

    }

    @Override
    public void removeRole(Role role) {

        if (!enabled) {
            logger.info("Keycloak disabled! Ignored removeRole: " + role.getId());
            return;
        }

        try {
            getManagementAPI().roles().delete(role.getId());
        } catch (Exception e) {
            String msg = "Auth0 removeRole failed: " + e.getMessage();
            logger.warn(msg, e);
            throw new OrganisationException(msg);
        }


    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {

    }

    private ManagementAPI getManagementAPI() throws Auth0Exception {
        AuthAPI authAPI = new AuthAPI(domain, clientId, clientSecret);
        AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        TokenHolder holder = authRequest.execute();
        return new ManagementAPI(domain, holder.getAccessToken());
    }

    private void updateRoles(User user, List<Role> roles) {

        try {
            Request<com.auth0.json.mgmt.users.User> request = getManagementAPI().users().addRoles(user.getUsername(), roles.stream().map(role -> role.getName()).collect(Collectors.toList()));
            com.auth0.json.mgmt.users.User response = request.execute();
        } catch (Auth0Exception exception) {
            throw new OrganisationException("Failed to update roles in Auth0", 0);
        }
    }

    private com.auth0.json.mgmt.users.User toAuth0User(User user) {

        com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User(AUTH0_CONNECTION);
        auth0User.setUsername(user.getUsername());


        if (user.getContactDetails() != null) {
            auth0User.setGivenName(user.getContactDetails().getFirstName());
            auth0User.setFamilyName(user.getContactDetails().getLastName());
            auth0User.setEmail(user.getContactDetails().getEmail());
        }


        /*if (user.getResponsibilitySets() != null) {
            Map<String, List<String>> attributes = new HashMap<>();
            List<String> roleAssignments = new ArrayList<>();

            for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
                if (responsibilitySet.getRoles() != null) {
                    responsibilitySet.getRoles().forEach(rra -> roleAssignments.add(toAtr(rra)));
                }
            }

            attributes.put("roles", roleAssignments);
            auth0User.set.setAttributes(attributes);
        }*/

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
            return getManagementAPI().users().get("auth0|" + username, null).execute();
        } catch (Auth0Exception e) {
            logger.warn("Exception while retrieving the user details", e);
            throw new OrganisationException("Failed to retrieve user in Auth0", 0);
        }
    }
}
