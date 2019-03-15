package no.rutebanken.baba.chouette;

import org.junit.Assert;
import org.junit.Test;

public class ReferentialServiceTest {

    @Test
    public void testValidateSchemaName() throws Exception {

        ChouetteReferentialService referentialService = new ChouetteReferentialService();
        Assert.assertTrue(referentialService.validateSchemaName("abc"));
        Assert.assertTrue(referentialService.validateSchemaName("rb_abc"));
        Assert.assertFalse(referentialService.validateSchemaName("abcd"));
        Assert.assertFalse(referentialService.validateSchemaName("rb_abcd"));
        Assert.assertFalse(referentialService.validateSchemaName("ab"));
        Assert.assertFalse(referentialService.validateSchemaName("rb_ab"));
        Assert.assertFalse(referentialService.validateSchemaName(""));
    }

}
