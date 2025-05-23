package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontype;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.IngestionFlowFileProcessorActivity;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;

/**
 * Interface for importing debt position type.
 * Defines methods for processing debt position type import files and individual debt position type.
 */

@ActivityInterface
public interface DebtPositionTypeIngestionActivity extends IngestionFlowFileProcessorActivity<DebtPositionTypeIngestionFlowFileResult> {

  /**
   * Processes a debt position type import file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier of the file to process.
   * @return {@link DebtPositionTypeIngestionFlowFileResult} containing the list of ipa code.
   */
  @ActivityMethod
  @Override
  DebtPositionTypeIngestionFlowFileResult processFile(Long ingestionFlowFileId);

}
