package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Slf4j
@Service
public class IngestionFlowFileClient {

    private final ProcessExecutionsApisHolder processExecutionsApisHolder;

    public IngestionFlowFileClient(ProcessExecutionsApisHolder ingestionFlowFileApisHolders) {
        this.processExecutionsApisHolder = ingestionFlowFileApisHolders;
    }

    public IngestionFlowFile findById(Long ingestionFlowFileId, String accessToken) {
        try{
            return processExecutionsApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                    .crudGetIngestionflowfile(String.valueOf(ingestionFlowFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find IngestionFlowFile having id {}", ingestionFlowFileId);
            return null;
        }
    }

    public Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFileStatus oldStatus, IngestionFlowFileStatus newStatus, IngestionFlowFileResult ingestionFlowFileResult, String accessToken) {
        if(ingestionFlowFileResult==null){
            ingestionFlowFileResult = new IngestionFlowFileResult();
        }
        try {
            return processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                    .updateStatus(ingestionFlowFileId, oldStatus, newStatus,
                            ingestionFlowFileResult.getProcessedRows(), ingestionFlowFileResult.getTotalRows(),
                            ingestionFlowFileResult.getErrorDescription(), ingestionFlowFileResult.getDiscardedFileName());
        } catch (HttpClientErrorException.NotFound e) {
            return 0;
        }
    }

    public PagedModelIngestionFlowFile findByOrganizationIDFlowTypeCreateDate(Long organizationId, IngestionFlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom, String accessToken) {
        LocalDateTime creationDateFromLocalDateTime = null;
        if (creationDateFrom != null) {
            creationDateFromLocalDateTime = creationDateFrom.atZoneSameInstant(Utilities.ZONEID).toLocalDateTime();
        }
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), creationDateFromLocalDateTime, null, null, null, null, null, null, null);
    }

    public PagedModelIngestionFlowFile findByOrganizationIDFlowTypeFilename(Long organizationId, IngestionFlowFileTypeEnum flowFileType, String fileName, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), null, null, null, fileName, null, null, null, null);
    }

    public Integer updateProcessingIfNoOtherProcessing(Long ingestionFlowFileId, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                .crudIngestionFlowFilesUpdateProcessingIfNoOtherProcessing(ingestionFlowFileId);
    }
}
