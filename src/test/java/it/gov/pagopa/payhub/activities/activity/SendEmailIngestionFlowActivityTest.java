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
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Optional;

/**
 * test class for send mail for ingestion activity
 */
@SpringBootTest(
  classes = {
    SendEmailIngestionFlowActivityImpl.class,
    EmailTemplatesConfiguration.class,
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
    private IngestionFlowFileDTO errorIngestionFlowFileDTO;

    private IngestionFlowFileDTO invalidIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidPathIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidDiscardedFileNameDTO;
    private UserInfoDTO validUserInfoDTO;
    private UserInfoDTO invalidUserInfoDTO;
    private OrganizationDTO validOrganizationInfoDTO;
    private OrganizationDTO invalidOrganizationInfoDTO;
    private MailTo expectedMailTo;

    @BeforeEach
    void init() {
        String blank = "";
        String host = "HOST";
        createBeans();
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        sendMailService  = new SendMailService(host, blank, blank,blank,blank,blank,blank, javaMailSender);
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkValidUserErrorFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(errorIngestionFlowFileDTO ));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoValidUserValidFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(errorIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoValidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserValidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoInvalidUserValidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowInvalidOrgFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(invalidOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowInvalidPathFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowInvalidPathFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    @Test
    void sendEmailIngestionOkInvalidUserInvalidDiscardedFileFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidDiscardedFileFailed() {
        Long ingestionFlowFileId = 100L;
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(organizationService.getOrganizationInfo(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    private void createBeans() {
        expectedMailTo = MailTo.builder()
                .emailFromAddress("test_sender@mailtest.com")
                .mailSubject("Subject")
                .to(new String[]{"test_receiver@mailtest.com"})
                .htmlText("Html Text")
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
                .mappedExternalUserId("VALID_USER")
                .filePathName("PATH_NAME")
                .fileName("FILE_NAME")
                .discardedFileName(null)
                .flowFileType("R")
                .numTotalRows(123L)
                .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .filePathName("PATH_NAME")
                .discardedFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        errorIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .flowFileType("R")
                .mappedExternalUserId("VALID_USER")
                .filePathName("PATH_NAME")
                .discardedFileName("DISCARDED_FILE")
                .numTotalRows(123L)
                .build();
        invalidPathIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .discardedFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        invalidDiscardedFileNameDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType("WRONG_FLOW")
                .filePathName("PATH_NAME")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
    }

}

