package it.gov.pagopa.payhub.activities.activity.ingestionflow.organization;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.IngestionFlowFileProcessorActivity;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;

/**
 * Interface for importing organizations.
 * Defines methods for processing organization import files and individual organizations.
 */

@ActivityInterface
public interface OrganizationIngestionActivity extends IngestionFlowFileProcessorActivity<OrganizationIngestionFlowFileResult> {


  /**
   * Processes an organization import file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier of the file to process.
   * @return {@link OrganizationIngestionFlowFileResult} containing the list of ipa code.
   */
  @ActivityMethod
  @Override
  OrganizationIngestionFlowFileResult processFile(Long ingestionFlowFileId);

}
