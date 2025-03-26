package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
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

    public PaidExportFile findPaidExportFileById(Long exportFileId, String accessToken) {
        try{
            return processExecutionsApisHolder.getPaidExportFileEntityControllerApi(accessToken).crudGetPaidexportfile(String.valueOf(exportFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find PaidExportFile having id {}", exportFileId);
            return null;
        }
    }

    public ExportFile findById(Long exportFileId, String accessToken) {
        try{
            return processExecutionsApisHolder.getExportFileEntityControllerApi(accessToken).crudGetExportfile(String.valueOf(exportFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find ExportFile having id {}", exportFileId);
            return null;
        }
    }

    public Integer updateStatus(Long exportFileId, ExportFileStatus oldStatus, ExportFileStatus newStatus, String codError, String accessToken) {
        try{
            return processExecutionsApisHolder.getExportFileEntityExtendedControllerApi(accessToken)
                .updateExportFileStatus(exportFileId, oldStatus, newStatus, codError);
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find ExportFile having id {} and status {}", exportFileId, oldStatus);
            return null;
        }
    }
}
