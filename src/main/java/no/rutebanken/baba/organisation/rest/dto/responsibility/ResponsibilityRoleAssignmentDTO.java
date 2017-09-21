package no.rutebanken.baba.organisation.rest.dto.responsibility;

import io.swagger.annotations.ApiModel;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;
@ApiModel(description = "Reusable authorization grant. Combination of a role with one or more of: organisations, admin zone and list of entity classification assignments. ")
public class ResponsibilityRoleAssignmentDTO extends BaseDTO {

	public String typeOfResponsibilityRoleRef;
	public String responsibleOrganisationRef;
	public String responsibleAreaRef;

	public List<EntityClassificationAssignmentDTO> entityClassificationAssignments = new ArrayList<>();

	public ResponsibilityRoleAssignmentDTO() {
	}

	public ResponsibilityRoleAssignmentDTO(String typeOfResponsibilityRoleRef, String responsibleOrganisationRef) {
		this.typeOfResponsibilityRoleRef = typeOfResponsibilityRoleRef;
		this.responsibleOrganisationRef = responsibleOrganisationRef;
	}
}
