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

package no.rutebanken.baba.organisation.service;

import no.rutebanken.baba.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.baba.organisation.model.organisation.Authority;
import no.rutebanken.baba.organisation.model.organisation.Organisation;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassification;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassificationAssignment;
import no.rutebanken.baba.organisation.model.responsibility.EntityType;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.RoleAssignment;

import java.util.List;
import java.util.Set;

class IamUtilsTest {

    @Test
    void testMapResponsibilityRoleAssignmentToIamRoleAssignment() {
        ResponsibilityRoleAssignment orgRegRoleAssignment = new ResponsibilityRoleAssignment();
        Role role = new Role();
        role.setPrivateCode("testRole");
        Organisation organisation = new Authority();
        organisation.setPrivateCode("testOrg");

        EntityType entityType = new EntityType();
        entityType.setPrivateCode("StopPlace");

        EntityClassification entityClassification = new EntityClassification();
        entityClassification.setPrivateCode("*");
        entityClassification.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignment = new EntityClassificationAssignment(entityClassification, orgRegRoleAssignment, true);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignment);

        EntityClassification entityClassificationNegated = new EntityClassification();
        entityClassificationNegated.setPrivateCode("buss");
        entityClassificationNegated.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignmentNegated = new EntityClassificationAssignment(entityClassificationNegated, orgRegRoleAssignment, false);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignmentNegated);


        orgRegRoleAssignment.setTypeOfResponsibilityRole(role);
        orgRegRoleAssignment.setResponsibleOrganisation(organisation);

        AdministrativeZone administrativeZone = new AdministrativeZone();
        administrativeZone.setSource("KVE");
        administrativeZone.setPrivateCode("05");
        orgRegRoleAssignment.setResponsibleArea(administrativeZone);

        RoleAssignment iamRoleAssignment = IamUtils.toRoleAssignment(orgRegRoleAssignment);

        Assertions.assertEquals(role.getPrivateCode(), iamRoleAssignment.getRole());
        Assertions.assertEquals(organisation.getPrivateCode(), iamRoleAssignment.getOrganisation());


        Set<String> expectedCodes = Set.of(entityClassification.getPrivateCode(), "!" + entityClassificationNegated.getPrivateCode());
        List<String> classificationCodeList = iamRoleAssignment.getEntityClassifications().get(entityType.getPrivateCode());
        Assertions.assertEquals(expectedCodes,
                Set.copyOf(classificationCodeList));

        Assertions.assertEquals("KVE:TopographicPlace:05", iamRoleAssignment.getAdministrativeZone());
    }

    @Test
    void testGeneratePassword() {
        String password = IamUtils.generatePassword();
        Assertions.assertEquals(12, password.length());
        Assertions.assertNotEquals(password.toLowerCase(), password);
        Assertions.assertNotEquals(password.toUpperCase(), password);
    }


}
