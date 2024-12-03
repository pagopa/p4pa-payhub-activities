package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationServiceImpl;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;

/**
 * test class for send mail for ingestion activity
 */
@SpringBootTest(
  classes = {SendEmailIngestionFlowActivityImpl.class},
  webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
  "template.OK.body=mail body",
  "template.OK.subject=mail subject",
})
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    @Autowired
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;
    @MockBean
    private IngestionFlowRetrieverService ingestionFlowRetrieverService;
    @MockBean
    private SendMailService sendMailService ;
    @MockBean
    private UserAuthorizationServiceImpl userAuthorizationService;

    private UserInfoDTO validUserInfoDTO;
    private IngestionFlowFileDTO validIngestionFlowFileDTO;
    @BeforeEach
    void init() {
        validUserInfoDTO = UserInfoDTO.builder()
          .mappedExternalUserId("VALID_USER")
          .build();
        validIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
          .userId(UserDTO.builder().externalUserId(validUserInfoDTO.getMappedExternalUserId()).build())
          .fileName("VALID_FILE_NAME")
          .TotalRowsNumber(123L)
          .build();

    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() throws MessagingException {
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(validIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, true));
    }

//    /**
//     * test mail sending KO
//     */
//    @Test
//    void sendEmailIngestionError() {
//        String ingestionFlowId = "100";
//        boolean sent = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, false);
//        assertFalse(sent);
//    }
}

