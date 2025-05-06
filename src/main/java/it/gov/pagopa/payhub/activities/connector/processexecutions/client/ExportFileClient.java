package it.gov.pagopa.payhub.activities.connector.processexecutions.client;

import it.gov.pagopa.payhub.activities.connector.processexecutions.config.ProcessExecutionsApisHolder;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFile;
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

    public ReceiptsArchivingExportFile findReceiptsArchivingExportFileById(Long exportFileId, String accessToken){
        try{
            return processExecutionsApisHolder.getReceiptsArchivingExportFileEntityControllerApi(accessToken).crudGetReceiptsarchivingexportfile(String.valueOf(exportFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find ReceiptsArchivingExportFile having id {}", exportFileId);
            return null;
        }
    }

    public ClassificationsExportFile findClassificationsExportFileById(Long exportFileId, String accessToken){
        try{
            return processExecutionsApisHolder.getClassificationsExportFileEntityControllerApi(accessToken).crudGetClassificationsexportfile(String.valueOf(exportFileId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find ClassificationsExportFile having id {}", exportFileId);
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

    public Integer updateStatus(UpdateStatusRequest updateStatusRequest, String accessToken) {
        try{
            return processExecutionsApisHolder.getExportFileEntityExtendedControllerApi(accessToken)
                .updateExportFileStatus(
                    updateStatusRequest.getExportFileId(),
                    updateStatusRequest.getOldStatus(),
                    updateStatusRequest.getNewStatus(),
                    updateStatusRequest.getFilePathName(),
                    updateStatusRequest.getFileName(),
                    updateStatusRequest.getFileSize(),
                    updateStatusRequest.getExportedRows(),
                    updateStatusRequest.getErrorDescription());
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find ExportFile having id {} and status {}", updateStatusRequest.getExportFileId(), updateStatusRequest.getOldStatus());
            return null;
        }
    }
}
