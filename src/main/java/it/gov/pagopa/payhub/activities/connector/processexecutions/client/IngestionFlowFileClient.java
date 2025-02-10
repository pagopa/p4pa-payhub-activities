package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Service
public class IngestionFlowFileClient {

    private final ProcessExecutionsApisHolder processExecutionsApisHolder;

    public IngestionFlowFileClient(ProcessExecutionsApisHolder ingestionFlowFileApisHolders) {
        this.processExecutionsApisHolder = ingestionFlowFileApisHolders;
    }

    public IngestionFlowFile findById(Long ingestionFlowFileId, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                .crudGetIngestionflowfile(String.valueOf(ingestionFlowFileId));
    }


    public Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFile.StatusEnum  oldStatus, IngestionFlowFile.StatusEnum  newStatus, String codError, String discardFileName, String accessToken) {
        try{
            return processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                    .updateStatus(ingestionFlowFileId, oldStatus.name(), newStatus.name() ,codError, discardFileName);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return 0;
            }
            throw e;
        }
    }

    public PagedModelIngestionFlowFile findByOrganizationIDFlowTypeCreateDate(Long organizationId, FlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom, String accessToken) {
        LocalDateTime creationDateFromLocalDateTime = null;
        if(creationDateFrom!=null){
            creationDateFromLocalDateTime = creationDateFrom.atZoneSameInstant(Utilities.ZONEID).toLocalDateTime();
        }
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), creationDateFromLocalDateTime, null,null, null, null, null, null, null);
    }

    public PagedModelIngestionFlowFile findByOrganizationIDFlowTypeFilename(Long organizationId, FlowFileTypeEnum flowFileType, String fileName, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
            .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), List.of(flowFileType.getValue()), null, null,null, fileName, null, null, null, null);
    }
}
