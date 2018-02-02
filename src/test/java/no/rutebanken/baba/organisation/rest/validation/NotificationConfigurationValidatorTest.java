/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.rest.validation;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.model.user.NotificationType;
import no.rutebanken.baba.organisation.model.user.eventfilter.JobState;
import no.rutebanken.baba.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.baba.organisation.rest.dto.user.NotificationConfigDTO;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class NotificationConfigurationValidatorTest {

    private NotificationConfigurationValidator validator = new NotificationConfigurationValidator();

    @Test(expected = IllegalArgumentException.class)
    public void validateWithoutUserNameFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        validator.validate(null, config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateNotificationWithoutEventFilterFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().eventFilter = null;
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateNotificationWithoutNotificationTypeFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().notificationType = null;
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCrudFilterWithoutEntityClassificationsFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().eventFilter.entityClassificationRefs.clear();
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutJobDomainFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.jobDomain = null;
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithNullActionFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.actions = null;
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutActionFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.actions = new HashSet<>();
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithNullStatenFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.states = null;
        validator.validate("user", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutStatenFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.states = new HashSet<>();
        validator.validate("user", config);
    }


    protected Set<NotificationConfigDTO> withCrudFilter() {
        NotificationConfigDTO configDTO = new NotificationConfigDTO();
        configDTO.notificationType = NotificationType.EMAIL;

        EventFilterDTO eventFilter = new EventFilterDTO(EventFilterDTO.EventFilterType.CRUD);
        eventFilter.entityClassificationRefs = Sets.newHashSet("ref1");
        configDTO.eventFilter = eventFilter;

        return Sets.newHashSet(configDTO);
    }


    protected Set<NotificationConfigDTO> withJobFilter() {
        NotificationConfigDTO configDTO = new NotificationConfigDTO();
        configDTO.notificationType = NotificationType.EMAIL;

        EventFilterDTO eventFilter = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilter.actions = Sets.newHashSet("BUILD");
        eventFilter.jobDomain = EventFilterDTO.JobDomain.GEOCODER;
        eventFilter.states = Sets.newHashSet(JobState.FAILED);
        configDTO.eventFilter = eventFilter;

        return Sets.newHashSet(configDTO);
    }
}
