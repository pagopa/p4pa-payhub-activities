package it.gov.pagopa.payhub.activities.service.email.cache;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

public interface EmailTemplateCacheService {
    void saveInCache(EmailTemplate template, String brokerExternalId, EmailTemplateName emailTemplateName);
    EmailTemplate getFromCache(String brokerExternalId, EmailTemplateName emailTemplateName);
    boolean isTemplateInCache(String brokerExternalId, EmailTemplateName emailTemplateName);
}
