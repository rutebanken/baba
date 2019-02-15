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
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class JpaProviderRepositoryTest extends BaseIntegrationTest {

    @Autowired
    ProviderRepository repository;

    @Test
    public void testGetProviders() {
        Collection<Provider> providers = repository.getProviders();
        assertThat(providers).hasSize(3);
    }

    @Test
    public void testGetProviderById() {
        Provider provider = repository.getProvider(42L);
        assertThat(provider).isEqualTo(new Provider(42L, "Flybussekspressen", "42",
                                                           new ChouetteInfo(1L, "flybussekspressen", "http://www.ns.1", "flybussekspressen", "Rutebanken", "admin@rutebanken.org")));
    }


    @Test
    public void testCreateAndUpdateAndDeleteProvider() {

        ChouetteInfo chouetteInfo = new ChouetteInfo(null, "xmlns", "xmlnsurl", "refe", "org", "user");
        Provider newProvider = new Provider(null, "junit provider", "sftpAccount", chouetteInfo);
        repository.createProvider(newProvider);

        Provider providerForUpdate = repository.getProvider(newProvider.id);
        providerForUpdate.sftpAccount = "modified";

        repository.updateProvider(providerForUpdate);
        Provider providerForVerification = repository.getProvider(newProvider.id);

        Assert.assertEquals(providerForUpdate.sftpAccount, providerForVerification.sftpAccount);

        repository.deleteProvider(newProvider.id);

        Provider noProvider = repository.getProvider(newProvider.id);

        Assert.assertNull(noProvider);
    }

}