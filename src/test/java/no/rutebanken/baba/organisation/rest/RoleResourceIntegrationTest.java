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

package no.rutebanken.baba.organisation.rest;

import no.rutebanken.baba.organisation.TestConstantsOrganisation;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Arrays;

public class RoleResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/services/organisations/roles";

    @Test
    public void roleNotFound() {
        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(PATH + "/unknownRoles",
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void crudRole() {
        TypeDTO createRole = createRole("role name", "privateCode");
        URI uri = restTemplate.postForLocation(PATH, createRole);
        ResourceTestUtils.assertType(createRole, uri, restTemplate);

        TypeDTO updateRole = createRole("new name", createRole.privateCode);
        restTemplate.put(uri, updateRole);
        ResourceTestUtils.assertType(updateRole, uri, restTemplate);


        TypeDTO[] allRoles =
                restTemplate.getForObject(PATH, TypeDTO[].class);
        assertRoleInArray(updateRole, allRoles);

        restTemplate.delete(uri);

        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(uri,
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void testDeleteRoleInUse() {
        ResponseEntity<String> response = restTemplate.exchange(PATH + "/" + TestConstantsOrganisation.ROLE_ID, HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private void assertRoleInArray(TypeDTO role, TypeDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(role.privateCode)));
    }

    protected TypeDTO createRole(String name, String privateCode) {
        TypeDTO role = new TypeDTO();
        role.name = name;
        role.privateCode = privateCode;
        return role;
    }

    @Test
    public void createInvalidRole() {
        TypeDTO inRole = createRole("role name", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inRole, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }

}
