/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.service;

import no.rutebanken.baba.organisation.model.OrganisationException;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.RoleRepository;
import no.rutebanken.baba.organisation.repository.UserRepository;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
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
@Profile("keycloak")
public class KeycloakIamService implements IamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{'${iam.keycloak.default.roles:rutebanken}'.split(',')}")
    private List<String> defaultRoles;

    @Autowired
    private RealmResource iamRealm;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void createRole(Role role) {
        try {
            iamRealm.roles().create(toKeycloakRole(role));
        } catch (Exception e) {
            String msg = "Keycloak createRole failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

        logger.info("Role successfully created in Keycloak: " + role.getId());
    }

    @Override
    public void removeRole(Role role) {
        try {
            iamRealm.roles().get(role.getId()).remove();
            logger.info("Role successfully removed from Keycloak: " + role.getId());
        } catch (NotFoundException nfe) {
            logger.info("Ignoring removeRole for role not found in Keycloak: " + role.getId());
        } catch (Exception e) {
            String msg = "Keycloak removeRole failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

    }

    public String createUser(User user) {
        String password = generatePassword();
        Response rsp = iamRealm.users().create(toKeycloakUser(user));
        if (rsp.getStatus() >= 300) {
            String msg = "Failed to create user in Keycloak";
            if (rsp.getEntity() instanceof String) {
                msg += ": " + rsp.getEntity();
            }
            throw new OrganisationException(msg, rsp.getStatus());
        }
        try {
            resetPassword(user.getUsername(), password);
            updateRoles(user, roleRepository.findAll());
        } catch (Exception e) {
            logger.info("Password or role assignment failed for new Keycloak user. Attempting to remove user");
            removeUser(user);
            throw e;
        }

        logger.info("User successfully created in Keycloak: " + user.getUsername());
        return password;
    }

    public void updateUser(User user) {

        try {
            updateUser(user, roleRepository.findAll());
        } catch (Exception e) {
            String msg = "Keycloak updateUser failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public String resetPassword(User user) {
        String password = generatePassword();
        try {
            resetPassword(user.getUsername(), password);
        } catch (Exception e) {
            String msg = "Keycloak resetPassword failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

        return password;
    }

    private void updateUser(User user, List<Role> systemRoles) {
        UserResource iamUser = getUserResourceByUsername(user.getUsername());
        iamUser.update(toKeycloakUser(user));
        updateRoles(user, systemRoles);

        logger.info("User successfully updated in Keycloak: " + user.getUsername());
    }

    public void removeUser(User user) {
        try {
            UserResource iamUser = getUserResourceByUsername(user.getUsername());
            iamUser.remove();
            logger.info("User successfully removed from Keycloak: " + user.getUsername());
        } catch (NotFoundException nfe) {
            logger.info("Ignoring removeUser for user not found in Keycloak: " + user.getUsername());
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().startsWith("Username not found")) {
                logger.info("Ignoring removeUser for user not found in Keycloak: " + user.getUsername());
            } else {
                String msg = "Keycloak removeUser failed: " + e.getMessage();
                logger.info(msg, e);
                throw new OrganisationException(msg);
            }
        }
    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
        List<Role> systemRoles = roleRepository.findAll();

        try {
            userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> updateUser(u, systemRoles));
        } catch (Exception e) {
            String msg = "Keycloak updateResponsibilitySet failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

    }

    // Credentials may not be set when creating a user
    private void resetPassword(String username, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(Boolean.TRUE);
        getUserResourceByUsername(username).resetPassword(credential);
    }

    private Set<String> getRoleNames(User user) {
        Set<String> roleNames = new HashSet<>(defaultRoles);
        for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
            roleNames.addAll(responsibilitySet.getRoles().stream().map(r -> r.getTypeOfResponsibilityRole().getId()).collect(Collectors.toSet()));
        }
        return roleNames;
    }




    private void updateRoles(User user, List<Role> systemRoles) {
        Set<String> customRoleNames = systemRoles.stream().map(r -> r.getId()).collect(Collectors.toSet());

        List<RoleRepresentation> existingRoles = getUserResourceByUsername(user.getUsername()).roles().realmLevel().listEffective();

        Set<String> newRoleNames = getRoleNames(user);

        List<RoleRepresentation> removeRoles = existingRoles.stream().filter(r -> customRoleNames.contains(r.getName()))
                                                       .filter(r -> !newRoleNames.remove(r)).collect(Collectors.toList());

        if (!removeRoles.isEmpty()) {
            getUserResourceByUsername(user.getUsername()).roles().realmLevel().remove(removeRoles);
        }
        if (!newRoleNames.isEmpty()) {
            List<RoleRepresentation> newRole = newRoleNames.stream().map(rn -> iamRealm.roles().get(rn).toRepresentation()).collect(Collectors.toList());
            getUserResourceByUsername(user.getUsername()).roles().realmLevel().add(newRole);
        }

    }

    private UserResource getUserResourceByUsername(String username) {
        List<UserRepresentation> matchingUserRepresentations = iamRealm.users().search(username, null, null, null, 0, 50);

        List<UserRepresentation> userRepresentations = matchingUserRepresentations.stream().filter(ur -> username.equals(ur.getUsername())).collect(Collectors.toList());

        if (userRepresentations.isEmpty()) {
            throw new BadRequestException("Username not found in KeyCloak: " + username);
        } else if (userRepresentations.size() > 1) {
            throw new BadRequestException("Username not unique in KeyCloak: " + username);
        }
        return iamRealm.users().get(userRepresentations.get(0).getId());
    }

    RoleRepresentation toKeycloakRole(Role role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role.getId());
        return roleRepresentation;
    }

    UserRepresentation toKeycloakUser(User user) {
        UserRepresentation kcUser = new UserRepresentation();

        kcUser.setEnabled(true);
        kcUser.setUsername(user.getUsername());

        if (user.getContactDetails() != null) {
            kcUser.setFirstName(user.getContactDetails().getFirstName());
            kcUser.setLastName(user.getContactDetails().getLastName());
            kcUser.setEmail(user.getContactDetails().getEmail());
        }


        if (user.getResponsibilitySets() != null) {
            Map<String, List<String>> attributes = new HashMap<>();
            List<String> roleAssignments = new ArrayList<>();

            for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
                if (responsibilitySet.getRoles() != null) {
                    responsibilitySet.getRoles().forEach(rra -> roleAssignments.add(toAtr(rra)));
                }
            }

            attributes.put("roles", roleAssignments);
            kcUser.setAttributes(attributes);
        }


        return kcUser;
    }









}
