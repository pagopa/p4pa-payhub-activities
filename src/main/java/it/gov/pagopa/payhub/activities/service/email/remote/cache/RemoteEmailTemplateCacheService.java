package it.gov.pagopa.payhub.activities.service.email.remote.cache;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface RemoteEmailTemplateCacheService {
    void saveInCache(EmailTemplate template, String brokerExternalId, EmailTemplateName emailTemplateName);
    EmailTemplate getFromCache(String brokerExternalId, EmailTemplateName emailTemplateName);
}
