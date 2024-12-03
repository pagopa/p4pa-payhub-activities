package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;
    private IngestionFlowRetrieverService ingestionFlowRetrieverService;
    @Mock
    private SendMailService sendMailService ;
    @Mock
    private UserAuthorizationService userAuthorizationService;

    @BeforeEach
    void init() {
        sendEmailIngestionFlowActivity = mock(SendEmailIngestionFlowActivityImpl.class);
        ingestionFlowRetrieverService = mock(IngestionFlowRetrieverService.class);

        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(
                userAuthorizationService, ingestionFlowRetrieverService, sendMailService);
    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        String ingestionFlowId = "100";

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFilePathName("/valid/path");
        ingestionFlowFileDTO.setFileName("valid-file.zip");
        ingestionFlowFileDTO.setRequestTokenCode("valid-token");

        when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
                .thenReturn(ingestionFlowFileDTO);

        assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, true));
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        String ingestionFlowId = "100";
        assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, false));
    }

}