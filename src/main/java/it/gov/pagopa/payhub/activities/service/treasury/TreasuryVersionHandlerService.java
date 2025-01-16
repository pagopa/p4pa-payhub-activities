package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;

import java.io.File;
import java.util.List;

public interface TreasuryVersionHandlerService {

    List<Treasury> handle(File input, IngestionFlowFileDTO ingestionFlowFileDTO, int size);

}
