package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class DeletePaidInstallmentsOnPagoPaActivityImpl implements DeletePaidInstallmentsOnPagoPaActivity {
    public static final List<DebtPositionOrigin> ORDINARY_DEBT_POSITION_ORIGINS = List.of(DebtPositionOrigin.ORDINARY, DebtPositionOrigin.ORDINARY_SIL, DebtPositionOrigin.SPONTANEOUS, DebtPositionOrigin.SPONTANEOUS_SIL, DebtPositionOrigin.RECEIPT_FILE);

    private final ReceiptService receiptService;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;
    private final InstallmentService installmentService;

    @Override
    public void deletePaidInstallmentsOnPagoPa(DebtPositionDTO debtPositionDTO, Long receiptId) {
        if (!ORDINARY_DEBT_POSITION_ORIGINS.contains(debtPositionDTO.getDebtPositionOrigin())) return;
        if (!debtPositionDTO.getFlagPuPagoPaPayment()) return;

        ReceiptDTO receipt = receiptService.getByReceiptId(receiptId);

        if (receipt == null) {
            log.error("receipt not found for receipt id {}", receiptId);
            return;
        }

        Optional<Organization> organization = organizationService.getOrganizationById(debtPositionDTO.getOrganizationId());
        String brokerFiscalCode = organization.map(Organization::getOrgFiscalCode).orElse(null);
        Broker broker = brokerService.getBrokerByFiscalCode(brokerFiscalCode);

        if (
                broker.getGpdKey() != null
                && broker.getGpdKey().length > 0
                && !ReceiptOriginType.RECEIPT_PAGOPA.equals(receipt.getReceiptOrigin())
        ) {
            return;
        }

        Optional<InstallmentDTO> installment = debtPositionDTO.getPaymentOptions()
                .stream()
                .flatMap(option -> option.getInstallments().stream())
                .filter(installmentDTO -> receiptId.equals(installmentDTO.getReceiptId()))
                .findFirst();

        if (installment.isEmpty()) {
            log.error("installment not found for receipt id {}", receiptId);
            return;
        }

        installmentService.updateStatusAndSyncStatus(
                installment.get().getInstallmentId(),
                InstallmentStatus.CANCELLED,
                InstallmentSyncStatus.builder().syncStatusFrom(Objects.requireNonNull(installment.get().getSyncStatus()).getSyncStatusTo()).syncStatusTo(InstallmentStatus.UNPAID).build()
        );
    }
}
