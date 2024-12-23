package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IngestionFlowFileEmailTemplateResolverService {

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public IngestionFlowFileEmailTemplateResolverService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public EmailTemplate resolve(IngestionFlowFileDTO ingestionFlowFileDTO, boolean success) {
        if (!ingestionFlowFileDTO.getFlowFileType().equals(IngestionFlowFileType.PAYMENTS_REPORTING)) {
            throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type PAYMENTS_REPORTING");
        }

        return success
                ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                : emailTemplatesConfiguration.getPaymentsReportingFlowKo();
    }
}
