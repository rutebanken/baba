package no.rutebanken.baba.organisation.repository;

import no.rutebanken.baba.organisation.model.VersionedEntity;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;

import java.util.List;

public interface ResponsibilitySetRepositoryCustom {
    List<ResponsibilitySet> getResponsibilitySetsReferringTo(VersionedEntity entity);

}
