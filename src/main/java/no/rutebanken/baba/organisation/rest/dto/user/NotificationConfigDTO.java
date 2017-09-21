package no.rutebanken.baba.organisation.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import no.rutebanken.baba.organisation.model.user.NotificationType;
@ApiModel(description = "Configuration of a single type of notifications for a user. Combines an event filter describing which " +
                          "events should be notified with a notification type describing how they should be notified")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationConfigDTO {

    public NotificationType notificationType;

    public EventFilterDTO eventFilter;

    public boolean enabled = true;

    public NotificationConfigDTO(NotificationType notificationType, boolean enabled, EventFilterDTO eventFilter) {
        this.notificationType = notificationType;
        this.enabled = enabled;
        this.eventFilter = eventFilter;
    }

    public NotificationConfigDTO() {
    }
}
