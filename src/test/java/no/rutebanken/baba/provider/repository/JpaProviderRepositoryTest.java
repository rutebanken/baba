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


import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.provider.domain.ChouetteInfo;
import no.rutebanken.baba.provider.domain.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


class JpaProviderRepositoryTest extends BaseIntegrationTest {

    @Autowired
    ProviderRepository repository;

    @Test
    void testGetProviders() {
        Collection<Provider> providers = repository.getProviders();
        assertThat(providers).hasSize(3);
    }

    @Test
    void testGetProviderById() {
        Provider provider = repository.getProvider(42L);
        assertThat(provider).isEqualTo(new Provider(42L, "Flybussekspressen",
                                                           new ChouetteInfo(1L, "flybussekspressen", "flybussekspressen", "Rutebanken", "admin@rutebanken.org")));
    }

    @Test
    void testGetProviderByNullLongId() {
        Assertions.assertNull(repository.getProvider((Long) null));
    }

    @Test
    void testGetProviderByNullStringId() {
        Assertions.assertNull(repository.getProvider((String) null));
    }


    @Test
    void testCreateAndUpdateAndDeleteProvider() {

        ChouetteInfo chouetteInfo = new ChouetteInfo(null, "xmlns", "refe", "org", "user");
        Provider newProvider = new Provider(null, "junit provider", chouetteInfo);
        repository.createProvider(newProvider);

        Provider providerForUpdate = repository.getProvider(newProvider.id);

        repository.updateProvider(providerForUpdate);
        repository.deleteProvider(newProvider.id);

        Provider noProvider = repository.getProvider(newProvider.id);

        Assertions.assertNull(noProvider);
    }

}