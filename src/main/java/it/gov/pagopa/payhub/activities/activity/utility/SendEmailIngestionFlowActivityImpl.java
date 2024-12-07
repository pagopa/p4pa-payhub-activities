package it.gov.pagopa.payhub.activities.activity.utility;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivity;
import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
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


    @Value("${activity.root.path}")
    private String fsRootPath;

    public SendEmailIngestionFlowActivityImpl(
            EmailTemplatesConfiguration emailTemplatesConfiguration,
            UserAuthorizationService userAuthorizationService,
            OrganizationService organizationAuthorizationService,
            IngestionFlowFileDao ingestionFlowFileDao,
            SendMailService sendMailService) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
        this.userAuthorizationService = userAuthorizationService;
        this.organizationAuthorizationService = organizationAuthorizationService;
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
            String ipaCode = ingestionFlowFileDTO.getOrg().getIpaCode();
            UserInfoDTO userInfoDTO = userAuthorizationService.getUserInfo(ipaCode, ingestionFlowFileDTO.getOperatorName());
            OrganizationDTO organizationDTO = organizationAuthorizationService.getOrganizationInfo(ipaCode);
            MailTo mailTo = getMailFromIngestionFlow(ingestionFlowFileDTO, success);
            mailTo.setTo(new String[]{userInfoDTO.getEmail()});
            if (organizationDTO!= null && StringUtils.isNotBlank(organizationDTO.getAdminEmail()) &&
                ! organizationDTO.getAdminEmail().equalsIgnoreCase(userInfoDTO.getEmail())) {
                    mailTo.setCc(new String[]{organizationDTO.getAdminEmail()});
            }
            mailTo.setHtmlText(mailTo.getHtmlText());
            sendMailService.sendMail(mailTo);
        }
        catch (Exception e){
            log.error("Sending mail failed", e);
            return false;
        }
        return true;
    }

    private MailTo getMailFromIngestionFlow(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success) throws Exception {
        String flowType = ingestionFlowFileDTO.getFlowFileType();
        if (! flowType.equalsIgnoreCase("R")) {
            log.error("Sending e-mail not supported for flow type: {}", flowType);
            throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type: "+flowType);
        }

        String subject = (success
                ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo()
                ).getSubject();
        String body = (success
                ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo()
                ).getBody() ;


        MailTo mailTo = new MailTo();
        mailTo.setMailSubject(StringSubstitutor.replace(subject, mailTo.getParams(), "{", "}"));
        String htmlText = StringSubstitutor.replace(body, mailTo.getParams(), "{", "}");
        String plainText = Jsoup.clean(htmlText, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        mailTo.setHtmlText(plainText);
        mailTo.setParams(getMailParameters(ingestionFlowFileDTO, body));
        mailTo.setMailSubject(subject);

        if (StringUtils.isNotBlank(ingestionFlowFileDTO.getDiscardedFileName()) &&
                StringUtils.isNotBlank(ingestionFlowFileDTO.getFilePathName()))  {
            mailTo.setAttachmentPath(
                    fsRootPath +
                    Constants.REPORTING_PATH +
                    ingestionFlowFileDTO.getDiscardedFileName() +
                    Constants.SLASH +
                    ingestionFlowFileDTO.getFileName());
        }
        return mailTo;
    }

    /**
     *  insert in a map specific mail parameters for ingestion flow
     *
     * @param ingestionFlowFileDTO ingestion flow data
     * @param body  mail body
     * @return Map containing
     */
    private Map<String, String> getMailParameters(IngestionFlowFileDTO ingestionFlowFileDTO, String body) {
        Map<String, String> mailMap = new HashMap<>();
        mailMap.put("actualDate", MAIL_DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        mailMap.put("fileName", ingestionFlowFileDTO.getFileName());
        mailMap.put("totalRowsNumber", String.valueOf(ingestionFlowFileDTO.getNumTotalRows()));
        mailMap.put("mailText", StringSubstitutor.replace(body, mailMap, "{", "}"));
        return mailMap;
    }

}