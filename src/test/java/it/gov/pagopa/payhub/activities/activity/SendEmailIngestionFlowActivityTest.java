package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.SendMailService;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.model.MailParams;
import it.gov.pagopa.payhub.activities.utils.Constants;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {

    @Mock
    IngestionFlowRetrieverService ingestionFlowRetrieverService;

    @Mock
    private IngestionFlowDao ingestionFlowDao;

    @Mock
    SendMailService sendMailService;

    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;

    private MailParams mailParams;

    @BeforeEach
    void init() {
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        mailParams = new MailParams();
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(ingestionFlowRetrieverService, sendMailService, mailParams, javaMailSender);
        ingestionFlowRetrieverService = new IngestionFlowRetrieverService(ingestionFlowDao);
    }

    @Test
    void sendEmailIngestionSuccess() {
        boolean success = true;
        String manageFlussoId = "100";
        Long fileSize = 1000L;
        Long totalRowsNumber = 10L;
        String templateName = "mail-importFlussoRendicontazione-ok";
        setMailParams(templateName, Long.valueOf(manageFlussoId), fileSize, totalRowsNumber);
        boolean result = sendEmailIngestionFlowActivity.sendEmail(manageFlussoId, success);
        assertTrue(result);
    }

    @Test
    void sendEmailIngestionError() {
        boolean success = true;
        String manageFlussoId = "100";
        Long fileSize = 0L;
        Long totalRowsNumber = 0L;
        String templateName = "mail-importFlussoRendicontazione-error";
        setMailParams(templateName, Long.valueOf(manageFlussoId), fileSize, totalRowsNumber);
        boolean result = sendEmailIngestionFlowActivity.sendEmail(manageFlussoId, success);
        assertTrue(result);
    }

    /***
     * setting email parameters for test
     *
     * @param templateName      name of the mail template
     * @param ingestionFlowId   id of ingestion flow
     * @param fileSize          file size of loaded file
     * @param totalRowsNumber   total rows number of loaded file
     */
    void setMailParams( String templateName, Long ingestionFlowId, Long fileSize, Long totalRowsNumber){
        IngestionFlowDTO ingestionFlowDTO = new IngestionFlowDTO();
        ingestionFlowDTO.setFileName("test.zip");
        ingestionFlowDTO.setIngestionFlowId(ingestionFlowId);
        ingestionFlowDTO.setDownloadedFileSize(fileSize);
        ingestionFlowDTO.setTotalRowsNumber(totalRowsNumber);
        String mailText = getMailText(ingestionFlowDTO);

        mailParams.setEmailFromAddress("test@test.com");
        mailParams.setEmailFromName("test");

        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());

        Map<String,String> map = new HashMap<>();
        map.put(Constants.MAIL_TEXT, mailText);
        map.put(Constants.ACTUAL_DATE,actualDate);
        map.put(Constants.FILE_NAME, ingestionFlowDTO.getFileName());
        mailParams.setTo(new String[]{mailParams.getEmailFromAddress()});
        mailParams.setTemplateName(templateName);
        mailParams.setMailText(mailText);
        mailParams.setParams(map);
        mailParams.setId(String.valueOf(ingestionFlowId));
        mailParams.setIngestionFlowDTO(ingestionFlowDTO);
    }

    /**
     * method to get mail text for send email ingestion flow
     * @param ingestionFlowDTO bean containing fields of ingestion flow
     * @return String containing email test
     */
    private String getMailText(IngestionFlowDTO ingestionFlowDTO) {
        Long fileSize = ingestionFlowDTO.getDownloadedFileSize();
        Long totalRowsNumber = ingestionFlowDTO.getTotalRowsNumber();
        String mailText = "Il caricamento del file " + ingestionFlowDTO.getFileName();
        if (fileSize>0 && totalRowsNumber>0) {
            mailText += " è andato a buon fine, tutti i " + totalRowsNumber + " dati presenti sono stati caricati correttamente.";
        }
        else  {
            mailText += " NON è andato a buon fine";
        }
        return mailText;
    }
}

