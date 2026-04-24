package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface EmailTemplateRetrieverService {
    EmailTemplate retrieveTemplate(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
