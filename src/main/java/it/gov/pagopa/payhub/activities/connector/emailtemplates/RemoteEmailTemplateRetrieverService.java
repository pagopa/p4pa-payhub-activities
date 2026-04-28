package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface RemoteEmailTemplateRetrieverService {
    EmailTemplate retrieve(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
