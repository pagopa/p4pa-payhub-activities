package it.gov.pagopa.payhub.activities.service.email.facade;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.email.cache.EmailTemplateCacheService;
import it.gov.pagopa.payhub.activities.service.email.retriever.EmailTemplateRetrieverService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailTemplateFacadeServiceImplTest {

    @Mock
    private EmailTemplateCacheService emailTemplateCacheServiceMock;
    @Mock
    private EmailTemplateRetrieverService emailTemplateRetrieverServiceMock;

    @InjectMocks
    private EmailTemplateFacadeServiceImpl emailTemplateFacadeService;

    @AfterEach
    void tear() {
        Mockito.verifyNoMoreInteractions(
                emailTemplateCacheServiceMock,
                emailTemplateRetrieverServiceMock
        );
    }

    @Test
    void givenNullBrokerIdWhenFetchTemplateThenReturnNull() {
        //GIVEN
        String brokerExternalId = null;
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNull(actualeEmailTemplate);
    }

    @Test
    void givenTemplateAlreadyNotFoundWhenFetchTemplateThenReturnNull() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        Mockito.when(emailTemplateRetrieverServiceMock.isTemplateAlreadyNotFound(brokerExternalId, templateName))
                .thenReturn(true);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNull(actualeEmailTemplate);
    }

    @Test
    void givenTemplateIsInCacheWhenFetchTemplateThenOk() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        Mockito.when(emailTemplateRetrieverServiceMock.isTemplateAlreadyNotFound(brokerExternalId, templateName))
                .thenReturn(false);
        EmailTemplate emailTemplateMock = Mockito.mock(EmailTemplate.class);
        Mockito.when(emailTemplateCacheServiceMock.isTemplateInCache(brokerExternalId, templateName))
                .thenReturn(true);
        Mockito.when(emailTemplateCacheServiceMock.getFromCache(brokerExternalId, templateName))
                .thenReturn(emailTemplateMock);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNotNull(actualeEmailTemplate);
        Assertions.assertEquals(emailTemplateMock, actualeEmailTemplate);
    }

    @Test
    void givenTemplateIsNotInCacheWhenFetchTemplateThenOk() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        Mockito.when(emailTemplateRetrieverServiceMock.isTemplateAlreadyNotFound(brokerExternalId, templateName))
                .thenReturn(false);
        EmailTemplate emailTemplateMock = Mockito.mock(EmailTemplate.class);
        Mockito.when(emailTemplateCacheServiceMock.isTemplateInCache(brokerExternalId, templateName))
                .thenReturn(false);
        Mockito.when(emailTemplateRetrieverServiceMock.retrieve(brokerExternalId, templateName, emailSubject))
                .thenReturn(emailTemplateMock);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNotNull(actualeEmailTemplate);
        Assertions.assertEquals(emailTemplateMock, actualeEmailTemplate);
        Mockito.verify(emailTemplateCacheServiceMock).saveInCache(emailTemplateMock, brokerExternalId, templateName);
    }

}