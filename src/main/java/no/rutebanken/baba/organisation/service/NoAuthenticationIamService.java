package no.rutebanken.baba.organisation.service;

import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static no.rutebanken.baba.organisation.service.IamUtils.generatePassword;

@Service
@Profile("test")
public class NoAuthenticationIamService implements IamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String createUser(User user) {
        logger.info("Authentication disabled! Ignored createUser: {}", user.getUsername());
        return generatePassword();
    }

    @Override
    public void updateUser(User user) {
        logger.info("Authentication disabled! Ignored updateUser: {}", user.getUsername());
    }

    @Override
    public String resetPassword(User user) {
        logger.info("Authentication disabled! Ignored resetPassword: {}", user.getUsername());
        return generatePassword();
    }

    @Override
    public void removeUser(User user) {
        logger.info("Authentication disabled! Ignored removeUser: {}", user.getUsername());
    }

    @Override
    public void createRole(Role role) {
        logger.info("Authentication disabled! Ignored createRole: {}", role.getId());
    }

    @Override
    public void removeRole(Role role) {
        logger.info("Authentication disabled! Ignored removeRole: {}", role.getId());
    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
        logger.info("Authentication disabled! Ignored updateResponsibilitySet: {}", responsibilitySet.getName());
    }

}
