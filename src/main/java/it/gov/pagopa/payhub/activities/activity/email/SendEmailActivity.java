package it.gov.pagopa.payhub.activities.activity.email;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;

/**
 * Sends an email.
 */
@ActivityInterface
public interface SendEmailActivity {
    @ActivityMethod
    void send(EmailDTO email);
}
