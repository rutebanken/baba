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

package no.rutebanken.baba.organisation.repository;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.ContactDetails;
import no.rutebanken.baba.organisation.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;


public class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void testInsertUser() {

        User user = User.builder().withUsername("raffen").withPrivateCode("2").withOrganisation(defaultOrganisation).withContactDetails(minimalContactDetails()).build();
        User createdUser = userRepository.saveAndFlush(user);

        User fetchedUser = userRepository.getOne(createdUser.getPk());
        Assert.assertTrue(fetchedUser.getId().equals("User:2"));


    }

    protected ContactDetails minimalContactDetails() {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail("valid@email.org");
        return contactDetails;
    }


    @Test
    public void testFindByResponsibilitySet() {
        Role role = roleRepository.save(new Role("testCode", "testRole"));
        ResponsibilityRoleAssignment responsibilityRoleAssignment =
                ResponsibilityRoleAssignment.builder().withPrivateCode("pCode").withResponsibleOrganisation(defaultOrganisation)
                        .withTypeOfResponsibilityRole(role).withCodeSpace(defaultCodeSpace).build();
        ResponsibilitySet responsibilitySet = new ResponsibilitySet(defaultCodeSpace, "pCode", "name", Sets.newHashSet(responsibilityRoleAssignment));

        responsibilitySet = responsibilitySetRepository.save(responsibilitySet);

        User userWithRespSet =
                userRepository.saveAndFlush(User.builder()
                                                    .withUsername("userWithRespSet").withPrivateCode("userWithRespSet")
                                                    .withOrganisation(defaultOrganisation)
                                                    .withResponsibilitySets(Sets.newHashSet(responsibilitySet))
                                                    .withContactDetails(minimalContactDetails())
                                                    .build());
        User userWithoutRespSet = userRepository.saveAndFlush(User.builder().withUsername("userWithoutRespSet").withPrivateCode("userWithoutRespSet").withOrganisation(defaultOrganisation).withContactDetails(minimalContactDetails()).build());
        List<User> usersWithRespSet = userRepository.findUsersWithResponsibilitySet(responsibilitySet);

        Assert.assertEquals(1, usersWithRespSet.size());
        Assert.assertTrue(usersWithRespSet.contains(userWithRespSet));
        Assert.assertFalse(usersWithRespSet.contains(userWithoutRespSet));

        userRepository.delete(userWithRespSet);

        em.flush();
    }

}

