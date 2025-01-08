package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TreasuryVersionHandlerService {

    Map<TreasuryOperationEnum, List<TreasuryDTO>> handle(File input, IngestionFlowFileDTO ingestionFlowFileDTO, int size, String errorDirectory);

}
