package it.gov.pagopa.payhub.activities.activity;

import com.sun.jdi.LongValue;
import it.gov.pagopa.payhub.activities.activity.reportingflow.service.AsyncSendMailService;
import it.gov.pagopa.payhub.activities.activity.reportingflow.service.IngestionFlowMailService;
import it.gov.pagopa.payhub.activities.activity.reportingflow.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dao.DebtPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;

import it.gov.pagopa.payhub.activities.model.MailParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
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

    private IngestionFlowMailService ingestionFlowMailService;

    private MailParams mailParams;

    @BeforeEach
    void init() {
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        mailParams = new MailParams();
        ingestionFlowMailService = new IngestionFlowMailService(asyncSendMailService, mailParams, javaMailSender);
    }

    @Test
    void sendEmailIngestionSuccess() {
        boolean success = true;
        String manageFlussoId = "100";
        setMailParams(manageFlussoId, "mail-importFlussoRendicontazione-ok");
        boolean result = ingestionFlowMailService.sendEmail(manageFlussoId, success);
        assertTrue(result);
    }

    @Test
    void sendEmailIngestionError() {
        boolean success = false;
        String manageFlussoId = "100";
        setMailParams(manageFlussoId, "mail-importFlussoRendicontazione-error");
        boolean result = ingestionFlowMailService.sendEmail(manageFlussoId, success);
        assertFalse(result);
    }

    void setMailParams(String manageFlussoId, String templatename){
        Long ingestionFlowId = Long.valueOf(manageFlussoId);
        IngestionFlowDTO ingestionFlowDTO = new IngestionFlowDTO();
        ingestionFlowDTO.setFileName("test.zip");
        ingestionFlowDTO.setFlowHandlerId(ingestionFlowId);

        HashMap<String,String> hm = new HashMap<>();
        hm.put("nomeFile", ingestionFlowDTO.getFileName());
        mailParams.setEmailFromAddress("test@test.com");
        mailParams.setEmailFromName("test");
        mailParams.setParams(hm);
        mailParams.setTemplateName(templatename);
    }


}

