package no.rutebanken.baba.organisation.rest;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.TestConstantsOrganisation;
import no.rutebanken.baba.organisation.model.user.NotificationType;
import no.rutebanken.baba.organisation.model.user.eventfilter.JobState;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.baba.organisation.rest.dto.user.NotificationConfigDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

public class NotificationConfigurationResourceTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/services/organisations/users";


    private String url(String userName) {
        return PATH + "/" + userName + "/" + "notification_configurations";
    }

    @Test
    public void userNotFound() throws Exception {
        ResponseEntity<String> entity = restTemplate.getForEntity(url("unknownUser"),
                String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

    }


    @Test
    public void crudNotificationConfig() throws Exception {

        String url = url(TestConstantsOrganisation.USER_USERNAME);

        Set<NotificationConfigDTO> config = Sets.newHashSet(new NotificationConfigDTO(NotificationType.EMAIL, true, crudEventFilter()),
                new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter()));
        restTemplate.put(url, config, String.class);
        assertConfig(config);

        Set<NotificationConfigDTO> updateConfig = Sets.newHashSet(new NotificationConfigDTO(NotificationType.EMAIL, true, jobEventFilter()));
        restTemplate.put(url, updateConfig);
        assertConfig(updateConfig);

        restTemplate.delete(url);

        ResponseEntity<NotificationConfigDTO[]> entity = restTemplate.getForEntity(url,
                NotificationConfigDTO[].class);
        Assert.assertEquals(0, entity.getBody().length);

    }

    private EventFilterDTO crudEventFilter() {
        EventFilterDTO eventFilterDTO = new EventFilterDTO(EventFilterDTO.EventFilterType.CRUD);
        eventFilterDTO.entityClassificationRefs = Sets.newHashSet(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID);

        eventFilterDTO.administrativeZoneRefs = new HashSet<>(ResourceTestUtils.addAdminZones(restTemplate, "z1", "z2"));
        return eventFilterDTO;
    }

    private EventFilterDTO jobEventFilter() {
        EventFilterDTO eventFilterDTO = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilterDTO.organisationRef = TestConstantsOrganisation.ORGANISATION_ID;
        eventFilterDTO.actions = Sets.newHashSet("VALIDATION_LEVEL_1");
        eventFilterDTO.jobDomain = EventFilterDTO.JobDomain.TIMETABLE;
        eventFilterDTO.states = Sets.newHashSet(JobState.FAILED);
        return eventFilterDTO;
    }


    protected void assertConfig(Set<NotificationConfigDTO> inConfig) {
        ResponseEntity<NotificationConfigDTO[]> rsp = restTemplate.getForEntity(url(TestConstantsOrganisation.USER_USERNAME), NotificationConfigDTO[].class);
        Set<NotificationConfigDTO> outConfig = Sets.newHashSet(rsp.getBody());


        if (CollectionUtils.isEmpty(inConfig)) {
            Assert.assertTrue(CollectionUtils.isEmpty(outConfig));
        } else {
            Assert.assertEquals(inConfig.size(), outConfig.size());
            for (NotificationConfigDTO in : inConfig) {
                Assert.assertTrue(outConfig.stream().anyMatch(out -> isEqual(in, out)));
            }
        }
    }


    private boolean isEqual(NotificationConfigDTO in, NotificationConfigDTO out) {
        return in.notificationType == out.notificationType && isEqual(in.eventFilter, out.eventFilter);
    }

    private boolean isEqual(EventFilterDTO in, EventFilterDTO out) {
        return in.equals(out);
    }

    @Test
    public void createInvalidNotificationConfig() throws Exception {
        Set<NotificationConfigDTO> inConfig = Sets.newHashSet(
                new NotificationConfigDTO(null, true, jobEventFilter()));
        restTemplate.put(PATH, inConfig);

    }

}
