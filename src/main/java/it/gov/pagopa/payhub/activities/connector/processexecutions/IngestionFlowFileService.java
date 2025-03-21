package it.gov.pagopa.payhub.activities.connector.processexecutions;

import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;



public interface IngestionFlowFileService {
    Optional<IngestionFlowFile> findById(Long ingestionFlowFileId);
    Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFileStatus oldStatus, IngestionFlowFileStatus newStatus, String codError, String discardFileName);
    List<IngestionFlowFile> findByOrganizationIdFlowTypeCreateDate(Long organizationId, FlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom);
    List<IngestionFlowFile> findByOrganizationIdFlowTypeFilename(Long organizationId, FlowFileTypeEnum flowFileType, String fileName);
    Integer updateProcessingIfNoOtherProcessing(Long ingestionFlowFileId);
}
