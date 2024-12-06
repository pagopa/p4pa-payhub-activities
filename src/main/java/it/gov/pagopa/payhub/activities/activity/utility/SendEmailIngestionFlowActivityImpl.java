package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.activity.utility.util.Constants;
import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
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

import static it.gov.pagopa.payhub.activities.activity.utility.util.Constants.MAIL_DATE_FORMAT;
import static it.gov.pagopa.payhub.activities.activity.utility.util.Constants.WS_USER;

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
    private final SendMailService sendMailService;
    private final IngestionFlowFileDao ingestionFlowFileDao;

    @Value("${activity.root.path}")
    private String fsRootPath;

    public SendEmailIngestionFlowActivityImpl(
            EmailTemplatesConfiguration emailTemplatesConfiguration,
            UserAuthorizationService userAuthorizationService,
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
            String ipaCode = ingestionFlowFileDTO.getOrg().getIpaCode();
            UserInfoDTO userInfoDTO = userAuthorizationService.getUserInfo(ipaCode, ingestionFlowFileDTO.getOperatorName());
            UserInfoDTO organizationUserInfo = userAuthorizationService.getUserInfo(ipaCode,ipaCode+WS_USER);
            MailTo mailTo = getMailFromIngestionFlow(ingestionFlowFileDTO, success);
            mailTo.setTo(new String[]{userInfoDTO.getEmail()});
            if (organizationUserInfo!= null && StringUtils.isNotBlank(organizationUserInfo.getEmail()) &&
                ! organizationUserInfo.getEmail().equalsIgnoreCase(userInfoDTO.getEmail())) {
                    mailTo.setCc(new String[]{organizationUserInfo.getEmail()});
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
        String htmlText = StringSubstitutor.replace(body, mailTo.getParams(), "{", "}");
        String plainText = Jsoup.clean(htmlText, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        mailTo.setHtmlText(plainText);
        mailTo.setTemplateName(template);
        mailTo.setParams(mailMap);
        mailTo.setMailSubject(subject);

        if (StringUtils.isNotBlank(ingestionFlowFileDTO.getFilePathName()) && StringUtils.isNotBlank(ingestionFlowFileDTO.getFileName()))  {
            String relativeDataPath = ingestionFlowFileDTO.getFilePathName()+"/"+ingestionFlowFileDTO.getFileName();
            mailTo.setAttachmentPath(fsRootPath.concat(Constants.REPORTING_PATH).concat(relativeDataPath));;
        }

        return mailTo;
    }

}