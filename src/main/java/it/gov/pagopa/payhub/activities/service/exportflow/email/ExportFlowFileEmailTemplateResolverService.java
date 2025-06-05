package it.gov.pagopa.payhub.activities.service.exportflow.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileTypeNotSupported;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ExportFlowFileEmailTemplateResolverService {

    public EmailTemplateName resolve(ExportFile exportFile, boolean success) {
        try{
            return EmailTemplateName.valueOf("EXPORT_" + exportFile.getExportFileType() + (success? "_OK" : "_KO"));
        } catch (Exception e){
            throw new ExportFileTypeNotSupported("Sending e-mail not supported for flow type " + exportFile.getExportFileType() + ": " + e.getMessage());
        }
    }
}
