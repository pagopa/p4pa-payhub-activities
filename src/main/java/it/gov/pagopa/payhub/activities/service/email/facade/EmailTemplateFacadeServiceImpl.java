package it.gov.pagopa.payhub.activities.service.email.facade;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.email.cache.EmailTemplateCacheService;
import it.gov.pagopa.payhub.activities.service.email.retriever.EmailTemplateRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Lazy
@Service
@Slf4j
public class EmailTemplateFacadeServiceImpl implements EmailTemplateFacadeService {

    private final EmailTemplateCacheService emailTemplateCacheService;
    private final EmailTemplateRetrieverService emailTemplateRetrieverService;

    public EmailTemplateFacadeServiceImpl(EmailTemplateCacheService emailTemplateCacheService, EmailTemplateRetrieverService emailTemplateRetrieverService) {
        this.emailTemplateCacheService = emailTemplateCacheService;
        this.emailTemplateRetrieverService = emailTemplateRetrieverService;
    }

    @Override
    public EmailTemplate retrieveTemplate(String brokerExternalId, EmailTemplateName templateName, String emailSubject) {
        if(brokerExternalId == null || emailTemplateRetrieverService.isTemplateAlreadyNotFound(brokerExternalId, templateName)) {
            return null;
        }
        if(emailTemplateCacheService.isTemplateInCache(brokerExternalId, templateName)) {
            return emailTemplateCacheService.getFromCache(brokerExternalId, templateName);
        } else {
            EmailTemplate emailTemplate = emailTemplateRetrieverService.retrieve(brokerExternalId, templateName, emailSubject);
            if(emailTemplate != null) {
                emailTemplateCacheService.saveInCache(emailTemplate, brokerExternalId, templateName);
            }
            return emailTemplate;
        }
    }

}
