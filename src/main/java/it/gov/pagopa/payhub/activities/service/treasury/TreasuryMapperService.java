package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;

import java.util.List;
import java.util.Map;


public interface TreasuryMapperService<T> {

    Map<TreasuryOperationEnum, List<Treasury>> apply(T xml, IngestionFlowFileDTO ingestionFlowFileDTO);

}