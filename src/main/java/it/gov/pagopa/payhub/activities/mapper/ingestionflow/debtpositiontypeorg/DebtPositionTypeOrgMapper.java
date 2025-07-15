package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrgRequestBody;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionTypeOrgMapper {

  private final OrgSilServiceService orgSilServiceService;

    public DebtPositionTypeOrgMapper(OrgSilServiceService orgSilServiceService) {
        this.orgSilServiceService = orgSilServiceService;
    }

    public DebtPositionTypeOrgRequestBody map(DebtPositionTypeOrgIngestionFlowFileDTO dto,Long debtPositionTypeId, Long organizationId) {

    return DebtPositionTypeOrgRequestBody.builder()
        .debtPositionTypeId(debtPositionTypeId)
        .organizationId(organizationId)
        .balance(dto.getBalance())
        .code(dto.getCode())
        .description(dto.getDescription())
        .iban(dto.getIban())
        .postalIban(dto.getPostalIban())
        .postalAccountCode(dto.getPostalAccountCode())
        .holderPostalCc(dto.getHolderPostalCc())
        .orgSector(dto.getOrgSector())
        .xsdDefinitionRef(dto.getXsdDefinitionRef())
        .amountCents(dto.getAmountCents())
        .externalPaymentUrl(dto.getExternalPaymentUrl())
        .flagAnonymousFiscalCode(dto.isFlagAnonymousFiscalCode())
        .flagMandatoryDueDate(dto.isFlagMandatoryDueDate())
        .flagSpontaneous(dto.isFlagSpontaneous())
        .flagNotifyIo(dto.isFlagNotifyIo())
        .ioTemplateSubject(dto.getIoTemplateSubject())
        .ioTemplateMessage(dto.getIoTemplateMessage())
        .flagActive(dto.isFlagActive())
        .flagNotifyOutcomePush(dto.isFlagNotifyOutcomePush())
        .flagAmountActualization(dto.isFlagAmountActualization())
        .flagExternal(dto.isFlagExternal())
        .notifyOutcomePushOrgSilServiceId(getOrgSilServiceId(organizationId, OrgSilServiceType.PAID_NOTIFICATION_OUTCOME))
        .amountActualizationOrgSilServiceId(getOrgSilServiceId(organizationId, OrgSilServiceType.ACTUALIZATION))
        .build();
  }


  private Long getOrgSilServiceId(Long organizationId, OrgSilServiceType orgSilServiceType) {
    if (orgSilServiceType == null) {
      return null;
    }
    return orgSilServiceService.getAllByOrganizationIdAndServiceType(organizationId, orgSilServiceType)
        .stream()
        .findFirst()
        .map(OrgSilService::getOrgSilServiceId)
        .orElse(null);
  }


}
