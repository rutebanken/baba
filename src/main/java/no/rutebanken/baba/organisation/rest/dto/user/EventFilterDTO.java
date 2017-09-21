package no.rutebanken.baba.organisation.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.rutebanken.baba.organisation.model.user.eventfilter.JobState;
import no.rutebanken.baba.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.baba.organisation.rest.dto.responsibility.EntityClassificationDTO;

import java.util.HashSet;
import java.util.Set;

@ApiModel(description = "Filter describing events of interest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventFilterDTO {

    public enum JobDomain {TIMETABLE, GEOCODER, GRAPH, TIAMAT}

    public enum EventFilterType {JOB, CRUD}


    public EventFilterType type;

    public String organisationRef;

    // TODO components/subclasses?

    // Job event filter values
    public JobDomain jobDomain;

    public Set<String> actions;

    public Set<JobState> states;

    // Crud event filter values
    public Set<String> administrativeZoneRefs;

    public Set<String> entityClassificationRefs;

    @ApiModelProperty("Fully mapped object for ease of use. Disregarded in CRUD operations (use reference)")
    public Set<EntityClassificationDTO> entityClassifications = new HashSet<>();


    @ApiModelProperty("Fully mapped object for ease of use. Disregarded in CRUD operations (use reference)")
    public OrganisationDTO organisation;


    public EventFilterDTO(EventFilterType type) {
        this.type = type;
    }

    public EventFilterDTO() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventFilterDTO that = (EventFilterDTO) o;

        if (type != that.type) return false;
        if (organisationRef != null ? !organisationRef.equals(that.organisationRef) : that.organisationRef != null)
            return false;
        if (jobDomain != that.jobDomain) return false;
        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (states != null ? !states.equals(that.states) : that.states != null) return false;
        if (administrativeZoneRefs != null ? !administrativeZoneRefs.equals(that.administrativeZoneRefs) : that.administrativeZoneRefs != null)
            return false;
        return entityClassificationRefs != null ? entityClassificationRefs.equals(that.entityClassificationRefs) : that.entityClassificationRefs == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (organisationRef != null ? organisationRef.hashCode() : 0);
        result = 31 * result + (jobDomain != null ? jobDomain.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (states != null ? states.hashCode() : 0);
        result = 31 * result + (administrativeZoneRefs != null ? administrativeZoneRefs.hashCode() : 0);
        result = 31 * result + (entityClassificationRefs != null ? entityClassificationRefs.hashCode() : 0);
        return result;
    }
}
