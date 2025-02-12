package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import java.util.List;
import java.util.Map;


public interface TreasuryMapperService<T> {

    Map<TreasuryOperationEnum, List<Treasury>> apply(T xml, IngestionFlowFile ingestionFlowFileDTO);

}