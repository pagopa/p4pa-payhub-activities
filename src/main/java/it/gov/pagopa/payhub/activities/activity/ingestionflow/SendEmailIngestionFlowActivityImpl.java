package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivity;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.auth.UserAuthorizationService;
import it.gov.pagopa.payhub.activities.utility.SendEmailActivity;
import it.gov.pagopa.payhub.activities.utility.SendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final UserAuthorizationService authorizationService;
    private final SendMailService sendMailService;
    private final IngestionFlowRetrieverService ingestionFlowRetrieverService;

    public SendEmailIngestionFlowActivityImpl(
            UserAuthorizationService authorizationService,
            IngestionFlowRetrieverService ingestionFlowRetrieverService,
            SendMailService sendMailService) {
        this.authorizationService = authorizationService;
        this.ingestionFlowRetrieverService  = ingestionFlowRetrieverService;
        this.sendMailService = sendMailService;
    }

    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @Override
    public boolean sendEmail(String ingestionFlowId, boolean success) throws Exception {
        try {
            IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId));
            UserInfoDTO userInfoDTO = authorizationService.getUserInfoDTO(ingestionFlowDTO.getUserId().getExternalUserId());
            MailTo mailTo =  getMailFromIngestionFlow(ingestionFlowDTO, success);
            mailTo.setTo(new String[]{userInfoDTO.getEmail()});
            SendEmailActivity sendEmailActivity = new SendEmailActivityImpl();
            EmailConfig emailConfig = new EmailConfig();
            JavaMailSender javaMailSender = emailConfig.getJavaMailSender();
            sendEmailActivity.sendEmail(sendMailService, javaMailSender, mailTo);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static MailTo getMailFromIngestionFlow(IngestionFlowDTO ingestionFlowDTO, boolean success) throws Exception {
        Properties properties = MailParameterHelper.getProperties();
        String template = success ? properties.getProperty(Constants.TEMPLATE_LOAD_FILE_OK) :  properties.getProperty(Constants.TEMPLATE_LOAD_FILE_KO);
        DateFormat parser = new SimpleDateFormat(Constants.MAIL_DATE_FORMAT);
        String actualDate = parser.format(new Date());

        Map<String, String> mailMap = new HashMap<>();
        mailMap.put(Constants.ACTUAL_DATE, actualDate);
        mailMap.put(Constants.FILE_NAME, ingestionFlowDTO.getFileName());
        mailMap.put(Constants.TOTAL_ROWS_NUMBER, String.valueOf(ingestionFlowDTO.getTotalRowsNumber()));
        mailMap.put(Constants.MAIL_TEXT, StringSubstitutor.replace(template, mailMap, "{", "}"));

        MailParameterHelper mailParameterHelper = new MailParameterHelper();
        MailTo mailTo = new MailTo();
        mailTo.setTemplateName(template);
        mailTo.setParams(mailMap);
        MailTo dto = mailParameterHelper.getMailParameters(mailTo);
        mailTo.setMailSubject(dto.getMailSubject());
        mailTo.setHtmlText(dto.getHtmlText());
        return mailTo;
    }

}