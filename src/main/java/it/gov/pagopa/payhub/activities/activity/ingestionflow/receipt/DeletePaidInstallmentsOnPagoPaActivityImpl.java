package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagoPaInteractionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class DeletePaidInstallmentsOnPagoPaActivityImpl implements DeletePaidInstallmentsOnPagoPaActivity {

    private static final List<DebtPositionOrigin> ORDINARY_DEBT_POSITION_ORIGINS = List.of(DebtPositionOrigin.ORDINARY,
            DebtPositionOrigin.ORDINARY_SIL, DebtPositionOrigin.SPONTANEOUS, DebtPositionOrigin.SPONTANEOUS_SIL, DebtPositionOrigin.RECEIPT_FILE);

    private final ReceiptService receiptService;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;
    private final AcaService acaService;
    private final GpdService gpdService;

    @Override
    public void deletePaidInstallmentsOnPagoPa(DebtPositionDTO debtPositionDTO, Long receiptId) {
        if (!ORDINARY_DEBT_POSITION_ORIGINS.contains(debtPositionDTO.getDebtPositionOrigin())
                || !debtPositionDTO.getFlagPuPagoPaPayment()) {
            log.info("Debt position with id {} has technical origin or is not synchronized with PagoPA", debtPositionDTO.getDebtPositionId());
            return;
        }

        ReceiptDTO receipt = receiptService.getByReceiptId(receiptId);
        if (receipt == null) {
            log.info("Receipt not found for receipt with id {}", receiptId);
            return;
        }

        Organization organization = organizationService.getOrganizationById(debtPositionDTO.getOrganizationId()).orElse(null);
        if (organization == null) {
            log.info("Organization not found for debt position with id {}", debtPositionDTO.getDebtPositionId());
            return;
        }

        Broker broker = brokerService.getBrokerById(organization.getBrokerId());

        if (broker == null) {
            log.info("Broker not found with id {}", organization.getBrokerId());
            return;
        }

        InstallmentDTO installment = debtPositionDTO.getPaymentOptions()
                .stream().flatMap(paymentOptionDTO -> paymentOptionDTO.getInstallments().stream())
                .filter(installmentDTO -> receiptId.equals(installmentDTO.getReceiptId()))
                .findFirst()
                .orElse(null);

        if (installment == null) {
            log.info("Installment not found with receiptId {} for debt position with id {}", receiptId, debtPositionDTO.getDebtPositionId());
            return;
        }

        PagoPaInteractionModel pagoPaInteractionModel = broker.getPagoPaInteractionModel();

        if (pagoPaInteractionModel == PagoPaInteractionModel.SYNC_ACA) {
            updateSyncStatusInstallmentToDelete(installment, debtPositionDTO);
            try {
                acaService.syncInstallmentAca(installment.getIud(), debtPositionDTO);
            } catch (Exception e) {
                log.info("Error when deleting installment with id {} on ACA: {}", installment.getInstallmentId(), e.getMessage());
            }
        }
        // TODO to check if this behavior is expected
//        else if (pagoPaInteractionModel == PagoPaInteractionModel.ASYNC_GPD && !ReceiptOriginType.RECEIPT_PAGOPA.equals(receipt.getReceiptOrigin())) {
//            updateSyncStatusInstallmentToDelete(installment, debtPositionDTO);
//            try {
//                gpdService.syncInstallmentGpd(installment.getIud(), debtPositionDTO);
//            } catch (Exception e) {
//                log.info("Error when deleting installment with id {} on GPD: {}", installment.getInstallmentId(), e.getMessage());
//            }
//        }
    }

    private void updateSyncStatusInstallmentToDelete(InstallmentDTO installmentDTO, DebtPositionDTO debtPositionDTO) {
        debtPositionDTO.getPaymentOptions()
                .forEach(po -> po.getInstallments().stream()
                        .filter(inst -> Objects.equals(inst.getInstallmentId(), installmentDTO.getInstallmentId()))
                        .forEach(inst -> {
                            inst.setSyncStatus(InstallmentSyncStatus.builder()
                                    .syncStatusFrom(InstallmentStatus.UNPAID)
                                    .syncStatusTo(InstallmentStatus.CANCELLED).build());
                            inst.setStatus(InstallmentStatus.TO_SYNC);
                        }));
    }
}
