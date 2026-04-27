package it.gov.pagopa.payhub.activities.service.email.facade;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface EmailTemplateFacadeService {
    EmailTemplate retrieveTemplate(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
