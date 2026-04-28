package it.gov.pagopa.payhub.activities.service.email.remote.retriever;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface RemoteEmailTemplateRetrieverService {
    EmailTemplate retrieve(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject);
}
