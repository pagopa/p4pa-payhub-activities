package it.gov.pagopa.payhub.activities.activity.utility;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivity;
import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.exception.DiscardedIngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.activity.utility.Constants.MAIL_DATE_TIME_FORMATTER;

/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final EmailTemplatesConfiguration emailTemplatesConfiguration;
    private final UserAuthorizationService userAuthorizationService;
    private final OrganizationService organizationAuthorizationService;
    private final SendMailService sendMailService;
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final String baseUrl;

    public SendEmailIngestionFlowActivityImpl(
            EmailTemplatesConfiguration emailTemplatesConfiguration,
            UserAuthorizationService userAuthorizationService,
            OrganizationService organizationAuthorizationService,
            IngestionFlowFileDao ingestionFlowFileDao,
            SendMailService sendMailService,
            @Value("activity.baseUrl") String baseUrl) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
        this.userAuthorizationService = userAuthorizationService;
        this.organizationAuthorizationService = organizationAuthorizationService;
        this.ingestionFlowFileDao  = ingestionFlowFileDao;
        this.sendMailService = sendMailService;
        this.baseUrl = baseUrl;
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
                    .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowFileId));
            String ipaCode = ingestionFlowFileDTO.getOrg().getIpaCode();
            UserInfoDTO userInfoDTO = userAuthorizationService.getUserInfo(ipaCode, ingestionFlowFileDTO.getOperatorName());
            OrganizationDTO organizationDTO = organizationAuthorizationService.getOrganizationInfo(ipaCode);
            MailTo mailTo = getMailFromIngestionFlow(ingestionFlowFileDTO, success);
            mailTo.setTo(new String[]{userInfoDTO.getEmail()});
            if (organizationDTO!= null && StringUtils.isNotBlank(organizationDTO.getAdminEmail()) &&
                ! organizationDTO.getAdminEmail().equalsIgnoreCase(userInfoDTO.getEmail())) {
                    mailTo.setCc(new String[]{organizationDTO.getAdminEmail()});
            }
            sendMailService.sendMail(mailTo);
        }
        catch (Exception e){
            log.error("Sending mail failed", e);
            return false;
        }
        return true;
    }

    private MailTo getMailFromIngestionFlow(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success)
            throws IngestionFlowTypeNotSupportedException, DiscardedIngestionFlowFileNotFoundException {
        Map<String, String> textMap = new HashMap<>();
        String flowType = ingestionFlowFileDTO.getFlowFileType();
        if (! flowType.equalsIgnoreCase("R")) {
            log.error("Sending e-mail not supported for flow type: {}", flowType);
            throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type: "+flowType);
        }
        if (! success && (StringUtils.isBlank(ingestionFlowFileDTO.getDiscardedFileName()) || StringUtils.isBlank(ingestionFlowFileDTO.getFilePathName())))  {
            log.error("Sending error mail when discarded fine not exists not supported");
            throw new DiscardedIngestionFlowFileNotFoundException("Sending error mail when discarded fine not exists not supported");
        }

        String subject = (success
                ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo()
                ).getSubject();
        String body = (success
                ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo()
                ).getBody() ;

        String mailText =
                success ? emailTemplatesConfiguration.getMailTextLoadOk() : emailTemplatesConfiguration.getMailTextLoadKo();

        MailTo mailTo = new MailTo();
        mailTo.setParams(getMailParameters(ingestionFlowFileDTO, success));
        mailTo.setMailSubject(StringSubstitutor.replace(subject, mailTo.getParams(), "{", "}"));
        textMap.put("text", StringSubstitutor.replace(mailText, mailTo.getParams(), "{", "}"));
        String htmlText = StringSubstitutor.replace(body, mailTo.getParams(), "{", "}");
        String newHtmlText = StringSubstitutor.replace(htmlText, textMap, "{", "}");
        String plainText = Jsoup.clean(newHtmlText, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        String finalText = StringSubstitutor.replace(plainText, mailTo.getParams(), "{", "}");
        mailTo.setHtmlText(finalText);
        mailTo.setMailSubject(subject);
        return mailTo;
    }

    /**
     *  insert in a map specific mail parameters for ingestion flow
     *
     * @param ingestionFlowFileDTO ingestion flow data
     * @return Map containing
     */
    private Map<String, String> getMailParameters(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success) {
        Map<String, String> mailMap = new HashMap<>();
        mailMap.put("actualDate", MAIL_DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        mailMap.put("totalRowsNumber", String.valueOf(ingestionFlowFileDTO.getNumTotalRows()));
        if (success) {
            mailMap.put("fileName", ingestionFlowFileDTO.getFileName());
        }
        else  {
            String errorFile = baseUrl +
                    Constants.SLASH + Constants.REPORTING_PATH +
                    Constants.SLASH + Constants.WASTE +
                    Constants.SLASH + ingestionFlowFileDTO.getFilePathName()+
                    Constants.SLASH + ingestionFlowFileDTO.getDiscardedFileName();
            mailMap.put("fileName", ingestionFlowFileDTO.getDiscardedFileName());
            mailMap.put("errorFileLink", errorFile);
        }
        return mailMap;
    }
}