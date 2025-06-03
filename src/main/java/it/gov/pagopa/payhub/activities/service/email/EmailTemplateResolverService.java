package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class EmailTemplateResolverService {

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    public EmailTemplateResolverService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }

    public EmailTemplate resolve(EmailTemplateName templateName) {
        return switch (templateName){
            case INGESTION_PAYMENTS_REPORTING_OK -> emailTemplatesConfiguration.getPaymentsReportingFlow().getOk();
            case INGESTION_PAYMENTS_REPORTING_KO -> emailTemplatesConfiguration.getPaymentsReportingFlow().getKo();

            case INGESTION_TREASURY_OPI_OK -> emailTemplatesConfiguration.getTreasuryOpiFlow().getOk();
            case INGESTION_TREASURY_OPI_KO -> emailTemplatesConfiguration.getTreasuryOpiFlow().getKo();

            case INGESTION_DP_INSTALLMENTS_OK -> emailTemplatesConfiguration.getDpInstallmentsFlow().getOk();
            case INGESTION_DP_INSTALLMENTS_KO -> emailTemplatesConfiguration.getDpInstallmentsFlow().getKo();

            case INGESTION_PAGOPA_RT -> emailTemplatesConfiguration.getReceivedPagopaReceipt();

            case INGESTION_ORGANIZATIONS_OK -> emailTemplatesConfiguration.getOrganizationsFlow().getOk();
            case INGESTION_ORGANIZATIONS_KO -> emailTemplatesConfiguration.getOrganizationsFlow().getKo();

            case INGESTION_ORGANIZATIONS_SIL_SERVICE_OK -> emailTemplatesConfiguration.getOrganizationsSilServiceFlow().getOk();
            case INGESTION_ORGANIZATIONS_SIL_SERVICE_KO -> emailTemplatesConfiguration.getOrganizationsSilServiceFlow().getKo();

            case INGESTION_DEBT_POSITIONS_TYPE_OK -> emailTemplatesConfiguration.getDebtPositionsTypeFlow().getOk();
            case INGESTION_DEBT_POSITIONS_TYPE_KO -> emailTemplatesConfiguration.getDebtPositionsTypeFlow().getKo();

            case INGESTION_DEBT_POSITIONS_TYPE_ORG_OK -> emailTemplatesConfiguration.getDebtPositionsTypeOrgFlow().getOk();
            case INGESTION_DEBT_POSITIONS_TYPE_ORG_KO -> emailTemplatesConfiguration.getDebtPositionsTypeOrgFlow().getKo();

            case INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_OK -> emailTemplatesConfiguration.getDebtPositionsTypeOrgOperatorsFlow().getOk();
            case INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_KO -> emailTemplatesConfiguration.getDebtPositionsTypeOrgOperatorsFlow().getKo();

            case INGESTION_ASSESSMENTS_OK -> emailTemplatesConfiguration.getAssessmentsFlow().getOk();
            case INGESTION_ASSESSMENTS_KO -> emailTemplatesConfiguration.getAssessmentsFlow().getKo();

            case EXPORT_PAID_OK -> emailTemplatesConfiguration.getExportPaidFile().getOk();
            case EXPORT_PAID_KO -> emailTemplatesConfiguration.getExportPaidFile().getKo();
            case EXPORT_RECEIPTS_ARCHIVING_OK -> emailTemplatesConfiguration.getExportReceiptsArchivingFile().getOk();
            case EXPORT_RECEIPTS_ARCHIVING_KO -> emailTemplatesConfiguration.getExportReceiptsArchivingFile().getKo();
            case EXPORT_CLASSIFICATIONS_OK -> emailTemplatesConfiguration.getExportClassificationsFile().getOk();
            case EXPORT_CLASSIFICATIONS_KO -> emailTemplatesConfiguration.getExportClassificationsFile().getKo();
        };
    }
}
