package no.rutebanken.baba.organisation.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Common fields for entities")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO {

    @ApiModelProperty("Unique id for the entity")
    public String id;

    @ApiModelProperty("The code space the entity belongs to")
    public String codeSpace;

    @ApiModelProperty("Private code for the entity. Unique for type within code space")
    public String privateCode;
}
