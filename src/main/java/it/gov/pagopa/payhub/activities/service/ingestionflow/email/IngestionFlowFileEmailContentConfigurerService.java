package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Lazy
@Service
public class IngestionFlowFileEmailContentConfigurerService {

    private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public IngestionFlowFileEmailContentConfigurerService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public Map<String, String> configureParams(IngestionFlowFile ingestionFlowFileDTO, boolean success) {
        return getMailParameters(ingestionFlowFileDTO, success);
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
