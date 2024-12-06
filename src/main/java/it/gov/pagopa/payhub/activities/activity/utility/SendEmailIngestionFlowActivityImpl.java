package it.gov.pagopa.payhub.activities.activity.utility;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.activity.utility.util.Constants;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.activity.utility.util.Constants.MAIL_DATE_FORMAT;

/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final EmailTemplatesConfiguration emailTemplatesConfiguration;
    private final UserAuthorizationServiceImpl userAuthorizationService;
    private final SendMailService sendMailService;
    private final IngestionFlowFileDao ingestionFlowFileDao;

    public SendEmailIngestionFlowActivityImpl(
            EmailTemplatesConfiguration emailTemplatesConfiguration,
            UserAuthorizationServiceImpl userAuthorizationService,
            IngestionFlowFileDao ingestionFlowFileDao,
            SendMailService sendMailService) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
        this.userAuthorizationService = userAuthorizationService;
        this.ingestionFlowFileDao  = ingestionFlowFileDao;
        this.sendMailService = sendMailService;
    }

    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowFileId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @Override
    public boolean sendEmail(Long ingestionFlowFileId, boolean success) {
        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                    .orElseThrow(() -> new IngestionFlowNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));
            UserInfoDTO userInfoDTO = userAuthorizationService.getUserInfo(ingestionFlowFileDTO.getOrg().getIpaCode(), ingestionFlowFileDTO.getOperatorName());
            MailTo mailTo = getMailFromIngestionFlow(ingestionFlowFileDTO, success);
            mailTo.setTo(new String[]{userInfoDTO.getEmail()});
            sendMailService.sendMail(mailTo);
        }
        catch (Exception e){
            log.error("Sending mail failed", e);
            return false;
        }
        return true;
    }

    private MailTo getMailFromIngestionFlow(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success) throws Exception {
        String template = "";
        String flowType = ingestionFlowFileDTO.getFlowFileType();
        if (! flowType.equalsIgnoreCase("R")) {
            log.error("Sending e-mail not supported for flow type: {}", flowType);
            throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type: "+flowType);
        }

        String subject = success ? emailTemplatesConfiguration.getPaymentsReportingFlowOk().getSubject()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo().getSubject();
        String body = success ? emailTemplatesConfiguration.getPaymentsReportingFlowOk().getBody()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo().getBody() ;

        Map<String, String> mailMap = new HashMap<>();
        mailMap.put(Constants.ACTUAL_DATE, MAIL_DATE_FORMAT.format(LocalDateTime.now()));
        mailMap.put(Constants.FILE_NAME, ingestionFlowFileDTO.getFileName());
        mailMap.put(Constants.TOTAL_ROWS_NUMBER, String.valueOf(ingestionFlowFileDTO.getNumTotalRows()));
        mailMap.put(Constants.MAIL_TEXT, StringSubstitutor.replace(template, mailMap, "{", "}"));

        MailTo mailTo = new MailTo();
        mailTo.setMailSubject(StringSubstitutor.replace(subject, mailTo.getParams(), "{", "}"));
        mailTo.setHtmlText(StringSubstitutor.replace(body, mailTo.getParams(), "{", "}"));
        mailTo.setTemplateName(template);
        mailTo.setParams(mailMap);
        mailTo.setMailSubject(subject);
        return mailTo;
    }

}