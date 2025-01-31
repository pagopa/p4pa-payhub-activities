package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;



public interface IngestionFlowFileService {

    Optional<IngestionFlowFile> findById(Long ingestionFlowFileId);
    Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFile.StatusEnum status, String codError, String discardFileName);
    List<IngestionFlowFile> findByOrganizationIdFlowTypeCreateDate(Long organizationId, IngestionFlowFile.FlowFileTypeEnum flowFileType, OffsetDateTime creationDate);
}
