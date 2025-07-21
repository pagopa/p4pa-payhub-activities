package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
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
  private final AuthnService authnService;

  public ReceiptPagopaNotifySilActivityImpl(OrganizationService organizationService,
      DebtPositionTypeOrgService debtPositionTypeOrgService, PuSilService puSilService,
      AuthnService authnService) {
    this.organizationService = organizationService;
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.puSilService = puSilService;
    this.authnService = authnService;
  }

  @Override
  public void notifyReceiptToSil(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO) {
    Organization organization = organizationService.getOrganizationByFiscalCode(receiptDTO.getOrgFiscalCode())
        .orElseThrow(()-> new OrganizationNotFoundException("Organization with fiscalCode " + receiptDTO.getOrgFiscalCode() + " not found"));
    if(organization.getFlagNotifyOutcomePush()) {
      DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService
          .getDebtPositionTypeOrgByOrganizationIdAndCode(organization.getOrganizationId(),receiptDTO.getDebtPositionTypeOrgCode());
      if(debtPositionTypeOrg.getNotifyOutcomePushOrgSilServiceId()!=null){
        puSilService.notifyPayment(debtPositionTypeOrg.getNotifyOutcomePushOrgSilServiceId(), installmentDTO,
            authnService.getAccessToken(organization.getIpaCode()));
      }
    }
  }
}
