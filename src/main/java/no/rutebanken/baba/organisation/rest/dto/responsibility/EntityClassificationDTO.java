package no.rutebanken.baba.organisation.rest.dto.responsibility;

import io.swagger.annotations.ApiModel;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
@ApiModel(description = "Sub classification of an entity type")
public class EntityClassificationDTO extends TypeDTO {

    public EntityTypeDTO entityType;
}
