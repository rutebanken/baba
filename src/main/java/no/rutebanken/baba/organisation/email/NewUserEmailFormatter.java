package no.rutebanken.baba.organisation.email;

import no.rutebanken.baba.organisation.model.user.User;

import java.util.Locale;

public interface NewUserEmailFormatter {
    String getSubject(Locale locale);

    String formatMessage(User user, Locale locale);
}
