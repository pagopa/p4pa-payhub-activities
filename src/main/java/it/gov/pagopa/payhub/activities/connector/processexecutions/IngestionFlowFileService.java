package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import java.util.Optional;



public interface IngestionFlowFileService {

    Optional<IngestionFlowFile> findById(Long ingestionFlowFileId);
    Integer updateStatus(Long ingestionFlowFileId, String status,String codError, String discardFileName);


}
