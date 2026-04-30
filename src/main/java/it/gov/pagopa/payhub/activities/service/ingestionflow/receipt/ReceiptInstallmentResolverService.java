package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ResolvedInstallmentResult;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReceiptInstallmentResolverService {

    private final OrganizationService organizationService;
    private final InstallmentService installmentService;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;

    public ReceiptInstallmentResolverService(OrganizationService organizationService, InstallmentService installmentService, DebtPositionTypeOrgService debtPositionTypeOrgService) {
        this.organizationService = organizationService;
        this.installmentService = installmentService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    }

    public ResolvedInstallmentResult resolveInstallment(ReceiptWithAdditionalNodeDataDTO receiptDTO) {
        if (receiptDTO.getOrgFiscalCode().startsWith("UNKNOWN_")) {
            return ResolvedInstallmentResult.empty();
        }

        Optional<Organization> organization = organizationService.getOrganizationById(receiptDTO.getOrganizationId());
        if(organization.isEmpty()){
            log.info("Organization with id {} not found", receiptDTO.getOrganizationId());
            return ResolvedInstallmentResult.empty();
        }

        InstallmentDTO mixedInstallment = null;
        InstallmentDTO lastNotifiableInstallment = null;
        List<ResolvedInstallmentResult.NotifiableInstallment> notifiableInstallments = new ArrayList<>();

        List<InstallmentDTO> installments = installmentService
                .getByOrganizationIdAndReceiptId(organization.get().getOrganizationId(), receiptDTO.getReceiptId(), null);

        for (InstallmentDTO installment : installments) {
            DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService
                    .getDebtPositionTypeOrgByInstallmentId(installment.getInstallmentId());

            if ("MIXED".equals(debtPositionTypeOrg.getCode())) {
                mixedInstallment = installment;
            }

            if (debtPositionTypeOrg.getDebtPositionTypeId() > 0) {
                if (debtPositionTypeOrg.getNotifyOutcomePushOrgSilServiceId() != null) {
                    notifiableInstallments.add(
                            new ResolvedInstallmentResult.NotifiableInstallment(debtPositionTypeOrg, installment));
                    lastNotifiableInstallment = installment;
                } else {
                    log.warn("OrgSilServiceId is null for DebtPositionTypeOrg with Id {} and code {}",
                            debtPositionTypeOrg.getDebtPositionTypeOrgId(), debtPositionTypeOrg.getCode());
                }
            }
        }

        InstallmentDTO resolved = mixedInstallment != null ? mixedInstallment : lastNotifiableInstallment;
        return new ResolvedInstallmentResult(resolved, notifiableInstallments, organization.get());
    }
}
