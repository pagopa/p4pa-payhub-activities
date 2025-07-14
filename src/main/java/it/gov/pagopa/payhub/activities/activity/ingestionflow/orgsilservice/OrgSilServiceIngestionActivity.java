package it.gov.pagopa.payhub.activities.activity.ingestionflow.orgsilservice;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;

/**
 * Interface for importing organization sil service.
 * Defines methods for processing organization sil service import files and individual organization sil service.
 */

@ActivityInterface
public interface OrgSilServiceIngestionActivity {


  /**
   * Processes an organization import file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier of the file to process.
   * @return {@link OrgSilServiceIngestionFlowFileResult} containing the list of ipa code.
   */
  @ActivityMethod(name = "ProcessOrganizationSilServiceFile")
  OrgSilServiceIngestionFlowFileResult processFile(Long ingestionFlowFileId);

}
