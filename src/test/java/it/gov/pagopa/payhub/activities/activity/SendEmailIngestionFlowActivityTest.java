package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.reportingflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.activity.reportingflow.service.AsyncSendMailService;
import it.gov.pagopa.payhub.activities.activity.reportingflow.service.IngestionFlowRetrieverService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {

    @Mock
    IngestionFlowRetrieverService ingestionFlowRetrieverService;

    @Mock
    private IngestionFlowDao ingestionFlowDao;

    @Mock
    AsyncSendMailService asyncSendMailService;

    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;

    private MailParams mailParams;

    @BeforeEach
    void init() {
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        mailParams = new MailParams();
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(ingestionFlowRetrieverService, asyncSendMailService, ingestionFlowDao, mailParams, javaMailSender);
    }

    @Test
    void sendEmailIngestionSuccess() {
        boolean success = true;
        String manageFlussoId = "100";
        setMailParams(manageFlussoId, "mail-importFlussoRendicontazione-ok", 1000L);
        boolean result = sendEmailIngestionFlowActivity.sendEmail(manageFlussoId, success);
        assertTrue(result);
    }

    @Test
    void sendEmailIngestionError() {
        boolean success = true;
        String manageFlussoId = "100";
        setMailParams(manageFlussoId, "mail-importFlussoRendicontazione-error", 0L);
        boolean result = sendEmailIngestionFlowActivity.sendEmail(manageFlussoId, success);
        assertTrue(result);
    }

    void setMailParams(String manageFlussoId, String templatename, Long fileSize){
        Long ingestionFlowId = Long.valueOf(manageFlussoId);
        IngestionFlowDTO ingestionFlowDTO = new IngestionFlowDTO();
        ingestionFlowDTO.setFileName("test.zip");
        ingestionFlowDTO.setFlowHandlerId(ingestionFlowId);
        ingestionFlowDTO.setDownloadedFileSize(fileSize);

        /*
        when(ingestionFlowDao.getIngestionFlow(ingestionFlowId))
                .thenReturn(Optional.of(ingestionFlowDTO));
        */

        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());
        String mailText = "Il caricamento del file " + ingestionFlowDTO.getFileName();
        if (fileSize>0) {
            ingestionFlowDTO.setTotalRowsNumber(100L);
            mailText += " è andato a buon fine, tutti i " + fileSize + " dati presenti sono stati caricati correttamente.";
        }
        else  {
            ingestionFlowDTO.setTotalRowsNumber(0L);
            mailText += " NON è andato a buon fine";
        }

        Map<String,String> map = new HashMap<>();
        map.put(Constants.MAIL_TEXT, mailText);
        map.put(Constants.ACTUAL_DATE,actualDate);
        map.put(Constants.FILE_NAME, ingestionFlowDTO.getFileName());

        mailParams.setEmailFromAddress("test@test.com");
        mailParams.setEmailFromName("test");
        mailParams.setTemplateName(templatename);
        mailParams.setParams(map);
        mailParams.setIngestionFlowDTO(ingestionFlowDTO);
    }


}

