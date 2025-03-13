package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
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

    public PagedInstallmentsPaidView getExportPaidInstallments(String accessToken, Long organizationId, String operatorExternalUserId, OffsetDateTime paymentDateFrom, OffsetDateTime paymentDateTo, Long debtPositionTypeOrgId, Integer page, Integer size, List<String> sort) {
        return debtPositionApisHolder.getDataExportsApi(accessToken).exportPaidInstallments(organizationId, operatorExternalUserId, paymentDateFrom, paymentDateTo, debtPositionTypeOrgId, page, size, sort);
    }

}
