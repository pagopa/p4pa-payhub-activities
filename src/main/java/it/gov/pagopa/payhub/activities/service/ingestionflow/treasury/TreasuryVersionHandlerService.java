package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

public interface TreasuryVersionHandlerService {

    Pair<IngestionFlowFileResult, List<Treasury>> handle(File input, IngestionFlowFile ingestionFlowFileDTO, int size, List<TreasuryErrorDTO> parsingErrors);

}
