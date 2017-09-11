package no.rutebanken.baba.organisation.rest.dto.organisation;


import no.rutebanken.baba.organisation.model.organisation.AdministrativeZoneType;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;
import org.wololo.geojson.Polygon;

public class AdministrativeZoneDTO extends BaseDTO {

	public String name;

	public String source;

	public Polygon polygon;

	public AdministrativeZoneType type;
}
