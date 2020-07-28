package no.rutebanken.baba.chouette;

import no.rutebanken.baba.provider.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Service for managing the Chouette referential counterpart of a provider.
 */
@Component
public class ChouetteReferentialService {

    private static final Pattern SCHEMA_PATTERN = Pattern.compile("^(rb_)?[a-z]{3}$");

    @Autowired
    private ChouetteReferentialRestClient chouetteReferentialRestClient;

    public void createChouetteReferential(Provider provider) {
        ChouetteReferentialInfo referential = new ChouetteReferentialInfo(provider);

        String schemaName = referential.getSchemaName();
        if (!validateSchemaName(schemaName)) {
            throw new IllegalArgumentException("Invalid referential name: '" + schemaName + "'. Should be 3 lowercase letters optionally prefixed by 'rb_'");
        }
        if (referential.getDataspaceName() == null) {
            throw new IllegalArgumentException("Mandatory field missing: dataspace name");
        }
        if (referential.getOrganisationName() == null) {
            throw new IllegalArgumentException("Mandatory field missing: organisation name");
        }
        if (referential.getUserName() == null) {
            throw new IllegalArgumentException("Mandatory field missing: user name");
        }


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

    protected boolean validateSchemaName(final String schemaName) {
        return SCHEMA_PATTERN.matcher(schemaName).matches();
    }

}
