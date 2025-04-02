package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateNames;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class EmailTemplateResolverService {

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public EmailTemplateResolverService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public EmailTemplate resolve(EmailTemplateNames templateName) {
        return switch (templateName){
            case INGESTION_PAYMENTS_REPORTING_OK -> emailTemplatesConfiguration.getPaymentsReportingFlow().getOk();
            case INGESTION_PAYMENTS_REPORTING_KO -> emailTemplatesConfiguration.getPaymentsReportingFlow().getKo();

            case INGESTION_TREASURY_OPI_OK -> emailTemplatesConfiguration.getTreasuryOpiFlow().getOk();
            case INGESTION_TREASURY_OPI_KO -> emailTemplatesConfiguration.getTreasuryOpiFlow().getKo();

            case INGESTION_DP_INSTALLMENTS_OK -> emailTemplatesConfiguration.getDpInstallmentsFlow().getOk();
            case INGESTION_DP_INSTALLMENTS_KO -> emailTemplatesConfiguration.getDpInstallmentsFlow().getKo();

            case INGESTION_PAGOPA_RT -> emailTemplatesConfiguration.getReceivedPagopaReceipt();
        };
    }
}
