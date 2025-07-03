package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;

/**
 * Interface for the AssessmentsIngestionActivity.
 * Defines methods for processing assessments files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface AssessmentsIngestionActivity {

  /**
   * Processes a processing assessments file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier related to the file to process.
   * @return {@link AssessmentsIngestionFlowFileResult} containing the list of IUDs and organization Id.
   */
  @ActivityMethod(name = "ProcessAssessmentsFile")
  AssessmentsIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
