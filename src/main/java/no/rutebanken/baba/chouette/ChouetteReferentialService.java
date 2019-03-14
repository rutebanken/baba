package no.rutebanken.baba.chouette;

import no.rutebanken.baba.provider.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service for managing the Chouette referential counterpart of a provider.
 */
@Component
public class ChouetteReferentialService {


    @Autowired
    ChouetteReferentialRestClient chouetteReferentialRestClient;

    public void createChouetteReferential(Provider provider) {
        ChouetteReferentialInfo referential = new ChouetteReferentialInfo(provider);
        chouetteReferentialRestClient.createReferential(referential);
    }

    public void updateChouetteReferential(Provider provider) {

        ChouetteReferentialInfo referential = new ChouetteReferentialInfo(provider);
        chouetteReferentialRestClient.updateReferential(referential);
    }

    public void deleteChouetteReferential(Provider provider) {
        ChouetteReferentialInfo referential = new ChouetteReferentialInfo(provider);
        chouetteReferentialRestClient.deleteReferential(referential);
    }

}
