package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessmentsregistry;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;

/**
 * Interface for the AssessmentsRegistryIngestionActivity.
 * Defines methods for processing assessments registry files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface AssessmentsRegistryIngestionActivity {

  /**
   * Processes a processing assessments file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier related to the file to process.
   * @return {@link AssessmentsRegistryIngestionFlowFileResult} containing the list of IUDs and organization Id.
   */
  @ActivityMethod(name = "ProcessAssessmentsRegistryFile")
  AssessmentsRegistryIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
