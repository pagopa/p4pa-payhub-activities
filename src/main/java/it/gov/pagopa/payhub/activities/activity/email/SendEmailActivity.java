package it.gov.pagopa.payhub.activities.activity.email;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;

/**
 * Sends an email.
 */
@ActivityInterface
public interface SendEmailActivity {
    @ActivityMethod
    void sendTemplatedEmail(TemplatedEmailDTO email);
    @ActivityMethod
    void sendEmail(EmailDTO email);
}
