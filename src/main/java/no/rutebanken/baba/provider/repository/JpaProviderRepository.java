package no.rutebanken.baba.provider.repository;


import no.rutebanken.baba.provider.domain.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;


@Repository
@Transactional
public class JpaProviderRepository implements ProviderRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<Provider> getProviders() {
        return this.entityManager.createQuery("SELECT p FROM Provider p order by p.id", Provider.class).setHint("org.hibernate.cacheable", Boolean.TRUE).getResultList();
    }

    @Override
    public Provider getProvider(Long id) {
        return entityManager.find(Provider.class, id);
    }

	@Override
	public void updateProvider(Provider provider) {
		entityManager.merge(provider);
	}

	@Override
	public Provider createProvider(Provider provider) {
		 entityManager.persist(provider);
		 return provider;
	}

	@Override
	public void deleteProvider(Long providerId) {
		Provider provider = getProvider(providerId);
		if(provider != null) {
			entityManager.remove(provider);
		}
	}
}
