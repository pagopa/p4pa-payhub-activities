package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivity;
import it.gov.pagopa.payhub.activities.activity.utility.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
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

import java.util.Optional;

/**
 * test class for send mail for ingestion activity
 */
@SpringBootTest(
  classes = {
    SendEmailIngestionFlowActivityImpl.class,
    EmailTemplatesConfiguration.class
  },
  webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    @Autowired
    private SendEmailIngestionFlowActivity sendEmailIngestionFlowActivity;
    @MockBean
    private IngestionFlowFileDao ingestionFlowFileDao;
    @MockBean
    private SendMailService sendMailService;
    @MockBean
    private UserAuthorizationService userAuthorizationService;
    @MockBean
    private OrganizationService organizationService;

    private IngestionFlowFileDTO validIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidPathIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidDiscardedFileNameDTO;
    private UserInfoDTO validUserInfoDTO;
    private UserInfoDTO invalidUserInfoDTO;
    private OrganizationDTO validOrganizationInfoDTO;
    private OrganizationDTO invalidOrganizationInfoDTO;
    private MailTo expectedMailTo;
    private MailTo expectedMailToAttachPath;

    @BeforeEach
    void init() {
        createBeans();
    }

    /**
     * send email for OK loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionOkValidUserValidFlowSuccess() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    /**
     * send email for KO loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionKoValidUserValidFlowSuccess() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    /**
     * send email for OK loading flow, success: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkValidUserInvalidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    /**
     * send email for KO loading flow, failed: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoValidUserInvalidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserValidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoInvalidUserValidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowInvalidOrgFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(invalidOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowInvalidPathFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowInvalidPathFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    @Test
    void sendEmailIngestionOkInvalidUserInvalidDiscardedFileFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidDiscardedFileFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailTo);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    // with attachments
    /**
     * send email for OK loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionOkValidUserValidFlowAttachSuccess() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    /**
     * send email for KO loading flow, success: valid user and valid flow
     */
    @Test
    void sendEmailIngestionKoValidUserValidFlowAttachSuccess() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    /**
     * send email for OK loading flow, success: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionOkValidUserInvalidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    /**
     * send email for KO loading flow, failed: valid user and invalid flow
     */
    @Test
    void sendEmailIngestionKoValidUserInvalidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserValidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoInvalidUserValidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowInvalidOrgAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(invalidOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowInvalidPathAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowInvalidPathAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    @Test
    void sendEmailIngestionOkInvalidUserInvalidDiscardedFileAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidDiscardedFileAttachFailed() throws MessagingException {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.doNothing().when(sendMailService).sendMail(expectedMailToAttachPath);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    private void createBeans() {
        expectedMailTo = MailTo.builder()
                .emailFromAddress("test_sender@mailtest.com")
                .mailSubject("Subject")
                .to(new String[]{"test_receiver@mailtest.com"})
                .mailText("Mail Text")
                .htmlText("Html Text")
                .attachmentPath(null)
                .build();
        expectedMailToAttachPath = MailTo.builder()
                .emailFromAddress("test_sender@mailtest.com")
                .mailSubject("Subject")
                .to(new String[]{"test_receiver@mailtest.com"})
                .mailText("Mail Text")
                .htmlText("Html Text")
                .attachmentPath("/TMP")
                .build();
        validOrganizationInfoDTO = OrganizationDTO.builder()
                .ipaCode("IPA_CODE")
                .adminEmail("codIpaOrg@testuser.com")
                .build();
        invalidOrganizationInfoDTO = OrganizationDTO.builder()
                .applicationCode("")
                .build();
        validUserInfoDTO = UserInfoDTO.builder()
                .mappedExternalUserId("VALID_USER")
                .email("usertest@testuser.com")
                .build();
        invalidUserInfoDTO = UserInfoDTO.builder()
                .mappedExternalUserId(null)
                .email("usertest@testuser.com")
                .build();
        validIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .operatorName("VALID_USER")
                .filePathName("PATH_NAME")
                .fileName("FILE_NAME")
                .flowFileType("R")
                .numTotalRows(123L)
                .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .operatorName("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .filePathName("PATH_NAME")
                .discardedFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .operatorName("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .filePathName("PATH_NAME")
                .discardedFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        invalidPathIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .operatorName("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .discardedFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        invalidDiscardedFileNameDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .operatorName("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .filePathName("PATH_NAME")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();


    }


}

