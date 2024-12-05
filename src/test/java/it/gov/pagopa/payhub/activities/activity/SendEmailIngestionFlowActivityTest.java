package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.UserDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

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
    @MockBean
    private IngestionFlowFileDao ingestionFlowFileDao;
    @MockBean
    private SendMailService sendMailService;
    @MockBean
    private UserAuthorizationService userAuthorizationService;
    @MockBean
    private Environment environment;

    private UserInfoDTO validUserInfoDTO;
    private UserInfoDTO invalidUserInfoDTO;
    private IngestionFlowFileDTO validIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidIngestionFlowFileDTO;

    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;

    @BeforeEach
    void init() {
        createBeans();
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(environment, userAuthorizationService, ingestionFlowFileDao, sendMailService);
    }

    /**
     * send email for OK loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionOkValidUserValidFlowSuccess() throws MessagingException {
        boolean sendMailOK = true;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrgId().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for KO loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionKoValidUserValidFlowSuccess() throws MessagingException {
        boolean sendMailOK = false;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrgId().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for OK loading flow, success: invalid user and valid flow
     */
    @Test
    void sendEmailIngestionOkInvalidUserValidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        Long ingestionFlowFileId = 100L;

        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrgId().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: invalid user and valid flow
     */
    @Test
    void sendEmailIngestionKoInvalidUserValidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrgId().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for OK loading flow, success: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkValidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrgId().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoValidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrgId().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for OK loading flow, failed: invalid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = true;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrgId().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    /**
     * send email for KO loading flow, failed: invalid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowFailed() throws MessagingException {
        boolean sendMailOK = false;
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrgId().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(any(MailTo.class));

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, sendMailOK));
    }

    private void createBeans() {
        OrganizationDTO organizationDTO = OrganizationDTO.builder()
                .orgId(1L)
                .ipaCode("IPA_CODE").build();

        validUserInfoDTO = UserInfoDTO.builder()
                .mappedExternalUserId("VALID_USER")
                .email("usertest@testuser.com")
                .build();
        invalidUserInfoDTO = UserInfoDTO.builder()
                .mappedExternalUserId(null)
                .email("usertest@testuser.com")
                .build();
        validIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .userId(UserDTO.builder().externalUserId(validUserInfoDTO.getMappedExternalUserId()).build())
                .orgId(organizationDTO)
                .fileName("VALID_FILE_NAME")
                .flowType("REPORTING")
                .TotalRowsNumber(123L)
                .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .userId(UserDTO.builder().externalUserId(validUserInfoDTO.getMappedExternalUserId()).build())
                .orgId(organizationDTO)
                .fileName("VALID_FILE_NAME")
                .flowType("WRONG_FLOW")
                .TotalRowsNumber(123L)
                .build();
    }


}

