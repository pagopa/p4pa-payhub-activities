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
    private UserInfoDTO invalidUserInfoDTO;
    private IngestionFlowFileDTO validIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidIngestionFlowFileDTO;

    @BeforeEach
    void init() {
        validUserInfoDTO = UserInfoDTO.builder()
            .mappedExternalUserId("VALID_USER")
            .build();
        invalidUserInfoDTO = UserInfoDTO.builder()
                .mappedExternalUserId(null)
                .build();
        validIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
            .userId(UserDTO.builder().externalUserId(validUserInfoDTO.getMappedExternalUserId()).build())
            .fileName("VALID_FILE_NAME")
            .flowType("REPORTING")
            .TotalRowsNumber(123L)
            .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .userId(UserDTO.builder().externalUserId(validUserInfoDTO.getMappedExternalUserId()).build())
                .fileName("VALID_FILE_NAME")
                .flowType("WRONG_FLOW")
                .TotalRowsNumber(123L)
                .build();
    }

    /**
     * send email for OK loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionOkValidUserValidFlowSuccess() throws MessagingException {
        boolean sendMailOK = true;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(validIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for KO loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionKoValidUserValidFlowSuccess() throws MessagingException {
        boolean sendMailOK = false;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(validIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for OK loading flow, success: invalid user and valid flow
     */
    @Test
    void sendEmailIngestionOkInvalidUserValidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(validIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: invalid user and valid flow
     */
    @Test
    void sendEmailIngestionKoInvalidUserValidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(validIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for OK loading flow, success: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkValidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(invalidIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoValidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(invalidIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for OK loading flow, failed: invalid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(invalidIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: invalid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        String ingestionFlowId = "100";
        Mockito.when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId))).thenReturn(invalidIngestionFlowFileDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, sendMailOK));
    }



}

