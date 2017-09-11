package no.rutebanken.baba.organisation.repository;

import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.user.User;

import java.util.List;

public interface UserRepositoryCustom {

	List<User> findUsersWithResponsibilitySet(ResponsibilitySet responsibilitySet);
}
