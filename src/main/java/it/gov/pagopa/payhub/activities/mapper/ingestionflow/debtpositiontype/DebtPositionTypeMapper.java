package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionTypeMapper {

  public DebtPositionTypeRequestBody map(DebtPositionTypeIngestionFlowFileDTO dto,Long brokerId) {

    return DebtPositionTypeRequestBody.builder()
        .brokerId(brokerId)
        .code(dto.getDebtPositionTypeCode())//todo corretto?
        .description(dto.getDescription())
        .orgType(dto.getOrgType())
        .macroArea(dto.getMacroArea())
        .serviceType(dto.getServiceType())
        .collectingReason(dto.getCollectingReason())
        .taxonomyCode(dto.getTaxonomyCode())
        .flagAnonymousFiscalCode(dto.getFlagAnonymousFiscalCode())
        .flagMandatoryDueDate(dto.getFlagMandatoryDueDate())
        .flagNotifyIo(dto.getFlagNotifyIo())
        .ioTemplateMessage(dto.getIoTemplateMessage())
        .ioTemplateSubject(dto.getIoTemplateSubject())
        .build();
  }
}

