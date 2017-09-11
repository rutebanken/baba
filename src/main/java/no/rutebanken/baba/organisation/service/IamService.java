package no.rutebanken.baba.organisation.service;

import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.User;

public interface IamService {

	String createUser(User user);

	void updateUser(User user);

	String resetPassword(User user);

	void removeUser(User user);

	void createRole(Role role);

	void removeRole(Role role);

	void updateResponsibilitySet(ResponsibilitySet responsibilitySet);
}
