package no.rutebanken.baba.chouette;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReferentialServiceTest {

    @Test
    void testValidateSchemaName() {

        ChouetteReferentialService referentialService = new ChouetteReferentialService(null);
        Assertions.assertTrue(referentialService.validateSchemaName("abc"));
        Assertions.assertTrue(referentialService.validateSchemaName("rb_abc"));
        Assertions.assertFalse(referentialService.validateSchemaName("abcd"));
        Assertions.assertFalse(referentialService.validateSchemaName("rb_abcd"));
        Assertions.assertFalse(referentialService.validateSchemaName("ab"));
        Assertions.assertFalse(referentialService.validateSchemaName("rb_ab"));
        Assertions.assertFalse(referentialService.validateSchemaName(""));
    }

}
