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
