package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pu_sil.PuSilService;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ReceiptPagopaNotifySilActivity} for sending notification to SIL of received receipt.
 * This class handles sending notification to SIL of received receipt.
 */
@Slf4j
@Lazy
@Component
public class ReceiptPagopaNotifySilActivityImpl implements ReceiptPagopaNotifySilActivity {

  private final OrganizationService organizationService;
  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final PuSilService puSilService;
  private final InstallmentService installmentService;

  public ReceiptPagopaNotifySilActivityImpl(OrganizationService organizationService,
      DebtPositionTypeOrgService debtPositionTypeOrgService, PuSilService puSilService,
      InstallmentService installmentService) {
    this.organizationService = organizationService;
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.puSilService = puSilService;
    this.installmentService = installmentService;
  }

  @Override
  public InstallmentDTO notifyReceiptToSil(ReceiptWithAdditionalNodeDataDTO receiptDTO) {
    log.info("Notify receipt to SIL by receiptId {}", receiptDTO.getReceiptId());
    Organization organization = organizationService.getOrganizationByFiscalCode(receiptDTO.getOrgFiscalCode())
        .orElseThrow(()-> new OrganizationNotFoundException("Organization with fiscalCode " + receiptDTO.getOrgFiscalCode() + " not found"));

    if(organization.getFlagNotifyOutcomePush()) {
      // we expect just one Installment
      InstallmentDTO installmentToNotify = installmentService.getByOrganizationIdAndReceiptId(organization.getOrganizationId(),
          receiptDTO.getReceiptId(),null).getFirst();

      DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService
          .getDebtPositionTypeOrgByInstallmentId(installmentToNotify.getInstallmentId());

      // ignoring technical debt position types
      if(debtPositionTypeOrg.getDebtPositionTypeId() > 0) {
        if (debtPositionTypeOrg.getNotifyOutcomePushOrgSilServiceId() != null) {
          puSilService.notifyPayment(debtPositionTypeOrg.getNotifyOutcomePushOrgSilServiceId(), installmentToNotify, organization.getIpaCode());
          return installmentToNotify;
        } else {
          log.warn("OrgSilServiceId is null for DebtPositionTypeOrg with Id {} and code {}",
              debtPositionTypeOrg.getDebtPositionTypeOrgId(), debtPositionTypeOrg.getCode());
        }
      }
    }
    return null;
  }
}
