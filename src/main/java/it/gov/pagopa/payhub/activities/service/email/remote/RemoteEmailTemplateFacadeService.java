package it.gov.pagopa.payhub.activities.service.email.remote;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface RemoteEmailTemplateFacadeService {
    EmailTemplate fetchTemplate(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
