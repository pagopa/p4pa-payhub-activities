package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import it.gov.pagopa.payhub.activities.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final Environment env;
    private final UserAuthorizationService userAuthorizationService;
    private final SendMailService sendMailService;
    private final IngestionFlowFileDao ingestionFlowFileDao;

    public SendEmailIngestionFlowActivityImpl(
            Environment env,
            UserAuthorizationService userAuthorizationService,
            IngestionFlowFileDao ingestionFlowFileDao,
            SendMailService sendMailService) {
        this.env = env;
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
            UserInfoDTO userInfoDTO = userAuthorizationService.getUserInfo(ingestionFlowFileDTO.getOrgId().getIpaCode(),ingestionFlowFileDTO.getUserId().getExternalUserId());
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
        String flowType = ingestionFlowFileDTO.getFlowType();
        if (flowType.equals("REPORTING")) {
            template += "reportingFlow-";
            template += success ? "ok" : "ko";
        } else {
            log.error("Wrong flow type: {}", flowType);
            throw new Exception("Wrong flow type: "+flowType);
        }

        DateFormat parser = new SimpleDateFormat(Constants.MAIL_DATE_FORMAT);
        String actualDate = parser.format(new Date());

        Map<String, String> mailMap = new HashMap<>();
        mailMap.put(Constants.ACTUAL_DATE, actualDate);
        mailMap.put(Constants.FILE_NAME, ingestionFlowFileDTO.getFileName());
        mailMap.put(Constants.TOTAL_ROWS_NUMBER, String.valueOf(ingestionFlowFileDTO.getTotalRowsNumber()));
        mailMap.put(Constants.MAIL_TEXT, StringSubstitutor.replace(template, mailMap, "{", "}"));

        MailTo mailTo = new MailTo();
        mailTo.setTemplateName(template);
        mailTo.setParams(mailMap);
        MailTo dto = getMailParameters(mailTo);
        mailTo.setMailSubject(dto.getMailSubject());
        mailTo.setHtmlText(dto.getHtmlText());
        return mailTo;
    }

    /**
     *  helper for composing e-mail parameters
     *
     * @param mailDTO parameters not updated
     * @return parameters updated
     */
    public MailTo getMailParameters(MailTo mailDTO) {
        String templateName = mailDTO.getTemplateName();
        String subject = env.getProperty("template."+templateName+".subject");
        String body = env.getProperty("template."+templateName+".body");
        //Assert.notNull(subject, "Invalid email template (missing subject) "+templateName);
        //Assert.notNull(body, "Invalid email template (missing body) "+templateName);
        mailDTO.setMailSubject(StringSubstitutor.replace(subject, mailDTO.getParams(), "{", "}"));
        mailDTO.setHtmlText(StringSubstitutor.replace(body, mailDTO.getParams(), "{", "}"));
        return mailDTO;
    }
}