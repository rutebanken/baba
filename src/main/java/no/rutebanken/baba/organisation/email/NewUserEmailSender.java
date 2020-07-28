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

package no.rutebanken.baba.organisation.email;

import no.rutebanken.baba.organisation.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NewUserEmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewUserEmailSender.class);
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NewUserEmailFormatter newUserEmailFormatter;

    @Value("${new.user.email.from:noreply@entur.org}")
    private String emailFrom;

    @Value("${new.user.email.language.default:no}")
    private String emailLanguageDefault;


    @Value("${new.user.email.enabled:true}")
    private boolean sendEmailEnabled;

    public void sendEmail(User user) {
        Locale locale = new Locale(emailLanguageDefault); // TODO get users default from user
        sendEmail(user.getContactDetails().getEmail(), newUserEmailFormatter.getSubject(locale), newUserEmailFormatter.formatMessage(user, locale));
    }


    protected void sendEmail(String to, String subject, String msg) {
        if (sendEmailEnabled) {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText(msg, true);
                helper.setSubject(subject);
                helper.setTo(to);
                helper.setFrom(emailFrom);
            });
            LOGGER.info("Sent email with account information to: {}", to);
        } else {
            LOGGER.info("Not sending email to new user: {} as this has been disabled", to);
        }
    }

}
