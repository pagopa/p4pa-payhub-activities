package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentsRequestDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Slf4j
@Service
public class DataExportClient {

   private final DebtPositionApisHolder debtPositionApisHolder;

    public DataExportClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public PagedInstallmentsPaidView getExportPaidInstallments(String accessToken, PaidInstallmentsRequestDTO paidInstallmentsRequestDTO, Integer page, Integer size, List<String> sort) {
        return debtPositionApisHolder.getDataExportsApi(accessToken).exportPaidInstallments(paidInstallmentsRequestDTO.getOrganizationId(), paidInstallmentsRequestDTO.getOperatorExternalUserId(), paidInstallmentsRequestDTO.getPaymentDateFrom(), paidInstallmentsRequestDTO.getPaymentDateTo(), paidInstallmentsRequestDTO.getDebtPositionTypeOrgId(), page, size, sort);
    }

}
