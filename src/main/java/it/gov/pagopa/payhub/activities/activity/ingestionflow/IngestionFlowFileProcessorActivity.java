package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;

public interface IngestionFlowFileProcessorActivity<T extends IngestionFlowFileResult> {
  T processFile(Long ingestionFlowFileId);
}
