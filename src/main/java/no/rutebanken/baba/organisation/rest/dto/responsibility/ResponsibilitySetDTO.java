package no.rutebanken.baba.organisation.rest.dto.responsibility;

import no.rutebanken.baba.organisation.rest.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class ResponsibilitySetDTO extends BaseDTO {

	public String name;

	public List<ResponsibilityRoleAssignmentDTO> roles = new ArrayList<>();
}
