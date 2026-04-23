package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailTemplateResolverService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {

    public static final String MAIL_SENDER_ADDRESS = "sender@email.com";

    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileEmailTemplateResolverService emailTemplateResolverServiceMock;
    @Mock
    private IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverServiceMock;
    @Mock
    private IngestionFlowFileEmailContentConfigurerService contentConfigurerServiceMock;
    @Mock
    private SendEmailActivity sendEmailActivityMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private BrokerService brokerServiceMock;

    private SendEmailIngestionFlowActivity activity;

    @BeforeEach
    void init() {
        activity = new SendEmailIngestionFlowActivityImpl(
                ingestionFlowFileServiceMock,
                emailTemplateResolverServiceMock,
                destinationRetrieverServiceMock,
                contentConfigurerServiceMock,
                sendEmailActivityMock,
                organizationServiceMock,
                brokerServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                emailTemplateResolverServiceMock,
                destinationRetrieverServiceMock,
                contentConfigurerServiceMock,
                sendEmailActivityMock,
                organizationServiceMock
        );
    }

    @Test
    void givenNotIngestionFlowFileRecordWhenSendIngestionFlowFileCompleteEmailThenIngestionFlowFileNotFoundException() {
        // Given
        long ingestionFlowFileId = 1L;
        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> activity.sendIngestionFlowFileCompleteEmail(ingestionFlowFileId, true));
    }

    @Test
    void givenCompleteConfigurationWhenSendIngestionFlowFileCompleteEmailThenOk() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        boolean success = true;
        EmailTemplateName templateName = EmailTemplateName.INGESTION_PAYMENTS_REPORTING_OK;
        Map<String, String> params = Map.of();
        String[] to = new String[0];
        String[] cc = new String[0];

        Organization organization = new Organization();
        organization.setBrokerId(1L);

        TemplatedEmailDTO expectedTemplatedEmail = new TemplatedEmailDTO();
        expectedTemplatedEmail.setTo(to);
        expectedTemplatedEmail.setCc(cc);
        expectedTemplatedEmail.setTemplateName(templateName);
        expectedTemplatedEmail.setParams(params);
        expectedTemplatedEmail.setFrom(MAIL_SENDER_ADDRESS);

        ArgumentCaptor<TemplatedEmailDTO> templatedEmailDTOArgumentCaptor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

        BrokerConfiguration brokerConfiguration = mock(BrokerConfiguration.class);
        Mockito.when(brokerConfiguration.getMailSenderAddress()).thenReturn(MAIL_SENDER_ADDRESS);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileDTO.getIngestionFlowFileId()))
                .thenReturn(Optional.of(ingestionFlowFileDTO));
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFileDTO.getOrganizationId()))
                        .thenReturn(Optional.of(organization));
        Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L))
                .thenReturn(brokerConfiguration);
        Mockito.when(emailTemplateResolverServiceMock.resolve(Mockito.same(ingestionFlowFileDTO), Mockito.same(success)))
                        .thenReturn(templateName);
        Mockito.when(contentConfigurerServiceMock.configureParams(ingestionFlowFileDTO, success))
                .thenReturn(params);
        Mockito.when(destinationRetrieverServiceMock.retrieveEmailDestinations(ingestionFlowFileDTO))
                .thenReturn(Pair.of(to, cc));

        // When
        activity.sendIngestionFlowFileCompleteEmail(ingestionFlowFileDTO.getIngestionFlowFileId(), success);

        // Then
        Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(organization.getBrokerId()), templatedEmailDTOArgumentCaptor.capture());

        TemplatedEmailDTO actualTemplatedEmailDTO = templatedEmailDTOArgumentCaptor.getValue();

        Assertions.assertArrayEquals(expectedTemplatedEmail.getTo(), actualTemplatedEmailDTO.getTo());
        Assertions.assertArrayEquals(expectedTemplatedEmail.getCc(), actualTemplatedEmailDTO.getCc());
        Assertions.assertEquals(MAIL_SENDER_ADDRESS, actualTemplatedEmailDTO.getFrom());
        Assertions.assertEquals(EmailTemplateName.INGESTION_PAYMENTS_REPORTING_OK, actualTemplatedEmailDTO.getTemplateName());
        Assertions.assertEquals(expectedTemplatedEmail.getParams(), actualTemplatedEmailDTO.getParams());
    }

}

