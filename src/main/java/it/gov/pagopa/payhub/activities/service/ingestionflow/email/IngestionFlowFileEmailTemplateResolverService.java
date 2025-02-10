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
        return switch (ingestionFlowFileDTO.getFlowFileType()) {
            case IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING -> success
                    ? emailTemplatesConfiguration.getPaymentsReportingFlowOk()
                    : emailTemplatesConfiguration.getPaymentsReportingFlowKo();
            case IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI -> success
                    ? emailTemplatesConfiguration.getTreasuryOpiFlowOk()
                    : emailTemplatesConfiguration.getTreasuryOpiFlowKo();
            default ->
                    throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type " + ingestionFlowFileDTO.getFlowFileType());
        };
    }
}
