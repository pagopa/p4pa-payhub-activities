package it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization;

import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationRequestBody;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

  public OrganizationRequestBody map(OrganizationIngestionFlowFileDTO dto,Long brokerId) {

    return OrganizationRequestBody.builder()
        .ipaCode(dto.getIpaCode())
        .orgFiscalCode(dto.getOrgFiscalCode())
        .orgName(dto.getOrgName())
        .orgTypeCode(dto.getOrgTypeCode())
        .orgEmail(dto.getOrgEmail())
        .postalIban(dto.getPostalIban())
        .iban(dto.getIban())
        .segregationCode(dto.getSegregationCode())
        .cbillInterBankCode(dto.getCbillInterBankCode())
        .orgLogo(dto.getOrgLogo())
        .status(OrganizationStatus.valueOf(dto.getStatus()))
        .additionalLanguage(dto.getAdditionalLanguage())
        .startDate(dto.getStartDate() != null ? dto.getStartDate().toLocalDate() : null)
        .brokerId(brokerId)
        .flagNotifyIo(dto.getFlagNotifyIo())
        .flagNotifyOutcomePush(dto.getFlagNotifyOutcomePush())
        .flagPaymentNotification(dto.getFlagNotifyOutcomePush())
        .pdndEnabled(false)
        .flagTreasury(dto.getFlagTreasury())
        .build();
  }
}


