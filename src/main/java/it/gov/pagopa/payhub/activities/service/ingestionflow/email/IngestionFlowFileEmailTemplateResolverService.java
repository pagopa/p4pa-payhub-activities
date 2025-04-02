package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IngestionFlowFileEmailTemplateResolverService {

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public IngestionFlowFileEmailTemplateResolverService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public EmailTemplate resolve(IngestionFlowFile ingestionFlowFileDTO, boolean success) {
        EmailTemplatesConfiguration.EmailOutcomeBasedTemplates ingestionFlowOutcomeTemplates = switch (ingestionFlowFileDTO.getIngestionFlowFileType()) {
            case IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING -> emailTemplatesConfiguration.getPaymentsReportingFlow();
            case IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_OPI -> emailTemplatesConfiguration.getTreasuryOpiFlow();
            case IngestionFlowFile.IngestionFlowFileTypeEnum.DP_INSTALLMENTS -> emailTemplatesConfiguration.getDpInstallmentsFlow();
            default ->
                    throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type " + ingestionFlowFileDTO.getIngestionFlowFileType());
        };

        return success
                ? ingestionFlowOutcomeTemplates.getOk()
                : ingestionFlowOutcomeTemplates.getKo();
    }
}
