package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.LocalDateIntervalFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Slf4j
@Service
public class DataExportClient {

   private final DebtPositionApisHolder debtPositionApisHolder;

    public DataExportClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public PagedInstallmentsPaidView getExportPaidInstallments(String accessToken, Long organizationId, String operatorExternalUserId, PaidExportFileFilter paidExportFileFilter, Integer page, Integer size, List<String> sort) {
        LocalDateIntervalFilter paymentDate = paidExportFileFilter.getPaymentDate();
        OffsetDateTime from = paymentDate != null ? Utilities.toOffsetDateTime(paymentDate.getFrom()): null;
        OffsetDateTime to = paymentDate != null ? Utilities.toOffsetDateTime(paymentDate.getTo()): null;

        return debtPositionApisHolder.getDataExportsApi(accessToken).exportPaidInstallments(organizationId, operatorExternalUserId, from, to, paidExportFileFilter.getDebtPositionTypeOrgId(), page, size, sort);
    }

}
