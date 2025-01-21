package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.IngestionFlowFileApisHolder;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IngestionFlowFileClient {

    private final IngestionFlowFileApisHolder ingestionFlowFileApisHolder;

    public IngestionFlowFileClient(IngestionFlowFileApisHolder ingestionFlowFileApisHolders) {
        this.ingestionFlowFileApisHolder = ingestionFlowFileApisHolders;
    }

    public IngestionFlowFile findById(Long ingestionFlowFileId, String accessToken) {
        return ingestionFlowFileApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                .crudGetIngestionflowfile(String.valueOf(ingestionFlowFileId));
    }


    public Integer updateStatus(Long ingestionFlowFileId, String status, String codError, String discardFileName, String accessToken) {
        return ingestionFlowFileApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                .updateStatus(ingestionFlowFileId, status,codError, discardFileName);
    }



}
