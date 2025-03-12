package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Slf4j
@Service
public class ExportFileClient {

    private final ProcessExecutionsApisHolder processExecutionsApisHolder;

    public ExportFileClient(ProcessExecutionsApisHolder processExecutionsApisHolder) {
        this.processExecutionsApisHolder = processExecutionsApisHolder;
    }

    public PaidExportFile findById(Long exportFileId, String accessToken) {
        try{
            return processExecutionsApisHolder.getPaidExportFileEntityControllerApi(accessToken).crudGetPaidexportfile(String.valueOf(exportFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find PaidExportFile having id {}", exportFileId);
            return null;
        }
    }
}
