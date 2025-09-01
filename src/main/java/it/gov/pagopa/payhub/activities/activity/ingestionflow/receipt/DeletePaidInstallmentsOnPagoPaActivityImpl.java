package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagoPaInteractionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class DeletePaidInstallmentsOnPagoPaActivityImpl implements DeletePaidInstallmentsOnPagoPaActivity {
    private static final List<DebtPositionOrigin> ORDINARY_DEBT_POSITION_ORIGINS = List.of(DebtPositionOrigin.ORDINARY, DebtPositionOrigin.ORDINARY_SIL, DebtPositionOrigin.SPONTANEOUS, DebtPositionOrigin.SPONTANEOUS_SIL, DebtPositionOrigin.RECEIPT_FILE);

    private final ReceiptService receiptService;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;
    private final InstallmentService installmentService;

    @Override
    public void deletePaidInstallmentsOnPagoPa(DebtPositionDTO debtPositionDTO, Long receiptId) {
        if (!isDebtPositionAndReceiptIdValid(debtPositionDTO, receiptId)) {
            return;
        }

        Optional<InstallmentDTO> installment = debtPositionDTO.getPaymentOptions()
                .stream()
                .flatMap(option -> option.getInstallments().stream())
                .filter(installmentDTO -> receiptId.equals(installmentDTO.getReceiptId()))
                .findFirst();

        if (installment.isEmpty()) {
            log.info("installment not found for receipt id {}", receiptId);
            return;
        }

        installmentService.updateStatusAndSyncStatus(
                installment.get().getInstallmentId(),
                InstallmentStatus.CANCELLED,
                null
        );
    }

    private boolean isDebtPositionAndReceiptIdValid(DebtPositionDTO debtPositionDTO, Long receiptId) {
        if (
                !ORDINARY_DEBT_POSITION_ORIGINS.contains(debtPositionDTO.getDebtPositionOrigin())
                        || !debtPositionDTO.getFlagPuPagoPaPayment()
        ) {
            return false;
        }

        ReceiptDTO receipt = receiptService.getByReceiptId(receiptId);

        if (receipt == null) {
            log.info("receipt not found for receipt id {}", receiptId);
            return false;
        }

        Optional<Organization> organization = organizationService.getOrganizationById(debtPositionDTO.getOrganizationId());
        String brokerFiscalCode = organization.map(Organization::getOrgFiscalCode).orElse(null);

        if (brokerFiscalCode == null) {
            log.info("brokerFiscalCode not found for organization id {}", debtPositionDTO.getOrganizationId());
            return false;
        }

        Broker broker = brokerService.getBrokerByFiscalCode(brokerFiscalCode);

        if (broker == null) {
            log.info("broker not found with brokerFiscalCode {}", brokerFiscalCode);
            return false;
        }

        return PagoPaInteractionModel.SYNC_ACA.equals(broker.getPagoPaInteractionModel())
                || (PagoPaInteractionModel.ASYNC_GPD.equals(broker.getPagoPaInteractionModel())
                && ReceiptOriginType.RECEIPT_PAGOPA.equals(receipt.getReceiptOrigin()));
    }
}
