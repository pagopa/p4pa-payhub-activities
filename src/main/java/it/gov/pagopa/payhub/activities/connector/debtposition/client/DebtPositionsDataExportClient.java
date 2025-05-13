package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class DebtPositionsDataExportClient {

   private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionsDataExportClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public PagedInstallmentsPaidView getExportPaidInstallments(String accessToken, Long organizationId, String operatorExternalUserId, PaidExportFileFilter paidExportFileFilter, Integer page, Integer size, List<String> sort) {
        OffsetDateTimeIntervalFilter paymentDateTime = paidExportFileFilter.getPaymentDateTime();
        OffsetDateTime from = paymentDateTime != null ? paymentDateTime.getFrom() : null;
        OffsetDateTime to = paymentDateTime != null ? paymentDateTime.getTo() : null;

        return debtPositionApisHolder.getDataExportsApi(accessToken).exportPaidInstallments(organizationId, operatorExternalUserId, from, to, paidExportFileFilter.getDebtPositionTypeOrgId(), page, size, sort);
    }

    public PagedReceiptsArchivingView getExportReceiptsArchivingView(String accessToken, Long organizationId, String operatorExternalUserId, ReceiptsArchivingExportFileFilter receiptsArchivingExportFileFilter, Integer page, Integer size, List<String> sort) {
        OffsetDateTimeIntervalFilter paymentDateTime = receiptsArchivingExportFileFilter.getPaymentDateTime();
        OffsetDateTime from = paymentDateTime != null ? paymentDateTime.getFrom() : null;
        OffsetDateTime to = paymentDateTime != null ? paymentDateTime.getTo() : null;

        return debtPositionApisHolder.getDataExportsApi(accessToken).exportArchivingReceipts(organizationId, operatorExternalUserId, from, to, page, size, sort);
    }

}
