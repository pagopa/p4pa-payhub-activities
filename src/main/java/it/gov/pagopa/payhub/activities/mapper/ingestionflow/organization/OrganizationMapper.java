package it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization;

import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

    public OrganizationCreateDTO map(OrganizationIngestionFlowFileDTO dto, Long brokerId) {

        return OrganizationCreateDTO.builder()
                .brokerId(brokerId)
                .segregationCode(dto.getSegregationCode())

                .ipaCode(dto.getIpaCode())
                .orgFiscalCode(dto.getOrgFiscalCode())
                .externalOrganizationId(dto.getExternalOrganizationId())

                .orgName(dto.getOrgName())
                .orgEmail(dto.getOrgEmail())
                .orgTypeCode(dto.getOrgTypeCode())
                .orgLogo(dto.getOrgLogo())
                .status(OrganizationStatus.valueOf(dto.getStatus()))
                .startDate(dto.getStartDate() != null ? dto.getStartDate().toLocalDate() : null)
                .additionalLanguage(dto.getAdditionalLanguage())

                .iban(dto.getIban())
                .postalIban(dto.getPostalIban())
                .cbillInterBankCode(dto.getCbillInterBankCode())

                .flagNotifyIo(dto.getFlagNotifyIo())
                .flagNotifyOutcomePush(dto.getFlagNotifyOutcomePush())
                .flagPaymentNotification(dto.getFlagNotifyOutcomePush())
                .pdndEnabled(false)
                .flagTreasury(dto.getFlagTreasury())

                .ioApiKey(dto.getIoApiKey())
                .sendApiKey(dto.getSendApiKey())
                .generateNoticeApiKey(dto.getGenerateNoticeApiKey())
                .build();
    }
}


