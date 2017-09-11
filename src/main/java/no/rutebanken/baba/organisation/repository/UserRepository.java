package no.rutebanken.baba.organisation.repository;

import no.rutebanken.baba.organisation.model.user.User;


public interface UserRepository extends VersionedEntityRepository<User>, UserRepositoryCustom {

    User getUserByUsername(String username);
}
