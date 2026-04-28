package it.gov.pagopa.payhub.activities.service.email.remote;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.email.remote.cache.RemoteEmailTemplateCacheService;
import it.gov.pagopa.payhub.activities.service.email.remote.retriever.RemoteEmailTemplateRetrieverService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoteEmailTemplateFacadeServiceImplTest {

    @Mock
    private RemoteEmailTemplateCacheService remoteEmailTemplateCacheServiceMock;
    @Mock
    private RemoteEmailTemplateRetrieverService remoteEmailTemplateRetrieverServiceMock;

    @InjectMocks
    private RemoteEmailTemplateFacadeServiceImpl emailTemplateFacadeService;

    @AfterEach
    void tear() {
        Mockito.verifyNoMoreInteractions(
                remoteEmailTemplateCacheServiceMock,
                remoteEmailTemplateRetrieverServiceMock
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
    void givenTemplateIsInCacheWhenFetchTemplateThenOk() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        EmailTemplate emailTemplateMock = Mockito.mock(EmailTemplate.class);
        Mockito.when(remoteEmailTemplateCacheServiceMock.getFromCache(brokerExternalId, templateName))
                .thenReturn(emailTemplateMock);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNotNull(actualeEmailTemplate);
        Assertions.assertEquals(emailTemplateMock, actualeEmailTemplate);
        Mockito.verify(remoteEmailTemplateRetrieverServiceMock, Mockito.times(0))
                .retrieve(brokerExternalId, templateName, emailSubject);
    }

    @Test
    void givenTemplateIsNotInAndTemplateIsFoundOnRepoCacheWhenFetchTemplateThenOk() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        EmailTemplate emailTemplateMock = Mockito.mock(EmailTemplate.class);
        Mockito.when(remoteEmailTemplateCacheServiceMock.getFromCache(brokerExternalId, templateName))
                .thenReturn(null);
        Mockito.when(remoteEmailTemplateRetrieverServiceMock.retrieve(brokerExternalId, templateName, emailSubject))
                .thenReturn(emailTemplateMock);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNotNull(actualeEmailTemplate);
        Assertions.assertEquals(emailTemplateMock, actualeEmailTemplate);
        Mockito.verify(remoteEmailTemplateCacheServiceMock)
                .saveInCache(emailTemplateMock, brokerExternalId, templateName);
    }

    @Test
    void givenTemplateIsNotInAndTemplateIsNotFoundOnRepoWhenFetchTemplateThenOk() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        EmailTemplate emailTemplateMock = Mockito.mock(EmailTemplate.class);
        Mockito.when(remoteEmailTemplateCacheServiceMock.getFromCache(brokerExternalId, templateName))
                .thenReturn(null);
        Mockito.when(remoteEmailTemplateRetrieverServiceMock.retrieve(brokerExternalId, templateName, emailSubject))
                .thenReturn(null);
        //WHEN
        EmailTemplate actualeEmailTemplate = emailTemplateFacadeService.fetchTemplate(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNull(actualeEmailTemplate);
        Mockito.verify(remoteEmailTemplateCacheServiceMock, Mockito.times(0))
                .saveInCache(emailTemplateMock, brokerExternalId, templateName);
    }

}