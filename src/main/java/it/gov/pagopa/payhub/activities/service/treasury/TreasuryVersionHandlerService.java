package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import java.io.File;
import java.util.List;

public interface TreasuryVersionHandlerService {

    List<Treasury> handle(File input, IngestionFlowFile ingestionFlowFileDTO, int size);

}
