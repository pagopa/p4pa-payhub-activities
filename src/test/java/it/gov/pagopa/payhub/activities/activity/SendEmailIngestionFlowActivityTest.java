package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import it.gov.pagopa.pu.p4paauth.model.generated.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @MockBean
    JavaMailSenderImpl javaMailSender;

    private IngestionFlowFileDTO validIngestionFlowFileDTO;
    private IngestionFlowFileDTO errorIngestionFlowFileDTO;

    private IngestionFlowFileDTO invalidIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidPathIngestionFlowFileDTO;
    private IngestionFlowFileDTO invalidDiscardedFileNameDTO;
    private UserInfo validUserInfoDTO;
    private UserInfo invalidUserInfoDTO;
    private OrganizationDTO validOrganizationInfoDTO;
    private OrganizationDTO invalidOrganizationInfoDTO;

    @BeforeEach
    void init() {
        createTestData();
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkValidUserErrorFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(errorIngestionFlowFileDTO ));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoValidUserValidFlowSuccess() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(errorIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoValidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserValidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionKoInvalidUserValidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionOkValidUserValidFlowInvalidOrgFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Mockito.when(organizationService.getOrganizationByIpaCode(invalidOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);

        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void sendEmailIngestionOkInvalidUserInvalidFlowInvalidPathFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidFlowInvalidPathFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidPathIngestionFlowFileDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }


    @Test
    void sendEmailIngestionOkInvalidUserInvalidDiscardedFileFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(validOrganizationInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @Test
    void sendEmailIngestionKoInvalidUserInvalidDiscardedFileFailed() {
        Long ingestionFlowFileId = 100L;
        createMailServiceData();
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(invalidDiscardedFileNameDTO));
        Mockito.when(organizationService.getOrganizationByIpaCode(validOrganizationInfoDTO.getIpaCode())).thenReturn(invalidOrganizationInfoDTO);
        Mockito.when(userAuthorizationService.getUserInfo(invalidIngestionFlowFileDTO.getOrg().getIpaCode(), invalidUserInfoDTO.getMappedExternalUserId())).thenReturn(invalidUserInfoDTO);

        Assertions.assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"host","port", "username", "password"})
    void sendEmailWithOneParam(String param) {
        Long ingestionFlowFileId = 10L;
        createMailServiceData(param);
        Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(validIngestionFlowFileDTO));
        Mockito.when(userAuthorizationService.getUserInfo(validIngestionFlowFileDTO.getOrg().getIpaCode(), validUserInfoDTO.getMappedExternalUserId())).thenReturn(validUserInfoDTO);
        Assertions.assertTrue(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowFileId, true));
    }

    private void createMailServiceData() {
        createMailServiceData("blank");
    }

    private void createMailServiceData(String param) {
        String blank = "";
        String host = "HOST";
        String port = "587";
        String username="USER";
        String password="PWD";
        switch (param) {
            case "blank":
                sendMailService  = new SendMailService(blank,blank,blank,blank,blank,blank,blank,javaMailSender);
                break;
            case "host":
                sendMailService  = new SendMailService(host,blank,blank,blank,blank,blank,blank,javaMailSender);
                break;
            case "port":
                sendMailService  = new SendMailService(blank,port,blank,blank,blank,blank,blank,javaMailSender);
                break;
            case "username":
                sendMailService  = new SendMailService(blank,blank,username,blank,blank,blank,blank,javaMailSender);
                break;
            case "password":
                sendMailService  = new SendMailService(blank,blank,blank,password,blank,blank,blank,javaMailSender);
                break;
            default:
                break;
        }

    }

    private void createTestData() {
        String userMail = "usertest@testuser.com";
        validOrganizationInfoDTO = OrganizationDTO.builder()
                .ipaCode("IPA_CODE")
                .adminEmail("codIpaOrg@testuser.com")
                .build();
        invalidOrganizationInfoDTO = OrganizationDTO.builder()
                .applicationCode("")
                .build();

        validUserInfoDTO = new UserInfo();
        validUserInfoDTO.setMappedExternalUserId("VALID_USER");
        validUserInfoDTO.setEmail(userMail);

        invalidUserInfoDTO = new UserInfo();
        invalidUserInfoDTO.mappedExternalUserId(null);
        invalidUserInfoDTO.setEmail(userMail);

        validIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .filePath("PATH_NAME")
                .fileName("FILE_NAME")
                .discardFileName(null)
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .numTotalRows(123L)
                .build();
        invalidIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType(IngestionFlowFileType.OPI)
                .filePath("PATH_NAME")
                .discardFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        errorIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .mappedExternalUserId("VALID_USER")
                .filePath("PATH_NAME")
                .discardFileName("DISCARDED_FILE")
                .numTotalRows(123L)
                .build();
        invalidPathIngestionFlowFileDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .discardFileName("DISCARDED_FILE")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
        invalidDiscardedFileNameDTO = IngestionFlowFileDTO.builder()
                .org(validOrganizationInfoDTO)
                .mappedExternalUserId("VALID_USER")
                .flowFileType(IngestionFlowFileType.PAYMENTS_REPORTING)
                .filePath("PATH_NAME")
                .fileName("FILE_NAME")
                .numTotalRows(123L)
                .build();
    }
}

