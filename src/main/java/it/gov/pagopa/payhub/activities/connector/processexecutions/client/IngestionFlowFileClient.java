package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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


    public Integer updateStatus(Long ingestionFlowFileId, String status, String codError, String discardFileName, String accessToken) {
        return processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                .updateStatus(ingestionFlowFileId, status,codError, discardFileName);
    }



}
