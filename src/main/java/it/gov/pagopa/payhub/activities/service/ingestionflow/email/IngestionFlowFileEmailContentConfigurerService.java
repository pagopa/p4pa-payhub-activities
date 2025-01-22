package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Lazy
@Service
public class IngestionFlowFileEmailContentConfigurerService {

    private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");

    private final IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService;
    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public IngestionFlowFileEmailContentConfigurerService(IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService, EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplateResolverService = emailTemplateResolverService;
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public EmailDTO configure(IngestionFlowFile ingestionFlowFileDTO, boolean success) {
        EmailTemplate template = emailTemplateResolverService.resolve(ingestionFlowFileDTO, success);

        Map<String, String> mailParams = getMailParameters(ingestionFlowFileDTO, success);
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setParams(mailParams);
        emailDTO.setMailSubject(StringSubstitutor.replace(template.getSubject(), mailParams, "{", "}"));
        String plainText = Jsoup.clean(
                StringSubstitutor.replace(template.getBody(), mailParams, "{", "}"),
                "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        emailDTO.setHtmlText(plainText);
        return emailDTO;
    }

    private Map<String, String> getMailParameters(IngestionFlowFile ingestionFlowFileDTO, boolean success) {
        String mailText;
        if (success) {
            mailText = emailTemplatesConfiguration.getMailTextLoadOk();
        } else {
            mailText = emailTemplatesConfiguration.getMailTextLoadKo();
        }
        return Map.of(
                "actualDate", MAILDATETIMEFORMATTER.format(LocalDateTime.now()),
                "totalRowsNumber", String.valueOf(ingestionFlowFileDTO.getNumTotalRows()),
                "fileName", ingestionFlowFileDTO.getFileName(),
                "mailText", mailText
        );
    }
}
