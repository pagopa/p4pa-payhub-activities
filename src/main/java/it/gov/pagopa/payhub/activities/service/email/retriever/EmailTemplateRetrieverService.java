package it.gov.pagopa.payhub.activities.service.email.retriever;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface EmailTemplateRetrieverService {
    boolean isTemplateAlreadyNotFound(String brokerExternalId, EmailTemplateName emailTemplateName);
    EmailTemplate retrieve(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
