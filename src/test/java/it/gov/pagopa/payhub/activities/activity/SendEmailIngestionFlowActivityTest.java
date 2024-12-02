package it.gov.pagopa.payhub.activities.activity;

import helper.IngestionMailHelper;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.SendMailService;
import it.gov.pagopa.payhub.activities.dto.MailDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    @Mock
    SendMailService sendMailService;

    private JavaMailSender javaMailSender;
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;

    private String fileName;
    private  String templateName;
    private Long ingestionFlowId;
    private Long fileSize;
    private Long totalRowsNumber;
    private String emailFromAddress;
    private String emailToAddress;

    @BeforeEach
    void init() {
        javaMailSender = new JavaMailSenderImpl();
        ingestionFlowId = 100L;
        emailFromAddress = "activities@test.com";
        emailToAddress = "test@test.com";
        fileName = "test.zip";
    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        boolean success = true;
        fileSize = 1000L;
        totalRowsNumber = 10L;
        templateName = "mail-reportingFlow-ok";

        try {
            MailDTO mailDTO = setMailParams();
            sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(sendMailService, javaMailSender, mailDTO);
            assertTrue(sendEmailIngestionFlowActivity.sendEmail(String.valueOf(ingestionFlowId), success));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        boolean success = true;
        fileSize = 0L;
        totalRowsNumber = 0L;
        templateName = "mail-reportingFlow-error";

        try {
            MailDTO mailDTO = setMailParams();
            sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(sendMailService, javaMailSender, mailDTO);
            assertTrue(sendEmailIngestionFlowActivity.sendEmail(String.valueOf(ingestionFlowId), success));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * setting email parameters for test
     */
    private MailDTO setMailParams(){
        IngestionFlowDTO ingestionFlowDTO = new IngestionFlowDTO();
        ingestionFlowDTO.setFileName(fileName);
        ingestionFlowDTO.setIngestionFlowId(ingestionFlowId);
        ingestionFlowDTO.setDownloadedFileSize(fileSize);
        ingestionFlowDTO.setTotalRowsNumber(totalRowsNumber);

        //when(ingestionFlowDao.getIngestionFlow(ingestionFlowId)).thenReturn(Optional.of(ingestionFlowDTO));

        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());

        MailDTO mailDTO  = IngestionMailHelper.getMailIngestionFlowText(ingestionFlowDTO, actualDate);
        mailDTO.setTemplateName(templateName);
        mailDTO.setEmailFromAddress(emailFromAddress);
        mailDTO.setTo(new String[]{emailToAddress});
        mailDTO.setEmailFromAddress(emailFromAddress);

        return mailDTO;
    }
}

