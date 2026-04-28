package it.gov.pagopa.payhub.activities.service.email.remote;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.email.remote.cache.RemoteEmailTemplateCacheService;
import it.gov.pagopa.payhub.activities.connector.emailtemplates.RemoteEmailTemplateRetrieverService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class RemoteEmailTemplateFacadeServiceImpl implements RemoteEmailTemplateFacadeService {

    private final RemoteEmailTemplateCacheService remoteEmailTemplateCacheService;
    private final RemoteEmailTemplateRetrieverService remoteEmailTemplateRetrieverService;

    public RemoteEmailTemplateFacadeServiceImpl(RemoteEmailTemplateCacheService remoteEmailTemplateCacheService, RemoteEmailTemplateRetrieverService remoteEmailTemplateRetrieverService) {
        this.remoteEmailTemplateCacheService = remoteEmailTemplateCacheService;
        this.remoteEmailTemplateRetrieverService = remoteEmailTemplateRetrieverService;
    }

    @Override
    public EmailTemplate fetchTemplate(String brokerExternalId, EmailTemplateName templateName, String emailSubject) {
        if(brokerExternalId == null) {
            return null;
        }
        EmailTemplate cachedTemplate = remoteEmailTemplateCacheService.getFromCache(brokerExternalId, templateName);
        if(cachedTemplate != null) {
            return cachedTemplate;
        } else {
            EmailTemplate emailTemplate = remoteEmailTemplateRetrieverService.retrieve(brokerExternalId, templateName, emailSubject);
            if(emailTemplate != null) {
                remoteEmailTemplateCacheService.saveInCache(emailTemplate, brokerExternalId, templateName);
            }
            return emailTemplate;
        }
    }

}
