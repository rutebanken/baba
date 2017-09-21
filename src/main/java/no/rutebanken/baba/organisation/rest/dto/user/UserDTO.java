package no.rutebanken.baba.organisation.rest.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;
import no.rutebanken.baba.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.baba.organisation.rest.dto.responsibility.ResponsibilitySetDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiModel(description = "User")
public class UserDTO extends BaseDTO {

    public String username;

    @ApiModelProperty("Id of the organisation the user belongs to")
    public String organisationRef;

    @ApiModelProperty("References to the set of responsibility sets describing the users authorizations")
    public List<String> responsibilitySetRefs = new ArrayList<>();


    public ContactDetailsDTO contactDetails;

    @ApiModelProperty("Fully mapped object for ease of use. Disregarded in CRUD operations (use reference)")
    public Set<NotificationConfigDTO> notifications = new HashSet<>();
    @ApiModelProperty("Fully mapped object for ease of use. Disregarded in CRUD operations (use reference)")
    public OrganisationDTO organisation;
    @ApiModelProperty("Fully mapped object for ease of use. Disregarded in CRUD operations (use reference)")
    public List<ResponsibilitySetDTO> responsibilitySets;
}
