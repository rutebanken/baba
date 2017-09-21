package no.rutebanken.baba.organisation.rest.dto.responsibility;

import io.swagger.annotations.ApiModel;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;
@ApiModel(description = "Set of responsibility role assignments describing a reusable set of user authorizations")
public class ResponsibilitySetDTO extends BaseDTO {

	public String name;

	public List<ResponsibilityRoleAssignmentDTO> roles = new ArrayList<>();
}
