package no.rutebanken.baba.organisation.rest.dto.organisation;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZoneType;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;
import org.wololo.geojson.Polygon;
@ApiModel(description = "A named geographical area.")
public class AdministrativeZoneDTO extends BaseDTO {

    public String name;

    @ApiModelProperty(value = "Source of the data. Should be a code space styled value, ala KVE (Kartverket) or WOF (whosonfirst)")
    public String source;

    @ApiModelProperty(value = "Geojson polygon encoded value describing the surface of the admin zone")
    public Polygon polygon;

    public AdministrativeZoneType type;
}
