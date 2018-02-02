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

package no.rutebanken.baba.organisation.rest.validation;

import no.rutebanken.baba.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.baba.organisation.rest.dto.user.UserDTO;
import org.junit.Test;

public class UserValidatorTest {

    private UserValidator userValidator = new UserValidator();


    @Test
    public void validateCreateMinimalOk() {
        userValidator.validateCreate(minimalUser());
    }


    @Test
    public void validateCreateWithCapitalAndNumberAllowed() {
        UserDTO user = minimalUser();
        user.username = "userNo1";
        userValidator.validateCreate(user);
    }


    @Test
    public void validateCreateWithDotAllowed() {
        UserDTO user = minimalUser();
        user.username = "user.No1";
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithInvalidUsernameFails() {
        UserDTO user = minimalUser();
        user.username = "user 1";
        userValidator.validateCreate(user);
    }


    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutOrganisationFails() {
        UserDTO user = minimalUser();
        user.organisationRef = null;
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutContactDetailsFails() {
        UserDTO user = minimalUser();
        user.contactDetails = null;
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutEmailFails() {
        UserDTO user = minimalUser();
        user.contactDetails.email = null;
        userValidator.validateCreate(user);
    }

    @Test
    public void validateUpdateMinimalUserOK() {
        userValidator.validateUpdate(minimalUser(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidEmailFails() {
        UserDTO user = minimalUser();
        user.contactDetails = new ContactDetailsDTO("first", "last", "34234", "illegalEmail");
        userValidator.validateCreate(user);
    }

    @Test
    public void validateValidEmailOK() {
        UserDTO user = minimalUser();
        user.contactDetails = new ContactDetailsDTO("first", "last", "34234", "legal@email.com");
        userValidator.validateCreate(user);
    }

    protected UserDTO minimalUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.username = "username";
        userDTO.organisationRef = "organisation";
        userDTO.contactDetails = new ContactDetailsDTO(null, null, null, "valid@email.org");

        return userDTO;
    }

}
