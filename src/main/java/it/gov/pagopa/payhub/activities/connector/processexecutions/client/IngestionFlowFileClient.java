package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.PagedModelIngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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


    public Integer updateStatus(Long ingestionFlowFileId, IngestionFlowFile.StatusEnum  status, String codError, String discardFileName, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                .updateStatus(ingestionFlowFileId, status.name() ,codError, discardFileName);
    }

    public PagedModelIngestionFlowFile findByOrganizationIDFlowTypeCreateDate(Long organizationId, FlowFileTypeEnum flowFileType, OffsetDateTime creationDateFrom, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(organizationId), flowFileType.getValue(), creationDateFrom, null,null, null, null, null, null, null);
    }

}
