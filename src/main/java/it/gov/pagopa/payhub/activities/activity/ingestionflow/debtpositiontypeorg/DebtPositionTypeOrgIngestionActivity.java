package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontypeorg;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;

/**
 * Interface for importing debt position type org.
 * Defines methods for processing debt position type org import files and individual debt position type org.
 */

@ActivityInterface
public interface DebtPositionTypeOrgIngestionActivity {


  /**
   * Processes a debt position type org import file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier of the file to process.
   * @return {@link DebtPositionTypeOrgIngestionFlowFileResult} containing the list of ipa code.
   */
  @ActivityMethod(name = "ProcessDpTypeOrgFile")
  DebtPositionTypeOrgIngestionFlowFileResult processFile(Long ingestionFlowFileId);

}
