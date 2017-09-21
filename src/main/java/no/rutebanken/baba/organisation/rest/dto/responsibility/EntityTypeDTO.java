package no.rutebanken.baba.organisation.rest.dto.responsibility;

import io.swagger.annotations.ApiModel;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;

import java.util.ArrayList;
import java.util.List;
@ApiModel(description = "A type of entity or field")
public class EntityTypeDTO extends TypeDTO {

	public List<TypeDTO> classifications = new ArrayList<>();

}
