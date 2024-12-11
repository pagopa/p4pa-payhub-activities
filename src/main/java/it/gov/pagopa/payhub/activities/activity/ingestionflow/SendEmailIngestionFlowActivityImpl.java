package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.enums.FlowFileType;
import it.gov.pagopa.payhub.activities.exception.DiscardedIngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import it.gov.pagopa.payhub.activities.util.Utility;
import it.gov.pagopa.pu.p4paauth.dto.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private final OrganizationService organizationService;
    private final SendMailService sendMailService;
    private final IngestionFlowFileDao ingestionFlowFileDao;
    public static DateTimeFormatter MAIL_DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");

    public SendEmailIngestionFlowActivityImpl(
            EmailTemplatesConfiguration emailTemplatesConfiguration,
            UserAuthorizationService userAuthorizationService,
            OrganizationService organizationService,
            IngestionFlowFileDao ingestionFlowFileDao,
            SendMailService sendMailService) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
        this.userAuthorizationService = userAuthorizationService;
        this.organizationService = organizationService;
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
                    .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot find ingestionFlow having id: "+ ingestionFlowFileId));
            String ipaCode = ingestionFlowFileDTO.getOrg().getIpaCode();
            UserInfo userInfoDTO = userAuthorizationService.getUserInfo(ipaCode, ingestionFlowFileDTO.getMappedExternalUserId());
            OrganizationDTO organizationDTO = organizationService.getOrganizationByIpaCode(ipaCode);
            MailTo mailTo = configureMailFromIngestionFlow(ingestionFlowFileDTO, success);
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

    private MailTo configureMailFromIngestionFlow(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success)
            throws IngestionFlowTypeNotSupportedException, DiscardedIngestionFlowFileNotFoundException {
        Map<String, String> textMap = new HashMap<>();
        String flowType = ingestionFlowFileDTO.getFlowFileType();
        if (! flowType.equalsIgnoreCase(FlowFileType.REPORTING_FLOW_TYPE.getFlowFileType())) {
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
        String mailSubject = StringSubstitutor.replace(mailText, mailTo.getParams(), "{", "}");
        textMap.put("text", mailSubject);
        String htmlText = StringSubstitutor.replace(body, mailTo.getParams(), "{", "}");
        String newHtmlText = StringSubstitutor.replace(htmlText, textMap, "{", "}");
        String plainText = Jsoup.clean(newHtmlText, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        String finalText = StringSubstitutor.replace(plainText, mailTo.getParams(), "{", "}");
        mailTo.setHtmlText(finalText);
        mailTo.setMailSubject(mailSubject);
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
            String errorLink = geErrorFileLink(ingestionFlowFileDTO.getIngestionFlowFileId());
            mailMap.put("fileName", ingestionFlowFileDTO.getDiscardedFileName());
            if (Utility.isNotNullOrEmpty(errorLink)) {
                mailMap.put("errorFileLink", errorLink);
            }
            else {
                mailMap.put("errorFileLink", "");
            }
        }
        return mailMap;
    }

    public String geErrorFileLink(Long ingestionFlowFileId) {
        return ingestionFlowFileDao.findErrorFileUrl(ingestionFlowFileId);
    }

}