package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IngestionFlowFileEmailTemplateResolverService {

    public EmailTemplateName resolve(IngestionFlowFile ingestionFlowFileDTO, boolean success) {
        try{
            return EmailTemplateName.valueOf("INGESTION_" + ingestionFlowFileDTO.getIngestionFlowFileType() + (success? "_OK" : "KO"));
        } catch (Exception e){
            throw new IngestionFlowTypeNotSupportedException("Sending e-mail not supported for flow type " + ingestionFlowFileDTO.getIngestionFlowFileType() + ": " + e.getMessage());
        }
    }
}
